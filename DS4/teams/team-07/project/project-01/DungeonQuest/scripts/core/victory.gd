extends Control

func _ready() -> void:
	FadeIn.play(self)
	$ExitButton.pressed.connect(_on_quit_pressed)	
	$MenuButton.pressed.connect(_on_menu_pressed)


func _on_quit_pressed() -> void:
	var tween := create_tween().set_parallel(true)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 1.0)
	await tween.finished
	await get_tree().create_timer(0.1).timeout
	
	get_tree().quit()
	
func _on_menu_pressed() -> void:
	var cargador = load("res://scenes/SceneController.tscn").instantiate()
	cargador.proxima_escena = "res://scenes/Start.tscn"
	var tween := create_tween().set_parallel(true)
	tween.tween_property($FadeLayer/Fade, "modulate:a", 1.0, 1.0)
	await tween.finished
	await get_tree().create_timer(0.1).timeout
	
	# Añadimos el cargador justo antes de borrar la escena vieja
	get_tree().root.add_child(cargador)
	queue_free() 
