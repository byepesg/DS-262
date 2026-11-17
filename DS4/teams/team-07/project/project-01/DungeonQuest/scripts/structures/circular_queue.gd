class_name CircularQueue
extends RefCounted

# Cola (FIFO) sobre un arreglo circular que crece al llenarse.
# Se usa para los golpes de un ataque enemigo: cada respuesta del jugador
# se compara con la CABEZA de la cola.
#
# Complejidad:  enqueue O(1) amortizado · dequeue O(1) · peek O(1)
# (el crecimiento duplica la capacidad, como un arreglo dinámico)

var _data: Array = []
var _head: int = 0       # índice del primer elemento
var _count: int = 0

func _init(capacity: int = 4) -> void:
	_data.resize(maxi(1, capacity))

func size() -> int:
	return _count

func is_empty() -> bool:
	return _count == 0

func enqueue(item) -> void:
	if _count == _data.size():
		_grow()
	_data[(_head + _count) % _data.size()] = item   # indexación modular
	_count += 1

func dequeue():
	if _count == 0:
		return null
	var item = _data[_head]
	_data[_head] = null
	_head = (_head + 1) % _data.size()
	_count -= 1
	return item

func peek():
	if _count == 0:
		return null
	return _data[_head]

# Elementos en orden de llegada, sin modificar la cola
func to_array() -> Array:
	var result: Array = []
	for i in _count:
		result.append(_data[(_head + i) % _data.size()])
	return result

func clear() -> void:
	for i in _data.size():
		_data[i] = null
	_head = 0
	_count = 0

# Duplica la capacidad y reordena los elementos desde el índice 0
func _grow() -> void:
	var bigger: Array = []
	bigger.resize(_data.size() * 2)
	for i in _count:
		bigger[i] = _data[(_head + i) % _data.size()]
	_data = bigger
	_head = 0
