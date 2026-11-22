extends Control

func _ready() -> void:
	$ExitButton.pressed.connect(_on_quit_pressed)	
	$MenuButton.pressed.connect(_on_menu_pressed)


func _on_quit_pressed() -> void:
	var tween := create_tween().set_parallel(true)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 0.35)
	await tween.finished
	await get_tree().create_timer(0.1).timeout
	
	get_tree().quit()
	
func _on_menu_pressed() -> void:
	var cargador = load("res://scenes/game/scene_controller.tscn").instantiate()
	cargador.proxima_escena = "res://scenes/ui/screens/start_menu.tscn"
	cargador.animate_from_current = true
	get_tree().root.add_child(cargador)
