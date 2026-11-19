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

- **Godot 4.7**. El proyecto declara la serie 4.7 en `project.godot`; el lanzador de macOS (`RunScript_Mac`) configura `GODOT_VERSION="4.7"`. El proyecto se ha probado con Godot 4.7.1.
- No requiere complementos adicionales.

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

### Desde Windows

Ejecuta `RunScript_Windows.bat` desde el Explorador de archivos o desde una terminal. El script busca `godot.exe` en el `PATH`; si no lo encuentra, solicita la ruta completa a `Godot.exe`. Necesitas tener Godot 4.7 instalado.

## Controles

| Acción | Control |
|---|---|
| Moverse | Flechas o WASD |
| Mostrar una pista hacia la salida | H |
| Abrir/cerrar el árbol de habilidades | K |
| Atacar durante el combate | Clic en el botón del enemigo |
| Defenderse | Botón **Defender** |
| Consultar los combos | Menú desplegable **Combos** (arriba a la izquierda), en tu turno |
| Iniciar un poder | Botón **Poder** y luego ingresar de memoria la secuencia de flechas |
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

Los poderes se desbloquean en el árbol de habilidades. Los desbloqueados aparecen en el menú desplegable **Combos** (arriba a la izquierda, en tu turno), con su secuencia y su costo; los que no alcanzas por falta de PA salen en gris.

Al pulsar **Poder**, el juego **no muestra la secuencia**: solo ves las flechas que llevas y una barra de tiempo, así que hay que recordarla. Cada tecla debe ingresarse en menos de 2 segundos; si se agota el tiempo, **pierdes el turno**. Una tecla equivocada cancela el combo sin perder el turno, y **Esc** lo cancela también. El botón **Poder** se desactiva cuando los PA no alcanzan ni para el poder más barato.

| Poder | Secuencia | Efecto | Costo |
|---|---|---|---:|
| Corte | ↓ → | 16 de daño a un enemigo | 2 PA |
| Llamarada | ↑ ↑ → | 14 de daño a todos los enemigos | 3 PA |
| Sanación | ↓ ↓ ↑ | Recupera 20 de vida | 3 PA |
| Tormenta | ← → ← → | 30 de daño a un enemigo | 4 PA |

Las habilidades requieren desbloquear primero su habilidad padre. El árbol incluye las ramas Espada, Luz y Fuego; el progreso se guarda entre partidas.

## Estructuras de datos y algoritmos

### Grafo, BFS y generación de la mazmorra

`scripts/structures/dungeon_graph.gd` representa las salas como vértices y sus puertas como aristas. `bfs_path(inicio, meta)` busca la ruta más corta en cantidad de conexiones en **O(V + E)**; se utiliza para las pistas y para calcular distancias.

La generación procedural construye un mapa conexo con Union-Find, compresión de caminos y aristas adicionales para ofrecer rutas alternativas. `scripts/world/map_builder.gd` instancia las salas y puertas a partir del grafo.

### Estructuras adicionales

- `scripts/structures/min_heap.gd`: cola de prioridad para programar el orden de los turnos.
- `scripts/structures/circular_queue.gd`: cola circular utilizada para los golpes del parry, el búfer de teclas del combo y el recorrido por niveles del árbol de habilidades.
- `scripts/structures/combo_trie.gd`: trie que identifica los poderes a partir de sus secuencias de flechas.
- `scripts/skills/skill_tree.gd`: árbol general de habilidades, recorrido por niveles con BFS y guardado de progreso.

## Estructura del proyecto

```text
DungeonQuest/
├── project.godot
├── README.md
├── RunScript_Mac
├── RunScript_Windows.bat
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
│   ├── SkillNodeButton.tscn
│   ├── SceneController.tscn
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
    ├── run_tests.gd
    ├── run_in_editor.gd
    └── results.md
```

## Cómo correr las pruebas

Las pruebas se ejecutan sin abrir la ventana del juego. Desde PowerShell, abre primero la carpeta raíz del proyecto:

```powershell
cd "C:\ruta\a\DungeonQuest"
```

La primera vez, importa los recursos del proyecto:

```powershell
godot --headless --path . --import
```

Después ejecuta el archivo de pruebas actualizado, usando su ruta de recursos de Godot:

```powershell
godot --headless --path . --script res://tests/run_tests.gd
```

Si Godot no está en el `PATH`, sustituye `godot` por la ruta completa al ejecutable, por ejemplo:

```powershell
$godot = "C:\ruta\a\Godot.exe"
& $godot --headless --path . --import
& $godot --headless --path . --script res://tests/run_tests.gd
```

El script imprime el resultado de las verificaciones, ejecuta mediciones de rendimiento y genera `tests/results.md`. Devuelve código de salida `1` si alguna prueba falla. Las pruebas respaldan automáticamente el progreso del jugador (`user://dungeon_save.json`) y lo restauran al terminar.

También se pueden ejecutar desde el editor de Godot, sin terminal: abre `tests/run_in_editor.gd` y elige **Archivo → Ejecutar** (**Ctrl + Mayús + X**). El resultado aparece en el panel **Salida**. Durante la prueba del guardado corrupto aparece un `ERROR: Parse JSON failed`; es esperado y no indica un fallo.