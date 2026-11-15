extends Node

@onready var fade = $FadeLayer/Fade
var proxima_escena: String = ""

func _ready() -> void:
	# 1. Empezamos en negro absoluto (0 parpadeos)
	fade.modulate.a = 1.0
	
	# 2. Instanciamos la nueva escena en segundo plano de inmediato
	var nueva_escena = load(proxima_escena).instantiate()
	get_tree().root.add_child(nueva_escena)
	get_tree().current_scene = nueva_escena
	
	# 3. Quitamos el negro suavemente una vez que la escena ya existe
	var tween = create_tween()
	tween.tween_property(fade, "modulate:a", 0.0, 1.0)
	await tween.finished
	
	# 4. Nos destruimos a nosotros mismos
	queue_free()
