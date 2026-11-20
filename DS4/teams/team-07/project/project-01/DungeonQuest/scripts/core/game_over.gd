extends Control

func _ready() -> void:
	$MenuButton.pressed.connect(_on_play_pressed)
	$ExitButton.pressed.connect(_on_quit_pressed)

func _on_play_pressed() -> void:
	var cargador = load("res://scenes/SceneController.tscn").instantiate()
	cargador.proxima_escena = "res://scenes/Start.tscn"
	cargador.animate_from_current = true
	get_tree().root.add_child(cargador)

func _on_quit_pressed() -> void:
	var tween := create_tween().set_parallel(true)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 0.35)
	await tween.finished
	await get_tree().create_timer(0.1).timeout
	
	get_tree().quit()
