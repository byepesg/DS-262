# PathLab - Entrega 2 (base funcional verificable)

**Proyecto del curso (DS4, team-09).** Entrega 2 del proyecto final: búsqueda de rutas en mapas de cuadrícula.

**Autor:** Alfrek Arthur Pinto García ([@ArtFallen](https://github.com/ArtFallen)), equipo de una sola persona. Diseño, verificación, mediciones y documentación de toda la entrega.

**Java 17 o superior, sin librerías externas.** Construido como evolución del documento de la primera entrega.

## Qué funciona
- Lectura y validación de mapas de texto rectangulares.
- Grafo implícito de hasta cuatro movimientos por celda.
- Cola circular propia y heap mínimo binario propio.
- BFS (solamente costos unitarios), Dijkstra y A* (pesos de 1 a 9).
- Camino de S a E, costos, número de pasos, celdas exploradas, tiempo y vista ASCII.
- Pruebas automatizadas para estructuras, rutas, costos, obstáculos y entradas inválidas, más pruebas diferenciales de referencia.
- Benchmarks CSV y gráficas para resultados preliminares reproducibles.

## Entradas
Caracteres: `S` origen, `E` destino, `.` costo 1, `#` bloqueo, `1`..`9` costo de entrar. No hay movimientos diagonales. BFS **no** se ejecuta para mapas con pesos distintos de 1.

## Compilar y ejecutar (PowerShell, desde la carpeta del proyecto)

```powershell
./compilar.ps1
java -cp out pathlab.Main maps/simple.txt all
java -cp out pathlab.Main maps/ponderado.txt all
java -cp out pathlab.Main maps/sin_ruta.txt all
```

Si PowerShell bloquea los scripts por la política de ejecución, usa `powershell -ExecutionPolicy Bypass -File .\compilar.ps1` o la alternativa sin scripts.

Alternativa sin scripts:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem src/pathlab/*.java | ForEach-Object FullName)
java -cp out pathlab.Main maps/simple.txt all
```

## Probar
Se ejecutan 120 comprobaciones unitarias y 200 comparaciones diferenciales con una implementación independiente de referencia sobre 80 mapas aleatorios reproducibles (semilla fija).

```powershell
./probar.ps1
```

Alternativa:

```powershell
javac -encoding UTF-8 -d out (Get-ChildItem src/pathlab/*.java | ForEach-Object FullName)
javac -encoding UTF-8 -cp out -d out tests/pathlab/PathLabTest.java
java -cp out pathlab.PathLabTest
java -cp out pathlab.DifferentialTest
```

## Contenido de la entrega
- `docs/PathLab_Entrega2.pdf`: documento técnico IEEE de la entrega.
- `docs/PathLab_Entrega2.tex`: fuente LaTeX editable.
- `resultados/benchmark_*.csv`, `benchmark_consolidado.csv` y `grafica_*.png`: mediciones reales del equipo del autor (Java 27, Windows 11), 30 repeticiones por algoritmo.
- `evidencias/`: salidas reproducibles de pruebas, ejemplos, sistema y versión Java, generadas en el equipo del autor.
- `compilar.ps1`, `probar.ps1`, `benchmark.ps1`: comandos Windows PowerShell; también existen scripts `.sh` para Linux/macOS.
- `scripts/generate_maps.py`: generación de mapas aleatorios reproducibles.
- `scripts/generate_graphs.py`: regeneración de las gráficas a partir de `resultados/benchmark_consolidado.csv` (requiere matplotlib).

## Medir y obtener CSV

```powershell
java -cp out pathlab.Benchmark maps/simple.txt 100 > resultados_simple.csv
java -cp out pathlab.Benchmark maps/ponderado.txt 100 > resultados_ponderado.csv
```

La salida incluye algoritmo, tamaño, ruta, pasos, costo, explorados y tiempo medio en milisegundos. Para comparar rendimiento debe repetirse el experimento en el **mismo entorno** y con mapas de varias dimensiones. Los tiempos son sensibles a JIT, carga del equipo y tamaño del mapa.

## Complejidad teórica
- Consultar celda: `O(1)`, memoria del mapa `O(F*C)`.
- Cola circular: agregar/quitar `O(1)`, memoria `O(V)`.
- Heap binario: insertar/extraer mínimo `O(log n)`; espacio hasta `O(E)` por estados repetidos.
- BFS: `O(V+E)`, memoria `O(V)`.
- Dijkstra y A*: peor caso `O((V+E)log V)` para esta cuadrícula con grado máximo 4; memoria `O(V+E)` por heap con entradas repetidas, distancias y predecesores.

`V` es cantidad de celdas transitables, `E` es cantidad de conexiones posibles. La heurística Manhattan es admisible y consistente porque todos los costos de movimiento son >= 1.

## Arquitectura
- `GridMap`: validación, representación de mapa, pesos y obstáculos.
- `Position`: coordenadas inmutables.
- `CircularQueue`: cola FIFO para BFS.
- `BinaryMinHeap`: cola de prioridad de Dijkstra/A*.
- `PathFinder`: algoritmos, vecinos y reconstrucción.
- `SearchResult`: resumen de cada ejecución.
- `Main`: consola.
- `Benchmark`: experimentos.
- `PathLabTest`: pruebas sin dependencias.

## Limitaciones conocidas
- Solo movimientos ortogonales (sin diagonales) y costos enteros de 1 a 9.
- BFS se omite en mapas con pesos distintos de 1 porque no garantiza costo mínimo.
- Los tiempos de los benchmarks son sensibles a JIT, carga del equipo y tamaño del mapa; no son resultados universales.
- Sin interfaz gráfica ni mapas dinámicos.

## Autoría y referencias
- **Autor:** Alfrek Arthur Pinto García ([@ArtFallen](https://github.com/ArtFallen)), team-09 de DS4. Se usaron herramientas de IA como apoyo durante el desarrollo.
- **Verificación:** las evidencias en `evidencias/` y los benchmarks en `resultados/` se generaron en el equipo del autor (Windows 11, Java 27): 120 comprobaciones unitarias y 200 comparaciones diferenciales superadas.
- **Referencias:** Goodrich, Tamassia y Goldwasser, *Data Structures and Algorithms in Java* (6.a ed.); Dijkstra (1959); Hart, Nilsson y Raphael (1968). Detalladas en `docs/PathLab_Entrega2.pdf`.
