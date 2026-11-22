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
| Integración | 7 | 7 |
| **Total** | **101** | **101** |

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
| 1000 | 31.7 | 15.85 | 1.591 |
| 10000 | 326.5 | 16.33 | 1.229 |
| 100000 | 3912.1 | 19.56 | 1.178 |

### CircularQueue: n encolados seguidos de n desencolados

| n | tiempo total (ms) | µs por operación |
|---|---|---|
| 1000 | 1.4 | 0.704 |
| 10000 | 14.4 | 0.719 |
| 100000 | 138.9 | 0.695 |

### DungeonGraph: generación (Kruskal + Union-Find) y BFS según el número de salas

| Salas | generación (ms) | aristas | BFS entrada→salida (µs, promedio de 20) | salas en el camino |
|---|---|---|---|---|
| 8 | 0.19 | 8 | 15.4 | 5 |
| 32 | 1.33 | 39 | 115.3 | 10 |
| 128 | 13.63 | 159 | 219.5 | 25 |
| 512 | 144.60 | 639 | 1530.2 | 46 |

### SkillTree: n habilidades (cadena de dos ramas)

| n | n inserciones (ms) | find del último (µs) | levels() (ms) |
|---|---|---|---|
| 100 | 9.5 | 98 | 0.28 |
| 500 | 264.4 | 530 | 1.42 |
| 2000 | 4522.2 | 2067 | 5.80 |
