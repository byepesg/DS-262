class_name EnemyTypes

const ENEMY_SPRITES := "res://assets/sprites/characters/enemies/"

const TYPES := {
	"Serpiente":{"name": "Serpiente", "hp": 45, "attack": 7,  "speed": 9.0,  "hits": 2},
	"rata":     {"name": "Rata",      "hp": 20, "attack": 5,  "speed": 14.0, "hits": 3},
	"buho":     {"name": "Buho",      "hp": 70, "attack": 10, "speed": 5.0,  "hits": 1},
}

static var _frames_cache: Dictionary = {}

static func get_stats(type: String) -> Dictionary:
	var resolved_type := type if TYPES.has(type) else "buho"
	var stats: Dictionary = TYPES[resolved_type].duplicate()
	stats["sprite_frames"] = _frames_for(resolved_type)
	return stats

static func _frames_for(type: String) -> SpriteFrames:
	if _frames_cache.has(type):
		return _frames_cache[type]

	var frames: SpriteFrames
	match type:
		"Serpiente":
			frames = _build_snake_frames()
		"rata":
			frames = _build_rat_frames()
		_:
			frames = _build_owl_frames()

	if frames == null and type != "buho":
		frames = _frames_for("buho")
	if frames != null:
		_frames_cache[type] = frames
	return frames

static func _new_sprite_frames() -> SpriteFrames:
	var frames := SpriteFrames.new()
	if frames.has_animation("default"):
		frames.remove_animation("default")
	return frames

static func _build_single_sheet_frames(relative_path: String, frame_count: int) -> SpriteFrames:
	var frames := _new_sprite_frames()
	var path := ENEMY_SPRITES + relative_path
	if not _add_strip(frames, "idle", path, frame_count, 6.0):
		return null
	if not _add_strip(frames, "move", path, frame_count, 10.0):
		return null
	return frames

static func _build_owl_frames() -> SpriteFrames:
	var frames := _new_sprite_frames()
	var root := ENEMY_SPRITES + "owl/"
	var strips := [
		["idle", "OwlAss.png", 10, 6.0],
		["idle_down", "OwlStandDown.png", 4, 4.0],
		["idle_up", "OwlStandUp.png", 12, 4.0],
		["idle_left", "OwlLeft.png", 6, 4.0],
		["idle_right", "OwlRight.png", 6, 4.0],
		["move_down", "OwlWalkDown.png", 5, 12.0],
		["move_up", "OwlWalkUp.png", 8, 12.0],
		["move_left", "OwlLeftWalk.png", 10, 12.0],
		["move_right", "OwlRightWalk.png", 10, 12.0],
		["attack", "OwlAttc.png", 8, 24.0],
	]
	for strip in strips:
		if not _add_strip(frames, strip[0], root + strip[1], strip[2], strip[3],
				strip[0] != "attack"):
			push_warning("No se pudo cargar la animación del búho: %s" % strip[0])
	return frames if frames.has_animation("idle") else null

static func _build_snake_frames() -> SpriteFrames:
	var frames := _new_sprite_frames()
	var root := ENEMY_SPRITES + "snake/"
	var strips := [
		["idle", "snake.png", 12, 6.0],
		["idle_down", "SnakeStandDown.png", 8, 4.0],
		["idle_up", "SnakeStandUp.png", 8, 4.0],
		["idle_left", "snakeLeft.png", 8, 4.0],
		["idle_right", "snakeRight.png", 8, 4.0],
		["move_down", "SnakeWalkDown.png", 8, 12.0],
		["move_up", "SnakeWalkUp.png", 7, 12.0],
<<<<<<< Updated upstream
		["move_left", "snakeLeftWalk.png", 8, 12.0],
		["move_right", "snakeRightWalk.png", 8, 12.0],
		["attack", "snakeAttc.png", 11, 24.0],
=======
		["move_left", "SnakeLeftWalk.png", 8, 12.0],
		["move_right", "SnakeRightWalk.png", 8, 12.0],
		["attack", "SnakeAttc.png", 11, 24.0],
>>>>>>> Stashed changes
	]
	for strip in strips:
		if not _add_strip(frames, strip[0], root + strip[1], strip[2], strip[3],
				strip[0] != "attack"):
			push_warning("No se pudo cargar la animación de la serpiente: %s" % strip[0])
	return frames if frames.has_animation("idle") else null

<<<<<<< Updated upstream
=======
static func _build_rat_frames() -> SpriteFrames:
	var frames := _new_sprite_frames()
	var root := ENEMY_SPRITES + "rat/"
	var strips := [
		["idle", "Rat.png", 12, 6.0],
		["idle_down", "RatStandDown.png", 8, 4.0],
		["idle_up", "RatStandUp.png", 8, 4.0],
		["idle_left", "RatLeft.png", 8, 4.0],
		["idle_right", "RatRight.png", 8, 4.0],
		["move_down", "RatWalkDown.png", 8, 12.0],
		["move_up", "RatWalkUp.png", 7, 12.0],
		["move_left", "RatLeftWalk.png", 8, 12.0],
		["move_right", "RatRightWalk.png", 8, 12.0],
		["attack", "RatAttc.png", 11, 24.0],
	]
	for strip in strips:
		if not _add_strip(frames, strip[0], root + strip[1], strip[2], strip[3],
				strip[0] != "attack"):
			push_warning("No se pudo cargar la animación de la rata: %s" % strip[0])
	return frames if frames.has_animation("idle") else null

>>>>>>> Stashed changes
static func _add_strip(frames: SpriteFrames, animation_name: StringName, path: String,
		frame_count: int, speed: float, loops: bool = true) -> bool:
	var texture := load(path) as Texture2D
	if texture == null:
		push_warning("No se encontró el sprite: %s" % path)
		return false
	var frame_size: int = texture.get_height()
	if frame_size <= 0 or texture.get_width() < frame_count * frame_size:
		push_warning("El sprite no contiene todos los frames requeridos: %s" % path)
		return false
	frames.add_animation(animation_name)
	frames.set_animation_speed(animation_name, speed)
	frames.set_animation_loop(animation_name, loops)
	for index in range(frame_count):
		var frame := AtlasTexture.new()
		frame.atlas = texture
		frame.region = Rect2(index * frame_size, 0, frame_size, frame_size)
		frames.add_frame(animation_name, frame)
	return true
	
# Más lejos de la entrada = más variedad y peligro
static func pick_for_distance(dist: int, rng: RandomNumberGenerator) -> String:
	var pool: Array = ["buho", "rata"]
	if dist >= 2:
		pool.append("Serpiente")
	return pool[rng.randi() % pool.size()]
