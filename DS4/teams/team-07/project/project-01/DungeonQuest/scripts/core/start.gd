extends Control

func _ready() -> void:
	$StartButton.pressed.connect(_on_play_pressed)
	$ExitButton.pressed.connect(_on_quit_pressed)
	$SkillsButton.pressed.connect(_on_skill_pressed)
	_setup_button_motion($StartButton)
	_setup_button_motion($SkillsButton)
	_setup_button_motion($ExitButton)

func _setup_button_motion(button: TextureButton) -> void:
	button.pivot_offset = button.size / 2.0
	button.focus_mode = Control.FOCUS_NONE
	button.mouse_entered.connect(func():
		if button.disabled:
			return
		_animate_button(button, Vector2(1.045, 1.045))
	)
	button.mouse_exited.connect(func():
		_animate_button(button, Vector2.ONE)
	)
	button.button_down.connect(func():
		_animate_button(button, Vector2(0.96, 0.96))
	)
	button.button_up.connect(func():
		_animate_button(button, Vector2.ONE)
	)

func _animate_button(button: TextureButton, target: Vector2) -> void:
	var tween := create_tween()
	tween.tween_property(button, "scale", target, 0.09) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)

func _on_play_pressed() -> void:
	var cargador = load("res://scenes/game/scene_controller.tscn").instantiate()
	cargador.proxima_escena = "res://scenes/game/dungeon.tscn"
	cargador.animate_from_current = true
	get_tree().root.add_child(cargador)
	
func _on_skill_pressed() -> void:
	var cargador = load("res://scenes/game/scene_controller.tscn").instantiate()
	cargador.proxima_escena = "res://scenes/ui/screens/skill_tree.tscn"
	cargador.animate_from_current = true
	get_tree().root.add_child(cargador)

func _on_quit_pressed() -> void:
	var tween := create_tween().set_parallel(true)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 0.35)
	await tween.finished
	await get_tree().create_timer(0.1).timeout
	
	get_tree().quit()
