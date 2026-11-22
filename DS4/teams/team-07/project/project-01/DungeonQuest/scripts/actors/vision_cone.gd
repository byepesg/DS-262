class_name VisionCone
extends Node2D

var sight_radius: float = 110.0
var angle_degrees: float = 90.0
var facing_direction: Vector2 = Vector2.RIGHT

func configure(radius: float, angle: float, direction: Vector2) -> void:
	sight_radius = radius
	angle_degrees = angle
	if not direction.is_zero_approx():
		facing_direction = direction.normalized()
	queue_redraw()

func _draw() -> void:
	if sight_radius <= 0.0:
		return
	var half_angle := deg_to_rad(angle_degrees * 0.5)
	var center_angle := facing_direction.angle()
	var start_angle := center_angle - half_angle
	var end_angle := center_angle + half_angle
	var points := PackedVector2Array([Vector2.ZERO])
	const SEGMENTS := 24
	for i in range(SEGMENTS + 1):
		var t := float(i) / SEGMENTS
		points.append(Vector2.from_angle(lerpf(start_angle, end_angle, t)) * sight_radius)
	draw_colored_polygon(points, Color(0.88, 0.8, 0.3, 0.12))
	draw_arc(Vector2.ZERO, sight_radius, start_angle, end_angle, SEGMENTS,
			Color(0.95, 0.86, 0.38, 0.38), 1.0, true)
	draw_line(Vector2.ZERO, Vector2.from_angle(start_angle) * sight_radius,
			Color(0.95, 0.86, 0.38, 0.24), 1.0, true)
	draw_line(Vector2.ZERO, Vector2.from_angle(end_angle) * sight_radius,
			Color(0.95, 0.86, 0.38, 0.24), 1.0, true)
