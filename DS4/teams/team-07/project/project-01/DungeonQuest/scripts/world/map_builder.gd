class_name MapBuilder
extends RefCounted

const DOOR_SCENE := preload("res://scenes/objects/door.tscn")
const ENTRY_SCENE := preload("res://scenes/rooms/entry_room.tscn")
const EXIT_SCENE := preload("res://scenes/rooms/exit_room.tscn")
const NORMAL_SCENES := [
	preload("res://scenes/rooms/room_a.tscn"),
	preload("res://scenes/rooms/room_b.tscn"),
	preload("res://scenes/rooms/room_c.tscn"),
	preload("res://scenes/rooms/room_d.tscn"),
]

var graph: DungeonGraph
var rng: RandomNumberGenerator
var world: Node2D
var rooms: Dictionary = {}    # id -> sala
var doors: Dictionary = {}    # Vector2i(from, to) -> puerta
var _normal_views: Array = []
var _on_door_crossed: Callable

func _init(p_graph: DungeonGraph, p_rng: RandomNumberGenerator) -> void:
	graph = p_graph
	rng = p_rng
	for scene in NORMAL_SCENES:
		_normal_views.append(_scene_views(scene))

# ---------- Generación del grafo ----------

# Busca una semilla válida y deja el rng sembrado con ella
func generate() -> bool:
	rng.randomize()
	var seed_value: int = rng.randi()
	var entry_view := _entry_link_view()
	var exit_view := _exit_link_view()
	for attempt in 30:
		if graph.generate_grid(GameConfig.ROOM_COUNT, GameConfig.EXTRA_EDGES,
				seed_value + attempt, entry_view, exit_view):
			rng.seed = seed_value + attempt
			print("Semilla: ", rng.seed)
			print("Grafo: ", graph.adjacency)
			print("Celdas: ", graph.cells)
			print("Ruta BFS: ", graph.bfs_path(1, GameConfig.ROOM_COUNT))
			return true
	push_error("No se pudo generar el mapa")
	return false

# ---------- Lectura de plantillas ----------

func _scene_views(scene: PackedScene) -> Array:
	var inst = scene.instantiate()
	var views: Array = []
	for s in inst.get_node("DoorSlots").get_free_slots():
		if not views.has(s.view):
			views.append(s.view)
	inst.free()
	return views

func _entry_link_view() -> int:
	var inst = ENTRY_SCENE.instantiate()
	var slots: Array = inst.get_node("DoorSlots").get_free_slots()
	var v: int = 0
	if not slots.is_empty():
		v = slots[0].view
	inst.free()
	return v

func _exit_link_view() -> int:
	var inst = EXIT_SCENE.instantiate()
	var v: int = inst.get_node("DoorSlots").get_child(GameConfig.EXIT_LINK_SLOT).view
	inst.free()
	return v

func _pick_template(id: int) -> PackedScene:
	var needed: Array = []
	for nb in graph.get_neighbors(id):
		needed.append(graph.view_to(id, nb))
	var valid: Array = []
	for i in NORMAL_SCENES.size():
		var fits := true
		for v in needed:
			if not _normal_views[i].has(v):
				fits = false
		if fits:
			valid.append(NORMAL_SCENES[i])
	if valid.is_empty():
		push_error("Ninguna plantilla cubre las paredes de la sala %d" % id)
		return NORMAL_SCENES[0]
	return valid[rng.randi() % valid.size()]

# ---------- Construcción ----------

func build(parent: Node, on_door_crossed: Callable) -> void:
	_on_door_crossed = on_door_crossed
	world = Node2D.new()
	world.name = "World"
	parent.add_child(world)

	var exit_room := GameConfig.ROOM_COUNT
	var slots_by_room: Dictionary = {}

	for i in range(1, GameConfig.ROOM_COUNT + 1):
		var scene: PackedScene
		if i == 1:
			scene = ENTRY_SCENE
		elif i == exit_room:
			scene = EXIT_SCENE
		else:
			scene = _pick_template(i)
		var room = scene.instantiate()
		room.name = "Room%d" % i
		room.position = GameConfig.WORLD_ORIGIN + Vector2(graph.cells[i]) * GameConfig.ROOM_STEP
		world.add_child(room)
		rooms[i] = room
		var slots: Array = room.get_free_slots()
		_shuffle(slots)
		slots_by_room[i] = slots

	for a in graph.adjacency:
		for b in graph.get_neighbors(a):
			if a < b:
				var v: int = graph.view_to(a, b)
				var slot_a = _slot_for(a, v, slots_by_room)
				var slot_b = _slot_for(b, v ^ 1, slots_by_room)
				if slot_a == null or slot_b == null:
					push_error("Sin slot en la pared correcta para la arista %d-%d" % [a, b])
					continue
				_make_door(a, b, slot_a)
				_make_door(b, a, slot_b)

	_make_door(exit_room, 0, rooms[exit_room].get_slot(GameConfig.EXIT_WIN_SLOT), true)

func _slot_for(room_id: int, view: int, slots_by_room: Dictionary):
	if room_id == GameConfig.ROOM_COUNT:
		return rooms[room_id].get_slot(GameConfig.EXIT_LINK_SLOT)
	return _take_slot(slots_by_room[room_id], view)

func _take_slot(slots: Array, wanted_view: int):
	for i in slots.size():
		if slots[i].view == wanted_view:
			return slots.pop_at(i)
	return null

func _make_door(from_id: int, to_id: int, slot: Node, is_exit: bool = false) -> void:
	var door = DOOR_SCENE.instantiate()
	door.from_room = from_id
	door.to_room = to_id
	door.is_exit = is_exit
	door.view = slot.view          # antes de add_child
	slot.add_child(door)
	door.name = "Door_%d_%d" % [from_id, to_id]
	doors[Vector2i(from_id, to_id)] = door
	door.door_crossed.connect(_on_door_crossed)

func _shuffle(arr: Array) -> void:
	for i in range(arr.size() - 1, 0, -1):
		var j: int = rng.randi() % (i + 1)
		var tmp = arr[i]
		arr[i] = arr[j]
		arr[j] = tmp

func get_room_doors(room_id: int) -> Array:
	var result: Array = []
	for key in doors:
		if key.x == room_id:
			result.append(doors[key])
	return result
