extends CharacterBody2D

@onready var animated_sprite: AnimatedSprite2D = $AnimatedSprite

var speed = 50.0
var last_direction = "down"
var can_move: bool = true

func _ready() -> void:
	print("Player layer = ", collision_layer, " mask = ", collision_mask)

func _physics_process(_delta: float) -> void:
	if not can_move:
		return
	get_input()
	move_and_slide()

func get_input():
	var input_direction = Input.get_vector("left", "right", "up", "down")
	velocity = input_direction * speed

	if input_direction == Vector2.ZERO:
		update_animation('idle')
		return

	if abs(input_direction.x) > abs(input_direction.y):
		last_direction = 'right' if input_direction.x > 0 else 'left'
	else:
		last_direction = 'down' if input_direction.y > 0 else 'up'

	update_animation('move')

func update_animation(state):
	animated_sprite.play(state + '_' + last_direction)

func play_walk(dir: Vector2) -> void:
	if abs(dir.x) > abs(dir.y):
		last_direction = 'right' if dir.x > 0 else 'left'
	else:
		last_direction = 'down' if dir.y > 0 else 'up'
	update_animation('move')
