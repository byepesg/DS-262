#!/usr/bin/env bash
set -euo pipefail
mkdir -p out
javac -encoding UTF-8 -d out src/pathlab/*.java
javac -encoding UTF-8 -cp out -d out tests/pathlab/*.java
java -cp out pathlab.PathLabTest
java -cp out pathlab.DifferentialTest
