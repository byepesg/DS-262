class_name EnemyTypes

const ENEMY_SPRITES := "res://assets/sprites/characters/enemies/"
const FRAME_SIZE := Vector2i(32, 32)

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
			frames = _build_single_sheet_frames("snake/snake.png", 5)
		"rata":
			frames = _build_single_sheet_frames("rat/rata.png", 5)
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
		["idle", "owl_ass.png", 10, 6.0],
		["idle_down", "owl_stand_down.png", 4, 4.0],
		["idle_up", "owl_stand_up.png", 12, 4.0],
		["idle_left", "owl_left.png", 6, 4.0],
		["idle_right", "owl_right.png", 6, 4.0],
		["move_down", "owl_walk_down.png", 5, 12.0],
		["move_up", "owl_walk_up.png", 8, 12.0],
		["move_left", "owl_left_walk.png", 10, 12.0],
		["move_right", "owl_right_walk.png", 10, 12.0],
		["attack", "owl_attack.png", 8, 24.0],   # original de 32x32, ya no hace falta owl_attack_16
	]
	for strip in strips:
		if not _add_strip(frames, strip[0], root + strip[1], strip[2], strip[3],
				strip[0] != "attack"):
			push_warning("No se pudo cargar la animación del búho: %s" % strip[0])
	return frames if frames.has_animation("idle") else null

# Carga una tira horizontal y la normaliza a celdas de FRAME_SIZE (32x32).
static func _load_strip_texture(path: String, frame_count: int) -> Texture2D:
	var source := load(path) as Texture2D
	if source == null:
		push_warning("No se encontró el sprite: %s" % path)
		return null
	var cell: int = source.get_height()                 # celdas cuadradas
	if cell <= 0 or source.get_width() < frame_count * cell:
		push_warning("El sprite no contiene todos los frames requeridos: %s" % path)
		return null
	if cell == FRAME_SIZE.y:
		return source
	var image := source.get_image()
	if image == null:
		return null
	image.convert(Image.FORMAT_RGBA8)
	var new_width: int = source.get_width() * FRAME_SIZE.y / cell
	image.resize(new_width, FRAME_SIZE.y, Image.INTERPOLATE_NEAREST)
	return ImageTexture.create_from_image(image)

static func _add_strip(frames: SpriteFrames, animation_name: StringName, path: String,
		frame_count: int, speed: float, loops: bool = true) -> bool:
	var texture := _load_strip_texture(path, frame_count)
	if texture == null:
		return false
	frames.add_animation(animation_name)
	frames.set_animation_speed(animation_name, speed)
	frames.set_animation_loop(animation_name, loops)
	for index in range(frame_count):
		var frame := AtlasTexture.new()
		frame.atlas = texture
		frame.region = Rect2(index * FRAME_SIZE.x, 0, FRAME_SIZE.x, FRAME_SIZE.y)
		frames.add_frame(animation_name, frame)
	return true
	
# Más lejos de la entrada = más variedad y peligro
static func pick_for_distance(dist: int, rng: RandomNumberGenerator) -> String:
	var pool: Array = ["buho", "rata"]
	if dist >= 2:
		pool.append("Serpiente")
	return pool[rng.randi() % pool.size()]
