class_name SkillDefinition
extends Resource

@export var id: StringName
@export var parent_id: StringName
@export var display_name: String
@export_multiline var description: String
@export_range(0, 1000, 1) var cost: int = 0
@export var power_id: StringName
@export var icon: Texture2D
@export var use_custom_position: bool = false
@export var editor_position: Vector2 = Vector2.ZERO
