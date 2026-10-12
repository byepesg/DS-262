extends Node2D

var graph := DungeonGraph.new()
var current_room: int = 1
var transitioning: bool = false
var doors: Dictionary = {}

func _ready() -> void:
	graph.build_default()
	print("Ruta BFS: ", graph.bfs_path(1, 8))
	for room in $Rooms.get_children():
		for d in room.get_children():
			if d.has_signal("door_crossed"):
				doors[Vector2i(d.from_room, d.to_room)] = d
				if not d.door_crossed.is_connected(_on_door_door_crossed):
					d.door_crossed.connect(_on_door_door_crossed)

func _on_door_door_crossed(from_room: int, to_room: int, door: Node2D) -> void:
	if transitioning:
		return
	if not graph.are_neighbors(from_room, to_room):
		print("Conexión inválida: ", from_room, " -> ", to_room)
		return
	var twin = doors.get(Vector2i(to_room, from_room))
	if twin == null:
		print("Falta la puerta gemela: ", to_room, " -> ", from_room)
		return
	if not has_node("FadeLayer/Fade"):
		print("FALTA FadeLayer/Fade")
		return

	transitioning = true
	var player = $Player
	var cam: Camera2D = $Player/Camera2D
	player.can_move = false
	player.velocity = Vector2.ZERO

	# 1. Cámara quieta
	var cam_pos := cam.global_position
	cam.position_smoothing_enabled = true
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
	await get_tree().create_timer(0.1).timeout

	# 3. Teletransporte con pantalla negra
	player.global_position = twin.get_arrival_position()
	player.animated_sprite.modulate.a = 1.0
	cam.top_level = false
	cam.position = Vector2.ZERO
	cam.position_smoothing_enabled = true
	cam.reset_smoothing()
	current_room = to_room

	# El jugador mira hacia el interior de la sala
	player.play_walk(-twin.get_walk_direction())
	player.update_animation('idle')

	# 4. Fade in
	var t2 := create_tween()
	t2.tween_property($FadeLayer/Fade, "modulate:a", 0.0, 0.3)
	await t2.finished
	player.can_move = true
	transitioning = false
