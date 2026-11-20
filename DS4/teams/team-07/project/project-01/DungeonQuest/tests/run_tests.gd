extends SceneTree

# Pruebas y mediciones de las estructuras de datos de Dungeon Quest.
# Se ejecuta sin abrir el juego:
#
#   godot --headless --path . --import                       (solo la primera vez)
#   godot --headless --path . --script res://tests/run_tests.gd
#
# Imprime un resumen, escribe tests/results.md (tablas para el informe) y
# termina con código 1 si alguna prueba falla.
#
# Cubre: MinHeap, CircularQueue, ComboTrie, DungeonGraph (grafo, BFS,
# Union-Find y generación procedural) y SkillTree (árbol general y guardado).
# El guardado real del jugador se respalda y se restaura al terminar.

const SAVE_PATH := SkillTree.SAVE_PATH

var _suite: String = ""
var _stats: Dictionary = {}        # suite -> [aprobadas, falladas]
var _failures: PackedStringArray = []
var _report: PackedStringArray = []

func _initialize() -> void:
	var save_backup := _backup_save()

	test_min_heap()
	test_circular_queue()
	test_combo_trie()
	test_graph_basics()
	test_bfs()
	test_union_find()
	test_generation()
	test_skill_tree()
	test_skill_tree_persistence()
	test_integration()

	_restore_save(save_backup)

	_print_summary()
	run_benchmarks()
	_write_report()

	var failed := 0
	for s in _stats:
		failed += _stats[s][1]
	quit(1 if failed > 0 else 0)

# ---------- Utilidades de prueba ----------

func check(cond: bool, label: String) -> void:
	if not _stats.has(_suite):
		_stats[_suite] = [0, 0]
	if cond:
		_stats[_suite][0] += 1
	else:
		_stats[_suite][1] += 1
		_failures.append("[%s] %s" % [_suite, label])

func _print_summary() -> void:
	print("")
	print("=== RESULTADO DE LAS PRUEBAS ===")
	var total_ok := 0
	var total_bad := 0
	for s in _stats:
		var ok: int = _stats[s][0]
		var bad: int = _stats[s][1]
		total_ok += ok
		total_bad += bad
		print("%-34s %3d/%-3d %s" % [s, ok, ok + bad, "OK" if bad == 0 else "FALLA"])
	print("--------------------------------------------")
	print("TOTAL: %d/%d verificaciones aprobadas" % [total_ok, total_ok + total_bad])
	for f in _failures:
		print("  FALLA ", f)

func _backup_save() -> Dictionary:
	if FileAccess.file_exists(SAVE_PATH):
		return {"existed": true, "text": FileAccess.get_file_as_string(SAVE_PATH)}
	return {"existed": false, "text": ""}

func _restore_save(backup: Dictionary) -> void:
	if backup["existed"]:
		var f := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
		if f != null:
			f.store_string(backup["text"])
	elif FileAccess.file_exists(SAVE_PATH):
		DirAccess.remove_absolute(ProjectSettings.globalize_path(SAVE_PATH))

func _ms(usec: int) -> float:
	return usec / 1000.0

# ---------- MinHeap ----------

func test_min_heap() -> void:
	_suite = "MinHeap"
	var h := MinHeap.new()
	check(h.is_empty() and h.size() == 0, "nuevo montículo está vacío")
	check(h.pop() == null and h.peek() == null, "pop/peek en vacío devuelven null")

	# Orden con prioridades desordenadas
	var rng := RandomNumberGenerator.new()
	rng.seed = 7
	var values: Array = []
	for i in 1000:
		var p := rng.randf_range(-50.0, 50.0)
		values.append(p)
		h.push(i, p)
	check(h.size() == 1000, "size tras 1000 push")
	var sorted_ok := true
	var prev := -INF
	for i in 1000:
		h.pop()
		if h.last_priority < prev:
			sorted_ok = false
		prev = h.last_priority
	check(sorted_ok, "1000 pop salen en orden no decreciente (incluye negativos)")
	check(h.is_empty(), "vacío tras sacar todo")

	# Estabilidad: misma prioridad -> orden de llegada
	var s := MinHeap.new()
	for i in 50:
		s.push(i, 5.0)
	var stable := true
	for i in 50:
		if s.pop() != i:
			stable = false
	check(stable, "prioridades iguales salen en orden de llegada (estable)")

	# peek no extrae
	var p := MinHeap.new()
	p.push("b", 2.0)
	p.push("a", 1.0)
	check(p.peek() == "a" and p.size() == 2, "peek devuelve el mínimo sin sacarlo")
	p.pop()
	check(is_equal_approx(p.last_priority, 1.0), "last_priority guarda la prioridad del último pop")

	# duplicate_heap es independiente
	var c := p.duplicate_heap()
	c.pop()
	check(c.is_empty() and p.size() == 1, "duplicate_heap no afecta al original")

	# Propiedad: operaciones mezcladas contra un modelo (min estable)
	var heap := MinHeap.new()
	var model: Array = []        # [prioridad, orden, item]
	var seq := 0
	var ok := true
	rng.seed = 99
	for i in 4000:
		if model.is_empty() or rng.randf() < 0.6:
			var pr := float(rng.randi_range(0, 20))   # muchos empates a propósito
			heap.push(seq, pr)
			model.append([pr, seq, seq])
			seq += 1
		else:
			var best := 0
			for j in model.size():
				if model[j][0] < model[best][0] or (model[j][0] == model[best][0] and model[j][1] < model[best][1]):
					best = j
			var expected = model[best][2]
			model.remove_at(best)
			if heap.pop() != expected:
				ok = false
		if heap.size() != model.size():
			ok = false
	check(ok, "4000 operaciones mezcladas coinciden con el modelo de referencia")

	# Orden de turnos del combate: velocidad alta -> retraso menor -> sale antes
	var turns := MinHeap.new()
	turns.push("lento", 1.0 / 5.0)
	turns.push("rapido", 1.0 / 14.0)
	check(turns.pop() == "rapido", "el de mayor velocidad actúa primero")

# ---------- CircularQueue ----------

func test_circular_queue() -> void:
	_suite = "CircularQueue"
	var q := CircularQueue.new()
	check(q.is_empty() and q.size() == 0, "nueva cola vacía")
	check(q.dequeue() == null and q.peek() == null, "dequeue/peek en vacía devuelven null")

	# FIFO con crecimiento (capacidad inicial 4)
	for i in 100:
		q.enqueue(i)
	check(q.size() == 100 and q.peek() == 0, "100 elementos y cabeza correcta tras crecer")
	var fifo := true
	for i in 100:
		if q.dequeue() != i:
			fifo = false
	check(fifo and q.is_empty(), "salen en orden FIFO")

	# Vuelta al inicio del arreglo (wrap-around)
	var w := CircularQueue.new(4)
	for i in 3:
		w.enqueue(i)
	w.dequeue()
	w.dequeue()
	for i in range(3, 7):
		w.enqueue(i)       # fuerza índices modulares y un crecimiento
	check(w.to_array() == [2, 3, 4, 5, 6], "orden correcto con wrap-around y crecimiento")

	# clear
	w.clear()
	check(w.is_empty() and w.peek() == null, "clear vacía la cola")
	w.enqueue("x")
	check(w.peek() == "x", "reutilizable tras clear")

	# Capacidad inválida
	var z := CircularQueue.new(0)
	z.enqueue(1)
	z.enqueue(2)
	check(z.size() == 2 and z.dequeue() == 1, "capacidad 0 se corrige a 1 y funciona")

	# Propiedad contra un Array de referencia
	var rng := RandomNumberGenerator.new()
	rng.seed = 42
	var qq := CircularQueue.new(2)
	var model: Array = []
	var ok := true
	for i in 6000:
		if rng.randf() < 0.55:
			qq.enqueue(i)
			model.append(i)
		else:
			var got = qq.dequeue()
			var want = null
			if not model.is_empty():
				want = model.pop_front()
			if got != want:
				ok = false
		var head = null
		if not model.is_empty():
			head = model[0]
		if qq.size() != model.size() or qq.peek() != head:
			ok = false
	check(ok, "6000 operaciones aleatorias coinciden con la cola de referencia")

	# Uso real del parry: eventos con tiempo, la cabeza es la que se compara
	var strikes := CircularQueue.new()
	for i in 3:
		strikes.enqueue(0.9 + i * 0.8)
	check(is_equal_approx(strikes.peek(), 0.9), "la cabeza es el primer golpe del ataque")

# ---------- ComboTrie ----------

func test_combo_trie() -> void:
	_suite = "ComboTrie"
	var t := ComboTrie.new()
	for id in Combat.POWERS:
		t.insert(Combat.POWERS[id]["seq"], id)

	# Cada secuencia real llega a su poder
	var all_ok := true
	for id in Combat.POWERS:
		var node := t.root
		for key in Combat.POWERS[id]["seq"]:
			node = t.step(node, key)
			if node == null:
				all_ok = false
				break
		if node == null or node.power_id != id:
			all_ok = false
	check(all_ok, "cada secuencia de Combat.POWERS termina en su poder")

	# Teclas inválidas
	check(t.step(t.root, "left") != null, "tecla válida avanza")
	check(t.step(t.root, "space") == null, "tecla desconocida devuelve null")
	var corte_mid := t.step(t.root, "down")
	check(t.step(corte_mid, "left") == null, "camino inexistente devuelve null")

	# Prefijos compartidos: ↓→ (Corte) y ↓↓↑ (Sanación) comparten el nodo ↓
	var shared := t.step(t.root, "down")
	var under := t.powers_under(shared)
	check(under.has("corte") and under.has("sanacion"), "powers_under de un prefijo lista los poderes posibles")
	check(not under.has("tormenta"), "no lista poderes de otras ramas")
	check(t.powers_under(t.root).size() == Combat.POWERS.size(), "desde la raíz están todos los poderes")

	# Un poder que es prefijo de otro
	var p := ComboTrie.new()
	p.insert(["up", "right"], "corto")
	p.insert(["up", "right", "down"], "largo")
	var n := p.step(p.step(p.root, "up"), "right")
	check(n.power_id == "corto", "el nodo del prefijo conserva su poder")
	check(p.powers_under(n).size() == 2, "y el subárbol incluye al poder más largo")

	# Reinsertar la misma secuencia sobrescribe
	p.insert(["up", "right"], "nuevo")
	check(p.step(p.step(p.root, "up"), "right").power_id == "nuevo", "reinsertar sobrescribe el poder")

	# Secuencia vacía
	var e := ComboTrie.new()
	e.insert([], "raiz")
	check(e.root.power_id == "raiz", "secuencia vacía marca la raíz")

# ---------- DungeonGraph: grafo básico ----------

func test_graph_basics() -> void:
	_suite = "DungeonGraph (grafo)"
	var g := DungeonGraph.new()
	check(g.get_neighbors(5) == [], "sala inexistente no tiene vecinas")
	g.add_edge(1, 2)
	check(g.are_neighbors(1, 2) and g.are_neighbors(2, 1), "add_edge es bidireccional")
	g.add_edge(1, 2)
	g.add_edge(2, 1)
	check(g.get_neighbors(1).size() == 1 and g.get_neighbors(2).size() == 1, "no duplica aristas")
	check(not g.are_neighbors(1, 3), "salas sin arista no son vecinas")
	g.add_room(9)
	check(g.get_neighbors(9) == [], "sala aislada existe sin vecinas")

# ---------- BFS ----------

func test_bfs() -> void:
	_suite = "BFS"
	var g := DungeonGraph.new()
	g.build_default()
	var path := g.bfs_path(1, 8)
	check(path.size() == 6, "camino más corto 1→8 en el mapa de prueba tiene 6 salas")
	check(path[0] == 1 and path[path.size() - 1] == 8, "empieza en el origen y termina en el destino")
	var consecutive := true
	for i in range(path.size() - 1):
		if not g.are_neighbors(path[i], path[i + 1]):
			consecutive = false
	check(consecutive, "salas consecutivas del camino son vecinas")
	check(g.bfs_path(3, 3) == [3], "origen = destino devuelve [origen]")
	check(g.bfs_path(1, 99) == [] and g.bfs_path(99, 1) == [], "sala inexistente devuelve []")

	var d := DungeonGraph.new()
	d.add_edge(1, 2)
	d.add_edge(3, 4)
	check(d.bfs_path(1, 4) == [], "grafo desconexo devuelve []")

	# Optimalidad: el atajo se agrega primero, así que un recorrido en profundidad
	# exploraría antes la rama larga y devolvería 6 salas en vez de 3
	var s := DungeonGraph.new()
	s.add_edge(1, 7)
	s.add_edge(7, 6)
	for i in range(1, 6):
		s.add_edge(i, i + 1)
	check(s.bfs_path(1, 6).size() == 3, "elige el atajo 1→7→6 y no la rama larga 1→2→3→4→5→6")

	# Comparación con una implementación independiente (relajación de distancias)
	var rng := RandomNumberGenerator.new()
	rng.seed = 5
	var optimal := true
	for trial in 200:
		var r := DungeonGraph.new()
		var rooms := rng.randi_range(4, 14)
		for i in range(1, rooms + 1):
			r.add_room(i)
		for k in rng.randi_range(rooms - 1, rooms * 2):
			r.add_edge(rng.randi_range(1, rooms), rng.randi_range(1, rooms))
		var dist: Dictionary = {1: 0}
		var changed := true
		while changed:
			changed = false
			for a in r.adjacency:
				if not dist.has(a):
					continue
				for b in r.get_neighbors(a):
					if not dist.has(b) or dist[b] > dist[a] + 1:
						dist[b] = dist[a] + 1
						changed = true
		for goal in range(1, rooms + 1):
			var p := r.bfs_path(1, goal)
			if dist.has(goal):
				if p.size() != dist[goal] + 1:
					optimal = false
			elif not p.is_empty():
				optimal = false
	check(optimal, "en 200 grafos aleatorios, bfs_path siempre coincide con la distancia mínima")

	# Optimalidad: en un ciclo largo con atajo, BFS elige el atajo
	var c := DungeonGraph.new()
	for i in range(1, 8):
		c.add_edge(i, i + 1)
	c.add_edge(1, 8)
	check(c.bfs_path(1, 7).size() == 3, "toma el atajo (1→8→7) en lugar del camino largo")

# ---------- Union-Find ----------

func test_union_find() -> void:
	_suite = "Union-Find"
	var g := DungeonGraph.new()
	var parent := {1: 2, 2: 3, 3: 4, 4: 4, 5: 5}
	check(g._find(parent, 1) == 4, "find sigue la cadena hasta la raíz")
	check(parent[1] == 3 or parent[1] == 4, "compresión de caminos acorta la cadena")
	check(g._find(parent, 1) == 4 and g._find(parent, 5) == 5, "raíces distintas se mantienen separadas")
	var single := {7: 7}
	check(g._find(single, 7) == 7, "un elemento aislado es su propia raíz")

# ---------- Generación procedural ----------

func _check_map(g: DungeonGraph, n: int, entry_view: int, exit_view: int) -> String:
	if g.adjacency.size() != n:
		return "número de salas distinto de n"
	if g.cells.size() != n:
		return "faltan celdas"
	var seen: Dictionary = {}
	for id in g.cells:
		if seen.has(g.cells[id]):
			return "dos salas comparten celda"
		seen[g.cells[id]] = true
	for a in g.adjacency:
		for b in g.get_neighbors(a):
			if not g.adjacency[b].has(a):
				return "arista no simétrica %d-%d" % [a, b]
			if g.view_to(a, b) == -1:
				return "arista entre celdas no adyacentes %d-%d" % [a, b]
	for id in range(1, n + 1):
		if g.bfs_path(1, id).is_empty():
			return "sala %d inalcanzable desde la entrada" % id
	if g.get_neighbors(1).size() != 1:
		return "la entrada no es una hoja"
	if g.get_neighbors(n).size() != 1:
		return "la salida no es una hoja"
	var entry_nb: int = g.get_neighbors(1)[0]
	var exit_nb: int = g.get_neighbors(n)[0]
	if g.cells[entry_nb] - g.cells[1] != DungeonGraph.DIRS[entry_view]:
		return "la vecina de la entrada no queda en la dirección de su puerta"
	if g.cells[exit_nb] - g.cells[n] != DungeonGraph.DIRS[exit_view]:
		return "la vecina de la salida no queda en la dirección de su puerta"
	return ""

func _edge_count(g: DungeonGraph) -> int:
	var total := 0
	for a in g.adjacency:
		total += g.get_neighbors(a).size()
	return total / 2

func test_generation() -> void:
	_suite = "Generación procedural"
	var g := DungeonGraph.new()
	check(not g.generate_grid(2, 2, 1, 0, 1), "n < 3 se rechaza")

	var n := 8
	var seeds := 150
	var total := 0
	var first_ok := 0
	var within30 := 0
	var bad_msg := ""
	var combos_failed := PackedStringArray()
	for ev in 4:
		for xv in 4:
			var combo_ok := true
			for sd in seeds:
				total += 1
				var gg := DungeonGraph.new()
				if gg.generate_grid(n, 2, sd, ev, xv):
					first_ok += 1
					var msg := _check_map(gg, n, ev, xv)
					if msg != "" and bad_msg == "":
						bad_msg = "seed %d vistas %d/%d: %s" % [sd, ev, xv, msg]
				# Reintentos como hace Main (hasta 30 semillas consecutivas)
				var found := false
				for attempt in 30:
					var g2 := DungeonGraph.new()
					if g2.generate_grid(n, 2, sd + attempt, ev, xv):
						found = true
						break
				if found:
					within30 += 1
				else:
					combo_ok = false
			if not combo_ok:
				combos_failed.append("%d/%d" % [ev, xv])
	check(bad_msg == "", "todos los mapas generados son válidos (%d generaciones). %s" % [total, bad_msg])
	check(first_ok > 0, "hay semillas que generan a la primera")
	check(within30 == total, "con hasta 30 reintentos siempre se genera un mapa (fallan: %s)" % ", ".join(combos_failed))

	_report.append("## Generación procedural (n = %d salas, 16 combinaciones de paredes de entrada/salida, %d semillas c/u)" % [n, seeds])
	_report.append("")
	_report.append("| Métrica | Resultado |")
	_report.append("|---|---|")
	_report.append("| Generaciones probadas | %d |" % total)
	_report.append("| Éxito a la primera | %d (%.1f %%) |" % [first_ok, 100.0 * first_ok / total])
	_report.append("| Éxito con hasta 30 reintentos | %d (%.1f %%) |" % [within30, 100.0 * within30 / total])
	_report.append("| Mapas con alguna violación (conexo, simétrico, celdas únicas, hojas, direcciones) | %s |" % ("ninguno" if bad_msg == "" else bad_msg))
	_report.append("")

	# Union-Find/Kruskal: sin aristas extra el mapa es un árbol (n - 1 aristas);
	# con k extra hay entre n - 1 y n - 1 + k aristas
	var tree_ok := true
	var bounded_ok := true
	var with_cycles := 0
	var tested := 0
	for sd in 200:
		var gt := DungeonGraph.new()
		if gt.generate_grid(n, 0, sd, 0, 1):
			tested += 1
			if _edge_count(gt) != n - 1:
				tree_ok = false
		var gx := DungeonGraph.new()
		if gx.generate_grid(n, 2, sd, 0, 1):
			var e := _edge_count(gx)
			if e < n - 1 or e > n - 1 + 2:
				bounded_ok = false
			if e > n - 1:
				with_cycles += 1
	check(tested > 0 and tree_ok, "con 0 aristas extra el mapa es un árbol de expansión (n-1 aristas)")
	check(bounded_ok, "con 2 extra hay entre n-1 y n+1 aristas")
	check(with_cycles > 0, "las aristas extra sí crean ciclos en algunos mapas")

	# Determinismo: misma semilla -> mismo mapa
	var a := DungeonGraph.new()
	var b := DungeonGraph.new()
	var seed_ok := 0
	for sd in range(0, 60):
		if a.generate_grid(n, 2, sd, 0, 1):
			b.generate_grid(n, 2, sd, 0, 1)
			if str(a.adjacency) == str(b.adjacency) and str(a.cells) == str(b.cells):
				seed_ok += 1
			else:
				seed_ok = -9999
	check(seed_ok > 0, "la misma semilla produce siempre el mismo mapa")

	# Semillas distintas producen mapas distintos (variedad)
	var distinct: Dictionary = {}
	for sd in range(0, 100):
		var gv := DungeonGraph.new()
		if gv.generate_grid(n, 2, sd, 0, 1):
			distinct[str(gv.adjacency) + str(gv.cells)] = true
	check(distinct.size() > 20, "100 semillas dan más de 20 mapas distintos (dieron %d)" % distinct.size())

	# La salida es la sala intermedia más lejana de la entrada
	var farthest_ok := true
	for sd in range(0, 100):
		var gf := DungeonGraph.new()
		if not gf.generate_grid(n, 2, sd, 0, 1):
			continue
		var entry_nb: int = gf.get_neighbors(1)[0]
		var exit_nb: int = gf.get_neighbors(n)[0]
		var d_exit: int = gf.bfs_path(entry_nb, exit_nb).size()
		for id in range(2, n):
			if gf.bfs_path(entry_nb, id).size() > d_exit and not gf.cells.values().has(gf.cells[id] - DungeonGraph.DIRS[1]):
				farthest_ok = false
	check(farthest_ok, "la salida cuelga de la sala más lejana que tiene espacio libre")

# ---------- SkillTree ----------

func _fresh_tree() -> SkillTree:
	var t := SkillTree.new()
	t.build_default()
	return t

func test_skill_tree() -> void:
	_suite = "SkillTree"
	var t := _fresh_tree()
	check(t.root.unlocked and t.points == SkillTree.START_POINTS, "raíz desbloqueada y puntos iniciales")
	check(t.find("tormenta") != null and t.find("no_existe") == null, "find (DFS) encuentra y distingue ausentes")
	check(not t.insert("aventurero", "espada", "X", "", 1), "insert rechaza ids duplicados")
	check(not t.insert("no_existe", "nuevo", "X", "", 1), "insert rechaza padres inexistentes")
	check(t.insert("fuego", "meteoro", "Meteoro", "", 4), "insert agrega una hoja nueva")
	check(t.find("meteoro").parent == t.find("fuego"), "el nodo nuevo queda enlazado a su padre")

	t = _fresh_tree()
	check(t.can_unlock(t.find("espada")), "se puede desbloquear una rama con puntos")
	check(not t.can_unlock(t.find("corte")), "no se puede sin desbloquear al padre")
	check(not t.can_unlock(t.root), "la raíz no se desbloquea")
	check(not t.unlock("corte") and t.points == 3, "unlock fallido no gasta puntos")
	check(t.why_not(t.find("corte")).begins_with("Primero"), "why_not explica el padre faltante")
	check(t.unlock("espada") and t.points == 2, "unlock gasta el costo")
	check(not t.unlock("espada"), "no se desbloquea dos veces")
	check(t.unlock("corte") and t.points == 1, "desbloquea el hijo después del padre")
	check(not t.unlock("tormenta") and t.why_not(t.find("tormenta")).begins_with("Te faltan"), "sin puntos suficientes se rechaza y se explica")
	check(not t.unlock("no_existe"), "unlock de id inexistente es false")
	check(t.unlocked_powers() == ["corte"], "unlocked_powers lista solo poderes desbloqueados (Espada no tiene poder)")

	# reset_branch devuelve los puntos de toda la rama
	t.add_points(3)
	check(t.unlock("tormenta"), "desbloquea una hoja de nivel 3")
	var before := t.points
	var refund := t.reset_branch("espada")
	check(refund == 1 + 1 + 3, "reiniciar Espada devuelve 5 puntos (Espada+Corte+Tormenta)")
	check(t.points == before + 5, "los puntos se suman al saldo")
	check(not t.find("espada").unlocked and not t.find("corte").unlocked and not t.find("tormenta").unlocked, "bloquea el nodo y todos sus descendientes")
	check(t.unlocked_powers().is_empty(), "sin poderes tras reiniciar la rama")
	check(t.reset_branch("aventurero") == 0, "no se puede reiniciar la raíz")
	check(t.reset_branch("no_existe") == 0, "reiniciar un id inexistente devuelve 0")

	# Recorrido por niveles (BFS con cola circular)
	var lv := _fresh_tree().levels()
	var sizes: Array = []
	for level in lv:
		sizes.append(level.size())
	check(sizes == [1, 3, 3, 1], "levels() agrupa por profundidad: 1, 3, 3, 1 nodos")
	check(lv[0][0].id == "aventurero" and lv[3][0].id == "tormenta", "raíz en el nivel 0 y Tormenta en el 3")

	# Camino a la raíz
	var path := _fresh_tree().path_to_root(_fresh_tree().find("tormenta"))
	var ids: Array = []
	for n in path:
		ids.append(n.id)
	check(ids == ["aventurero", "espada", "corte", "tormenta"], "path_to_root lista lo necesario antes de Tormenta")

func test_skill_tree_persistence() -> void:
	_suite = "SkillTree (guardado)"
	if FileAccess.file_exists(SAVE_PATH):
		DirAccess.remove_absolute(ProjectSettings.globalize_path(SAVE_PATH))

	var t := _fresh_tree()
	t.unlock("espada")
	t.unlock("corte")
	t.add_points(5)
	check(FileAccess.file_exists(SAVE_PATH), "unlock/add_points guardan el archivo")

	var r := _fresh_tree()
	r.load_progress()
	check(r.find("corte").unlocked and r.find("espada").unlocked, "load_progress restaura las habilidades")
	check(r.points == t.points, "load_progress restaura los puntos")
	check(not r.find("luz").unlocked, "lo no desbloqueado sigue bloqueado")

	# Guardado corrupto: no debe romper nada
	var f := FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	f.store_string("esto no es json {{{")
	f.close()
	var c := _fresh_tree()
	c.load_progress()
	check(c.points == SkillTree.START_POINTS and not c.find("espada").unlocked, "un guardado corrupto se ignora")

	# Guardado con ids desconocidos
	f = FileAccess.open(SAVE_PATH, FileAccess.WRITE)
	f.store_string(JSON.stringify({"points": 4, "unlocked": ["espada", "fantasma"]}))
	f.close()
	var u := _fresh_tree()
	u.load_progress()
	check(u.find("espada").unlocked and u.points == 4, "ids desconocidos se ignoran y el resto se carga")

	# Sin archivo
	DirAccess.remove_absolute(ProjectSettings.globalize_path(SAVE_PATH))
	var n := _fresh_tree()
	n.load_progress()
	check(n.points == SkillTree.START_POINTS, "sin archivo se empieza con los puntos iniciales")

# ---------- Integración entre estructuras ----------

func test_integration() -> void:
	_suite = "Integración"
	var effects_ok := true
	for id in Combat.POWERS:
		if not BattleEffects.COLORS.has(Combat.POWERS[id].get("effect", "")):
			effects_ok = false
	check(effects_ok, "cada poder de combate tiene un efecto visual definido")

	# Todo poder del árbol existe en el combate
	var powers_ok := true
	for definition in SkillTree.DEFAULT_CATALOG.definitions:
		if definition.power_id != &"" and not Combat.POWERS.has(str(definition.power_id)):
			powers_ok = false
	check(powers_ok, "cada poder del árbol de habilidades existe en Combat.POWERS")

	# Cada poder del combate se puede obtener desde el árbol
	var in_tree: Dictionary = {}
	for definition in SkillTree.DEFAULT_CATALOG.definitions:
		in_tree[str(definition.power_id)] = true
	var all_reachable := true
	for id in Combat.POWERS:
		if not in_tree.has(id):
			all_reachable = false
	check(all_reachable, "cada poder del combate se desbloquea con alguna habilidad")

	# Secuencias únicas (si no, el trie sobrescribiría un poder)
	var seqs: Dictionary = {}
	for id in Combat.POWERS:
		seqs[str(Combat.POWERS[id]["seq"])] = true
	check(seqs.size() == Combat.POWERS.size(), "no hay dos poderes con la misma secuencia")

	# Flujo completo: desbloquear -> trie -> lanzar
	var tree := _fresh_tree()
	tree.unlock("espada")
	tree.unlock("corte")
	var trie := ComboTrie.new()
	for id in tree.unlocked_powers():
		trie.insert(Combat.POWERS[id]["seq"], id)
	var node := trie.root
	for key in Combat.POWERS["corte"]["seq"]:
		node = trie.step(node, key)
	check(node != null and node.power_id == "corte", "un poder desbloqueado se lanza tecleando su combo")
	check(trie.step(trie.root, "left") == null, "un poder no desbloqueado (Tormenta) no responde")

	# Salas -> turnos: el heap ordena a los enemigos por velocidad
	var order := MinHeap.new()
	var speeds := {"slime": 8.0, "Serpiente": 9.0, "rata": 14.0, "buho": 5.0}
	for k in speeds:
		order.push(k, 1.0 / speeds[k])
	check(order.pop() == "rata" and order.pop() == "Serpiente", "rata y serpiente actúan antes que slime y buho")

# ---------- Mediciones ----------

func run_benchmarks() -> void:
	_report.append("## Mediciones de tiempo")
	_report.append("")
	_bench_heap()
	_bench_queue()
	_bench_graph()
	_bench_skill_tree()
	for l in _report:
		if l.begins_with("##") or l.begins_with("|"):
			print(l)

func _bench_heap() -> void:
	_report.append("### MinHeap: n inserciones seguidas de n extracciones")
	_report.append("")
	_report.append("| n | tiempo total (ms) | µs por operación | µs/op ÷ log₂(n) |")
	_report.append("|---|---|---|---|")
	var rng := RandomNumberGenerator.new()
	rng.seed = 1
	for n in [1000, 10000, 100000]:
		var h := MinHeap.new()
		var prios: Array = []
		for i in n:
			prios.append(rng.randf())
		var t0 := Time.get_ticks_usec()
		for i in n:
			h.push(i, prios[i])
		while not h.is_empty():
			h.pop()
		var dt := Time.get_ticks_usec() - t0
		var per_op: float = float(dt) / (2.0 * n)
		_report.append("| %d | %.1f | %.2f | %.3f |" % [n, _ms(dt), per_op, per_op / (log(n) / log(2.0))])
	_report.append("")

func _bench_queue() -> void:
	_report.append("### CircularQueue: n encolados seguidos de n desencolados")
	_report.append("")
	_report.append("| n | tiempo total (ms) | µs por operación |")
	_report.append("|---|---|---|")
	for n in [1000, 10000, 100000]:
		var q := CircularQueue.new()
		var t0 := Time.get_ticks_usec()
		for i in n:
			q.enqueue(i)
		while not q.is_empty():
			q.dequeue()
		var dt := Time.get_ticks_usec() - t0
		_report.append("| %d | %.1f | %.3f |" % [n, _ms(dt), float(dt) / (2.0 * n)])
	_report.append("")

func _bench_graph() -> void:
	_report.append("### DungeonGraph: generación (Kruskal + Union-Find) y BFS según el número de salas")
	_report.append("")
	_report.append("| Salas | generación (ms) | aristas | BFS entrada→salida (µs, promedio de 20) | salas en el camino |")
	_report.append("|---|---|---|---|---|")
	for n in [8, 32, 128, 512]:
		var g := DungeonGraph.new()
		var ok := false
		var dt_gen := 0
		for sd in 50:
			var t0 := Time.get_ticks_usec()
			ok = g.generate_grid(n, maxi(2, n / 4), sd, 0, 1)
			dt_gen = Time.get_ticks_usec() - t0
			if ok:
				break
		if not ok:
			_report.append("| %d | no generó en 50 semillas | - | - | - |" % n)
			continue
		var edges := 0
		for a in g.adjacency:
			edges += g.get_neighbors(a).size()
		edges /= 2
		var path_len := 0
		var t1 := Time.get_ticks_usec()
		for i in 20:
			path_len = g.bfs_path(1, n).size()
		var dt_bfs := float(Time.get_ticks_usec() - t1) / 20.0
		_report.append("| %d | %.2f | %d | %.1f | %d |" % [n, _ms(dt_gen), edges, dt_bfs, path_len])
	_report.append("")

func _bench_skill_tree() -> void:
	_report.append("### SkillTree: n habilidades (cadena de dos ramas)")
	_report.append("")
	_report.append("| n | n inserciones (ms) | find del último (µs) | levels() (ms) |")
	_report.append("|---|---|---|---|")
	for n in [100, 500, 2000]:
		var t := SkillTree.new()
		var t0 := Time.get_ticks_usec()
		for i in n:
			var parent := "aventurero" if i < 2 else "n%d" % (i - 2)
			t.insert(parent, "n%d" % i, "N", "", 1)
		var dt_ins := Time.get_ticks_usec() - t0
		var t1 := Time.get_ticks_usec()
		t.find("n%d" % (n - 1))
		var dt_find := Time.get_ticks_usec() - t1
		var t2 := Time.get_ticks_usec()
		t.levels()
		var dt_lv := Time.get_ticks_usec() - t2
		_report.append("| %d | %.1f | %d | %.2f |" % [n, _ms(dt_ins), dt_find, _ms(dt_lv)])
	_report.append("")

# ---------- Informe ----------

func _write_report() -> void:
	var lines := PackedStringArray()
	lines.append("# Resultados de las pruebas")
	lines.append("")
	lines.append("Generado con `tests/run_tests.gd` (Godot %s)." % Engine.get_version_info()["string"])
	lines.append("")
	lines.append("## Verificaciones por estructura")
	lines.append("")
	lines.append("| Estructura | Aprobadas | Total |")
	lines.append("|---|---|---|")
	var total_ok := 0
	var total := 0
	for s in _stats:
		var ok: int = _stats[s][0]
		var all: int = ok + _stats[s][1]
		total_ok += ok
		total += all
		lines.append("| %s | %d | %d |" % [s, ok, all])
	lines.append("| **Total** | **%d** | **%d** |" % [total_ok, total])
	lines.append("")
	lines.append_array(_report)
	var f := FileAccess.open("res://tests/results.md", FileAccess.WRITE)
	if f == null:
		push_warning("No se pudo escribir tests/results.md")
		return
	f.store_string("\n".join(lines))
