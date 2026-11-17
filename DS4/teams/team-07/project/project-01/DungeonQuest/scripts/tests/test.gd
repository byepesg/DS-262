extends Node2D

func _ready() -> void:
	for room in get_children():
		if not room.has_method("slot_count"):
			print(room.name, ": no es una plantilla (sin room.gd)")
		elif room.get_node_or_null("DoorSlots") == null:
			print(room.name, ": NO tiene nodo DoorSlots")
		else:
			print(room.name, ": ", room.slot_count(), " slots libres")
