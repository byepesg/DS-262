# Dungeon Quest

## Descripción

Dungeon Quest es un videojuego 2D de exploración y combate hecho con **Godot 4.6**. Cada partida genera una mazmorra de ocho salas conectadas mediante un grafo no dirigido y no ponderado. El jugador explora las salas, combate enemigos, desbloquea habilidades y busca la salida.

El proyecto, creado para el curso de Estructuras de Datos, aplica grafos, búsqueda BFS, Union-Find, colas, montículos y árboles de prefijos en un juego funcional.

## Funcionalidades

- Mazmorra procedural de ocho salas, con entrada, salida y conexiones validadas mediante un grafo.
- Movimiento con teclado, minimapa y pistas hacia la salida calculadas con BFS.
- Enemigos colocados en salas intermedias; al encontrarlos comienza un combate por turnos.
- Árbol de habilidades persistente entre partidas, con poderes que se desbloquean usando puntos.
- Pantallas de inicio, victoria y derrota.

## Requisitos

- **Godot 4.6**. El proyecto declara la serie 4.6 en `project.godot`; el lanzador incluido (`RunScript_Mac`) también configura `GODOT_VERSION="4.6"`. El proyecto se ha probado con Godot 4.6.1.
- No requiere complementos adicionales.

> En este checkout el lanzador se llama `RunScript_Mac`; no hay un archivo `ejecutar.sh`.

## Cómo ejecutar el juego

### Desde el editor de Godot

1. Abre Godot y selecciona **Importar**.
2. Selecciona `project.godot` en la carpeta `DungeonQuest`.
3. Abre el proyecto y pulsa **F6** para ejecutar la escena actual o **F5** para ejecutar el proyecto.

### Desde una terminal

Abre la terminal en la carpeta `DungeonQuest` y ejecuta:

```powershell
godot --path .
```

Si `godot` no está en el `PATH`, usa la ruta completa al ejecutable:

```powershell
& "C:\ruta\a\Godot.exe" --path .
```

## Controles

| Acción | Control |
|---|---|
| Moverse | Flechas o WASD |
| Mostrar una pista hacia la salida | H |
| Abrir/cerrar el árbol de habilidades | K |
| Atacar durante el combate | Clic en el botón del enemigo |
| Defenderse | Botón **Defender** |
| Iniciar un poder | Botón **Poder** y luego ingresar la secuencia de flechas |
| Hacer parry | Espacio, al cerrarse el aro del golpe enemigo |
| Cancelar el ingreso de un combo | Esc |

En el árbol de habilidades, haz clic en un nodo para desbloquearlo. Usa clic derecho sobre una habilidad desbloqueada para reiniciar esa rama y recuperar sus puntos. Puedes mover la vista con el ratón o las flechas/WASD; la rueda del ratón cambia el zoom y **Home** centra el árbol.

## Combate y enemigos

El combate es por turnos; el orden se determina según la velocidad de cada combatiente. El jugador puede atacar, defenderse o usar poderes. Los enemigos anuncian sus golpes con un indicador: un parry perfecto con Espacio evita el daño y contraataca; un bloqueo a tiempo reduce el daño.

| Enemigo | Vida | Ataque | Velocidad | Golpes por turno |
|---|---:|---:|---:|---:|
| Slime | 30 | 4 | 8 | 2 |
| Esqueleto | 45 | 7 | 9 | 2 |
| Murciélago | 20 | 5 | 14 | 3 |
| Bruto | 70 | 10 | 5 | 1 |

La variedad de enemigos posibles aumenta con la distancia de la sala desde la entrada.

## HUD

Durante la exploración, el HUD muestra el nombre del jugador, su vida y sus puntos de habilidad. El minimapa permite orientarse por la mazmorra. En combate se muestran la vida de los combatientes, los puntos de acción (PA), el orden de turnos, los mensajes del combate y los indicadores de combo y parry.

## Combos ocultos

Los poderes se descubren y desbloquean en el árbol de habilidades. Tras desbloquearlos, aparecen en el menú **Combos** durante el combate; ingresa sus secuencias con las flechas. Cada tecla debe ingresarse dentro del límite de tiempo mostrado.

| Poder | Secuencia | Efecto | Costo |
|---|---|---|---:|
| Corte | ↓ → | 16 de daño a un enemigo | 2 PA |
| Llamarada | ↑ ↑ → | 14 de daño a todos los enemigos | 3 PA |
| Sanación | ↓ ↓ ↑ | Recupera 20 de vida | 3 PA |
| Tormenta | ← → ← → | 30 de daño a un enemigo | 4 PA |

Las habilidades requieren desbloquear primero su habilidad padre. El árbol incluye las ramas Espada, Luz y Fuego; el progreso se guarda entre partidas.

## Estructuras de datos y algoritmos

### Grafo, BFS y generación de la mazmorra

`scripts/world/dungeon_graph.gd` representa las salas como vértices y sus puertas como aristas. `bfs_path(inicio, meta)` busca la ruta más corta en cantidad de conexiones en **O(V + E)**; se utiliza para las pistas y para calcular distancias.

La generación procedural construye un mapa conexo con Union-Find, compresión de caminos y aristas adicionales para ofrecer rutas alternativas. `scripts/world/map_builder.gd` instancia las salas y puertas a partir del grafo.

### Estructuras adicionales

- `scripts/structures/min_heap.gd`: cola de prioridad para programar el orden de los turnos.
- `scripts/structures/circular_queue.gd`: cola circular utilizada en BFS y para administrar los golpes de los enemigos.
- `scripts/structures/combo_trie.gd`: trie que identifica los poderes a partir de sus secuencias de flechas.
- `scripts/skills/skill_tree.gd`: árbol general de habilidades, recorrido por niveles con BFS y guardado de progreso.

## Estructura del proyecto

```text
DungeonQuest/
├── project.godot
├── README.md
├── RunScript_Mac
├── assets/
│   ├── fonts/
│   └── sprites/
│       ├── backgrounds/
│       ├── doors/
│       ├── player/
│       └── tiles/
├── scenes/
│   ├── Start.tscn
│   ├── Main.tscn
│   ├── CombatScene.tscn
│   ├── PlayerHUD.tscn
│   ├── SkillScreen.tscn
│   ├── Victory.tscn
│   ├── GameOver.tscn
│   ├── Foe.tscn
│   ├── FoeRow.tscn
│   ├── objects/
│   └── rooms/
├── scripts/
│   ├── actors/
│   ├── combat/
│   ├── core/
│   ├── skills/
│   ├── structures/
│   └── world/
└── tests/
    └── run_tests.gd
```

## Cómo correr las pruebas

Las pruebas se ejecutan sin abrir la ventana del juego. Abre una terminal en la carpeta `DungeonQuest`. La primera vez, importa los recursos del proyecto:

```powershell
godot --headless --path . --import
```

Después ejecuta el script de pruebas:

```powershell
godot --headless --path . --script res://tests/run_tests.gd
```

Si Godot no está en el `PATH`, sustituye `godot` por la ruta completa al ejecutable, por ejemplo:

```powershell
$godot = "C:\ruta\a\Godot.exe"
& $godot --headless --path . --import
& $godot --headless --path . --script res://tests/run_tests.gd
```

El script imprime el resultado de las verificaciones, ejecuta mediciones de rendimiento y genera `tests/results.md`. Devuelve código de salida `1` si alguna prueba falla. Antes de probar el guardado de habilidades, respalda y restaura el archivo de progreso del jugador.
