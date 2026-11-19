class_name PlayerHUD
extends CanvasLayer

# Indicador del jugador en el mapa: nombre, vida y puntos de habilidad.

@onready var name_label: Label = %NameLabel
@onready var points_label: Label = %PointsLabel
@onready var hp_bar: ProgressBar = %HPBar
@onready var hp_label: Label = %HPLabel

var max_hp: int = 1

# Llamar DESPUÉS de add_child
func setup(player_name: String, hp: int, p_max_hp: int, points: int) -> void:
	name_label.text = player_name
	max_hp = p_max_hp
	hp_bar.max_value = max_hp
	hp_bar.value = hp
	hp_label.text = "%d/%d" % [hp, max_hp]
	set_points(points)

func set_hp(hp: int) -> void:
	hp = clampi(hp, 0, max_hp)
	hp_label.text = "%d/%d" % [hp, max_hp]
	create_tween().tween_property(hp_bar, "value", float(hp), 0.3)

func set_points(points: int) -> void:
	points_label.text = "Pts %d" % points
