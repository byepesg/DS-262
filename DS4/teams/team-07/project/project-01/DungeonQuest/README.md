# Dungeon Quest

## Descripción
Dungeon Quest es un prototipo 2D de exploración de un calabozo hecho con Godot 4. El jugador recorre ocho habitaciones conectadas por puertas hasta encontrar la salida. El mapa se modela como un grafo: cada habitación es un vértice y cada puerta compartida entre dos habitaciones es una arista.

## Qué incluye esta entrega
- Menú de inicio con botones **Jugar** y **Salir**.
- Ocho habitaciones (1 a 8) conectadas entre sí, más una puerta de salida en la habitación 8.
- Personaje con movimiento en cuatro direcciones y animaciones de caminar y reposo.
- Puertas que solo permiten pasar entre habitaciones adyacentes según el grafo; si una conexión no existe en el grafo, se rechaza y se imprime un aviso en la consola.
- Transición entre habitaciones con fundido a negro, caminata del jugador por la puerta y cámara suavizada.
- Ayuda basada en BFS: al pulsar **H**, la puerta que lleva al siguiente paso del camino más corto hacia la salida brilla durante un instante.
- Pantalla de victoria al cruzar la puerta de salida, con opciones **Reintentar** y **Menú**.

## Requisitos
- Godot 4.x (el proyecto fue creado con la versión 4.7 y el renderizador *Forward Plus*; se recomienda abrirlo con esa versión o una posterior).
- No requiere complementos ni dependencias externas.
- 
## Cómo ejecutar (macOS)

Este proyecto incluye un script de Bash (`ejecutar.sh`) que automatiza por completo la configuración en macOS: descarga e instala **Godot 4.3** de forma automática (si no está instalado), configura los permisos de seguridad del sistema y abre el juego directamente.

### Pasos para iniciar

1. **Dar permisos de ejecución al script**  
   Abre la Terminal en la raíz del proyecto y ejecuta el siguiente comando (solo es necesario hacerlo la primera vez):
   ```bash
   chmod +x ejecutar.sh
   ```

2. **Lanzar el script**  
   Inicia el asistente automático con el comando:
   ```bash
   ./ejecutar.sh
   ```

3. **Indicar la ruta del proyecto**  
   Cuando la Terminal lo solicite, **arrastra la carpeta de este proyecto** directamente a la ventana de la Terminal (o escribe su ruta manualmente) y presiona `Enter`.

El script se encargará de validar tu entorno, solucionar el bloqueo de Gatekeeper de macOS si descarga el motor por primera vez, e iniciará el juego inmediatamente sin necesidad de configurar nada de forma manual.

## Cómo ejecutar
1. Abre Godot.
2. Selecciona **Importar** y elige el archivo `project.godot`.
3. Abre el proyecto y espera a que Godot importe los recursos.
4. Pulsa **F5** para ejecutarlo. La escena principal es `scenes/Start.tscn` (menú de inicio).

## Controles
| Acción | Tecla |
|---|---|
| Mover al personaje | `W` `A` `S` `D` o flechas |
| Mostrar la puerta del camino más corto a la salida | `H` |
| Cruzar una puerta | Caminar hacia ella |

En los menús se usa el ratón.

## Mapa del calabozo
Aristas definidas en `DungeonGraph.build_default()`:

| Habitación | Conectada con |
|---|---|
| 1 (entrada) | 2 |
| 2 | 1, 3, 4 |
| 3 | 2, 5 |
| 4 | 2, 5, 6 |
| 5 | 3, 4, 7 |
| 6 | 4, 7 |
| 7 | 5, 6, 8 |
| 8 | 7, salida |

La salida es una puerta especial (`is_exit = true`) ubicada en la habitación 8. Desde la habitación 1, el camino más corto calculado por BFS es `1 → 2 → 3 → 5 → 7 → 8`, seguido de la puerta de salida.

## Estructura del proyecto
- `project.godot`: configuración del proyecto, ventana de 480×270 escalada a 1280×720 y acciones de entrada.
- `scenes/Start.tscn`: menú de inicio (escena principal).
- `scenes/Main.tscn`: nivel con las habitaciones, las puertas, el jugador y la capa de fundido.
- `scenes/player.tscn`: jugador.
- `scenes/objects/Door.tscn`: puerta reutilizable (vistas frontal, trasera, izquierda y derecha, con brillo de ayuda).
- `scenes/SceneController.tscn`: cargador que cambia de escena con fundido.
- `scenes/Victory.tscn`: pantalla de victoria.
- `scripts/DungeonGraph.gd`: grafo (lista de adyacencia) y búsqueda BFS.
- `scripts/Main.gd`: coordinación del nivel, validación de puertas, transiciones, ayuda y victoria.
- `scripts/door.gd`: lógica de la puerta, señal `door_crossed` y posiciones de llegada.
- `scripts/player.gd`: movimiento y animaciones.
- `scripts/scene_controller.gd`, `scripts/start.gd`, `scripts/victory.gd`: cambio de escena y menús.
- `assets/`: fuentes y sprites (ver `assets/README.md`).

## Estructura de datos y algoritmo
El calabozo se representa mediante un grafo no dirigido y no ponderado, implementado como diccionario de listas de adyacencia en `DungeonGraph.gd`:
- `add_edge(a, b)` registra la conexión en ambos sentidos y evita duplicados.
- `are_neighbors(a, b)` valida si el movimiento entre dos habitaciones está permitido.
- `bfs_path(start, goal)` recorre el grafo por niveles con una cola, guarda el padre de cada vértice visitado y reconstruye la ruta desde la meta hacia atrás. Devuelve el camino con menos conexiones o un arreglo vacío si no hay camino.

Cada conexión se materializa en el nivel como dos puertas gemelas (por ejemplo `Door_2-3` y `Door_3-2`). Al cruzar una, `Main.gd` comprueba el grafo, busca la puerta gemela y coloca al jugador en su punto de llegada.

## Estado y limitaciones conocidas
- No incluye combate, inventario, enemigos ni generación aleatoria de niveles.
- El grafo está definido en código (`build_default()`) y debe mantenerse consistente con las puertas de `Main.tscn`.
- `scripts/GraphView.gd` está vacío: era el dibujo del mapa de la primera versión y ya no se usa.
- La ayuda con **H** siempre calcula la ruta hacia la habitación 8, no hacia la puerta de salida como tal.
- La ruta BFS también se imprime en la consola al iniciar el nivel (`Ruta BFS: ...`).
