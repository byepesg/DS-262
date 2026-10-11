class_name DungeonGraph
extends RefCounted

# Lista de adyacencia: cada habitación -> sus vecinas
var adjacency: Dictionary = {}

func add_room(id: int) -> void:
	if not adjacency.has(id):
		adjacency[id] = []

func add_edge(a: int, b: int) -> void:
	add_room(a)
	add_room(b)
	if not adjacency[a].has(b):
		adjacency[a].append(b)
	if not adjacency[b].has(a):
		adjacency[b].append(a)

func get_neighbors(id: int) -> Array:
	return adjacency.get(id, [])

func are_neighbors(a: int, b: int) -> bool:
	return get_neighbors(a).has(b)

func build_default() -> void:
	add_edge(1, 2)
	add_edge(2, 3)
	add_edge(2, 4)
	add_edge(3, 5)
	add_edge(4, 5)
	add_edge(5, 7)
	add_edge(4, 6)
	add_edge(6, 7)
	add_edge(7, 8)
	
func bfs_path(start: int, goal: int) -> Array:
	if not adjacency.has(start) or not adjacency.has(goal):
		return []
	var queue: Array = [start]
	var visited: Dictionary = {start: true}
	var parent: Dictionary = {}
	while not queue.is_empty():
		var current: int = queue.pop_front()
		if current == goal:
			break
		for n in get_neighbors(current):
			if not visited.has(n):
				visited[n] = true
				parent[n] = current
				queue.append(n)
	if not visited.has(goal):
		return []
	var path: Array = [goal]
	while path[0] != start:
		path.push_front(parent[path[0]])
	return path
