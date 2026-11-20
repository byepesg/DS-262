extends Node2D

const TOAST_FONT := preload("res://assets/fonts/alagard.ttf")
const COMBAT_SCENE := preload("res://scenes/CombatScene.tscn")
const HUD_SCENE := preload("res://scenes/PlayerHUD.tscn")

var hud: PlayerHUD
var graph := DungeonGraph.new()
var rng := RandomNumberGenerator.new()
var builder: MapBuilder
var spawner: EnemySpawner
var fx: ScreenFX
var minimap: Minimap
@onready var player: PlayerActor = $Player
@onready var player_camera: Camera2D = $Player/Camera2D
var skills := SkillTree.new()      # progreso que se mantiene entre partidas
var skill_screen: SkillScreen

var current_room: int = 1
var exit_room: int = GameConfig.ROOM_COUNT
var transitioning: bool = false
var player_hp: int = Combat.PLAYER_STATS["max_hp"]

func _ready() -> void:
	fx = ScreenFX.new()
	add_child(fx)

	skills.build_default()
	skills.load_progress()

	builder = MapBuilder.new(graph, rng)
	if not builder.generate():
		return
	builder.build($Dungeon, _on_door_door_crossed)
	builder.world.y_sort_enabled = true
	player.reparent(builder.world)
	player.z_index = 0

	_place_player_at_start()
	_create_minimap()
	
	hud = HUD_SCENE.instantiate()
	add_child(hud)
	hud.setup(Combat.PLAYER_STATS["name"], player_hp,
			Combat.PLAYER_STATS["max_hp"], skills.points)
	spawner = EnemySpawner.new(builder.world, builder.rooms, graph, rng, player, _on_enemy_caught)
	spawner.spawn()

func _create_minimap() -> void:
	var layer := CanvasLayer.new()
	layer.name = "MinimapLayer"
	layer.layer = 50
	add_child(layer)
	minimap = Minimap.new()
	minimap.position = Vector2(12, 12)
	layer.add_child(minimap)
	minimap.setup(graph, exit_room)
	minimap.visit(1)

func _place_player_at_start() -> void:
	var spawn = builder.rooms[1].get_node_or_null("PlayerSpawn")
	if spawn == null:
		push_error("EntryRoom no tiene PlayerSpawn")
		return
	player_camera.position_smoothing_enabled = false
	player.global_position = spawn.global_position
	current_room = 1
	_set_camera_limits(1)
	player_camera.make_current()
	player_camera.reset_smoothing()
	player_camera.force_update_scroll()
	player_camera.position_smoothing_enabled = true

func _set_camera_limits(room_id: int) -> void:
	var room: Node2D = builder.rooms[room_id]
	var cam: Camera2D = player_camera
	cam.limit_left = int(room.position.x)
	cam.limit_top = int(room.position.y)
	cam.limit_right = int(room.position.x + GameConfig.ROOM_SIZE.x)
	cam.limit_bottom = int(room.position.y + GameConfig.ROOM_SIZE.y)

# ---------- Combate ----------

func _on_enemy_caught(enemy: Enemy) -> void:
	if transitioning:
		return
	transitioning = true
	player.can_move = false
	player.velocity = Vector2.ZERO

	var group := spawner.group_of(enemy.room_id)
	spawner.freeze_all()

	await fx.close_transition(0.4, 0.024, 0.18)
	get_tree().paused = true
	var combat: Combat = COMBAT_SCENE.instantiate()
	add_child(combat)
	combat.finished.connect(_on_combat_finished.bind(group))
	var types: Array = []
	for e in group:
		types.append(e.type)
	combat.start(player_hp, types, skills.unlocked_powers())
	await fx.open_transition(0.38, 0.02, 0.18)

func _on_combat_finished(player_won: bool, hp_left: int, group: Array) -> void:
	player_hp = hp_left
	hud.set_hp(player_hp)
	if player_won:
		await fx.close_transition()
		spawner.remove_group(group)
		get_tree().paused = false
		skills.add_points(group.size())
		hud.set_points(skills.points)
		await fx.open_transition()
		player.can_move = true
		transitioning = false
		fx.toast("+%d punto(s) de habilidad  (K: árbol de habilidades)" % group.size())
	else:
		_game_over()

func _game_over() -> void:
	get_tree().paused = false
	_transition_to_scene("res://scenes/GameOver.tscn", true)

# ---------- Cruce de puertas ----------

func _on_door_door_crossed(from_room: int, to_room: int, door: Node2D) -> void:
	if transitioning:
		return
	if door.is_exit:
		_win(door)
		return
	if not graph.are_neighbors(from_room, to_room):
		print("Conexión inválida: ", from_room, " -> ", to_room)
		return
	var twin = builder.doors.get(Vector2i(to_room, from_room))
	if twin == null:
		print("Falta la puerta gemela: ", to_room, " -> ", from_room)
		return

	transitioning = true
	var cam: Camera2D = player_camera
	player.can_move = false
	player.velocity = Vector2.ZERO

	var cam_pos := cam.global_position
	cam.position_smoothing_enabled = false
	cam.top_level = true
	cam.global_position = cam_pos

	var dir: Vector2 = door.get_walk_direction()
	var target: Vector2 = player.global_position + dir * door.get_walk_distance()
	player.play_walk(dir)
	var tween := create_tween()
	tween.tween_property(player, "global_position", target, 0.42)
	tween.parallel().tween_property(player.animated_sprite, "modulate:a", 0.0, 0.14).set_delay(0.2)
	await tween.finished
	await fx.close_transition(0.16, 0.008)
	await get_tree().create_timer(0.04).timeout

	player.global_position = twin.get_arrival_position()
	player.animated_sprite.modulate.a = 1.0
	spawner.set_room_active(from_room, false)
	current_room = to_room
	_set_camera_limits(to_room)
	minimap.visit(to_room)
	cam.top_level = false
	cam.position = Vector2.ZERO
	cam.position_smoothing_enabled = true
	cam.reset_smoothing()

	player.play_walk(-twin.get_walk_direction())
	player.update_animation('idle')
	player.animated_sprite.modulate.a = 0.0
	var reveal := create_tween()
	reveal.tween_property(player.animated_sprite, "modulate:a", 1.0, 0.08)
	await reveal.finished
	await fx.open_transition(0.16, 0.006)
	spawner.set_room_active(to_room, true)
	player.can_move = true
	transitioning = false

func _win(door: Node2D) -> void:
	transitioning = true
	skills.add_points(GameConfig.WIN_POINTS)
	player.can_move = false
	player.velocity = Vector2.ZERO
	var dir: Vector2 = door.get_walk_direction()
	var target: Vector2 = player.global_position + dir * door.get_walk_distance()
	player.play_walk(dir)
	var tween := create_tween()
	tween.tween_property(player, "global_position", target, 0.42)
	tween.parallel().tween_property(player.animated_sprite, "modulate:a", 0.0, 0.14).set_delay(0.2)
	await tween.finished
	_transition_to_scene("res://scenes/Victory.tscn")

# ---------- Entrada, habilidades y pista ----------

func _unhandled_input(event: InputEvent) -> void:
	if event.is_action_pressed("help") and not event.is_echo():
		_show_hint()
	var key := event as InputEventKey
	if key != null and key.pressed and not key.echo and key.keycode == KEY_K:
		_open_skills()

func _open_skills() -> void:
	if transitioning or skill_screen != null:
		return
	transitioning = true
	get_tree().paused = true
	await fx.close_transition()
	skill_screen = SkillScreen.open(self, skills)
	skill_screen.closed.connect(_on_skills_closed)
	await fx.open_transition()

func _on_skills_closed() -> void:
	transitioning = true
	var closing_screen := skill_screen
	skill_screen = null
	hud.set_points(skills.points)   # por si gastaste puntos
	await fx.close_transition()
	if is_instance_valid(closing_screen):
		closing_screen.queue_free()
	get_tree().paused = false
	await fx.open_transition()
	transitioning = false

func _transition_to_scene(scene_path: String, use_fade: bool = false) -> void:
	get_tree().paused = false
	var controller = load("res://scenes/SceneController.tscn").instantiate()
	controller.proxima_escena = scene_path
	controller.fade_transition = use_fade
	controller.animate_from_current = not use_fade
	get_tree().root.add_child(controller)

func _show_hint() -> void:
	if current_room == exit_room:
		for d in builder.get_room_doors(current_room):
			if d.is_exit:
				d.door_glow()
		return
	var path: Array = graph.bfs_path(current_room, exit_room)
	if path.size() < 2:
		return
	for d in builder.get_room_doors(current_room):
		if d.to_room == path[1]:
			d.door_glow()
