# Dungeon Quest — Prototipo inicial

## Descripción
Dungeon Quest es un prototipo 2D de exploración de un calabozo. El mapa se representa mediante un grafo: cada habitación es un vértice y cada conexión es una arista.

## Objetivo de esta entrega
- Mostrar seis habitaciones y sus conexiones.
- Permitir el movimiento únicamente entre habitaciones adyacentes.
- Calcular una ruta desde la entrada hasta la salida usando búsqueda en anchura (BFS).
- Mostrar visualmente la ruta encontrada y detectar la llegada a la salida.

## Requisitos
- Godot 4.x.
- No requiere complementos ni recursos externos.

## Cómo ejecutar
1. Abre Godot.
2. Selecciona **Importar** y elige el archivo `project.godot`.
3. Abre el proyecto.
4. Pulsa **F5** para ejecutar el proyecto.

## Controles
- Pulsa los botones de habitaciones vecinas para moverte.
- Pulsa **Encontrar salida (BFS)** para calcular y resaltar una ruta.
- Pulsa **Reiniciar** para volver a la habitación de entrada.

## Estructura del proyecto
- `scenes/Main.tscn`: escena principal.
- `scripts/Main.gd`: interfaz y coordinación del prototipo.
- `scripts/DungeonGraph.gd`: grafo y algoritmo BFS.
- `scripts/GraphView.gd`: dibujo del mapa, conexiones y ruta.
- `assets/README.md`: información sobre los recursos gráficos.

## Estructura de datos y algoritmo
El calabozo se representa mediante un grafo no dirigido y no ponderado. Las habitaciones corresponden a vértices y los corredores a aristas.

El algoritmo BFS (Breadth-First Search o búsqueda en anchura) recorre el grafo por niveles y permite encontrar una ruta con el menor número de conexiones entre la habitación inicial y la salida.

## Alcance de la entrega
Este prototipo académico busca demostrar el uso de grafos, el recorrido BFS y la restricción del movimiento según las conexiones del mapa.

Por el momento, no incluye combate, inventario, animaciones ni generación aleatoria de niveles.