class_name FoeView
extends Node2D

# Enemigo del combate. Las animaciones faltantes usan el frame de reposo
# hasta que cada tipo tenga sus propios SpriteFrames.

@onready var sprite: AnimatedSprite2D = %Sprite
@onready var name_label: Label = %NameLabel
@onready var hp_bar: ProgressBar = %HPBar
@onready var hp_label: Label = %HPLabel
@onready var attack_button: Button = %AttackButton
@onready var arrow: Polygon2D = %Arrow

var tint: Color = Color.WHITE
var max_hp: int = 1
var last_hp: int = 1
var alive: bool = true
var _sprite_base: Vector2

func _ready() -> void:
	_sprite_base = sprite.position
	attack_button.mouse_entered.connect(func(): arrow.visible = not attack_button.disabled)
	attack_button.mouse_exited.connect(func(): arrow.visible = false)
	attack_button.pressed.connect(func(): arrow.visible = false)
	_play_animation("idle")
	_start_idle()

func setup(display_name: String, p_max_hp: int, hp: int, color: Color,
		frames: SpriteFrames) -> void:
	name_label.text = display_name
	max_hp = p_max_hp
	last_hp = hp
	tint = color
	sprite.sprite_frames = frames
	sprite.modulate = tint
	hp_bar.max_value = max_hp
	hp_bar.value = hp
	hp_label.text = "%d/%d" % [hp, max_hp]
	_play_animation("idle")

# Llamar cada vez que cambie la vida: anima solo si bajó
func update_hp(hp: int) -> void:
	hp = maxi(hp, 0)
	hp_bar.value = hp
	hp_label.text = "%d/%d" % [hp, max_hp]
	if hp < last_hp:
		if hp == 0:
			play_death()
		else:
			play_hit()
	last_hp = hp

func _start_idle() -> void:
	await get_tree().create_timer(randf() * 0.6).timeout
	if not alive:
		return
	var t := create_tween().set_loops()
	t.tween_property(sprite, "position:y", _sprite_base.y - 1.5, 0.45).set_trans(Tween.TRANS_SINE)
	t.tween_property(sprite, "position:y", _sprite_base.y + 1.5, 0.45).set_trans(Tween.TRANS_SINE)

func play_hit() -> void:
	_play_animation("hurt")
	sprite.modulate = Color(2.5, 2.5, 2.5)
	create_tween().tween_property(sprite, "modulate", tint, 0.25)
	var s := create_tween()
	for dx in [-4.0, 4.0, -3.0, 3.0, 0.0]:
		s.tween_property(sprite, "position:x", _sprite_base.x + dx, 0.04)

# Embestida hacia el jugador (a la izquierda)
func play_attack() -> void:
	_play_animation("attack")
	var x0 := position.x
	var t := create_tween()
	t.tween_property(self, "position:x", x0 - 18.0, 0.12).set_trans(Tween.TRANS_QUAD)
	t.tween_property(self, "position:x", x0, 0.2)

func play_death() -> void:
	alive = false
	_play_animation("death")
	var t := create_tween().set_parallel(true)
	t.tween_property(self, "modulate:a", 0.0, 0.5)
	t.tween_property(sprite, "position:y", _sprite_base.y + 8.0, 0.5)

func _play_animation(animation_name: String) -> void:
	if sprite.sprite_frames == null:
		return
	if sprite.sprite_frames.has_animation(animation_name):
		sprite.play(animation_name)
	elif sprite.sprite_frames.has_animation("idle"):
		sprite.play("idle")
