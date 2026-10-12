@tool
extends StaticBody2D

signal door_crossed(from_room: int, to_room: int, door: Node2D)

enum View { FRONT, BACK, LEFT, RIGHT }

@export var from_room: int = 0
@export var to_room: int = 0
@export var view: View = View.FRONT:
	set(value):
		view = value
		_apply_view()

@export var walk_distance_front: float = 18.0
@export var walk_distance_side: float = 12.0
@export var walk_distance_back: float = 12.0

@onready var sprite_back: AnimatedSprite2D = $DoorBack
@onready var sprite_front: AnimatedSprite2D = $DoorFront
@onready var sprite_glow: AnimatedSprite2D = $DoorGlow

func _ready() -> void:
	sprite_glow.modulate.a = 0.0
	_apply_view()

func door_glow() -> void:
	var glow_tween = create_tween()
	glow_tween.tween_property(sprite_glow, "modulate:a", 1.0, 0.5)
	glow_tween.tween_property(sprite_glow, "modulate:a", 0.0, 0.5)

func _apply_view() -> void:
	if not is_node_ready():
		return
	var suffix: String = View.keys()[view].capitalize()
	var anim: String = suffix.to_lower()
	sprite_back.animation = anim
	sprite_front.animation = anim
	sprite_glow.animation = anim
	# En vista Front el jugador pasa por encima de todo el arco
	if view == View.FRONT:
		sprite_front.z_index = 2
	else:
		sprite_front.z_index = 4
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

func get_walk_distance() -> float:
	if view == View.LEFT or view == View.RIGHT:
		return walk_distance_side
	if view == View.BACK:
		return walk_distance_back
	return walk_distance_front

func get_arrival_position() -> Vector2:
	var suffix: String = View.keys()[view].capitalize()
	var m: Marker2D = get_node("Arrival_" + suffix)
	return m.global_position

func _on_pass_area_body_entered(body: Node2D) -> void:
	if Engine.is_editor_hint():
		return
	if not body.is_in_group("player"):
		return
	door_crossed.emit(from_room, to_room, self)
	
