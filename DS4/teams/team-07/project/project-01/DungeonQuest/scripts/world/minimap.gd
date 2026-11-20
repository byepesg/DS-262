class_name Minimap
extends Control

# Minimapa con niebla. Dibuja el grafo sobre la cuadrícula de salas:
# - Sala visitada: cuadro relleno.
# - Sala vecina de una visitada (aún no explorada): solo contorno.
# - Cualquier otra: oculta.
# - Una conexión se ve si al menos uno de sus extremos fue visitado.

@export var cell_px: float = 18.0   # separación entre salas en pantalla
@export var box_px: float = 12.0    # tamaño del cuadro de cada sala

const PADDING := 8.0

var graph: DungeonGraph
var exit_room: int = 0
var visited: Dictionary = {}        # conjunto de salas visitadas: id -> true
var current: int = 0
var min_cell := Vector2i.ZERO

func setup(g: DungeonGraph, exit_id: int) -> void:
	graph = g
	exit_room = exit_id
	mouse_filter = Control.MOUSE_FILTER_IGNORE

	# Límites de la cuadrícula, para que el mapa no se mueva al descubrir salas
	var max_cell := Vector2i(-9999, -9999)
	min_cell = Vector2i(9999, 9999)
	for id in graph.cells:
		var c: Vector2i = graph.cells[id]
		min_cell = Vector2i(mini(min_cell.x, c.x), mini(min_cell.y, c.y))
		max_cell = Vector2i(maxi(max_cell.x, c.x), maxi(max_cell.y, c.y))
	var span := Vector2(max_cell - min_cell + Vector2i.ONE)
	size = span * cell_px + Vector2(PADDING, PADDING) * 2.0
	queue_redraw()

func visit(id: int) -> void:
	visited[id] = true
	current = id
	queue_redraw()

# Una sala es "conocida" si fue visitada o es vecina de una visitada
func _is_known(id: int) -> bool:
	if visited.has(id):
		return true
	for nb in graph.get_neighbors(id):
		if visited.has(nb):
			return true
	return false

func _center(id: int) -> Vector2:
	var c: Vector2i = graph.cells[id]
	return Vector2(c - min_cell) * cell_px + Vector2(PADDING, PADDING) + Vector2(cell_px, cell_px) / 2.0

func _box(id: int) -> Rect2:
	var s := Vector2(box_px, box_px)
	return Rect2(_center(id) - s / 2.0, s)

func _draw() -> void:
	if graph == null:
		return
	draw_rect(Rect2(Vector2.ZERO, size), Color(0, 0, 0, 0.55))

	# Conexiones (primero, para que los cuadros queden encima)
	for a in graph.adjacency:
		for b in graph.get_neighbors(a):
			if a < b and (visited.has(a) or visited.has(b)):
				draw_line(_center(a), _center(b), Color(0.45, 0.8, 0.85), 2.0)

	# Salas
	for id in graph.cells:
		if visited.has(id):
			var col := Color(0.35, 0.8, 0.9)
			if id == exit_room:
				col = Color(0.95, 0.55, 0.3)
			draw_rect(_box(id), col)
		elif _is_known(id):
			draw_rect(_box(id), Color(0.45, 0.8, 0.85, 0.8), false, 1.5)

	# Sala actual
	if current != 0:
		draw_rect(_box(current).grow(2.0), Color.WHITE, false, 2.0)
