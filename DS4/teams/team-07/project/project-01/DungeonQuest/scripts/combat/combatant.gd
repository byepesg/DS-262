class_name Combatant
extends RefCounted

# Datos de un participante del combate (jugador o enemigo).

var display_name: String
var max_hp: int
var hp: int
var attack: int
var speed: float
var is_player: bool
var defending: bool = false
var hits: int = 2

func _init(n: String, hp_max: int, atk: int, spd: float, player: bool) -> void:
	display_name = n
	max_hp = hp_max
	hp = hp_max
	attack = atk
	speed = spd
	is_player = player

func is_alive() -> bool:
	return hp > 0

func take_damage(amount: int) -> void:
	hp = maxi(0, hp - amount)

# Tiempo que pasa hasta su siguiente turno: más velocidad, turnos más frecuentes
func next_delay() -> float:
	return 100.0 / speed
