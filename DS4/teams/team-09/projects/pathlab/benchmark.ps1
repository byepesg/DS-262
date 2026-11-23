$ErrorActionPreference = "Stop"
./compilar.ps1
New-Item -ItemType Directory -Force -Path resultados | Out-Null
foreach ($n in @(20,50,100,200)) {
  & java -cp out pathlab.Benchmark "maps/aleatorio_$n.txt" 30 | Out-File -Encoding utf8 "resultados/benchmark_$n.csv"
  if ($LASTEXITCODE -ne 0) { throw "Benchmark fallido en tamano $n" }
}
Write-Host "CSV disponibles en resultados/"
