extends Node

var proxima_escena: String = ""
var animate_from_current: bool = false
var fade_transition: bool = false

func _ready() -> void:
	process_mode = Node.PROCESS_MODE_ALWAYS
	$FadeLayer.layer = 200   # por encima de cualquier pantalla (SkillScreen usa 70)
	var was_paused := get_tree().paused
	get_tree().paused = true

	var viewport_size := get_viewport().get_visible_rect().size
	var previous_scene := get_tree().current_scene
	var strips: Array[ColorRect] = []
	var cover: ColorRect
	var fade_cover: ColorRect
	if fade_transition:
		fade_cover = _create_fade_cover(viewport_size)
		await _tween_fade_cover(fade_cover, 1.0, 0.95)
	elif animate_from_current:
		strips = _create_strips(viewport_size, true)
		await _close_strips(strips, 0.42, 0.02)
	else:
		cover = _create_transition_cover(viewport_size)
		await get_tree().process_frame

	var nueva_escena = load(proxima_escena).instantiate()
	get_tree().root.add_child(nueva_escena)
	get_tree().current_scene = nueva_escena
	if fade_transition:
		if is_instance_valid(previous_scene):
			previous_scene.queue_free()
		await _tween_fade_cover(fade_cover, 0.0, 0.95)
	elif animate_from_current:
		if is_instance_valid(previous_scene):
			previous_scene.queue_free()
		await _open_strips(strips, viewport_size, 0.52, 0.016)
	else:
		strips = _create_strips(viewport_size, false)
		cover.queue_free()
		await _open_strips(strips, viewport_size, 0.82, 0.024)
	get_tree().paused = was_paused
	queue_free()

func _create_fade_cover(viewport_size: Vector2) -> ColorRect:
	var cover := ColorRect.new()
	cover.color = Color.BLACK
	cover.modulate.a = 0.0
	cover.mouse_filter = Control.MOUSE_FILTER_IGNORE
	cover.position = Vector2.ZERO
	cover.size = viewport_size
	$FadeLayer.add_child(cover)
	return cover

func _tween_fade_cover(cover: ColorRect, alpha: float, duration: float) -> void:
	var tween := create_tween()
	tween.tween_property(cover, "modulate:a", alpha, duration) \
			.set_trans(Tween.TRANS_SINE).set_ease(Tween.EASE_IN_OUT)
	await tween.finished

func _create_transition_cover(viewport_size: Vector2) -> ColorRect:
	var cover := ColorRect.new()
	cover.color = Color.BLACK
	cover.mouse_filter = Control.MOUSE_FILTER_IGNORE
	cover.position = Vector2.ZERO
	cover.size = viewport_size
	$FadeLayer.add_child(cover)
	return cover

func _create_strips(viewport_size: Vector2, start_offscreen: bool) -> Array[ColorRect]:
	var strips: Array[ColorRect] = []
	var strip_count := 12
	for i in strip_count:
		var top := floori(i * viewport_size.y / strip_count)
		var bottom := floori((i + 1) * viewport_size.y / strip_count)
		var strip := ColorRect.new()
		strip.color = Color.BLACK
		var start_x := 0.0
		if start_offscreen:
			start_x = -viewport_size.x if i % 2 == 0 else viewport_size.x
		strip.position = Vector2(start_x, top)
		strip.size = Vector2(viewport_size.x, bottom - top + 1.0)
		strip.mouse_filter = Control.MOUSE_FILTER_IGNORE
		$FadeLayer.add_child(strip)
		strips.append(strip)
	return strips

func _close_strips(strips: Array[ColorRect], duration: float, stagger: float) -> void:
	var tween := create_tween().set_parallel(true)
	for i in strips.size():
		tween.tween_property(strips[i], "position:x", 0.0, duration) \
				.set_delay(i * stagger).set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	await tween.finished

func _open_strips(strips: Array[ColorRect], viewport_size: Vector2,
		duration: float, stagger: float) -> void:
	var tween := create_tween().set_parallel(true)
	for i in strips.size():
		var direction := -1.0 if i % 2 == 0 else 1.0
		var delay := (strips.size() - i) * stagger
		tween.tween_property(strips[i], "position:x", direction * viewport_size.x, duration) \
				.set_delay(delay).set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_IN)
	await tween.finished
