class_name ScreenFX
extends Node

const TOAST_FONT := preload("res://assets/fonts/alagard.ttf")

var fade: ColorRect

func _ready() -> void:
	var layer := CanvasLayer.new()
	layer.layer = 100
	add_child(layer)
	fade = ColorRect.new()
	fade.color = Color(0, 0, 0, 1)
	fade.mouse_filter = Control.MOUSE_FILTER_IGNORE
	fade.set_anchors_preset(Control.PRESET_FULL_RECT)
	fade.modulate.a = 0.0
	layer.add_child(fade)

# Aviso breve en la parte baja de la pantalla
func toast(text: String) -> void:
	var layer := CanvasLayer.new()
	layer.layer = 80
	add_child(layer)
	var label := Label.new()
	label.text = text + "\n"
	label.set_anchors_preset(Control.PRESET_FULL_RECT)
	label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	label.vertical_alignment = VERTICAL_ALIGNMENT_BOTTOM

	# Fuente y estilo del aviso
	label.add_theme_font_override("font", TOAST_FONT)
	label.add_theme_font_size_override("font_size", 12)
	label.add_theme_color_override("font_outline_color", Color.BLACK)
	label.add_theme_constant_override("outline_size", 3)

	layer.add_child(label)
	var t := create_tween()
	t.tween_interval(2.0)
	t.tween_property(label, "modulate:a", 0.0, 0.8)
	t.tween_callback(layer.queue_free)
