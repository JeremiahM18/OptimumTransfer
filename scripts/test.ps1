$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mainSource = Join-Path $projectRoot "src/main/java"
$testSource = Join-Path $projectRoot "src/test/java"
$testOutput = Join-Path $projectRoot "build/classes/test"

& (Join-Path $PSScriptRoot "compile.ps1")

if (-not (Test-Path $testSource)) {
    Write-Host "No test sources found under $testSource"
    exit 0
}

New-Item -ItemType Directory -Force -Path $testOutput | Out-Null

$mainFiles = Get-ChildItem -Path $mainSource -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$testFiles = Get-ChildItem -Path $testSource -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if ($testFiles.Count -eq 0) {
    Write-Host "No test sources found under $testSource"
    exit 0
}

$allFiles = @($mainFiles) + @($testFiles)
javac -d $testOutput $allFiles
java -cp $testOutput tests.TestRunner
