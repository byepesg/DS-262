class_name EnemyTypes

const PLACEHOLDER_FRAMES := preload("res://assets/sprites/enemies/placeholder_frames.tres")

const TYPES := {
	"slime":    {"name": "Slime",     "hp": 30, "attack": 4,  "speed": 8.0,  "hits": 2, "sprite_frames": PLACEHOLDER_FRAMES},
	"Serpiente":{"name": "Serpiente", "hp": 45, "attack": 7,  "speed": 9.0,  "hits": 2, "sprite_frames": PLACEHOLDER_FRAMES},
	"rata":     {"name": "Rata",      "hp": 20, "attack": 5,  "speed": 14.0, "hits": 3, "sprite_frames": PLACEHOLDER_FRAMES},
	"buho":     {"name": "Buho",      "hp": 70, "attack": 10, "speed": 5.0,  "hits": 1, "sprite_frames": PLACEHOLDER_FRAMES},
}

static func get_stats(type: String) -> Dictionary:
	return TYPES.get(type, TYPES["slime"])

# Más lejos de la entrada = más variedad y peligro
static func pick_for_distance(dist: int, rng: RandomNumberGenerator) -> String:
	var pool: Array = ["slime", "rata"]
	if dist >= 2:
		pool.append("Serpiente")
	if dist >= 3:
		pool.append("buho")
	return pool[rng.randi() % pool.size()]
