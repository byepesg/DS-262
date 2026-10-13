# DungeonQuest

## Descripción
DungeonQuest es un prototipo de juego 2D desarrollado con Godot 4 en el que el jugador explora un calabozo dividido en varias habitaciones conectadas entre sí. Cada espacio del mapa se representa como un nodo dentro de un grafo, y solo se permite avanzar entre salas adyacentes según la estructura del laberinto.

El proyecto sirve como demostración práctica de dos conceptos clave:
- representación de un mapa mediante grafos;
- búsqueda de ruta con BFS para encontrar el camino más corto entre la sala inicial y la salida.

## Funcionalidades actuales
- Mapa con varias habitaciones conectadas por puertas.
- Movimiento del personaje con WASD o flechas.
- Validación de conexiones: solo se puede pasar entre habitaciones vecinas del grafo.
- Transición visual entre salas con fade, cambio de cámara y suavizado.
- Sistema de rutas basado en BFS para calcular el recorrido mínimo.
- Estructura modular con escenas y scripts separados.

## Requisitos
- Godot 4.6 o superior.
- No requiere dependencias externas ni paquetes adicionales.

## Cómo ejecutar
1. Abre Godot.
2. Selecciona la opción Importar.
3. Elige la carpeta del proyecto (`project.godot`).
4. Espera a que cargue el proyecto.
5. Pulsa F5 para ejecutar la escena principal.

## Controles
- WASD o flechas: mover al personaje.
- Entrar en una puerta: traslada al jugador a la sala conectada si la conexión es válida.
- El juego valida automáticamente si se intenta cruzar una puerta no permitida.

## Estructura del proyecto
- `project.godot`: configuración principal del proyecto Godot.
- `scenes/Main.tscn`: escena principal del juego.
- `scenes/player.tscn`: escena del jugador.
- `scripts/Main.gd`: lógica central del flujo del nivel y las transiciones.
- `scripts/DungeonGraph.gd`: grafo de adyacencia y búsqueda BFS.
- `scripts/player.gd`: movimiento y animación del personaje.
- `scripts/door.gd`: lógica de puertas y activación de paso entre habitaciones.
- `assets/`: recursos gráficos y visuales del proyecto.

## Modelo de datos y algoritmo
El calabozo se representa con un grafo no dirigido:
- cada habitación es un vértice;
- cada conexión entre salas es una arista;
- la estructura se define en `DungeonGraph.gd`.

La búsqueda BFS (Breadth-First Search) recorre el grafo por niveles y devuelve la ruta más corta desde la habitación inicial hasta la meta. Esto permite garantizar que el camino encontrado sea el mínimo en número de conexiones.

## Alcance actual
Este proyecto corresponde a una entrega de prototipo académico centrada en:
- diseño de un mapa en grafo;
- navegación por habitaciones conectadas;
- uso de BFS para la generación de rutas;
- transición entre espacios de forma visual y controlada.

No incluye, por el momento, sistemas avanzados como combate, inventario, IA, generación aleatoria de niveles ni varias mecánicas de RPG.