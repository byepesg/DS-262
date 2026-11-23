$ErrorActionPreference = "Stop"
./compilar.ps1
$testFiles = Get-ChildItem -Path "tests/pathlab/*.java" | ForEach-Object { $_.FullName }
& javac -encoding UTF-8 -cp out -d out $testFiles
if ($LASTEXITCODE -ne 0) { throw "No se pudieron compilar las pruebas" }
& java -cp out pathlab.PathLabTest
if ($LASTEXITCODE -ne 0) { throw "Pruebas base fallidas" }
& java -cp out pathlab.DifferentialTest
if ($LASTEXITCODE -ne 0) { throw "Pruebas diferenciales fallidas" }
