class_name EnemyTypes

const PLACEHOLDER_FRAMES := preload("res://assets/sprites/enemies/placeholder_frames.tres")

const TYPES := {
	"slime":    {"name": "Slime",      "hp": 30, "attack": 4,  "speed": 8.0,  "hits": 2, "color": Color(0.4, 0.9, 0.4), "sprite_frames": PLACEHOLDER_FRAMES},
	"skeleton": {"name": "Esqueleto",  "hp": 45, "attack": 7,  "speed": 9.0,  "hits": 2, "color": Color(0.9, 0.9, 0.8), "sprite_frames": PLACEHOLDER_FRAMES},
	"bat":      {"name": "Murciélago", "hp": 20, "attack": 5,  "speed": 14.0, "hits": 3, "color": Color(0.7, 0.4, 0.9), "sprite_frames": PLACEHOLDER_FRAMES},
	"brute":    {"name": "Bruto",      "hp": 70, "attack": 10, "speed": 5.0,  "hits": 1, "color": Color(0.9, 0.4, 0.3), "sprite_frames": PLACEHOLDER_FRAMES},
}

static func get_stats(type: String) -> Dictionary:
	return TYPES.get(type, TYPES["slime"])

# Más lejos de la entrada = más variedad y peligro
static func pick_for_distance(dist: int, rng: RandomNumberGenerator) -> String:
	var pool: Array = ["slime", "bat"]
	if dist >= 2:
		pool.append("skeleton")
	if dist >= 3:
		pool.append("brute")
	return pool[rng.randi() % pool.size()]
