class_name ComboTrie
extends RefCounted

# Trie (árbol de prefijos) de combos de flechas.
# Cada poder es una secuencia de teclas ("up", "down", "left", "right").
# Cada tecla pulsada avanza UN nodo; si el nodo es final, se lanza el poder.
# Poderes que comparten el inicio de su secuencia comparten nodos.
#
# Complejidad:  insert O(L) · step O(1) por tecla · powers_under O(nodos del subárbol)
# (L = longitud de la secuencia)

class TrieNode extends RefCounted:
	var children: Dictionary = {}    # tecla -> TrieNode
	var power_id: String = ""        # no vacío si aquí termina un poder

var root := TrieNode.new()

func insert(sequence: Array, power_id: String) -> void:
	var node: TrieNode = root
	for key in sequence:
		if not node.children.has(key):
			node.children[key] = TrieNode.new()
		node = node.children[key]
	node.power_id = power_id

# Avanza un nodo con una tecla; null si ningún poder sigue ese camino
func step(node: TrieNode, key: String) -> TrieNode:
	return node.children.get(key, null)

# Poderes que todavía son posibles desde este nodo
func powers_under(node: TrieNode) -> Array:
	var result: Array = []
	if node.power_id != "":
		result.append(node.power_id)
	for key in node.children:
		result.append_array(powers_under(node.children[key]))
	return result
