extends Node2D

var graph := DungeonGraph.new()
var current_room: int = 1
var transitioning: bool = false

func _ready() -> void:
	graph.build_default()
	print("Ruta BFS: ", graph.bfs_path(1, 8))
	for d in $Dungeon/Doors.get_children():
		d.door_crossed.connect(_on_door_door_crossed)

func _on_door_door_crossed(from_room: int, to_room: int, door: Node2D) -> void:
	if transitioning:
		return
	if not graph.are_neighbors(from_room, to_room):
		print("Conexión inválida: ", from_room, " -> ", to_room)
		return
	var spawn = get_node_or_null("Rooms/Room%d/From%d" % [to_room, from_room])
	if spawn == null:
		print("No existe Rooms/Room", to_room, "/From", from_room)
		return

	transitioning = true
	var player = $Player
	var cam: Camera2D = $Player/Camera2D
	player.can_move = false
	player.velocity = Vector2.ZERO

	# 1. Cámara quieta
	var cam_pos := cam.global_position
	cam.position_smoothing_enabled = false
	cam.top_level = true
	cam.global_position = cam_pos

	# 2. Jugador camina por la puerta + fade out en paralelo
	var dir: Vector2 = door.get_walk_direction()
	var target: Vector2 = player.global_position + dir * door.get_walk_distance()
	player.play_walk(dir)
	var tween := create_tween().set_parallel(true)
	tween.tween_property(player, "global_position", target, 1.0)
	tween.tween_property(player.animated_sprite, "modulate:a", 0.0, 0.5).set_delay(0.5)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 1.0)
	await tween.finished

	# 3. Teletransporte con pantalla negra
	player.global_position = spawn.global_position
	cam.top_level = false
	cam.position = Vector2.ZERO
	cam.position_smoothing_enabled = true
	cam.reset_smoothing()
	current_room = to_room
	player.update_animation('idle')
	player.global_position = spawn.global_position
	player.animated_sprite.modulate.a = 1.0
	player.animated_sprite.visible = true

	# 4. Fade in
	var t2 := create_tween()
	t2.tween_property($FadeLayer/Fade, "modulate:a", 0.0, 0.3)
	await t2.finished
	player.can_move = true
	transitioning = false
