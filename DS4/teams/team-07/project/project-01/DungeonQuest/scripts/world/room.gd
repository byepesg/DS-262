class_name DungeonRoom
extends Node2D

@onready var door_slots = $DoorSlots

func get_free_slots() -> Array:
	return door_slots.get_free_slots()

func slot_count() -> int:
	return get_free_slots().size()

func get_slot(index: int) -> Node:
	return door_slots.get_child(index)

func get_enemy_spawn_points() -> Array[Marker2D]:
	var points: Array[Marker2D] = []
	var container: Node = get_node_or_null("EnemySpawnPoints")
	if container == null:
		return points
	for child in container.get_children():
		if child is Marker2D:
			points.append(child)
	return points
