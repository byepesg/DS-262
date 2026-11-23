#!/usr/bin/env bash
set -euo pipefail
mkdir -p out resultados
javac -encoding UTF-8 -d out src/pathlab/*.java
for n in 20 50 100 200; do
  java -cp out pathlab.Benchmark "maps/aleatorio_${n}.txt" 30 > "resultados/benchmark_${n}.csv"
done
