class_name ComboArrowDisplay
extends Control

const COLORS := {
	"physical": Color("d18bff"),
	"fire": Color("ff9b43"),
	"healing": Color("adff9b"),
	"lightning": Color("8bddff"),
	"parry": Color("ffe47d"),
}
const FAILURE_FONT := preload("res://assets/fonts/alagard.ttf")
const ARROW_SIZE := Vector2(36.0, 40.0)
const GAP := 10.0

var entered: Array[String] = []
var result_color: Color = Color.WHITE
var complete: bool = false
var broken: bool = false
var failure_label: String = "ROTO"
var failure_color: Color = Color("ff657b")
var _pulse: float = 0.0
var _pulse_duration: float = 0.24

func _process(delta: float) -> void:
	if _pulse > 0.0:
		_pulse = maxf(0.0, _pulse - delta / _pulse_duration)
		queue_redraw()

func show_input(keys: Array) -> void:
	entered.clear()
	for key in keys:
		entered.append(str(key))
	complete = false
	broken = false
	failure_label = "ROTO"
	failure_color = Color("ff657b")
	result_color = Color.WHITE
	_pulse = 0.0
	queue_redraw()

func show_result(keys: Array, color: Color) -> void:
	show_input(keys)
	complete = true
	result_color = color
	_pulse = 1.0
	_pulse_duration = 0.24
	queue_redraw()

func show_failure(keys: Array, label: String, color: Color) -> void:
	show_input(keys)
	broken = true
	failure_label = label
	failure_color = color
	result_color = color
	_pulse = 1.0
	_pulse_duration = 0.38
	queue_redraw()

func _draw() -> void:
	var keys := entered.duplicate()
	var colors: Array[Color] = []
	for _i in entered.size():
		colors.append(result_color if complete or broken else Color("fff0bc"))

	if keys.is_empty():
		if not broken:
			return

	if not keys.is_empty():
		var total_width := keys.size() * ARROW_SIZE.x + maxi(0, keys.size() - 1) * GAP
		var start_x := (size.x - total_width) / 2.0
		var center_y := size.y * 0.52
		var arrow_points := PackedVector2Array([
			Vector2(15.0, 0.0), Vector2(30.0, 15.0), Vector2(21.0, 15.0),
			Vector2(21.0, 34.0), Vector2(9.0, 34.0), Vector2(9.0, 15.0),
			Vector2(0.0, 15.0),
		])
		for i in keys.size():
			var color := colors[i]
			var arrow_size := ARROW_SIZE * (1.0 + _pulse * 0.28 if complete else 1.0)
			var center := Vector2(start_x + i * (ARROW_SIZE.x + GAP) + ARROW_SIZE.x / 2.0,
					center_y)
			var rotation := _rotation_for(keys[i])
			var transform := Transform2D(rotation, center)
			var points := PackedVector2Array()
			for point in arrow_points:
				var scaled_point := point * (arrow_size / ARROW_SIZE) - arrow_size / 2.0
				points.append(transform * scaled_point)
			draw_colored_polygon(points, Color(color, color.a))
			draw_polyline(_closed_points(points), Color(0.02, 0.01, 0.04, color.a), 2.0, true)
			if complete and _pulse > 0.01:
				draw_arc(center, 25.0 + _pulse * 8.0, 0.0, TAU, 32,
						Color(result_color, _pulse * 0.75), 2.0, true)

	if broken:
		_draw_cracks(failure_color)
		draw_string(FAILURE_FONT, Vector2(0.0, 14.0), failure_label,
				HORIZONTAL_ALIGNMENT_CENTER, size.x, 13, Color(failure_color, _pulse))

func _draw_cracks(color: Color) -> void:
	var w := size.x
	var h := size.y
	var cracks: Array[PackedVector2Array] = [
		PackedVector2Array([
			Vector2(w * 0.52, -4), Vector2(w * 0.45, h * 0.22),
			Vector2(w * 0.57, h * 0.39), Vector2(w * 0.48, h * 0.58),
			Vector2(w * 0.59, h * 0.78), Vector2(w * 0.53, h + 4),
		]),
		PackedVector2Array([
			Vector2(w * 0.47, h * 0.23), Vector2(w * 0.31, h * 0.18),
			Vector2(w * 0.24, h * 0.04),
		]),
		PackedVector2Array([
			Vector2(w * 0.56, h * 0.4), Vector2(w * 0.72, h * 0.34),
			Vector2(w * 0.82, h * 0.16),
		]),
		PackedVector2Array([
			Vector2(w * 0.49, h * 0.58), Vector2(w * 0.34, h * 0.69),
			Vector2(w * 0.27, h * 0.9),
		]),
		PackedVector2Array([
			Vector2(w * 0.58, h * 0.78), Vector2(w * 0.75, h * 0.84),
			Vector2(w * 0.86, h * 0.98),
		]),
	]
	for crack in cracks:
		draw_polyline(crack, Color(0.16, 0.025, 0.07, _pulse * 0.9), 5.0, true)
		draw_polyline(crack, Color(color, _pulse), 1.5, true)

func _rotation_for(key: String) -> float:
	match key:
		"right":
			return PI / 2.0
		"down":
			return PI
		"left":
			return -PI / 2.0
	return 0.0

func _offset_points(points: PackedVector2Array, offset: Vector2) -> PackedVector2Array:
	var result := PackedVector2Array()
	for point in points:
		result.append(point + offset)
	return result

func _closed_points(points: PackedVector2Array) -> PackedVector2Array:
	var result := points.duplicate()
	if not points.is_empty():
		result.append(points[0])
	return result
