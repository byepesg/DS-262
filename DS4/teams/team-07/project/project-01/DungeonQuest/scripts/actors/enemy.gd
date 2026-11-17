class_name Enemy
extends CharacterBody2D

# Enemigo de sala. Se construye por completo desde código (sin escena):
# sprite placeholder (icon.svg), colisión con paredes y un área de contacto.
#
# Comportamiento:
#   IDLE  -> quieto; si ve al jugador, alerta a TODOS los enemigos de su sala.
#   CHASE -> persigue al jugador hasta tocarlo (señal caught).
# Solo actúa mientras el jugador está en su sala (active = true).

signal caught(enemy: Enemy)

enum State { IDLE, CHASE }

@export var sight_radius: float = 110.0
@export var chase_speed: float = 40.0     # el jugador va a 50: se puede huir

# Capas de colisión (ajústalas si tus capas son distintas)
const LAYER_ENEMY := 16        # capa 5: el jugador no choca con enemigos
const MASK_WALLS := 1          # capa 1: paredes del TileMap
const MASK_DOORS := 4          # capa 3: pilares de las puertas
const MASK_PLAYER := 2         # capa 2: jugador

var room_id: int = 0
var home: Vector2 = Vector2.ZERO
var state: State = State.IDLE
var active: bool = false
var target: Node2D

var sprite: Sprite2D
var alert_mark: Label

var type: String = "slime"
var base_color: Color = Color.WHITE

func _ready() -> void:
	collision_layer = LAYER_ENEMY
	collision_mask = MASK_WALLS | MASK_DOORS
	z_index = 2

	# Sprite placeholder: el icono de Godot reducido a 16x16
	sprite = Sprite2D.new()
	sprite.texture = load("res://icon.svg")
	sprite.scale = Vector2(0.125, 0.125)
	add_child(sprite)

	# Cuerpo físico
	var body := CollisionShape2D.new()
	var circle := CircleShape2D.new()
	circle.radius = 6.0
	body.shape = circle
	add_child(body)

	# Área de contacto con el jugador
	var area := Area2D.new()
	area.collision_layer = 0
	area.collision_mask = MASK_PLAYER
	var area_shape := CollisionShape2D.new()
	var area_circle := CircleShape2D.new()
	area_circle.radius = 10.0
	area_shape.shape = area_circle
	area.add_child(area_shape)
	add_child(area)
	area.body_entered.connect(_on_body_entered)

	# Marca de alerta
	alert_mark = Label.new()
	alert_mark.text = "!"
	alert_mark.position = Vector2(-3, -26)
	alert_mark.visible = false
	add_child(alert_mark)

# Llamar DESPUÉS de add_child
func setup(room: int, player: Node2D, pos: Vector2, enemy_type: String = "slime") -> void:
	room_id = room
	target = player
	home = pos
	type = enemy_type
	global_position = pos
	base_color = EnemyTypes.get_stats(type)["color"]
	sprite.modulate = base_color
	add_to_group("enemies_room_%d" % room_id)

func set_active(value: bool) -> void:
	active = value
	if not value:
		reset()

func reset() -> void:
	state = State.IDLE
	velocity = Vector2.ZERO
	global_position = home
	sprite.modulate = base_color
	alert_mark.visible = false

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
			velocity = dir * chase_speed
			move_and_slide()

# Visión: dentro del radio y sin pared en medio
func _can_see_player() -> bool:
	if global_position.distance_to(target.global_position) > sight_radius:
		return false
	var query := PhysicsRayQueryParameters2D.create(
			global_position, target.global_position, MASK_WALLS)
	var hit := get_world_2d().direct_space_state.intersect_ray(query)
	return hit.is_empty()

func alert() -> void:
	if state == State.CHASE:
		return
	state = State.CHASE
	sprite.modulate = Color(1.0, 0.4, 0.4)
	alert_mark.visible = true

func _on_body_entered(body: Node2D) -> void:
	if active and body.is_in_group("player"):
		caught.emit(self)
