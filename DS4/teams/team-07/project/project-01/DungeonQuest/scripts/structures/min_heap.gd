class_name MinHeap
extends RefCounted

# Cola de prioridad con un montículo binario mínimo (min-heap) sobre un Array.
# Sale primero el elemento de MENOR prioridad. Con prioridades iguales, sale
# primero el que entró antes (orden estable).
#
# Complejidad:  push O(log n) · pop O(log n) · peek O(1)

var last_priority: float = 0.0     # prioridad del último elemento sacado

var _data: Array = []              # cada nodo: [prioridad, orden de llegada, elemento]
var _counter: int = 0

func size() -> int:
	return _data.size()

func is_empty() -> bool:
	return _data.is_empty()

func push(item, priority: float) -> void:
	_data.append([priority, _counter, item])
	_counter += 1
	_sift_up(_data.size() - 1)

func peek():
	if _data.is_empty():
		return null
	return _data[0][2]

func pop():
	if _data.is_empty():
		return null
	var top: Array = _data[0]
	var last: Array = _data.pop_back()
	if not _data.is_empty():
		_data[0] = last
		_sift_down(0)
	last_priority = top[0]
	return top[2]

# Copia independiente (sirve para simular turnos futuros sin tocar el original)
func duplicate_heap() -> MinHeap:
	var copy := MinHeap.new()
	for node in _data:
		copy._data.append(node.duplicate())   # copia [prioridad, orden, elemento]; el elemento se comparte
	copy._counter = _counter
	copy.last_priority = last_priority
	return copy

func _less(a: Array, b: Array) -> bool:
	if a[0] != b[0]:
		return a[0] < b[0]
	return a[1] < b[1]

func _swap(i: int, j: int) -> void:
	var tmp = _data[i]
	_data[i] = _data[j]
	_data[j] = tmp

func _sift_up(i: int) -> void:
	while i > 0:
		var parent: int = (i - 1) >> 1
		if _less(_data[i], _data[parent]):
			_swap(i, parent)
			i = parent
		else:
			break

func _sift_down(i: int) -> void:
	var n: int = _data.size()
	while true:
		var left: int = 2 * i + 1
		var right: int = left + 1
		var smallest: int = i
		if left < n and _less(_data[left], _data[smallest]):
			smallest = left
		if right < n and _less(_data[right], _data[smallest]):
			smallest = right
		if smallest == i:
			break
		_swap(i, smallest)
		i = smallest
