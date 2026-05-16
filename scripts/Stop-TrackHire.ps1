#Requires -Version 5.1
<#
.SYNOPSIS
    Shutdown Tomcat 9 referenced by .env.local (CATALINA_HOME).
#>
Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$RepoRoot = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$EnvFile = Join-Path $RepoRoot '.env.local'

function Write-Step { param([string]$Message) Write-Host "[TrackHire] $Message" -ForegroundColor Cyan }

if (-not (Test-Path -LiteralPath $EnvFile)) {
    Write-Error "Missing $EnvFile. Copy .env.local.example to .env.local."
}

. "$PSScriptRoot\Read-DotEnv.ps1"
$table = Import-DotEnvFile -Path $EnvFile

foreach ($k in $table.Keys) {
    $v = [string]$table[$k]
    if ([string]::IsNullOrWhiteSpace($v)) { continue }
    Set-Item -Path "Env:$k" -Value $v -Force
}

$catalina = $env:CATALINA_HOME
if ([string]::IsNullOrWhiteSpace($catalina)) {
    Write-Error 'CATALINA_HOME missing in .env.local.'
}

$catalina = $catalina.TrimEnd('\', '/')
$shutdownBat = Join-Path $catalina 'bin\shutdown.bat'
if (-not (Test-Path -LiteralPath $shutdownBat)) {
    Write-Error "shutdown.bat missing: $shutdownBat"
}

$binDir = Join-Path ((Resolve-Path $catalina).Path) 'bin'
Write-Step "Running Tomcat shutdown: $shutdownBat ..."
Start-Process -FilePath $shutdownBat -WorkingDirectory $binDir -WindowStyle Hidden -Wait | Out-Null
Write-Step 'Tomcat shutdown command completed.'
