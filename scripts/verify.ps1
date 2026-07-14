param(
    [string]$JavaHome = 'C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot',
    [string]$AndroidHome = 'C:\Users\Foo\AppData\Local\Android\Sdk',
    [string]$DriveLetter = 'W:'
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$driveRoot = "$DriveLetter\"

if (-not (Test-Path -LiteralPath "$JavaHome\bin\java.exe")) {
    throw "JDK 17 not found: $JavaHome"
}
if (-not (Test-Path -LiteralPath $AndroidHome)) {
    throw "Android SDK not found: $AndroidHome"
}
if (Test-Path -LiteralPath $driveRoot) {
    throw "$DriveLetter is already in use. Pass another unused drive letter."
}

subst $DriveLetter $projectRoot
try {
    $env:JAVA_HOME = $JavaHome
    $env:ANDROID_HOME = $AndroidHome
    $env:PATH = "$JavaHome\bin;$AndroidHome\platform-tools;$env:PATH"
    Push-Location $driveRoot
    try {
        & '.\gradlew.bat' :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
        if ($LASTEXITCODE -ne 0) {
            throw "Android verification failed with exit code $LASTEXITCODE"
        }
    } finally {
        Pop-Location
    }
} finally {
    subst $DriveLetter /d
}

