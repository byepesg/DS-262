$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path out | Out-Null
$javaFiles = Get-ChildItem -Path "src/pathlab/*.java" | ForEach-Object { $_.FullName }
if (-not $javaFiles) { throw "No se encontraron archivos Java" }
& javac -encoding UTF-8 -d out $javaFiles
if ($LASTEXITCODE -ne 0) { throw "No se pudo compilar" }
Write-Host "Compilación correcta. Ejemplo: java -cp out pathlab.Main maps/simple.txt all"
