class_name DungeonGraph
extends RefCounted

# Direcciones en el mismo orden que el enum View de door.gd:
# 0 = FRONT (arriba), 1 = BACK (abajo), 2 = LEFT, 3 = RIGHT
# La opuesta de una vista v es v ^ 1.
const DIRS := [Vector2i(0, -1), Vector2i(0, 1), Vector2i(-1, 0), Vector2i(1, 0)]

# Lista de adyacencia: sala -> vecinas
var adjacency: Dictionary = {}
# Celda de la cuadrícula de cada sala: sala -> Vector2i
var cells: Dictionary = {}

# ---------- Grafo básico ----------

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

# Vista (pared) por la que se sale de a para llegar a b. -1 si no son vecinas en la cuadrícula.
func view_to(a: int, b: int) -> int:
	return DIRS.find(cells[b] - cells[a])

# Mapa fijo de prueba (ya no se usa en el juego)
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

# ---------- BFS ----------

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

# ---------- Generación en cuadrícula ----------

# Sala 1 = entrada, sala n = salida, salas 2..n-1 = intermedias.
# entry_view / exit_view: pared por la que sale la única puerta de enlace
# de la entrada y de la salida. Su vecina queda en esa dirección.
# Devuelve false si no pudo construir el mapa (se reintenta con otra semilla).
func generate_grid(n: int, extra_edges: int, seed_value: int, entry_view: int, exit_view: int) -> bool:
	adjacency.clear()
	cells.clear()
	if n < 3:
		return false
	var rng := RandomNumberGenerator.new()
	rng.seed = seed_value

	for i in range(1, n + 1):
		add_room(i)

	# 1. Bloque contiguo de salas intermedias en la cuadrícula
	var occupied: Dictionary = {}    # Vector2i -> id
	occupied[Vector2i.ZERO] = 2
	cells[2] = Vector2i.ZERO
	for id in range(3, n):
		var options: Dictionary = {}
		for c in occupied:
			for d in DIRS:
				var nc: Vector2i = c + d
				if not occupied.has(nc):
					options[nc] = true
		var keys: Array = options.keys()
		var pick: Vector2i = keys[rng.randi() % keys.size()]
		occupied[pick] = id
		cells[id] = pick

	# 2. Aristas candidatas: solo entre celdas adyacentes
	var candidates: Array = []
	for id in range(2, n):
		for d in [1, 3]:    # abajo y derecha, para no repetir pares
			var nc: Vector2i = cells[id] + DIRS[d]
			if occupied.has(nc):
				candidates.append(Vector2i(id, int(occupied[nc])))
	_shuffle(candidates, rng)

	# 3. Kruskal aleatorio con Union-Find (árbol de expansión)
	var parent: Dictionary = {}
	for id in range(2, n):
		parent[id] = id
	var leftovers: Array = []
	for e in candidates:
		var ra: int = _find(parent, e.x)
		var rb: int = _find(parent, e.y)
		if ra != rb:
			parent[ra] = rb
			add_edge(e.x, e.y)
		else:
			leftovers.append(e)

	# 4. Aristas extra para crear ciclos
	var added := 0
	for e in leftovers:
		if added >= extra_edges:
			break
		add_edge(e.x, e.y)
		added += 1

	# 5. Entrada: su vecina queda en la dirección de su puerta
	var entry_vec: Vector2i = DIRS[entry_view]
	var exit_vec: Vector2i = DIRS[exit_view]
	var entry_options: Array = []
	for id in range(2, n):
		if not occupied.has(cells[id] - entry_vec):
			entry_options.append(id)
	if entry_options.is_empty():
		return false
	var entry_nb: int = entry_options[rng.randi() % entry_options.size()]
	var entry_cell: Vector2i = cells[entry_nb] - entry_vec
	cells[1] = entry_cell
	occupied[entry_cell] = 1
	add_edge(1, entry_nb)

	# 6. Salida: la intermedia más lejana (BFS) con espacio libre para la salida
	var best := -1
	var exit_options: Array = []
	for id in range(2, n):
		if occupied.has(cells[id] - exit_vec):
			continue
		var dist: int = bfs_path(entry_nb, id).size()
		if dist > best:
			best = dist
			exit_options = [id]
		elif dist == best:
			exit_options.append(id)
	if exit_options.is_empty():
		return false
	var exit_nb: int = exit_options[rng.randi() % exit_options.size()]
	cells[n] = cells[exit_nb] - exit_vec
	add_edge(n, exit_nb)
	return true

# ---------- Utilidades ----------

func _find(parent: Dictionary, x: int) -> int:
	while parent[x] != x:
		parent[x] = parent[parent[x]]   # compresión de caminos
		x = parent[x]
	return x

func _shuffle(arr: Array, rng: RandomNumberGenerator) -> void:
	for i in range(arr.size() - 1, 0, -1):
		var j: int = rng.randi() % (i + 1)
		var tmp = arr[i]
		arr[i] = arr[j]
		arr[j] = tmp
