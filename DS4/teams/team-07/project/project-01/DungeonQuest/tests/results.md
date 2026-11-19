# Resultados de las pruebas

Generado con `tests/run_tests.gd` (Godot 4.7.2-stable (official)).

## Verificaciones por estructura

| Estructura | Aprobadas | Total |
|---|---|---|
| MinHeap | 11 | 11 |
| CircularQueue | 10 | 10 |
| ComboTrie | 11 | 11 |
| DungeonGraph (grafo) | 5 | 5 |
| BFS | 9 | 9 |
| Union-Find | 4 | 4 |
| Generación procedural | 10 | 10 |
| SkillTree | 27 | 27 |
| SkillTree (guardado) | 7 | 7 |
| Integración | 6 | 6 |
| **Total** | **100** | **100** |

## Generación procedural (n = 8 salas, 16 combinaciones de paredes de entrada/salida, 150 semillas c/u)

| Métrica | Resultado |
|---|---|
| Generaciones probadas | 2400 |
| Éxito a la primera | 2398 (99.9 %) |
| Éxito con hasta 30 reintentos | 2400 (100.0 %) |
| Mapas con alguna violación (conexo, simétrico, celdas únicas, hojas, direcciones) | ninguno |

## Mediciones de tiempo

### MinHeap: n inserciones seguidas de n extracciones

| n | tiempo total (ms) | µs por operación | µs/op ÷ log₂(n) |
|---|---|---|---|
| 1000 | 13.4 | 6.70 | 0.672 |
| 10000 | 197.9 | 9.89 | 0.745 |
| 100000 | 2353.5 | 11.77 | 0.708 |

### CircularQueue: n encolados seguidos de n desencolados

| n | tiempo total (ms) | µs por operación |
|---|---|---|
| 1000 | 1.3 | 0.626 |
| 10000 | 8.7 | 0.435 |
| 100000 | 84.9 | 0.424 |

### DungeonGraph: generación (Kruskal + Union-Find) y BFS según el número de salas

| Salas | generación (ms) | aristas | BFS entrada→salida (µs, promedio de 20) | salas en el camino |
|---|---|---|---|---|
| 8 | 0.19 | 8 | 8.2 | 5 |
| 32 | 0.65 | 39 | 31.8 | 10 |
| 128 | 6.73 | 159 | 125.2 | 25 |
| 512 | 86.91 | 639 | 500.9 | 46 |

### SkillTree: n habilidades (cadena de dos ramas)

| n | n inserciones (ms) | find del último (µs) | levels() (ms) |
|---|---|---|---|
| 100 | 5.4 | 58 | 0.17 |
| 500 | 134.8 | 297 | 1.01 |
| 2000 | 2448.8 | 2448 | 4.12 |
