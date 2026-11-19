class_name Combat
extends CanvasLayer

# LÓGICA del combate por turnos. La interfaz está en scenes/CombatScene.tscn
# (se diseña en el editor); este script solo busca sus nodos por nombre único.
# Se instancia ENCIMA de Main, así que el mapa, el grafo y el minimapa no se tocan.
#
# Estructuras de datos:
#   - MinHeap (cola de prioridad): orden de turnos por velocidad.
#   - CircularQueue (cola circular): golpes de un ataque enemigo (parry) y
#     búfer de teclas del combo.
#   - ComboTrie (trie): cada poder es una secuencia de flechas.
#   - Dictionary (tabla hash): datos de cada poder por id.
#
# Nodos que DEBE tener CombatScene.tscn (con "Acceder como nombre único"):
#   %TurnLabel  %FoeList  %PlayerBar  %PlayerHPLabel  %APLabel
#   %DefendButton  %PowerButton  %ParryView  %ComboBox  %ComboLabel
#   %ComboBar  %LogLabel
# Y FoeRow.tscn: %NameLabel  %HPBar  %AttackButton

signal finished(player_won: bool, player_hp: int)

const FOE_VIEW := preload("res://scenes/Foe.tscn")

const PLAYER_STATS := {"name": "Jugador", "max_hp": 60, "attack": 10, "speed": 10.0}

const PREVIEW_TURNS := 6
const STEP_DELAY := 0.7

# Puntos de acción
const START_AP := 2
const MAX_AP := 6

# Poderes: secuencia de flechas, costo, tipo y valor
const POWERS := {
	"corte": {"name": "Corte", "seq": ["down", "right"], "cost": 2, "kind": "single", "value": 16},
	"llamarada": {"name": "Llamarada", "seq": ["up", "up", "right"], "cost": 3, "kind": "all", "value": 14},
	"sanacion": {"name": "Sanación", "seq": ["down", "down", "up"], "cost": 3, "kind": "heal", "value": 20},
	"tormenta": {"name": "Tormenta", "seq": ["left", "right", "left", "right"], "cost": 4, "kind": "single", "value": 30},
}
const KEY_TIME_LIMIT := 2.0      # segundos máximos entre teclas del combo
const ARROWS := {"up": "↑", "down": "↓", "left": "←", "right": "→"}
const KEY_MAP := {KEY_UP: "up", KEY_DOWN: "down", KEY_LEFT: "left", KEY_RIGHT: "right"}

# Parry
const PARRY_KEY := KEY_SPACE
const STRIKE_START := 0.9        # segundos hasta el primer golpe
const STRIKE_GAP := 0.8          # separación entre golpes
const PERFECT_WINDOW := 0.12
const GOOD_WINDOW := 0.25
const PARRY_COUNTER := 4         # daño al atacante con un parry perfecto
const END_DELAY := 0.6
const MENU_FONT := preload("res://assets/fonts/ModernDOS8x16.ttf")
const BREAK_LOSES_TURN := true   # true: equivocarte de flecha también cuesta el turno

@onready var combo_menu: MenuButton = %ComboMenu

class StrikeEvent extends RefCounted:
	var time: float
	func _init(t: float) -> void:
		time = t

# --- Nodos de la escena (por nombre único) ---
@onready var turn_label: Label = %TurnLabel
@onready var foe_slots: Node2D = %FoeSlots
@onready var player_sprite: AnimatedSprite2D = %PlayerSprite
@onready var log_label: RichTextLabel = %LogLabel
@onready var player_bar: ProgressBar = %PlayerBar
@onready var player_hp_label: Label = %PlayerHPLabel
@onready var ap_label: Label = %APLabel
@onready var defend_button: Button = %DefendButton
@onready var power_button: Button = %PowerButton
@onready var parry_view: ParryView = %ParryView
@onready var combo_box: Control = %ComboBox
@onready var combo_label: RichTextLabel = %ComboLabel
@onready var combo_bar: ProgressBar = %ComboBar

var player: Combatant
var foes: Array = []
var heap := MinHeap.new()
var clock: float = 0.0
var over: bool = false
var ap: int = START_AP
var available_powers: Array = []   # ids de poderes desbloqueados en el árbol de habilidades

# Ataque enemigo
var strikes := CircularQueue.new()
var attacking: bool = false
var attacker: Combatant
var phase_time: float = 0.0

# Combo
var trie := ComboTrie.new()
var trie_node: ComboTrie.TrieNode
var combo_mode: bool = false
var combo_time: float = 0.0
var combo_buffer := CircularQueue.new()

var foe_rows: Dictionary = {}     # Combatant -> {"bar": ProgressBar, "button": Button}
var log_lines: Array = []

func _init() -> void:
	layer = 60
	process_mode = Node.PROCESS_MODE_ALWAYS

# Llamar DESPUÉS de add_child
func start(player_hp: int, foe_types: Array, unlocked: Array = []) -> void:
	player = Combatant.new(PLAYER_STATS["name"], PLAYER_STATS["max_hp"],
			PLAYER_STATS["attack"], PLAYER_STATS["speed"], true)
	player.hp = player_hp
	for i in foe_types.size():
		var s: Dictionary = EnemyTypes.get_stats(foe_types[i])
		var foe := Combatant.new("%s %d" % [s["name"], i + 1], s["hp"],
				s["attack"], s["speed"], false)
		foe.hits = int(s["hits"])
		foe.set_meta("color", s["color"])
		foe.set_meta("sprite_frames", s["sprite_frames"])
		foes.append(foe)
		
	# Solo los poderes desbloqueados en el árbol de habilidades entran al trie
	for id in unlocked:
		if POWERS.has(id):
			available_powers.append(id)
			trie.insert(POWERS[id]["seq"], id)

	_setup_ui()

	heap.push(player, player.next_delay())
	for f in foes:
		heap.push(f, f.next_delay())

	_log("¡Combate! %d enemigo(s)." % foes.size())
	_next_turn()

# ---------- Interfaz: se conecta a los nodos de la escena ----------

func _setup_ui() -> void:
	player_bar.max_value = player.max_hp
	player_bar.value = player.hp
	_play_player_animation("idle_right")

	# ESPACIO es el parry: que no pulse botones por accidente
	defend_button.focus_mode = Control.FOCUS_NONE
	power_button.focus_mode = Control.FOCUS_NONE
	defend_button.pressed.connect(_on_defend_pressed)
	power_button.pressed.connect(_on_power_pressed)
	if available_powers.is_empty():
		power_button.text = "Poder (sin habilidades)"

	parry_view.lead = STRIKE_GAP
	parry_view.perfect = PERFECT_WINDOW
	combo_bar.max_value = KEY_TIME_LIMIT
	combo_box.visible = false

	# Una fila por enemigo, creada desde FoeRow.tscn
	var slots := foe_slots.get_children()
	for i in foes.size():
		var foe: Combatant = foes[i]
		var view: FoeView = FOE_VIEW.instantiate()
		slots[mini(i, slots.size() - 1)].add_child(view)
		view.setup(foe.display_name, foe.max_hp, foe.hp,
				foe.get_meta("color", Color.WHITE), foe.get_meta("sprite_frames"))
		view.attack_button.focus_mode = Control.FOCUS_NONE
		view.attack_button.pressed.connect(_on_attack_pressed.bind(foe))
		foe_rows[foe] = {"view": view, "button": view.attack_button}
	log_label.get_v_scroll_bar().modulate.a = 0.0   # oculta la barra de scroll
	_idle_bob(player_sprite)
	combo_menu.focus_mode = Control.FOCUS_NONE
	var popup := combo_menu.get_popup()
	popup.add_theme_font_override("font", MENU_FONT)
	popup.add_theme_font_size_override("font_size", 16)
	_refresh_combo_menu()
	
func _refresh_combo_menu() -> void:
	var popup := combo_menu.get_popup()
	popup.clear()
	for i in available_powers.size():
		var p: Dictionary = POWERS[available_powers[i]]
		var seq := PackedStringArray()
		for k in p["seq"]:
			seq.append(ARROWS[k])
		popup.add_item("%s  %s  (%d PA)" % [p["name"], " ".join(seq), p["cost"]], i)
		popup.set_item_disabled(i, int(p["cost"]) > ap)

func _min_cost() -> int:
	var best := 99
	for id in available_powers:
		best = mini(best, int(POWERS[id]["cost"]))
	return best

# Pierdes el turno (tiempo agotado o combo roto)
func _lose_combo_turn(reason: String) -> void:
	_close_combo_panel()
	_log(reason)
	await get_tree().create_timer(STEP_DELAY).timeout
	_next_turn()
	
func _refresh() -> void:
	player_bar.value = player.hp
	player_hp_label.text = "%d/%d" % [player.hp, player.max_hp]
	ap_label.text = "PA %d/%d" % [ap, MAX_AP]
	for foe in foes:
		foe_rows[foe]["view"].update_hp(foe.hp)
	if _last_player_hp >= 0 and player.hp < _last_player_hp:
		_animate_player_hit()
	_last_player_hp = player.hp
	_refresh_combo_menu()

func _set_actions(enabled: bool) -> void:
	defend_button.disabled = not enabled
	var no_powers := available_powers.is_empty()
	var no_ap := ap < _min_cost()
	power_button.disabled = (not enabled) or no_powers or no_ap
	if no_powers:
		power_button.text = "Poder (sin habilidades)"
	elif no_ap:
		power_button.text = "Poder (sin PA)"
	else:
		power_button.text = "Poder (combo)"
	combo_menu.disabled = (not enabled) or no_powers
	for foe in foes:
		foe_rows[foe]["button"].disabled = (not enabled) or (not foe.is_alive())
		
func _log(text: String) -> void:
	log_lines.append(text)
	if log_lines.size() > 4:
		log_lines.pop_front()
	log_label.text = "\n".join(PackedStringArray(log_lines))

func _gain_ap(amount: int) -> void:
	ap = mini(MAX_AP, ap + amount)

# ---------- Flujo de turnos ----------

func _next_turn() -> void:
	if over:
		return
	if not player.is_alive():
		_finish(false)
		return
	if _all_foes_dead():
		_finish(true)
		return

	# Saca de la cola al siguiente que actúa (salta a los caídos)
	var actor: Combatant = null
	while not heap.is_empty():
		var c: Combatant = heap.pop()
		if c.is_alive():
			actor = c
			break
	if actor == null:
		return
	clock = heap.last_priority
	heap.push(actor, clock + actor.next_delay())   # programa su siguiente turno
	actor.defending = false

	turn_label.text = "Turno de: %s\nSiguientes: %s" % [actor.display_name, _preview_order()]
	_refresh()

	if actor.is_player:
		_set_actions(true)
	else:
		_set_actions(false)
		await get_tree().create_timer(STEP_DELAY).timeout
		_enemy_attack(actor)

# Simula los próximos turnos sobre una copia del montículo
func _preview_order() -> String:
	var sim := heap.duplicate_heap()
	var names := PackedStringArray()
	var guard := 0
	while names.size() < PREVIEW_TURNS and not sim.is_empty() and guard < 100:
		guard += 1
		var c: Combatant = sim.pop()
		if not c.is_alive():
			continue
		names.append(c.display_name)
		sim.push(c, sim.last_priority + c.next_delay())
	return " → ".join(names)

func _all_foes_dead() -> bool:
	for f in foes:
		if f.is_alive():
			return false
	return true

func _first_alive_foe() -> Combatant:
	for f in foes:
		if f.is_alive():
			return f
	return null

# ---------- Acciones del jugador ----------

func _roll(base: int) -> int:
	return maxi(1, base + randi_range(-1, 1))

func _on_attack_pressed(foe: Combatant) -> void:
	if over or not foe.is_alive():
		return
	_set_actions(false)
	var dmg := _roll(player.attack)
	foe.take_damage(dmg)
	_lunge(player_sprite, 16.0)
	_play_player_animation("attack_right", "move_right")
	_gain_ap(1)
	_log("Atacas a %s: %d de daño. (+1 PA)" % [foe.display_name, dmg])
	_refresh()
	await get_tree().create_timer(STEP_DELAY).timeout
	_play_player_animation("idle_right")
	_next_turn()

func _on_defend_pressed() -> void:
	if over:
		return
	_set_actions(false)
	player.defending = true
	_gain_ap(1)
	_log("Te preparas para el golpe. (+1 PA)")
	_refresh()
	await get_tree().create_timer(STEP_DELAY).timeout
	_next_turn()

# ---------- Combos (trie) ----------

func _on_power_pressed() -> void:
	if over:
		return
	_set_actions(false)
	combo_mode = true
	combo_time = 0.0
	trie_node = trie.root
	combo_buffer.clear()
	parry_view.visible = false
	combo_box.visible = true
	_update_combo_view()

func _close_combo_panel() -> void:
	combo_mode = false
	combo_buffer.clear()
	combo_box.visible = false
	parry_view.visible = true

# Cancelar no gasta el turno
func _cancel_combo(reason: String) -> void:
	_close_combo_panel()
	_log(reason)
	_set_actions(true)

# Cada flecha avanza un nodo del trie
func _combo_key(key: String) -> void:
	var next = trie.step(trie_node, key)
	if next == null:
		if BREAK_LOSES_TURN:
			_lose_combo_turn("Combo roto. Pierdes el turno.")
		else:
			_cancel_combo("Combo roto.")
		return
	trie_node = next
	combo_buffer.enqueue(key)     # búfer de las teclas tecleadas
	combo_time = 0.0
	if trie_node.power_id != "":
		_cast_power(trie_node.power_id)
	else:
		_update_combo_view()

func _update_combo_view() -> void:
	var typed := PackedStringArray()
	for k in combo_buffer.to_array():
		typed.append(ARROWS[k])
	var typed_text: String = " ".join(typed) if typed.size() > 0 else "…"
	combo_label.text = "[center]Combo\n\n%s\n\n[color=#888888]Esc: cancelar[/color][/center]" % typed_text
	
func _cast_power(id: String) -> void:
	var p: Dictionary = POWERS[id]
	var cost: int = int(p["cost"])
	var value: int = int(p["value"])
	_close_combo_panel()

	if ap < cost:
		_log("No tienes PA para %s." % p["name"])
		_set_actions(true)
		return

	ap -= cost
	match p["kind"]:
		"single":
			var foe := _first_alive_foe()
			foe.take_damage(value)
			_log("%s: %d de daño a %s." % [p["name"], value, foe.display_name])
		"all":
			for f in foes:
				if f.is_alive():
					f.take_damage(value)
			_log("%s: %d de daño a todos." % [p["name"], value])
		"heal":
			player.hp = mini(player.max_hp, player.hp + value)
			_log("%s: recuperas %d de vida." % [p["name"], value])
	_refresh()
	await get_tree().create_timer(STEP_DELAY).timeout
	_next_turn()

# ---------- Ataque enemigo con parry ----------

# El ataque genera una cola de golpes con instantes exactos
func _enemy_attack(foe: Combatant) -> void:
	if over:
		return
	attacker = foe
	foe_rows[foe]["view"].play_attack()
	strikes.clear()
	for i in attacker.hits:
		strikes.enqueue(StrikeEvent.new(STRIKE_START + i * STRIKE_GAP))
	phase_time = 0.0
	attacking = true
	_log("%s ataca: ¡ESPACIO cuando el aro se cierre!" % foe.display_name)

func _process(delta: float) -> void:
	if parry_view == null:
		return

	# Límite de tiempo entre teclas del combo
	if combo_mode:
		combo_time += delta
		combo_bar.value = maxf(0.0, KEY_TIME_LIMIT - combo_time)
		if combo_time > KEY_TIME_LIMIT:
			_lose_combo_turn("¡Se acabó el tiempo! Pierdes el turno.")
		return

	if not attacking:
		if parry_view.active:
			parry_view.active = false
			parry_view.queue_redraw()
		return

	phase_time += delta

	# Los golpes que ya pasaron sin respuesta cuentan como fallo
	var head: StrikeEvent = strikes.peek()
	while head != null and phase_time > head.time + GOOD_WINDOW:
		strikes.dequeue()
		_apply_strike("miss")
		head = strikes.peek()

	parry_view.active = true
	parry_view.now = phase_time
	parry_view.head_time = head.time if head != null else -1.0
	parry_view.queue_redraw()

	if strikes.is_empty():
		attacking = false
		_end_enemy_phase()

func _input(event: InputEvent) -> void:
	var key := event as InputEventKey
	if key == null or not key.pressed or key.echo:
		return

	# Teclear un combo
	if combo_mode:
		if key.keycode == KEY_ESCAPE:
			_cancel_combo("Combo cancelado.")
		elif KEY_MAP.has(key.keycode):
			_combo_key(KEY_MAP[key.keycode])
		get_viewport().set_input_as_handled()
		return

	# Parry
	if attacking and key.keycode == PARRY_KEY:
		var head: StrikeEvent = strikes.peek()
		if head == null:
			return
		var diff: float = absf(phase_time - head.time)
		if diff > GOOD_WINDOW:
			return     # demasiado pronto: se ignora
		strikes.dequeue()
		_apply_strike("perfect" if diff <= PERFECT_WINDOW else "good")
		get_viewport().set_input_as_handled()

# Resuelve el golpe de la cabeza de la cola según la respuesta
func _apply_strike(grade: String) -> void:
	var dmg: int = _roll(attacker.attack)
	if player.defending:
		dmg = ceili(dmg / 2.0)
	match grade:
		"perfect":
			dmg = 0
			attacker.take_damage(PARRY_COUNTER)
			_log("¡PARRY perfecto! Contraatacas por %d." % PARRY_COUNTER)
		"good":
			dmg = ceili(dmg / 2.0)
			_log("Bloqueo parcial: %d de daño." % dmg)
		_:
			_log("%s te golpea: %d de daño." % [attacker.display_name, dmg])
	if dmg > 0:
		player.take_damage(dmg)
	_refresh()
	# Si alguien cae, se cancelan los golpes que faltan
	if not player.is_alive() or not attacker.is_alive():
		strikes.clear()

func _end_enemy_phase() -> void:
	await get_tree().create_timer(END_DELAY).timeout
	_next_turn()

func _finish(won: bool) -> void:
	over = true
	_set_actions(false)
	_log("¡Victoria!" if won else "Has caído...")
	await get_tree().create_timer(1.0).timeout
	finished.emit(won, player.hp)
	queue_free()

var _last_player_hp: int = -1

func _idle_bob(node: Node2D) -> void:
	var base := node.position.y
	var t := create_tween().set_loops()
	t.tween_property(node, "position:y", base - 1.5, 0.5).set_trans(Tween.TRANS_SINE)
	t.tween_property(node, "position:y", base + 1.5, 0.5).set_trans(Tween.TRANS_SINE)

func _lunge(node: Node2D, dx: float) -> void:
	var x0 := node.position.x
	var t := create_tween()
	t.tween_property(node, "position:x", x0 + dx, 0.1)
	t.tween_property(node, "position:x", x0, 0.15)

func _animate_player_hit() -> void:
	_play_player_animation("hurt_right")
	var x0 := player_sprite.position.x
	player_sprite.modulate = Color(2.5, 2.5, 2.5)
	create_tween().tween_property(player_sprite, "modulate", Color.WHITE, 0.25)
	var s := create_tween()
	for dx in [-4.0, 4.0, -3.0, 3.0, 0.0]:
		s.tween_property(player_sprite, "position:x", x0 + dx, 0.04)

func _play_player_animation(animation_name: String, fallback: String = "idle_right") -> void:
	if player_sprite.sprite_frames == null:
		return
	if player_sprite.sprite_frames.has_animation(animation_name):
		player_sprite.play(animation_name)
	elif player_sprite.sprite_frames.has_animation(fallback):
		player_sprite.play(fallback)
