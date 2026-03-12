$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$testSource = Join-Path $projectRoot "src/test/java"
$mainOutput = Join-Path $projectRoot "build/classes/main"
$testOutput = Join-Path $projectRoot "build/classes/test"

& (Join-Path $PSScriptRoot "compile.ps1")

if (-not (Test-Path $testSource)) {
    Write-Host "No test sources found under $testSource"
    exit 0
}

New-Item -ItemType Directory -Force -Path $testOutput | Out-Null

$testFiles = Get-ChildItem -Path $testSource -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if ($testFiles.Count -eq 0) {
    Write-Host "No test sources found under $testSource"
    exit 0
}

javac -cp $mainOutput -d $testOutput $testFiles
java -cp "$mainOutput;$testOutput" tests.TestRunner
