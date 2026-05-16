#Requires -Version 5.1
<#
.SYNOPSIS
    Seed MySQL, build TrackHire (`mvn clean package` or `clean verify`), optionally deploy WAR to Tomcat 9 and start Tomcat.

.DESCRIPTION
    Reads .env.local at repo root. Uses mysql CLI (same server as MySQL Workbench).
    Prerequisites: JDK 11, Maven 3.8+, MySQL Server 8+, MySQL CLI. Tomcat (`CATALINA_HOME`) required unless `-SkipTomcat`.

    `-Verify`: **`mvn clean verify`** (Surefire + Failsafe on H2). Default **`mvn clean package`** runs Surefire unless `-SkipTests` or **`TRACKHIRE_SKIP_MVN_TESTS=1`**. `-Verify` with `-SkipTests` skips Maven tests—avoid that combo if you want integration tests.

    `-SkipTomcat`: seed + Maven only; emits **`target\S1-TEAM18.war`**. Does not set JDBC env on a Tomcat process—configure **`DB_USER`**, **`DB_PASSWORD`**, optional **`DB_URL`** on your server (e.g. Tomcat **`setenv.bat`** or IDE Community Server Connector).

#>
param(
    [switch]$SkipTests,
    [switch]$SkipSeed,
    [switch]$Verify,
    [switch]$SkipTomcat
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$EnvFile = Join-Path $RepoRoot '.env.local'

function Write-Step { param([string]$Message) Write-Host "[TrackHire] $Message" -ForegroundColor Cyan }

if (-not (Test-Path -LiteralPath $EnvFile)) {
    Write-Error "Missing $EnvFile. Copy .env.local.example to .env.local and configure DB_* (and CATALINA_HOME unless you use -SkipTomcat only)."
}

. "$PSScriptRoot\Read-DotEnv.ps1"
$table = Import-DotEnvFile -Path $EnvFile
foreach ($k in $table.Keys) {
    $v = [string]$table[$k]
    if ([string]::IsNullOrWhiteSpace($v)) { continue }
    Set-Item -Path "Env:$k" -Value $v -Force
}

if ([string]::IsNullOrWhiteSpace($env:DB_PASSWORD)) {
    Write-Error "DB_PASSWORD is required in .env.local (MySQL/JDBC credentials for runtime and seed)."
}

$mysqlHost = if ([string]::IsNullOrWhiteSpace($env:MYSQL_HOST)) { '127.0.0.1' } else { $env:MYSQL_HOST }
$mysqlPort = if ([string]::IsNullOrWhiteSpace($env:MYSQL_PORT)) { '3306' } else { $env:MYSQL_PORT }
$sqlUser = if ([string]::IsNullOrWhiteSpace($env:DB_USER)) { 'root' } else { $env:DB_USER }

$env:MYSQL_HOST = $mysqlHost
$env:MYSQL_PORT = $mysqlPort
$env:DB_USER = $sqlUser

$catalina = $null
$startupBat = $null

if (-not $SkipTomcat) {
    $catalina = $env:CATALINA_HOME
    if ([string]::IsNullOrWhiteSpace($catalina)) {
        Write-Error 'CATALINA_HOME is missing in .env.local (Apache Tomcat 9 installation directory). Or use -SkipTomcat when only building the WAR / seeding.'
    }
    $catalina = $catalina.TrimEnd('\', '/')

    $startupBat = Join-Path $catalina 'bin\startup.bat'
    if (-not (Test-Path -LiteralPath $startupBat)) {
        Write-Error "CATALINA_HOME is invalid: $startupBat not found."
    }
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error 'Maven (mvn) not on PATH. Add Maven bin to PATH.'
}

$mvnExe = (Get-Command mvn).Source

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Write-Error 'java not on PATH. Install JDK 11 and configure JAVA_HOME if needed.'
}

$mysqlExe = $env:MYSQL_EXECUTABLE
if ([string]::IsNullOrWhiteSpace($mysqlExe)) {
    $mc = Get-Command mysql.exe -ErrorAction SilentlyContinue
    if (-not $mc) {
        Write-Error 'mysql.exe not found. Install MySQL Server client tools or set MYSQL_EXECUTABLE in .env.local.'
    }

    $mysqlExe = $mc.Source
}
elseif (-not (Test-Path -LiteralPath $mysqlExe)) {
    Write-Error "MYSQL_EXECUTABLE points to a missing file: $mysqlExe"
}

Write-Step "Checking TCP $mysqlHost`:$mysqlPort ..."
try {
    $tnc = Test-NetConnection -ComputerName $mysqlHost -Port ([int]$mysqlPort) -WarningAction SilentlyContinue
    if (-not $tnc.TcpTestSucceeded) {
        Write-Error "Cannot reach MySQL at ${mysqlHost}:${mysqlPort}. Start MySQL Server, then retry (Workbench uses this same host)."
    }
}
catch {
    Write-Error "Failed to probe MySQL port: $_"
}

$seedSql = Join-Path $RepoRoot 'seed.sql'
if (-not (Test-Path -LiteralPath $seedSql)) {
    Write-Error "Missing seed.sql at $seedSql"
}

$defaultsFile = $null

if (-not $SkipSeed) {
    Write-Step 'Applying seed.sql via mysql CLI ...'
    $defaultsFile = [System.IO.Path]::GetTempFileName()
    Remove-Item -LiteralPath $defaultsFile -Force
    $defaultsFile += '.cnf'

    # MySQL cnf escapes: backslashes in password doubled
    $cnfPwd = $env:DB_PASSWORD -replace '\\', '\\' -replace '\r|\n', ' '

    @"
[client]
host=$mysqlHost
port=$mysqlPort
user=$sqlUser
password=$cnfPwd
"@ | Set-Content -LiteralPath $defaultsFile -Encoding ascii -Force

    try {
        $identity = [System.Security.Principal.WindowsIdentity]::GetCurrent().Name
        icacls $defaultsFile /inheritance:r | Out-Null
        icacls $defaultsFile /grant:r "${identity}:F" | Out-Null
    }
    catch {
        Write-Verbose "Could not tighten ACL on temp mysql defaults file: $_"
    }

    try {
        $canonicalSeed = (Resolve-Path $seedSql).ProviderPath

        # Pipe SQL on stdin (paths with spaces-safe). Avoids Start-Process -ArgumentList mangling -e "source '...'".
        $seedSqlText = Get-Content -LiteralPath $canonicalSeed -Raw -Encoding UTF8
        $seedSqlText | & $mysqlExe `
            "--defaults-extra-file=$defaultsFile" `
            --protocol=TCP `
            --default-character-set=utf8mb4

        $mysqlExit = $LASTEXITCODE
        if ($null -eq $mysqlExit) { $mysqlExit = -1 }
        if ([int]$mysqlExit -ne 0) {
            Write-Error "mysql seed failed (exit $mysqlExit). Check errors printed above. Verify DB_USER / DB_PASSWORD in .env.local (same as Workbench for ${mysqlHost})."
        }
    }
    finally {
        if ($defaultsFile -and (Test-Path -LiteralPath $defaultsFile)) {
            Remove-Item -LiteralPath $defaultsFile -Force -ErrorAction SilentlyContinue
        }

        $defaultsFile = $null
    }
}
else {
    Write-Step 'Skipping seed step (-SkipSeed).'
}

$mvnPhase = if ($Verify) { 'verify' } else { 'package' }
$mvnArgs = @('-f', (Join-Path $RepoRoot 'pom.xml'), 'clean', $mvnPhase)
if (
    $SkipTests -or
    (-not ([string]::IsNullOrWhiteSpace($env:TRACKHIRE_SKIP_MVN_TESTS)) -and $env:TRACKHIRE_SKIP_MVN_TESTS -eq '1')
) {
    $mvnArgs += '-DskipTests'
}

Write-Step "Running maven clean $mvnPhase ..."
& $mvnExe @mvnArgs
$mvnExit = $LASTEXITCODE
if ($null -eq $mvnExit) { $mvnExit = -1 }

if ($mvnExit -ne 0) {
    Write-Error "Maven build failed (exit $mvnExit)."
}

$war = Join-Path $RepoRoot 'target\S1-TEAM18.war'
if (-not (Test-Path -LiteralPath $war)) {
    Write-Error "WAR missing after build: $war"
}

$warPathPrint = (Resolve-Path -LiteralPath $war).Path

if ($SkipTomcat) {
    Write-Step 'Skipping Tomcat (-SkipTomcat): WAR not deployed; server not started.'
    Write-Host ('[TrackHire] WAR:  ' + $warPathPrint) -ForegroundColor Yellow
    Write-Host '[TrackHire] Deploy this WAR from your IDE (e.g. Community Server Connector) or copy to Tomcat webapps. Set JVM/env for DB_USER, DB_PASSWORD, and DB_URL on that server.' -ForegroundColor DarkGray
}
else {
    $webapps = Join-Path $catalina 'webapps'
    if (-not (Test-Path -LiteralPath $webapps)) {
        Write-Error "Missing webapps directory: $webapps"
    }

    $destWar = Join-Path $webapps 'S1-TEAM18.war'
    $exploded = Join-Path $webapps 'S1-TEAM18'

    Write-Step 'Deploying WAR to Tomcat webapps ...'
    if (Test-Path -LiteralPath $exploded) {
        Write-Verbose "Removing exploded folder for clean redeploy: $exploded"
        Remove-Item -LiteralPath $exploded -Recurse -Force -ErrorAction SilentlyContinue
    }

    Copy-Item -LiteralPath $war -Destination $destWar -Force

    Write-Step 'Applying JDBC env for Tomcat JVM (inherits DB_USER, DB_PASSWORD, optional DB_URL) ...'
    Set-Item -Path 'Env:DB_USER' -Value $sqlUser -Force
    Set-Item -Path 'Env:DB_PASSWORD' -Value $env:DB_PASSWORD -Force

    if (-not [string]::IsNullOrWhiteSpace($env:DB_URL)) {
        Set-Item -Path 'Env:DB_URL' -Value $env:DB_URL.Trim() -Force
    }
    else {
        Remove-Item -Path Env:DB_URL -ErrorAction SilentlyContinue
    }

    $catalinaRoot = Resolve-Path $catalina | Select-Object -ExpandProperty Path
    $binDir = Join-Path $catalinaRoot 'bin'

    Write-Step 'Starting Tomcat ...'
    Start-Process -FilePath $startupBat -WorkingDirectory $binDir -WindowStyle Hidden | Out-Null

    Write-Step 'Done. Open http://localhost:8080/S1-TEAM18/'
    Write-Host '[TrackHire] Tomcat writes logs under '
    Write-Host ('  ' + (Join-Path $catalinaRoot 'logs')) -ForegroundColor Yellow
}
