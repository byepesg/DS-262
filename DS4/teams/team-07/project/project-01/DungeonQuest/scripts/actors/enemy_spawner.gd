class_name EnemySpawner
extends RefCounted

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
	for id in range(2, GameConfig.ROOM_COUNT):
		var dist: int = _graph.bfs_path(1, id).size() - 1
		if dist < GameConfig.ENEMY_MIN_DISTANCE:
			continue
		if _rng.randf() > GameConfig.ENEMY_CHANCE:
			continue
		var count: int = 1 + _rng.randi() % 2
		for k in count:
			var offset := Vector2.ZERO
			if count == 2:
				offset = Vector2(-30 + 60 * k, 0)
			var pos: Vector2 = _rooms[id].position + GameConfig.ROOM_SIZE / 2.0 + offset
			var enemy := Enemy.new()
			_world.add_child(enemy)
			enemy.setup(id, _player, pos, EnemyTypes.pick_for_distance(dist, _rng))
			enemy.caught.connect(_on_caught)
			enemies.append(enemy)
	print("Enemigos: ", enemies.size())
	
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
