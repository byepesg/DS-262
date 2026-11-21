# Dungeon Quest

## macOS

### Instalar

El proyecto usa **Godot 4.7** (probado con **4.7.2**) y no necesita complementos.

No necesitas instalar Godot manualmente. El primer uso de `run_project_macos.sh` requiere conexión a Internet: descarga Godot 4.7.2 desde la publicación oficial, verifica el SHA-256 e instala la aplicación en `~/Applications`. No requiere permisos de administrador. Si ya hay una copia compatible de Godot 4.7.2 en el sistema, la reutiliza.

### Ejecutar con el script

Abre Terminal en la carpeta `DungeonQuest` y ejecuta:

```bash
bash ./run_project_macos.sh
```

El script instala Godot si hace falta y luego abre el proyecto de esta carpeta. No necesitas escribir ni arrastrar una ruta. macOS puede pedir autorización la primera vez que se abre la aplicación; confirma el diálogo del sistema. El script no desactiva Gatekeeper.

### Ejecutar las pruebas

El mismo script instala/reutiliza Godot y reenvía los argumentos de prueba. Desde la carpeta raíz `DungeonQuest`, ejecuta:

```bash
bash ./run_project_macos.sh --headless --import || exit $?
bash ./run_project_macos.sh --headless --script res://tests/run_tests.gd || exit $?
```

La importación solo es necesaria la primera vez o después de cambios en los recursos. Para ver los comandos de prueba de Windows, ve a [Pruebas (Windows)](#pruebas-windows).

La suite se enfoca en las estructuras de datos y algoritmos del proyecto, informa las verificaciones y mediciones de rendimiento y regenera `tests/results.md`. Devuelve código de salida `1` si hay verificaciones fallidas. El script respalda el progreso guardado (`user://dungeon_save.json`) y lo restaura al terminar. Al probar una carga corrupta puede aparecer `ERROR: Parse JSON failed`; es esperado. Godot también puede mostrar avisos de recursos al salir; revisa el total de verificaciones y el código de salida para confirmar el resultado.

## Windows

### Instalación

El proyecto usa **Godot 4.7** y no necesita complementos. Se ha probado con Godot **4.7.2**.

1. Descarga la versión estándar para Windows desde [godotengine.org/download/windows](https://godotengine.org/download/windows/).
2. Extrae el archivo descargado. Godot se distribuye como aplicación portable; no hace falta ejecutar un instalador.
3. Indica dónde está `Godot.exe`. El lanzador de Windows busca la ruta en este orden:
   - El archivo local `.godot-path`, en la carpeta raíz del proyecto.
   - `godot.exe` disponible en el `PATH`.
   - Una ruta que puedes escribir cuando el lanzador la solicite.

Para configurar `.godot-path`, abre PowerShell en la carpeta `DungeonQuest` y ejecuta este comando, sustituyendo el ejemplo por la ruta real al ejecutable:

```powershell
Set-Content -Path .godot-path -Value 'C:\ruta\a\Godot.exe'
```

El archivo contiene una sola línea con la ruta completa y **sin comillas**. Está excluido de Git, así que cada integrante puede guardar su propia ruta local.

El archivo de Godot para Windows también incluye una versión de consola cuyo nombre termina en `_console.exe`. Se usa para correr pruebas y ver sus resultados en la terminal.

### Ejecutar

#### Lanzador de Windows — recomendado

Haz doble clic en `run_project_windows.bat`. También puedes ejecutarlo desde PowerShell, ubicado en la carpeta del proyecto:

```powershell
.\run_project_windows.bat
```

El lanzador abre este proyecto y deja la ventana de consola disponible para mostrar errores. Si no configuraste `.godot-path` ni tienes Godot en el `PATH`, te pedirá la ruta al ejecutable.

#### Desde el editor de Godot

Abre Godot, importa el archivo `project.godot` de esta carpeta y ejecuta el proyecto con **F6** (escena actual) o **F5** (proyecto).

#### Desde PowerShell

Desde la carpeta del proyecto, usa `.godot-path`:

```powershell
$godot = (Get-Content .godot-path -Raw).Trim()
& $godot --path .
```

Si `godot.exe` ya está en el `PATH`, también puedes ejecutar `godot --path .`.

### Pruebas (Windows)

Las pruebas son automatizadas, se ejecutan sin abrir la ventana del juego y se enfocan en las estructuras de datos y algoritmos del proyecto. Se recomienda ejecutarlas desde PowerShell en la carpeta raíz `DungeonQuest`.

En Windows, ejecuta las pruebas con la versión de consola de Godot, cuyo nombre termina en `_console.exe`. Si `.godot-path` apunta al ejecutable normal, estos comandos localizan la versión de consola que viene junto a él:

```powershell
$godotExe = (Get-Content .godot-path -Raw).Trim()
$godotConsole = Join-Path (Split-Path $godotExe) (([IO.Path]::GetFileNameWithoutExtension($godotExe)) + '_console.exe')
if (-not (Test-Path $godotConsole)) { throw "No se encontró Godot Console: $godotConsole" }

& $godotConsole --headless --path . --import
if ($LASTEXITCODE -ne 0) { throw "Falló la importación de recursos." }

& $godotConsole --headless --path . --script res://tests/run_tests.gd
if ($LASTEXITCODE -ne 0) { throw "Fallaron las pruebas." }
```

Si no configuraste `.godot-path`, asigna `$godotConsole` directamente a la ruta del ejecutable `_console.exe`. También puedes usar el comando `godot` solo si en el `PATH` apunta a la versión de consola:

```powershell
$godotConsole = 'C:\ruta\a\Godot_v4.7.2-stable_win64_console.exe'
& $godotConsole --headless --path . --import
if ($LASTEXITCODE -ne 0) { throw "Falló la importación de recursos." }
& $godotConsole --headless --path . --script res://tests/run_tests.gd
if ($LASTEXITCODE -ne 0) { throw "Fallaron las pruebas." }
```


## Descripción

Dungeon Quest es un videojuego 2D de exploración y combate. Cada partida genera una mazmorra de ocho salas conectadas mediante un grafo no dirigido y no ponderado. El jugador explora las salas, combate enemigos, desbloquea habilidades y busca la salida.

Creado para el curso de Estructuras de Datos, el proyecto aplica grafos, búsqueda BFS, Union-Find, colas, montículos y árboles de prefijos. El [roadmap y las ideas pendientes](docs/README.md) describen el estado de desarrollo y la preparación de las entregas académicas.

## Funcionalidades

- Mazmorra procedural de ocho salas, con entrada, salida y conexiones validadas mediante un grafo.
- Movimiento con teclado, minimapa y pistas hacia la salida calculadas con BFS.
- Enemigos colocados en salas intermedias; al encontrarlos comienza un combate por turnos.
- Árbol de habilidades persistente entre partidas, con poderes que se desbloquean usando puntos.
- Pantallas de inicio, victoria y derrota.
- Transiciones de combate y feedback de impacto para parries y combos, con colores asociados al resultado o al tipo de poder.

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

El combate es por turnos; el orden se determina según la velocidad de cada combatiente. El jugador puede atacar, defenderse o usar poderes. Los enemigos anuncian sus golpes con un indicador: un parry perfecto con Espacio evita el daño y contraataca; un bloqueo a tiempo reduce el daño. El resultado del parry aparece con un efecto de impacto, y los cambios de escena usan transiciones visuales.

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

Al pulsar **Poder**, el juego **no muestra la secuencia**: solo ves las flechas que llevas y una barra de tiempo, así que hay que recordarla. Cada tecla debe ingresarse en menos de 2 segundos; si se agota el tiempo, **pierdes el turno**. Una tecla equivocada rompe el combo y **también pierdes el turno**; **Esc** lo cancela sin perderlo. Completar un poder sin los PA necesarios muestra **SIN PA** y también consume el turno. El botón **Poder** se desactiva cuando los PA no alcanzan ni para el poder más barato.

Al completar un combo, el nombre del poder se presenta en un impacto visual de su color y permanece en pantalla alrededor de un segundo. Las flechas completadas también toman el color del tipo de poder.

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
├── run_project_macos.sh
├── run_project_windows.bat
├── archive/
│   └── legacy/
│       └── duplicate_scenes/  # Copias antiguas preservadas, fuera de las escenas activas
├── assets/
│   ├── fonts/
│   ├── source/
│   │   └── aseprite/           # Fuentes editables
│   └── sprites/
│       ├── characters/
│       │   ├── enemies/
│       │   └── player/
│       ├── environment/
│       │   ├── doors/
│       │   └── tiles/
│       ├── skills/
│       └── ui/
│           ├── backgrounds/
│           └── buttons/
├── scenes/
│   ├── actors/
│   ├── game/
│   ├── objects/
│   ├── rooms/
│   └── ui/
│       ├── components/
│       └── screens/
├── docs/                       # Roadmap y documentación del proyecto
├── scripts/
│   ├── actors/
│   ├── combat/
│   ├── core/
│   ├── skills/
│   ├── structures/
│   └── world/
└── tests/
    ├── run_tests.gd
    └── results.md
```
