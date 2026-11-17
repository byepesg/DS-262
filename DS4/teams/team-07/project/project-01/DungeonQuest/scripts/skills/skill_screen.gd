class_name SkillScreen
extends CanvasLayer

# Pantalla del árbol de habilidades. La interfaz está en scenes/SkillScreen.tscn
# (se diseña en el editor); este script solo pone los nodos del árbol, las líneas
# y maneja el movimiento de la vista.
#
#   Rueda del ratón .... zoom (hacia el cursor)
#   Arrastrar fondo .... mover la vista (clic izquierdo en vacío, o botón central)
#   Flechas / WASD ..... mover la vista
#   Inicio (Home) ...... centrar y ajustar el árbol
#   Clic en nodo ....... desbloquear · Clic derecho: reiniciar rama
#   Esc o K ............ cerrar
#
# Nodos que DEBE tener SkillScreen.tscn (con "Acceder como nombre único"):
#   %PointsLabel  %CenterButton  %CloseButton  %ViewArea  %World  %InfoLabel
#
# Uso:  SkillScreen.open(self, skill_tree)

signal closed

const SCENE_PATH := "res://scenes/SkillScreen.tscn"
const NODE_BUTTON := preload("res://scenes/SkillNodeButton.tscn")

const ZOOM_MIN := 0.35
const ZOOM_MAX := 2.0
const ZOOM_STEP := 1.12
const PAN_SPEED := 500.0

const NODE_SIZE := Vector2(44, 44)
const COL_GAP := 64.0
const ROW_GAP := 70.0

const ICON_PLACEHOLDER := preload("res://icon.svg")

const BORDER_UNLOCKED := Color(0.5, 1.0, 0.6)       # activas: delineado verde
const BORDER_AVAILABLE := Color("d6dad3")           # disponibles
const BORDER_LOCKED := Color(0.35, 0.33, 0.42)      # bloqueadas
const TINT_LOCKED := Color(0.2, 0.2, 0.2, 0.65)     # negro semitransparente)

var _closing: bool = false

# Dibuja las líneas que unen cada nodo con sus hijos (hacia abajo, en codo)
class Lines extends Control:
	var positions: Dictionary = {}
	var node_size := Vector2.ZERO

	func _draw() -> void:
		for node in positions:
			for child in node.children:
				var col := Color(0.35, 0.38, 0.45)
				if child.unlocked:
					col = Color(0.4, 0.9, 0.5)
				var a: Vector2 = positions[node] + Vector2(0, node_size.y / 2.0)
				var b: Vector2 = positions[child] - Vector2(0, node_size.y / 2.0)
				var mid: float = (a.y + b.y) / 2.0
				draw_polyline(PackedVector2Array(
						[a, Vector2(a.x, mid), Vector2(b.x, mid), b]), col, 2.0)

@onready var points_label: Label = %PointsLabel
@onready var center_button: Button = %CenterButton
@onready var close_button: Button = %CloseButton
@onready var view_area: Control = %ViewArea
@onready var world: Control = %World
@onready var info_label: Label = %InfoLabel

var tree: SkillTree
var buttons: Dictionary = {}      # SkillNode -> Button
var positions: Dictionary = {}    # SkillNode -> centro del nodo (en el mundo)
var lines: Lines
var _leaf_counter: int = 0
var _max_depth: int = 0
var _hovered: SkillNode = null
var _dragging: bool = false
var standalone: bool = false

func _init() -> void:
	layer = 70
	process_mode = Node.PROCESS_MODE_ALWAYS

static func open(parent: Node, skill_tree: SkillTree) -> SkillScreen:
	var screen: SkillScreen = load(SCENE_PATH).instantiate()
	screen.tree = skill_tree
	parent.add_child(screen)
	return screen

func _ready() -> void:
	if tree == null:
		standalone = true
		tree = SkillTree.new()
		tree.build_default()
		tree.load_progress()
	else:
		_fade_in()   # abierta dentro de la partida
	_layout(tree.root, 0)
	_build_nodes()
	center_button.pressed.connect(_center_view)
	close_button.pressed.connect(_close)
	_refresh()
	# El tamaño del área se conoce un fotograma después
	await get_tree().process_frame
	_center_view()

# ---------- Posiciones (árbol vertical que crece hacia abajo) ----------

# La fila es la profundidad; las hojas ocupan columnas consecutivas y cada
# padre queda centrado respecto a sus hijos.
func _layout(node: SkillNode, depth: int) -> float:
	_max_depth = maxi(_max_depth, depth)
	var x: float
	if node.children.is_empty():
		x = _leaf_counter * COL_GAP
		_leaf_counter += 1
	else:
		var total := 0.0
		for child in node.children:
			total += _layout(child, depth + 1)
		x = total / node.children.size()
	positions[node] = Vector2(x + NODE_SIZE.x / 2.0, depth * ROW_GAP + NODE_SIZE.y / 2.0)
	return x

func _build_nodes() -> void:
	lines = Lines.new()
	lines.positions = positions
	lines.node_size = NODE_SIZE
	lines.mouse_filter = Control.MOUSE_FILTER_IGNORE
	lines.size = Vector2(
			(_leaf_counter - 1) * COL_GAP + NODE_SIZE.x,
			_max_depth * ROW_GAP + NODE_SIZE.y)
	world.add_child(lines)
	# Desactiva el descarte por área visible
	RenderingServer.canvas_item_set_custom_rect(
			lines.get_canvas_item(), true, Rect2(-2000, -2000, 6000, 6000))

	for node in positions:
		var button: Button = NODE_BUTTON.instantiate()
		button.size = NODE_SIZE
		button.position = positions[node] - NODE_SIZE / 2.0
		button.icon = ICON_PLACEHOLDER
		button.expand_icon = true
		button.icon_alignment = HORIZONTAL_ALIGNMENT_CENTER
		button.add_theme_constant_override("icon_max_width", 30)
		button.pressed.connect(_on_node_pressed.bind(node))
		button.gui_input.connect(_on_node_gui_input.bind(node))
		button.mouse_entered.connect(_on_node_hover.bind(node))
		button.mouse_exited.connect(_on_node_unhover)
		world.add_child(button)
		buttons[node] = button

func _refresh() -> void:
	points_label.text = "Puntos de habilidad: %d" % tree.points
	for node in buttons:
		var button: Button = buttons[node]
		button.text = ""   # el nombre y el costo salen en el panel de información
		if node.unlocked:
			_set_border(button, BORDER_UNLOCKED)
			button.modulate = Color.WHITE
		elif tree.can_unlock(node):
			_set_border(button, BORDER_AVAILABLE)
			button.modulate = Color.WHITE
		else:
			_set_border(button, BORDER_LOCKED)
			button.modulate = TINT_LOCKED
	lines.queue_redraw()

func _set_border(button: Button, color: Color) -> void:
	var style := button.get_theme_stylebox("normal").duplicate() as StyleBoxFlat
	if style == null:
		return
	style.border_color = color
	button.add_theme_stylebox_override("normal", style)

# ---------- Información y acciones sobre los nodos ----------

func _on_node_hover(node: SkillNode) -> void:
	_hovered = node
	_show_info(node)

func _on_node_unhover() -> void:
	_hovered = null

func _show_info(node: SkillNode) -> void:
	var path := PackedStringArray()
	for n in tree.path_to_root(node):
		path.append(n.display_name)
	info_label.text = "%s · costo %d\n%s\nRuta: %s" % [
			node.display_name, node.cost, node.description, " → ".join(path)]

func _on_node_pressed(node: SkillNode) -> void:
	if node == tree.root:
		return
	if tree.unlock(node.id):
		info_label.text = "Desbloqueaste %s." % node.display_name
	else:
		info_label.text = tree.why_not(node)
	_refresh()

func _on_node_gui_input(event: InputEvent, node: SkillNode) -> void:
	var mouse := event as InputEventMouseButton
	if mouse == null or not mouse.pressed or mouse.button_index != MOUSE_BUTTON_RIGHT:
		return
	if node == tree.root or not node.unlocked:
		return
	var refund := tree.reset_branch(node.id)
	info_label.text = "Rama reiniciada: recuperas %d punto(s)." % refund
	_refresh()

# ---------- Vista: mover y hacer zoom ----------

func _mouse_in_view() -> bool:
	var local: Vector2 = view_area.get_local_mouse_position()
	return Rect2(Vector2.ZERO, view_area.size).has_point(local)

# Zoom manteniendo fijo el punto del mundo que está bajo el cursor
func _zoom_at(factor: float) -> void:
	var old_scale: float = world.scale.x
	var new_scale: float = clampf(old_scale * factor, ZOOM_MIN, ZOOM_MAX)
	if is_equal_approx(old_scale, new_scale):
		return
	var m: Vector2 = view_area.get_local_mouse_position()
	world.position = m - (m - world.position) * (new_scale / old_scale)
	world.scale = Vector2(new_scale, new_scale)

# Ajusta el árbol completo al área visible y lo centra horizontalmente
func _center_view() -> void:
	var bounds := Vector2(
			(_leaf_counter - 1) * COL_GAP + NODE_SIZE.x,
			_max_depth * ROW_GAP + NODE_SIZE.y)
	var fit: float = minf(view_area.size.x / bounds.x, view_area.size.y / bounds.y)
	var s: float = clampf(fit, ZOOM_MIN, 1.0)
	world.scale = Vector2(s, s)
	world.position = Vector2((view_area.size.x - bounds.x * s) / 2.0, 24.0)

func _process(delta: float) -> void:
	# Flechas / WASD (las mismas acciones del movimiento del jugador)
	if InputMap.has_action("left") and InputMap.has_action("up"):
		var dir := Input.get_vector("left", "right", "up", "down")
		if dir != Vector2.ZERO:
			world.position -= dir * PAN_SPEED * delta

func _input(event: InputEvent) -> void:
	var key := event as InputEventKey
	if key != null and key.pressed and not key.echo:
		if key.keycode == KEY_ESCAPE or key.keycode == KEY_K:
			_close()
			get_viewport().set_input_as_handled()
		elif key.keycode == KEY_HOME:
			_center_view()
			get_viewport().set_input_as_handled()
		return

	var mouse := event as InputEventMouseButton
	if mouse != null:
		if mouse.pressed and mouse.button_index == MOUSE_BUTTON_WHEEL_UP and _mouse_in_view():
			_zoom_at(ZOOM_STEP)
			get_viewport().set_input_as_handled()
		elif mouse.pressed and mouse.button_index == MOUSE_BUTTON_WHEEL_DOWN and _mouse_in_view():
			_zoom_at(1.0 / ZOOM_STEP)
			get_viewport().set_input_as_handled()
		elif mouse.button_index == MOUSE_BUTTON_LEFT or mouse.button_index == MOUSE_BUTTON_MIDDLE:
			if mouse.pressed:
				# El clic izquierdo solo arrastra si no está sobre un nodo
				_dragging = _mouse_in_view() and (
						mouse.button_index == MOUSE_BUTTON_MIDDLE or _hovered == null)
			else:
				_dragging = false
		return

	var motion := event as InputEventMouseMotion
	if motion != null and _dragging:
		world.position += motion.relative
		get_viewport().set_input_as_handled()

func _close() -> void:
	if _closing:
		return
	_closing = true
	var t := create_tween().set_parallel(true)
	t.tween_property($UI, "modulate:a", 0.0, 0.25)
	if not standalone:
		t.tween_property($Dim, "modulate:a", 0.0, 0.25)
	await t.finished
	closed.emit()
	if standalone:
		get_tree().change_scene_to_file("res://scenes/Start.tscn")
	else:
		queue_free()

func _fade_in() -> void:
	var ui: Control = $UI
	ui.modulate.a = 0.0
	$Dim.modulate.a = 0.0
	var t := create_tween().set_parallel(true)
	t.tween_property(ui, "modulate:a", 1.0, 0.25)
	t.tween_property($Dim, "modulate:a", 1.0, 0.25)
