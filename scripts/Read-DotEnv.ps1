function Import-DotEnvFile {
    <#
        Loads KEY=VALUE lines from a dotenv-style file into a hashtable.
        Skips empty lines and # comments (lines whose first non-whitespace is #).
        Does not support multiline quoted values (not needed for TrackHire .env.local).
    #>
    [CmdletBinding()]
    param(
        [Parameter(Mandatory)][string]$Path
    )
    $result = @{}
    foreach ($raw in Get-Content -LiteralPath $Path -Encoding UTF8) {
        $line = $raw.Trim()
        if (-not $line) { continue }
        if ($line.StartsWith('#')) { continue }

        $idx = $line.IndexOf('=')
        if ($idx -lt 1) { continue }

        $key = $line.Substring(0, $idx).Trim()
        if (-not $key) { continue }
        $value = $line.Substring($idx + 1).Trim()

        $len = $value.Length
        if ($len -ge 2) {
            $first = $value[0]
            $last = $value[$len - 1]
            if (($first -eq [char]34 -and $last -eq [char]34) -or ($first -eq [char]39 -and $last -eq [char]39)) {
                $value = $value.Substring(1, $len - 2)
            }
        }
        $result[$key] = $value
    }
    return $result
}
