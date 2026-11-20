class_name SkillTree
extends RefCounted

# Árbol general de habilidades. Cada habilidad desbloquea las siguientes.
# El progreso (puntos y habilidades desbloqueadas) se guarda en disco y
# se mantiene entre partidas.
#
# Operaciones:
#   insert         agrega un nodo como hijo de otro         O(n) (usa find)
#   find           busca por id con recorrido DFS           O(n)
#   unlock         exige padre desbloqueado y puntos        O(n)
#   levels         recorrido por niveles con BFS            O(n)
#   path_to_root   habilidades necesarias antes de una      O(profundidad)
#   reset_branch   bloquea un nodo y sus descendientes      O(subárbol)

const SAVE_PATH := "user://dungeon_save.json"
const START_POINTS := 3

# Definición del árbol (el padre siempre aparece antes que sus hijos)
const DEFAULT_CATALOG := preload("res://resources/skills/default_skill_catalog.tres")

var root: SkillNode
var points: int = START_POINTS

func _init() -> void:
	root = SkillNode.new("aventurero", "Aventurero", "Raíz del árbol. Siempre desbloqueada.", 0, "-1", load("res://assets/sprites/skills/aventurero.png"))
	root.unlocked = true

func build_default(catalog: SkillCatalog = DEFAULT_CATALOG) -> void:
	for definition in catalog.definitions:
		if not insert(str(definition.parent_id), str(definition.id),
				definition.display_name, definition.description, definition.cost,
				str(definition.power_id), definition.icon, definition.use_custom_position,
				definition.editor_position):
			push_error("Definición de habilidad inválida o duplicada: %s" % definition.id)

# ---------- Operaciones del árbol ----------

func insert(parent_id: String, id: String, display_name: String, desc: String,
		cost: int, power: String = "", icon: Texture2D = null,
		use_custom_position: bool = false, editor_position: Vector2 = Vector2.ZERO) -> bool:
	var parent := find(parent_id)
	if parent == null or find(id) != null:
		return false
	var node := SkillNode.new(id, display_name, desc, cost, power, icon,
			use_custom_position, editor_position)
	node.parent = parent
	parent.children.append(node)
	return true

# Búsqueda por id con DFS
func find(id: String, node: SkillNode = null) -> SkillNode:
	if node == null:
		node = root
	if node.id == id:
		return node
	for child in node.children:
		var found := find(id, child)
		if found != null:
			return found
	return null

func can_unlock(node: SkillNode) -> bool:
	return not node.unlocked and node.parent != null \
			and node.parent.unlocked and points >= node.cost

# Motivo por el que no se puede desbloquear ("" si se puede)
func why_not(node: SkillNode) -> String:
	if node.unlocked:
		return "Ya la tienes."
	if node.parent != null and not node.parent.unlocked:
		return "Primero desbloquea %s." % node.parent.display_name
	if points < node.cost:
		return "Te faltan %d punto(s)." % (node.cost - points)
	return ""

func unlock(id: String) -> bool:
	var node := find(id)
	if node == null or not can_unlock(node):
		return false
	points -= node.cost
	node.unlocked = true
	save()
	return true

# Bloquea un nodo y todos sus descendientes; devuelve los puntos recuperados
func reset_branch(id: String) -> int:
	var node := find(id)
	if node == null or node == root:
		return 0
	var refund := _lock_subtree(node)
	points += refund
	save()
	return refund

func _lock_subtree(node: SkillNode) -> int:
	var refund := 0
	if node.unlocked:
		node.unlocked = false
		refund += node.cost
	for child in node.children:
		refund += _lock_subtree(child)
	return refund

# Recorrido por niveles (BFS) con la cola circular. Devuelve un arreglo
# de niveles; cada nivel es un arreglo de nodos.
func levels() -> Array:
	var result: Array = []
	var queue := CircularQueue.new()
	queue.enqueue([root, 0])
	while not queue.is_empty():
		var item: Array = queue.dequeue()
		var node: SkillNode = item[0]
		var depth: int = item[1]
		if result.size() <= depth:
			result.append([])
		result[depth].append(node)
		for child in node.children:
			queue.enqueue([child, depth + 1])
	return result

# Nodos desde la raíz hasta el indicado (qué hace falta antes)
func path_to_root(node: SkillNode) -> Array:
	var path: Array = []
	var current: SkillNode = node
	while current != null:
		path.push_front(current)
		current = current.parent
	return path

# Ids de los poderes de las habilidades desbloqueadas
func unlocked_powers() -> Array:
	var result: Array = []
	_collect_powers(root, result)
	return result

func _collect_powers(node: SkillNode, out: Array) -> void:
	if node.unlocked and node.power_id != "":
		out.append(node.power_id)
	for child in node.children:
		_collect_powers(child, out)

func add_points(amount: int) -> void:
	points += amount
	save()

# ---------- Guardado entre partidas ----------

func save() -> void:
	var ids: Array = []
	_collect_unlocked(root, ids)
	var data := {"points": points, "unlocked": ids}
	var file := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	if file == null:
		push_error("No se pudo guardar el progreso en %s" % SAVE_PATH)
		return
	file.store_string(JSON.stringify(data))

func _collect_unlocked(node: SkillNode, out: Array) -> void:
	if node.unlocked:
		out.append(node.id)
	for child in node.children:
		_collect_unlocked(child, out)

# Carga el progreso guardado sobre el árbol ya construido
func load_progress() -> void:
	if not FileAccess.file_exists(SAVE_PATH):
		return
	var file := FileAccess.open(SAVE_PATH, FileAccess.READ)
	if file == null:
		return
	var data = JSON.parse_string(file.get_as_text())
	if not (data is Dictionary):
		return
	points = int(data.get("points", START_POINTS))
	for id in data.get("unlocked", []):
		var node := find(str(id))
		if node != null:
			node.unlocked = true
