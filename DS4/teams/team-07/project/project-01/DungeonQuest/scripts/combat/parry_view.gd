class_name ParryView
extends Control

# Dibuja un aro fijo (el objetivo) y un aro que se cierra hacia él.
# El golpe cae cuando el aro móvil toca el objetivo.
# El aro se vuelve verde dentro de la ventana de parry perfecto.

const TARGET_RADIUS := 18.0
const START_EXTRA := 50.0

var active: bool = false
var head_time: float = -1.0     # instante del próximo golpe (-1 si no hay)
var now: float = 0.0
var lead: float = 0.8           # segundos que tarda el aro en cerrarse
var perfect: float = 0.12

func _draw() -> void:
	var c: Vector2 = size / 2.0
	draw_arc(c, TARGET_RADIUS, 0.0, TAU, 32, Color(1, 1, 1, 0.9), 2.0)
	if not active or head_time < 0.0:
		return
	var remaining: float = head_time - now
	var r: float = TARGET_RADIUS + clampf(remaining, 0.0, lead) / lead * START_EXTRA
	var col := Color(1.0, 0.85, 0.2)
	if absf(remaining) <= perfect:
		col = Color(0.3, 1.0, 0.4)
	draw_arc(c, r, 0.0, TAU, 48, col, 3.0)
