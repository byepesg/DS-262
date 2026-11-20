class_name Enemy
extends CharacterBody2D

# Enemigo de sala.
#
# Comportamiento:
#   IDLE  -> quieto; si ve al jugador, alerta a TODOS los enemigos de su sala.
#   CHASE -> persigue al jugador hasta tocarlo (señal caught).
# Solo actúa mientras el jugador está en su sala (active = true).

signal caught(enemy: Enemy)

enum State { IDLE, CHASE }

@export var sight_radius: float = 110.0
@export_range(10.0, 360.0, 1.0) var vision_angle_degrees: float = 90.0
@export var chase_speed: float = 40.0     # el jugador va a 50: se puede huir

# Capas de colisión
const LAYER_ENEMY := 16        # capa 5: el jugador no choca con enemigos
const MASK_WALLS := 1          # capa 1: paredes del TileMap
const MASK_DOORS := 4          # capa 3: pilares de las puertas
const MASK_PLAYER := 2         # capa 2: jugador
const BODY_RADIUS := 6.0

var room_id: int = 0
var home: Vector2 = Vector2.ZERO
var state: State = State.IDLE
var active: bool = false
var target: Node2D
var facing_direction: Vector2 = Vector2.RIGHT
var _spawn_direction: Vector2 = Vector2.RIGHT

@onready var sprite: AnimatedSprite2D = $AnimatedSprite
@onready var alert_mark: Label = $AlertMark
@onready var vision_cone: VisionCone = $VisionCone

var type: String = "slime"

func _ready() -> void:
	collision_layer = LAYER_ENEMY
	collision_mask = MASK_WALLS | MASK_DOORS
	z_index = 0
	$ContactArea.body_entered.connect(_on_body_entered)
	vision_cone.configure(sight_radius, vision_angle_degrees, facing_direction)
	_play_animation("idle")

# Llamar DESPUÉS de add_child
func setup(room: int, player: Node2D, pos: Vector2, enemy_type: String = "slime",
		direction: Vector2 = Vector2.RIGHT) -> void:
	room_id = room
	target = player
	home = pos
	type = enemy_type
	global_position = pos
	_spawn_direction = direction.normalized() if not direction.is_zero_approx() else Vector2.RIGHT
	facing_direction = _spawn_direction
	var stats: Dictionary = EnemyTypes.get_stats(type)
	if stats.has("sprite_frames"):
		sprite.sprite_frames = stats["sprite_frames"]
	sprite.modulate = Color.WHITE
	vision_cone.configure(sight_radius, vision_angle_degrees, facing_direction)
	add_to_group("enemies_room_%d" % room_id)

func set_active(value: bool) -> void:
	active = value
	if not value:
		reset()

func reset() -> void:
	state = State.IDLE
	velocity = Vector2.ZERO
	global_position = home
	facing_direction = _spawn_direction
	sprite.modulate = Color.WHITE
	alert_mark.visible = false
	vision_cone.visible = true
	vision_cone.configure(sight_radius, vision_angle_degrees, facing_direction)
	_play_animation("idle")

func _physics_process(_delta: float) -> void:
	if not active or target == null:
		return
	match state:
		State.IDLE:
			if _can_see_player():
				# Avisa a todos los enemigos de la sala
				get_tree().call_group("enemies_room_%d" % room_id, "alert")
		State.CHASE:
			var dir := (target.global_position - global_position).normalized()
			if not dir.is_zero_approx():
				facing_direction = dir
			velocity = dir * chase_speed
			move_and_slide()

# Visión: dentro del cono y sin pared o puerta en medio
func _can_see_player() -> bool:
	var to_target := target.global_position - global_position
	if to_target.length() > sight_radius or to_target.is_zero_approx():
		return false
	var direction := to_target.normalized()
	var half_angle := deg_to_rad(vision_angle_degrees * 0.5)
	if facing_direction.normalized().dot(direction) < cos(half_angle):
		return false
	var query := PhysicsRayQueryParameters2D.create(
			global_position, target.global_position, MASK_WALLS | MASK_DOORS)
	var hit := get_world_2d().direct_space_state.intersect_ray(query)
	return hit.is_empty()

func alert() -> void:
	if state == State.CHASE:
		return
	state = State.CHASE
	if target != null:
		var direction := target.global_position - global_position
		if not direction.is_zero_approx():
			facing_direction = direction.normalized()
	vision_cone.visible = false
	alert_mark.visible = true
	_play_animation("move")

func _on_body_entered(body: Node2D) -> void:
	if active and body.is_in_group("player"):
		caught.emit(self)

func _play_animation(animation_name: String) -> void:
	if sprite.sprite_frames == null:
		return
	if sprite.sprite_frames.has_animation(animation_name):
		sprite.play(animation_name)
	elif sprite.sprite_frames.has_animation("idle"):
		sprite.play("idle")
