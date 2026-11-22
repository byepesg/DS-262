class_name ParryView
extends Control

const TARGET_RADIUS := 20.0
const START_EXTRA := 62.0
const PARRY_FONT := preload("res://assets/fonts/alagard.ttf")
const READY_COLOR := Color("fbb954")
const PERFECT_COLOR := Color("f2ec8b")
const MISS_COLOR := Color("c100bc")
const KEY_FILL := Color("f5bd4b")
const KEY_TEXT := Color("190f1c")

var active: bool = false
var head_time: float = -1.0
var now: float = 0.0
var lead: float = 0.8
var perfect: float = 0.12
var _result_text: String = ""
var _result_color: Color = Color.WHITE
var _result_alpha: float = 0.0
var _result_scale: float = 1.0
var _result_rotation: float = 0.0
var _impact_alpha: float = 0.0
var _impact_scale: float = 1.0
var _impact_rotation: float = 0.0
var _result_tween: Tween

func _draw() -> void:
	var center := size / 2.0
	_draw_target(center)
	if active and head_time >= 0.0:
		_draw_incoming_ring(center)
	var result_center := center + Vector2(0.0, -47.0)
	if _impact_alpha > 0.01:
		_draw_result_impact(result_center)
	if _result_alpha > 0.01:
		draw_set_transform(result_center, _result_rotation, Vector2.ONE * _result_scale)
		draw_string(PARRY_FONT, Vector2(-100.0, 6.0), _result_text,
				HORIZONTAL_ALIGNMENT_CENTER, 200.0, 18,
				Color(0.06, 0.025, 0.035, _result_alpha * 0.9))
		draw_string(PARRY_FONT, Vector2(-102.0, 3.0), _result_text,
				HORIZONTAL_ALIGNMENT_CENTER, 200.0, 18,
				Color(_result_color, _result_alpha))

func _process(_delta: float) -> void:
	if _result_tween != null and _result_tween.is_running():
		queue_redraw()

func _draw_result_impact(center: Vector2) -> void:
	var burst := PackedVector2Array()
	for i in 16:
		var angle := TAU * float(i) / 16.0 + _impact_rotation
		var radius := 38.0 if i % 2 == 0 else 22.0
		burst.append(center + Vector2(cos(angle) * radius * 2.3,
				sin(angle) * radius * 0.72) * _impact_scale)
	draw_colored_polygon(burst, Color(_result_color, _impact_alpha * 0.3))

	for i in 12:
		var angle := TAU * float(i) / 12.0 + _impact_rotation
		var direction := Vector2(cos(angle), sin(angle))
		var inner := center + Vector2(direction.x * 91.0, direction.y * 30.0) * _impact_scale
		var outer := center + Vector2(direction.x * 108.0, direction.y * 44.0) * _impact_scale
		var alpha := _impact_alpha * (0.8 if i % 2 == 0 else 0.48)
		draw_line(inner, outer, Color(_result_color, alpha),
				2.5 if i % 3 == 0 else 1.5, true)

func _draw_target(center: Vector2) -> void:
	draw_circle(center, TARGET_RADIUS + 7.0, Color(0.025, 0.018, 0.04, 0.9))
	var ready := active and head_time >= 0.0 \
			and absf(head_time - now) <= perfect
	var color := PERFECT_COLOR if ready else Color("f5dd9b")
	var alpha := 0.95 if ready else 0.55
	draw_arc(center, TARGET_RADIUS, 0.0, TAU, 48, Color(color, alpha), 3.0, true)
	draw_arc(center, TARGET_RADIUS - 5.0, 0.0, TAU, 48,
			Color(color, alpha * 0.32), 1.0, true)

	for i in 12:
		var angle := TAU * float(i) / 12.0
		var direction := Vector2.from_angle(angle)
		draw_line(center + direction * 24.0, center + direction * 29.0,
				Color(color, alpha * 0.8), 2.0, true)

	var key_rect := Rect2(center + Vector2(-26.0, -11.0), Vector2(52.0, 22.0))
	draw_rect(key_rect.grow(2.0), Color(0.03, 0.02, 0.04, 0.92), true)
	draw_rect(key_rect, KEY_FILL, true)
	draw_rect(key_rect, Color(color, alpha), false, 2.0)
	draw_string(PARRY_FONT, center + Vector2(-18.0, 4.0), "SPACE",
			HORIZONTAL_ALIGNMENT_CENTER, 36.0, 9, KEY_TEXT)

func _draw_incoming_ring(center: Vector2) -> void:
	var remaining := head_time - now
	var fraction := clampf(remaining / maxf(lead, 0.001), 0.0, 1.0)
	var radius := TARGET_RADIUS + fraction * START_EXTRA
	var color := READY_COLOR
	if absf(remaining) <= perfect:
		color = PERFECT_COLOR
	elif remaining < 0.0:
		color = MISS_COLOR

	draw_arc(center, radius + 5.0, 0.0, TAU, 48, Color(color, 0.18), 6.0, true)
	draw_arc(center, radius, 0.0, TAU, 64, color, 3.0, true)

	var marker_angle := -PI / 2.0 + now * 2.8
	var marker := center + Vector2.from_angle(marker_angle) * radius
	draw_circle(marker, 5.0, Color(0.025, 0.018, 0.04, 0.95))
	draw_circle(marker, 3.0, color)

	var sweep_angle := TAU * fraction
	draw_arc(center, START_EXTRA + TARGET_RADIUS + 10.0,
			-PI / 2.0, -PI / 2.0 + sweep_angle,
			64, Color(color, 0.75), 2.0, true)

func show_result(grade: String) -> void:
	match grade:
		"perfect":
			_result_text = "¡PERFECTO!"
			_result_color = PERFECT_COLOR
		"good":
			_result_text = "¡BLOQUEO!"
			_result_color = READY_COLOR
		_:
			_result_text = "FALLO"
			_result_color = MISS_COLOR

	if _result_tween != null and _result_tween.is_running():
		_result_tween.kill()
	_result_alpha = 1.0
	_result_scale = 1.8
	_result_rotation = deg_to_rad(-7.0)
	_impact_alpha = 1.0
	_impact_scale = 0.25
	_impact_rotation = deg_to_rad(-10.0)
	_result_tween = create_tween().set_parallel(true)
	_result_tween.tween_property(self, "_result_scale", 1.0, 0.24) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	_result_tween.tween_property(self, "_result_rotation", 0.0, 0.22) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	_result_tween.tween_property(self, "_impact_scale", 1.0, 0.28) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	_result_tween.tween_property(self, "_impact_rotation", 0.0, 0.32) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	_result_tween.tween_property(self, "_impact_alpha", 0.0, 0.4) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	_result_tween.tween_property(self, "_result_alpha", 0.0, 0.35).set_delay(1.7)
	queue_redraw()
