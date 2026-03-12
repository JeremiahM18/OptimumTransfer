$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$mainSource = Join-Path $projectRoot "src/main/java"
$mainOutput = Join-Path $projectRoot "build/classes/main"

if (-not (Test-Path $mainSource)) {
    throw "Main source directory not found: $mainSource"
}

New-Item -ItemType Directory -Force -Path $mainOutput | Out-Null

$javaFiles = Get-ChildItem -Path $mainSource -Recurse -Filter *.java | ForEach-Object { $_.FullName }

if ($javaFiles.Count -eq 0) {
    throw "No Java source files found under $mainSource"
}

javac -d $mainOutput $javaFiles
Write-Host "Compiled $($javaFiles.Count) source files to $mainOutput"
