@tool
extends StaticBody2D

signal door_crossed(from_room: int, to_room: int, door: Node2D)

enum View { FRONT, BACK, LEFT, RIGHT }

@export var walk_distance_front: float = 14.0
@export var walk_distance_side: float = 8.0
@export var walk_distance_back: float = 8.0

func get_walk_distance() -> float:
	if view == View.LEFT or view == View.RIGHT:
		return walk_distance_side
	if view == View.BACK:
		return walk_distance_back
	return walk_distance_front
	
@export var from_room: int = 0
@export var to_room: int = 0
@export var view: View = View.FRONT:
	set(value):
		view = value
		_apply_view()

@onready var sprite_back: AnimatedSprite2D = $DoorBack
@onready var sprite_front: AnimatedSprite2D = $DoorFront

func _ready() -> void:
	_apply_view()

func _apply_view() -> void:
	if not is_node_ready():
		return
	var suffix: String = View.keys()[view].capitalize()
	var anim: String = suffix.to_lower()
	sprite_back.animation = anim
	sprite_front.animation = anim
	for c in get_children():
		if c is CollisionShape2D:
			c.set_deferred("disabled", not c.name.begins_with("Col_" + suffix))
	for c in $PassArea.get_children():
		if c is CollisionShape2D:
			c.set_deferred("disabled", not c.name.begins_with("Pass_" + suffix))

func get_walk_direction() -> Vector2:
	if view == View.BACK:
		return Vector2.DOWN
	if view == View.LEFT:
		return Vector2.LEFT
	if view == View.RIGHT:
		return Vector2.RIGHT
	return Vector2.UP

func _on_pass_area_body_entered(body: Node2D) -> void:
	if Engine.is_editor_hint():
		return
	if not body.is_in_group("player"):
		return
	door_crossed.emit(from_room, to_room, self)
