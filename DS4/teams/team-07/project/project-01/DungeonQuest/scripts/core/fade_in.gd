class_name FadeIn

# Entra desde negro. Se llama una vez en el _ready() de la pantalla.
static func play(host: Node, duration: float = 0.8) -> void:
	var layer := CanvasLayer.new()
	layer.layer = 200
	layer.process_mode = Node.PROCESS_MODE_ALWAYS
	var rect := ColorRect.new()
	rect.color = Color.BLACK
	rect.mouse_filter = Control.MOUSE_FILTER_IGNORE
	rect.set_anchors_preset(Control.PRESET_FULL_RECT)
	layer.add_child(rect)
	host.add_child(layer)
	var t := layer.create_tween()
	t.tween_property(rect, "color:a", 0.0, duration)
	t.tween_callback(layer.queue_free)
