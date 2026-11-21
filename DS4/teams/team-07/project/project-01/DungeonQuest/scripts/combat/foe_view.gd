class_name FoeView
extends Node2D

# Enemigo del combate. Las animaciones faltantes usan el frame de reposo.

@onready var sprite: AnimatedSprite2D = %Sprite
@onready var name_label: Label = %NameLabel
@onready var hp_bar: ProgressBar = %HPBar
@onready var hp_label: Label = %HPLabel
@onready var attack_button: Button = %AttackButton
@onready var arrow: Polygon2D = %Arrow

const COMBAT_SCALE := 4.945   

var max_hp: int = 1
var alive: bool = true
var _sprite_base: Vector2
var _sprite_scale: Vector2
var _hit_stopped: bool = false
var battle_effects: BattleEffects
var _motion_tween: Tween
var _flash_tween: Tween

func _ready() -> void:
	_sprite_base = sprite.position
	_sprite_scale = sprite.scale
	attack_button.mouse_entered.connect(func(): arrow.visible = not attack_button.disabled)
	attack_button.mouse_exited.connect(func(): arrow.visible = false)
	attack_button.pressed.connect(func(): arrow.visible = false)
	_play_idle()

func setup(display_name: String, p_max_hp: int, hp: int, frames: SpriteFrames,
		effects: BattleEffects) -> void:
	name_label.text = display_name
	max_hp = p_max_hp
	battle_effects = effects
	sprite.sprite_frames = frames
	_fit_sprite()
	sprite.modulate = Color.WHITE
	hp_bar.max_value = max_hp
	hp_bar.value = hp
	hp_label.text = "%d/%d" % [hp, max_hp]
	_play_idle()

func _fit_sprite() -> void:
	if sprite.sprite_frames == null:
		return
	var tex := sprite.sprite_frames.get_frame_texture("idle", 0)
	if tex != null:
		var s := COMBAT_SCALE * 16.0 / float(tex.get_width())
		sprite.scale = Vector2(s, s)
		
func update_hp(hp: int) -> void:
	hp = maxi(hp, 0)
	hp_bar.value = hp
	hp_label.text = "%d/%d" % [hp, max_hp]

func play_damage(amount: int, effect: StringName, lethal: bool) -> void:
	_reset_visuals()
	battle_effects.damage(sprite.global_position, amount, effect)
	if lethal:
		play_death()
	else:
		_play_idle()
		_animate_hurt()
		_hit_stop()

func _reset_visuals() -> void:
	if _motion_tween != null and _motion_tween.is_running():
		_motion_tween.kill()
	if _flash_tween != null and _flash_tween.is_running():
		_flash_tween.kill()
	sprite.position = _sprite_base
	sprite.scale = _sprite_scale
	sprite.rotation = 0.0
	sprite.modulate = Color.WHITE

func _animate_hurt() -> void:
	var direction := -1.0 if global_position.x > 240.0 else 1.0
	var t := create_tween()
	_motion_tween = t
	t.tween_property(sprite, "scale", _sprite_scale * Vector2(1.8, 0.48), 0.07) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	t.parallel().tween_property(sprite, "position:x", _sprite_base.x + direction * 13.0, 0.07)
	t.parallel().tween_property(sprite, "rotation", deg_to_rad(direction * 24.0), 0.07)
	t.tween_property(sprite, "scale", _sprite_scale * Vector2(0.68, 1.55), 0.08) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	t.parallel().tween_property(sprite, "position:x", _sprite_base.x - direction * 6.0, 0.08)
	t.parallel().tween_property(sprite, "rotation", deg_to_rad(-direction * 17.0), 0.08)
	for shake in [8.0, -7.0, 5.0, -3.0, 0.0]:
		t.tween_property(sprite, "position:x", _sprite_base.x + shake, 0.035)
	t.tween_property(sprite, "scale", _sprite_scale, 0.14) \
			.set_trans(Tween.TRANS_ELASTIC).set_ease(Tween.EASE_OUT)
	t.parallel().tween_property(sprite, "position", _sprite_base, 0.14)
	t.parallel().tween_property(sprite, "rotation", 0.0, 0.14)

	_flash_tween = create_tween()
	_flash_tween.tween_property(sprite, "modulate", Color(2.5, 2.2, 1.8), 0.045)
	_flash_tween.tween_property(sprite, "modulate", Color(1.0, 0.28, 0.22), 0.06)
	_flash_tween.tween_property(sprite, "modulate", Color.WHITE, 0.2)

# Embestida hacia el jugador (a la izquierda)
func play_attack() -> void:
	if sprite.sprite_frames != null and sprite.sprite_frames.has_animation("attack"):
		sprite.play("attack")
	else:
		_play_idle()
	var x0 := position.x
	var t := create_tween()
	t.tween_property(self, "position:x", x0 - 30.0, 0.1).set_trans(Tween.TRANS_BACK)
	t.parallel().tween_property(sprite, "scale", _sprite_scale * Vector2(1.45, 0.72), 0.1)
	t.tween_property(self, "position:x", x0 + 8.0, 0.07).set_trans(Tween.TRANS_BACK)
	t.parallel().tween_property(sprite, "scale", _sprite_scale * Vector2(0.72, 1.38), 0.07)
	t.tween_property(self, "position:x", x0, 0.16).set_trans(Tween.TRANS_ELASTIC)
	t.parallel().tween_property(sprite, "scale", _sprite_scale, 0.16).set_trans(Tween.TRANS_ELASTIC)
	await t.finished
	_play_idle()

func play_death() -> void:
	alive = false
	_reset_visuals()
	_play_idle()
	var t := create_tween()
	t.tween_interval(0.12)
	t.tween_property(sprite, "scale", _sprite_scale * Vector2(1.9, 0.32), 0.1) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	t.parallel().tween_property(sprite, "rotation", deg_to_rad(20.0), 0.1)
	t.tween_property(sprite, "scale", _sprite_scale * Vector2(1.6, 1.7), 0.12) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	t.parallel().tween_property(sprite, "position:y", _sprite_base.y - 22.0, 0.12)
	t.tween_property(sprite, "scale", Vector2.ZERO, 0.34) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_IN)
	t.parallel().tween_property(sprite, "position:y", _sprite_base.y + 34.0, 0.34)
	t.parallel().tween_property(sprite, "rotation", deg_to_rad(110.0), 0.34)
	t.parallel().tween_property(sprite, "modulate:a", 0.0, 0.34)
	t.parallel().tween_property(self, "modulate:a", 0.0, 0.34)

func _play_idle() -> void:
	if sprite.sprite_frames == null:
		return
	if sprite.sprite_frames.has_animation("idle"):
		sprite.play("idle")

func _hit_stop() -> void:
	if _hit_stopped or not sprite.is_playing():
		return
	_hit_stopped = true
	sprite.pause()
	await get_tree().create_timer(0.045, true).timeout
	_hit_stopped = false
	if is_instance_valid(sprite) and alive:
		_play_idle()
