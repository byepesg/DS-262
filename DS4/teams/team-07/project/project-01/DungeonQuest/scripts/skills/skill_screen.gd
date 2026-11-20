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

const NODE_SIZE := Vector2(50, 50)
const COL_GAP := 76.0
const ROW_GAP := 82.0

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
	var flash_child: SkillNode
	var flash_alpha: float = 0.0:
		set(value):
			flash_alpha = value
			queue_redraw()
	var flash_color := Color("fff3a7")

	func _draw() -> void:
		for node in positions:
			for child in node.children:
				var col := Color("65566f")
				if child.unlocked:
					col = Color("b9ff9d")
				elif node.unlocked:
					col = Color("d6a85b")
				var a: Vector2 = positions[node] + Vector2(0, node_size.y / 2.0)
				var b: Vector2 = positions[child] - Vector2(0, node_size.y / 2.0)
				var mid: float = (a.y + b.y) / 2.0
				var path := PackedVector2Array([a, Vector2(a.x, mid), Vector2(b.x, mid), b])
				draw_polyline(path, Color(0.025, 0.02, 0.04, 0.85), 5.0, true)
				draw_polyline(path, Color(col, 0.88), 2.5, true)
		if flash_child != null and flash_child.parent != null and flash_alpha > 0.01:
			var start: Vector2 = positions[flash_child.parent] + Vector2(0, node_size.y / 2.0)
			var end: Vector2 = positions[flash_child] - Vector2(0, node_size.y / 2.0)
			var mid_y := (start.y + end.y) / 2.0
			var flash_path := PackedVector2Array([
				start, Vector2(start.x, mid_y), Vector2(end.x, mid_y), end,
			])
			draw_polyline(flash_path, Color(flash_color, flash_alpha), 5.0, true)

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
var _content_size: Vector2 = Vector2.ZERO
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
	_normalize_positions()
	_build_nodes()
	center_button.pressed.connect(_center_view)
	close_button.pressed.connect(_close)
	_refresh()
	# El tamaño del área se conoce un fotograma después
	await get_tree().process_frame
	_setup_button_motion(center_button)
	_setup_button_motion(close_button)
	_center_view()
	_reveal_nodes()

# ---------- Posiciones (árbol vertical que crece hacia abajo) ----------

# La fila es la profundidad; las hojas ocupan columnas consecutivas y cada
# padre queda centrado respecto a sus hijos.
func _layout(node: SkillNode, depth: int) -> float:
	var center_x: float
	if node.children.is_empty():
		center_x = _leaf_counter * COL_GAP + NODE_SIZE.x / 2.0
		_leaf_counter += 1
	else:
		var total := 0.0
		for child in node.children:
			total += _layout(child, depth + 1)
		center_x = total / node.children.size()
	var center := Vector2(center_x, depth * ROW_GAP + NODE_SIZE.y / 2.0)
	if node.use_custom_position:
		center = node.editor_position + NODE_SIZE / 2.0
	positions[node] = center
	return center.x

func _normalize_positions() -> void:
	if positions.is_empty():
		return
	var half_size := NODE_SIZE / 2.0
	var first_position: Vector2 = positions.values()[0]
	var min_corner := first_position - half_size
	var max_corner := first_position + half_size
	for center in positions.values():
		min_corner.x = minf(min_corner.x, center.x - half_size.x)
		max_corner.x = maxf(max_corner.x, center.x + half_size.x)
		min_corner.y = minf(min_corner.y, center.y - half_size.y)
		max_corner.y = maxf(max_corner.y, center.y + half_size.y)
	for node in positions:
		positions[node] -= min_corner
	_content_size = max_corner - min_corner

func _build_nodes() -> void:
	lines = Lines.new()
	lines.positions = positions
	lines.node_size = NODE_SIZE
	lines.mouse_filter = Control.MOUSE_FILTER_IGNORE
	lines.size = _content_size
	world.add_child(lines)
	# Desactiva el descarte por área visible
	RenderingServer.canvas_item_set_custom_rect(
			lines.get_canvas_item(), true, Rect2(-2000, -2000, 6000, 6000))

	for node in positions:
		var button: Button = NODE_BUTTON.instantiate()
		button.size = NODE_SIZE
		button.position = positions[node] - NODE_SIZE / 2.0
		button.pivot_offset = button.size / 2.0
		button.scale = Vector2(0.72, 0.72)
		button.modulate.a = 0.0
		button.icon = node.icon if node.icon != null else ICON_PLACEHOLDER
		button.expand_icon = true
		button.icon_alignment = HORIZONTAL_ALIGNMENT_CENTER
		button.add_theme_constant_override("icon_max_width", 40)
		button.pressed.connect(_on_node_pressed.bind(node))
		button.gui_input.connect(_on_node_gui_input.bind(node))
		button.mouse_entered.connect(_on_node_hover.bind(node))
		button.mouse_entered.connect(_animate_node_hover.bind(button, true))
		button.mouse_exited.connect(_on_node_unhover)
		button.mouse_exited.connect(_animate_node_hover.bind(button, false))
		world.add_child(button)
		buttons[node] = button

func _setup_button_motion(button: BaseButton) -> void:
	button.pivot_offset = button.size / 2.0
	button.mouse_entered.connect(_animate_button.bind(button, Vector2(1.06, 1.06)))
	button.mouse_exited.connect(_animate_button.bind(button, Vector2.ONE))
	button.button_down.connect(_animate_button.bind(button, Vector2(0.96, 0.96)))
	button.button_up.connect(_animate_button.bind(button, Vector2.ONE))

func _animate_button(button: BaseButton, target: Vector2) -> void:
	var tween := create_tween()
	tween.tween_property(button, "scale", target, 0.09) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)

func _animate_node_hover(button: Button, hovered: bool) -> void:
	var target := Vector2(1.09, 1.09) if hovered else Vector2.ONE
	var tween := create_tween()
	tween.tween_property(button, "scale", target, 0.10) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)

func _refresh() -> void:
	points_label.text = "Puntos de habilidad: %d" % tree.points
	for node in buttons:
		var button: Button = buttons[node]
		button.text = ""   # el nombre y el costo salen en el panel de información
		if node.unlocked:
			_set_node_style(button, BORDER_UNLOCKED, Color("26372c"), 3)
			button.modulate = Color.WHITE
		elif tree.can_unlock(node):
			_set_node_style(button, BORDER_AVAILABLE, Color("34283b"), 2)
			button.modulate = Color.WHITE
		else:
			_set_node_style(button, BORDER_LOCKED, Color("17151f"), 1)
			button.modulate = TINT_LOCKED
	lines.queue_redraw()

func _set_node_style(button: Button, color: Color, fill: Color, width: int) -> void:
	var style := button.get_theme_stylebox("normal").duplicate() as StyleBoxFlat
	if style == null:
		return
	style.border_color = color
	style.border_width_left = width
	style.border_width_top = width
	style.border_width_right = width
	style.border_width_bottom = width
	style.bg_color = fill
	button.add_theme_stylebox_override("normal", style)

func _reveal_nodes() -> void:
	for node in buttons:
		var button: Button = buttons[node]
		var depth := int(round(positions[node].y / ROW_GAP))
		var delay := minf(0.32, depth * 0.06)
		var target_alpha := 0.65 if not node.unlocked and not tree.can_unlock(node) else 1.0
		button.modulate.a = 0.0
		var tween := create_tween()
		tween.tween_interval(delay)
		tween.tween_property(button, "scale", Vector2.ONE, 0.2) \
				.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
		tween.parallel().tween_property(button, "modulate:a", target_alpha, 0.14)

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
		_refresh()
		_animate_unlock(node)
	else:
		info_label.text = tree.why_not(node)
		_refresh()

func _animate_unlock(node: SkillNode) -> void:
	var button: Button = buttons[node]
	var branch_color := _branch_color(node)
	button.scale = Vector2(0.82, 0.82)
	var tween := create_tween()
	tween.tween_property(button, "scale", Vector2(1.34, 1.34), 0.1) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	tween.tween_property(button, "scale", Vector2.ONE, 0.2) \
			.set_trans(Tween.TRANS_ELASTIC).set_ease(Tween.EASE_OUT)
	tween.parallel().tween_property(button, "modulate", branch_color, 0.08)
	tween.tween_property(button, "modulate", Color.WHITE, 0.2)

	lines.flash_child = node
	lines.flash_alpha = 1.0
	lines.flash_color = branch_color
	var line_tween := create_tween()
	line_tween.tween_property(lines, "flash_alpha", 0.0, 0.42)

	var burst := Polygon2D.new()
	burst.polygon = _star_polygon(18.0, 7.0, 8)
	burst.position = positions[node]
	burst.color = branch_color
	burst.z_index = 5
	burst.scale = Vector2(0.15, 0.15)
	world.add_child(burst)
	var burst_tween := create_tween().set_parallel(true)
	burst_tween.tween_property(burst, "scale", Vector2(1.7, 1.7), 0.34) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	burst_tween.tween_property(burst, "rotation", PI / 5.0, 0.34)
	burst_tween.tween_property(burst, "modulate:a", 0.0, 0.34)
	burst_tween.finished.connect(burst.queue_free)

func _branch_color(node: SkillNode) -> Color:
	var branch := node
	while branch.parent != null and branch.parent.parent != null:
		branch = branch.parent
	match branch.id:
		"espada":
			return Color("d18bff")
		"fuego":
			return Color("ff9b43")
		"luz":
			return Color("adff9b")
		_:
			return Color("f5bd4b")

func _star_polygon(outer_radius: float, inner_radius: float, points: int) -> PackedVector2Array:
	var polygon := PackedVector2Array()
	for i in points * 2:
		var radius := outer_radius if i % 2 == 0 else inner_radius
		polygon.append(Vector2.from_angle(TAU * i / (points * 2)) * radius)
	return polygon

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
	var bounds := _content_size
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
	set_process_input(false)
	close_button.disabled = true
	center_button.disabled = true
	for button in buttons.values():
		button.disabled = true
	closed.emit()
	if standalone:
		var controller = load("res://scenes/SceneController.tscn").instantiate()
		controller.proxima_escena = "res://scenes/Start.tscn"
		controller.animate_from_current = true
		get_tree().root.add_child(controller)

func _fade_in() -> void:
	var ui: Control = $UI
	ui.modulate.a = 0.0
	ui.pivot_offset = get_viewport().get_visible_rect().size / 2.0
	ui.scale = Vector2(1.025, 1.025)
	$Dim.visible = true
	$Dim.modulate.a = 0.0
	var t := create_tween().set_parallel(true)
	t.tween_property(ui, "modulate:a", 1.0, 0.3)
	t.tween_property(ui, "scale", Vector2.ONE, 0.38) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	t.tween_property($Dim, "modulate:a", 1.0, 0.28)
