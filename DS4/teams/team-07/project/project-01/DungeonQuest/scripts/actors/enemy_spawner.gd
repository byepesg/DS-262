class_name EnemySpawner
extends RefCounted

const ENEMY_SCENE := preload("res://scenes/actors/exploration_enemy.tscn")
const SPAWN_CLEARANCE := 8.0
const MIN_SPAWN_SEPARATION := 24.0
const SPAWN_BLOCKERS := Enemy.MASK_WALLS | Enemy.MASK_DOORS | Enemy.MASK_PLAYER | Enemy.LAYER_ENEMY

var enemies: Array = []
var _world: Node2D
var _rooms: Dictionary
var _graph: DungeonGraph
var _rng: RandomNumberGenerator
var _player: Node2D
var _on_caught: Callable

func _init(world: Node2D, rooms: Dictionary, graph: DungeonGraph,
		rng: RandomNumberGenerator, player: Node2D, on_caught: Callable) -> void:
	_world = world
	_rooms = rooms
	_graph = graph
	_rng = rng
	_player = player
	_on_caught = on_caught

# Coloca enemigos en salas intermedias alejadas de la entrada
func spawn() -> void:
	await _world.get_tree().physics_frame
	for id in range(2, GameConfig.ROOM_COUNT):
		var dist: int = _graph.bfs_path(1, id).size() - 1
		if dist < GameConfig.ENEMY_MIN_DISTANCE:
			continue
		if _rng.randf() > GameConfig.ENEMY_CHANCE:
			continue
		var room: DungeonRoom = _rooms[id]
		var candidates: Array[Marker2D] = []
		for marker in room.get_enemy_spawn_points():
			if _is_spawn_point_clear(room, marker, candidates):
				candidates.append(marker)
		if candidates.is_empty():
			push_warning("La sala %d no tiene puntos de aparición despejados; se omite." % id)
			continue

		var requested_count: int = 1 + _rng.randi() % 2
		var count: int = mini(requested_count, candidates.size())
		if count < requested_count:
			push_warning("La sala %d solo tiene %d punto(s) válido(s); se reduce el grupo." % [id, count])
		for _i in count:
			var marker_index := _rng.randi_range(0, candidates.size() - 1)
			var marker: Marker2D = candidates.pop_at(marker_index)
			var enemy := ENEMY_SCENE.instantiate() as Enemy
			if enemy == null:
				push_error("exploration_enemy.tscn no contiene un nodo Enemy")
				return
			_world.add_child(enemy)
			enemy.setup(id, _player, marker.global_position,
					EnemyTypes.pick_for_distance(dist, _rng),
					Vector2.RIGHT.rotated(marker.global_rotation))
			enemy.caught.connect(_on_caught)
			enemies.append(enemy)
	print("Enemigos: ", enemies.size())

func _is_spawn_point_clear(room: DungeonRoom, marker: Marker2D,
		already_selected: Array[Marker2D]) -> bool:
	var margin := Vector2.ONE * SPAWN_CLEARANCE
	var bounds := Rect2(margin, GameConfig.ROOM_SIZE - margin * 2.0)
	if not bounds.has_point(room.to_local(marker.global_position)):
		return false
	for selected in already_selected:
		if marker.global_position.distance_to(selected.global_position) < MIN_SPAWN_SEPARATION:
			return false

	var shape := CircleShape2D.new()
	shape.radius = SPAWN_CLEARANCE
	var query := PhysicsShapeQueryParameters2D.new()
	query.shape = shape
	query.transform = Transform2D(0.0, marker.global_position)
	query.collision_mask = SPAWN_BLOCKERS
	query.collide_with_bodies = true
	query.collide_with_areas = false
	var space := _world.get_world_2d().direct_space_state
	return space.intersect_shape(query, 1).is_empty()

func set_room_active(room_id: int, value: bool) -> void:
	for e in enemies:
		if e.room_id == room_id:
			e.set_active(value)

func freeze_all() -> void:
	for e in enemies:
		e.active = false

func group_of(room_id: int) -> Array:
	var group: Array = []
	for e in enemies:
		if e.room_id == room_id:
			group.append(e)
	return group

func remove_group(group: Array) -> void:
	for e in group:
		enemies.erase(e)
		e.queue_free()
