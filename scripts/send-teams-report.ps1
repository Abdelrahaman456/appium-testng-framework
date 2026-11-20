<#
.SYNOPSIS
    Builds and sends the Tree Mobile Automation daily report to the Microsoft Teams
    "Mobile Automation" channel (or runs in -DryRun mode to preview locally).

.PARAMETER WebhookUrl
    Microsoft Teams Incoming Webhook URL (falls back to $env:TEAMS_WEBHOOK_URL).

.PARAMETER EnvName
    Target environment label (default: UAT).

.PARAMETER ChannelName
    Teams channel label rendered inside the report header (default: Mobile Automation).

.PARAMETER TestFilter
    Optional TestNG class filter passed to -Dtest=... when -RunTests is specified.

.PARAMETER RunTests
    Run Maven tests before building and sending the Teams report.

.PARAMETER DryRun
    Generate target/teams-report/daily_report.md and teams_card.json without posting to Teams.
#>
param(
    [string]$WebhookUrl = $env:TEAMS_WEBHOOK_URL,
    [string]$EnvName = "UAT",
    [string]$ChannelName = "Mobile Automation",
    [string]$TestFilter = "",
    [switch]$RunTests,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$RepoRoot = Split-Path -Parent $PSScriptRoot
Push-Location $RepoRoot

try {
    if ($RunTests) {
        $mvnCmd = Get-Command "mvn" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
        if (-not $mvnCmd) {
            $fallbackMvn = Join-Path (Split-Path -Parent $RepoRoot) "apache-maven-3.9.14-bin\apache-maven-3.9.14\bin\mvn.cmd"
            if (Test-Path $fallbackMvn) {
                $mvnCmd = $fallbackMvn
            } else {
                throw "Maven (mvn) was not found on PATH."
            }
        }
        $mvnArgs = @("-B", "test", "-Denv=$($EnvName.ToLower())")
        if ($TestFilter) {
            $mvnArgs += "-Dtest=$TestFilter"
        }
        Write-Host "[LOCAL RUNNER] Executing: $mvnCmd $($mvnArgs -join ' ')"
        & $mvnCmd @mvnArgs
    }

    if (-not $env:APP_ENV) { $env:APP_ENV = $EnvName }
    if (-not $env:TEAMS_CHANNEL_NAME) { $env:TEAMS_CHANNEL_NAME = $ChannelName }

    if (-not $env:GITHUB_REF_NAME) {
        $branch = (git rev-parse --abbrev-ref HEAD 2>$null)
        if ($LASTEXITCODE -eq 0 -and $branch) { $env:GITHUB_REF_NAME = $branch.Trim() }
    }
    if (-not $env:GITHUB_SHA) {
        $sha = (git rev-parse HEAD 2>$null)
        if ($LASTEXITCODE -eq 0 -and $sha) { $env:GITHUB_SHA = $sha.Trim() }
    }
    if (-not $env:COMMIT_MESSAGE) {
        $msg = (git log -1 --pretty=%s 2>$null)
        if ($LASTEXITCODE -eq 0 -and $msg) { $env:COMMIT_MESSAGE = $msg.Trim() }
    }

    $adb = Get-Command "adb" -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source
    if (-not $adb) {
        $sdkAdb = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
        if (Test-Path $sdkAdb) { $adb = $sdkAdb }
    }
    if ($adb) {
        $devLine = (& $adb devices 2>$null | Select-String -Pattern "\tdevice$" | Select-Object -First 1)
        if ($devLine) {
            $dev = ($devLine.ToString() -split "\s+")[0]
            if (-not $env:DEVICE_INFO) {
                $model = (& $adb -s $dev shell getprop ro.product.model 2>$null)
                $androidVer = (& $adb -s $dev shell getprop ro.build.version.release 2>$null)
                if ($model -or $androidVer) {
                    $env:DEVICE_INFO = "$($model.Trim()) (Android $($androidVer.Trim()))"
                }
            }
            if (-not $env:APP_VERSION) {
                $pkg = if ($EnvName.ToUpper() -eq "PROD") { "sa.com.tree.digital.insurance" } else { "sa.com.tree.digital.insurance.uat" }
                $verLine = (& $adb -s $dev shell dumpsys package $pkg 2>$null | Select-String -Pattern "versionName=" | Select-Object -First 1)
                if ($verLine) {
                    $env:APP_VERSION = ($verLine.ToString().Trim() -replace ".*versionName=", "")
                }
            }
        }
    }

    $pyArgs = @(
        (Join-Path $PSScriptRoot "send_teams_report.py"),
        "--channel", $ChannelName,
        "--env", $EnvName
    )
    if ($WebhookUrl) {
        $pyArgs += @("--webhook-url", $WebhookUrl)
    }
    if ($DryRun) {
        $pyArgs += "--dry-run"
    }

    Write-Host "[LOCAL RUNNER] Running Python Teams Reporter..."
    & python @pyArgs
    if ($LASTEXITCODE -ne 0) {
        exit $LASTEXITCODE
    }
}
finally {
    Pop-Location
}
