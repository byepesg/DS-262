@tool
extends Marker2D

enum View { FRONT, BACK, LEFT, RIGHT }   # mismo orden que en door.gd

@export var view: View = View.FRONT:
	set(value):
		view = value
		queue_redraw()

# Tamaño total de tus salas en píxeles (AJÚSTALO)
const ROOM_SIZE := Vector2(480, 270)

@export_tool_button("Vista automática") var _auto = auto_view

func auto_view() -> void:
	# Asume origen de la sala en la esquina superior izquierda
	var c := position - ROOM_SIZE / 2.0
	var nx := absf(c.x) / (ROOM_SIZE.x / 2.0)
	var ny := absf(c.y) / (ROOM_SIZE.y / 2.0)
	if nx > ny:
		view = View.RIGHT if c.x > 0 else View.LEFT
	else:
		view = View.BACK if c.y > 0 else View.FRONT

func _draw() -> void:
	if not Engine.is_editor_hint():
		return
	var dir := Vector2.UP
	match view:
		View.BACK:
			dir = Vector2.DOWN
		View.LEFT:
			dir = Vector2.LEFT
		View.RIGHT:
			dir = Vector2.RIGHT
	draw_line(Vector2.ZERO, dir * 12.0, Color.YELLOW, 1.5)
	draw_circle(dir * 12.0, 2.0, Color.YELLOW)
