class_name SkillNode
extends RefCounted

# Un nodo del árbol de habilidades.
# Un nodo puede tener un poder asociado (power_id); si no, es solo una rama.

var id: String
var display_name: String
var description: String
var cost: int
var power_id: String
var unlocked: bool = false
var parent: SkillNode = null
var children: Array = []

func _init(i: String, n: String, d: String, c: int, p: String = "") -> void:
	id = i
	display_name = n
	description = d
	cost = c
	power_id = p
