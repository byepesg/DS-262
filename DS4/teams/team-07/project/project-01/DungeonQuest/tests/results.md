# Resultados de las pruebas

Generado con `tests/run_tests.gd` (Godot 4.6.1-stable (official)).

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
| 1000 | 31.5 | 15.73 | 1.578 |
| 10000 | 314.4 | 15.72 | 1.183 |
| 100000 | 3623.2 | 18.12 | 1.091 |

### CircularQueue: n encolados seguidos de n desencolados

| n | tiempo total (ms) | µs por operación |
|---|---|---|
| 1000 | 1.3 | 0.635 |
| 10000 | 13.4 | 0.669 |
| 100000 | 127.1 | 0.636 |

### DungeonGraph: generación (Kruskal + Union-Find) y BFS según el número de salas

| Salas | generación (ms) | aristas | BFS entrada→salida (µs, promedio de 20) | salas en el camino |
|---|---|---|---|---|
| 8 | 0.22 | 8 | 11.8 | 5 |
| 32 | 0.94 | 39 | 48.0 | 10 |
| 128 | 10.54 | 159 | 194.1 | 25 |
| 512 | 130.14 | 639 | 788.5 | 46 |

### SkillTree: n habilidades (cadena de dos ramas)

| n | n inserciones (ms) | find del último (µs) | levels() (ms) |
|---|---|---|---|
| 100 | 7.7 | 81 | 0.24 |
| 500 | 342.0 | 646 | 1.62 |
| 2000 | 3739.8 | 1953 | 5.87 |
