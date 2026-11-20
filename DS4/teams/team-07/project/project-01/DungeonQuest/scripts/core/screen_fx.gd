class_name ScreenFX
extends Node

const TOAST_FONT := preload("res://assets/fonts/alagard.ttf")

var _transition_layer: CanvasLayer
var _transition_bars: Array[ColorRect] = []

func _init() -> void:
	process_mode = Node.PROCESS_MODE_ALWAYS

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

func close_transition(duration: float = 0.4, stagger: float = 0.024,
		flash_duration: float = 0.0) -> void:
	_clear_transition()
	var size := get_viewport().get_visible_rect().size
	_transition_layer = CanvasLayer.new()
	_transition_layer.layer = 120
	_transition_layer.process_mode = Node.PROCESS_MODE_ALWAYS
	add_child(_transition_layer)

	var count := 12
	var band_height := ceilf(size.y / count) + 1.0
	for i in count:
		var bar := ColorRect.new()
		bar.color = Color.BLACK
		bar.position = Vector2(-size.x if i % 2 == 0 else size.x, i * size.y / count)
		bar.size = Vector2(size.x, band_height)
		bar.mouse_filter = Control.MOUSE_FILTER_IGNORE
		_transition_layer.add_child(bar)
		_transition_bars.append(bar)

	var wipe := create_tween().set_parallel(true)
	for i in _transition_bars.size():
		wipe.tween_property(_transition_bars[i], "position:x", 0.0, duration) \
				.set_delay(i * stagger).set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	await wipe.finished
	if flash_duration > 0.0:
		await _transition_flash(flash_duration)

func open_transition(duration: float = 0.38, stagger: float = 0.02,
		flash_duration: float = 0.0) -> void:
	if _transition_bars.is_empty():
		_clear_transition()
		return
	if flash_duration > 0.0:
		await _transition_flash(flash_duration)
	var size := get_viewport().get_visible_rect().size
	var wipe := create_tween().set_parallel(true)
	for i in _transition_bars.size():
		var side := -size.x if i % 2 == 0 else size.x
		wipe.tween_property(_transition_bars[i], "position:x", side, duration) \
				.set_delay((_transition_bars.size() - i) * stagger) \
				.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_IN)
	await wipe.finished
	_clear_transition()

func _transition_flash(duration: float) -> void:
	var flash := ColorRect.new()
	flash.color = Color(1.0, 0.93, 0.78, 0.82)
	flash.mouse_filter = Control.MOUSE_FILTER_IGNORE
	flash.position = Vector2.ZERO
	flash.size = get_viewport().get_visible_rect().size
	_transition_layer.add_child(flash)
	var tween := create_tween()
	tween.tween_property(flash, "modulate:a", 0.0, duration)
	await tween.finished
	flash.queue_free()

func _clear_transition() -> void:
	if is_instance_valid(_transition_layer):
		_transition_layer.queue_free()
	_transition_layer = null
	_transition_bars.clear()
