class_name BattleEffects
extends Node2D

const COLORS := {
	"physical": Color(1.0, 0.92, 0.72),
	"fire": Color(1.0, 0.36, 0.12),
	"healing": Color(0.38, 1.0, 0.68),
	"lightning": Color(0.48, 0.82, 1.0),
	"parry": Color(1.0, 0.82, 0.3),
}
const LABEL_FONT := preload("res://assets/fonts/alagard.ttf")

var _shake_tween: Tween

func damage(world_position: Vector2, amount: int, effect: StringName = &"physical") -> void:
	var color: Color = COLORS.get(str(effect), COLORS["physical"])
	var position := to_local(world_position)
	_spawn_burst(position, color)
	_spawn_float(position, "-%d" % amount, color)
	_spawn_sparks(position, color, 9)
	_shake(5.0)

func heal(world_position: Vector2, amount: int) -> void:
	var color: Color = COLORS["healing"]
	var position := to_local(world_position)
	_spawn_burst(position, color)
	_spawn_float(position, "+%d" % amount, color)
	_spawn_sparks(position, color, 7)

func death(world_position: Vector2) -> void:
	var position := to_local(world_position)
	var color := Color("f05a74")
	_spawn_burst(position, color)
	_spawn_sparks(position, color, 12)
	_shake(8.0)

func _spawn_burst(position: Vector2, color: Color) -> void:
	var burst := Polygon2D.new()
	burst.polygon = _star_polygon(20.0, 7.0, 10)
	burst.position = position
	burst.color = color
	burst.modulate.a = 0.95
	burst.scale = Vector2(0.12, 0.12)
	burst.z_index = 10
	add_child(burst)

	var tween := create_tween().set_parallel(true)
	tween.tween_property(burst, "scale", Vector2(1.55, 1.55), 0.24) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	tween.tween_property(burst, "rotation", deg_to_rad(35.0), 0.24)
	tween.tween_property(burst, "modulate:a", 0.0, 0.24)
	tween.finished.connect(burst.queue_free)

	var ring := Line2D.new()
	for i in 25:
		ring.add_point(Vector2.from_angle(TAU * i / 24.0) * 14.0)
	ring.width = 3.0
	ring.default_color = color
	ring.position = position
	ring.scale = Vector2(0.15, 0.15)
	ring.z_index = 9
	add_child(ring)
	var ring_tween := create_tween().set_parallel(true)
	ring_tween.tween_property(ring, "scale", Vector2(2.2, 2.2), 0.28)
	ring_tween.tween_property(ring, "modulate:a", 0.0, 0.28)
	ring_tween.finished.connect(ring.queue_free)

func _spawn_sparks(position: Vector2, color: Color, count: int) -> void:
	for i in count:
		var angle := TAU * float(i) / count
		var direction := Vector2.from_angle(angle)
		var distance := 16.0 + float(i % 3) * 6.0
		var spark := Polygon2D.new()
		spark.polygon = PackedVector2Array([
			Vector2(0.0, -3.0), Vector2(2.0, 0.0),
			Vector2(0.0, 3.0), Vector2(-2.0, 0.0),
		])
		spark.position = position
		spark.color = color
		spark.z_index = 11
		add_child(spark)

		var tween := create_tween().set_parallel(true)
		tween.tween_property(spark, "position", position + direction * distance, 0.28) \
				.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
		tween.tween_property(spark, "scale", Vector2(0.2, 0.2), 0.28)
		tween.tween_property(spark, "modulate:a", 0.0, 0.28)
		tween.finished.connect(spark.queue_free)

func _spawn_float(position: Vector2, text: String, color: Color) -> void:
	var label := Label.new()
	label.text = text
	label.position = position + Vector2(-24.0, -25.0)
	label.size = Vector2(56.0, 24.0)
	label.z_index = 13
	label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	label.vertical_alignment = VERTICAL_ALIGNMENT_CENTER
	label.add_theme_font_override("font", LABEL_FONT)
	label.add_theme_font_size_override("font_size", 16)
	label.add_theme_color_override("font_color", color)
	label.add_theme_color_override("font_shadow_color", Color(0.05, 0.03, 0.08, 0.9))
	label.add_theme_constant_override("shadow_offset_x", 1)
	label.add_theme_constant_override("shadow_offset_y", 1)
	add_child(label)

	var tween := create_tween().set_parallel(true)
	tween.tween_property(label, "position:y", label.position.y - 25.0, 0.55) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	tween.tween_property(label, "scale", Vector2(1.25, 1.25), 0.12) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	tween.tween_property(label, "modulate:a", 0.0, 0.55).set_delay(0.16)
	tween.finished.connect(label.queue_free)

func _shake(intensity: float) -> void:
	var stage := get_parent() as Node2D
	if stage == null:
		return
	if _shake_tween != null and _shake_tween.is_running():
		_shake_tween.kill()
	stage.position = Vector2.ZERO
	_shake_tween = create_tween()
	for offset in [Vector2(-intensity, 2.0), Vector2(intensity, -2.0),
			Vector2(-intensity * 0.65, -1.0), Vector2(intensity * 0.4, 1.0)]:
		_shake_tween.tween_property(stage, "position", offset, 0.035)
	_shake_tween.tween_property(stage, "position", Vector2.ZERO, 0.08) \
			.set_trans(Tween.TRANS_ELASTIC)

func _star_polygon(outer_radius: float, inner_radius: float, points: int) -> PackedVector2Array:
	var polygon := PackedVector2Array()
	for i in points * 2:
		var radius := outer_radius if i % 2 == 0 else inner_radius
		polygon.append(Vector2.from_angle(TAU * float(i) / (points * 2)) * radius)
	return polygon
