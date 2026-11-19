extends Node2D

@export var disabled_slots: Array[int] = []

func get_free_slots() -> Array:
	var result: Array = []
	for i in get_child_count():
		if i not in disabled_slots:
			result.append(get_child(i))
	return result
