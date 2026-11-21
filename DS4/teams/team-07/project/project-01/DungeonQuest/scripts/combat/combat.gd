class_name Combat
extends CanvasLayer

# LÓGICA del combate por turnos. La interfaz está en scenes/game/combat.tscn
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
# Nodos que DEBE tener scenes/game/combat.tscn (con "Acceder como nombre único"):
#   %TurnLabel  %FoeSlots  %PlayerBar  %PlayerHPLabel  %APLabel
#   %DefendButton  %PowerButton  %ParryView  %ComboBox  %ComboView
#   %ComboBar  %LogLabel
# Y scenes/actors/combatant_row.tscn: %NameLabel  %HPBar  %AttackButton

signal finished(player_won: bool, player_hp: int)

const FOE_VIEW := preload("res://scenes/actors/combatant_view.tscn")
const COMBO_ARROW_SCRIPT := preload("res://scripts/combat/combo_arrows.gd")

const PLAYER_STATS := {"name": "Jugador", "max_hp": 60, "attack": 10, "speed": 10.0}

const PREVIEW_TURNS := 6
const STEP_DELAY := 0.7

# Puntos de acción
const START_AP := 2
const MAX_AP := 6

# Poderes: secuencia de flechas, costo, tipo y valor
const POWERS := {
	"corte": {"name": "Corte", "seq": ["down", "right"], "cost": 2, "kind": "single", "value": 16, "effect": "physical"},
	"llamarada": {"name": "Llamarada", "seq": ["up", "up", "right"], "cost": 3, "kind": "all", "value": 14, "effect": "fire"},
	"sanacion": {"name": "Sanación", "seq": ["down", "down", "up"], "cost": 3, "kind": "heal", "value": 20, "effect": "healing"},
	"tormenta": {"name": "Tormenta", "seq": ["left", "right", "left", "right"], "cost": 4, "kind": "single", "value": 30, "effect": "lightning"},
}
const KEY_TIME_LIMIT := 2.0      # segundos máximos entre teclas del combo
const ARROWS := {"up": "↑", "down": "↓", "left": "←", "right": "→"}
const KEY_MAP := {KEY_UP: "up", KEY_DOWN: "down", KEY_LEFT: "left", KEY_RIGHT: "right"}
const IMPACT_FONT := preload("res://assets/fonts/alagard.ttf")
const COMBO_BREAK_COLOR := Color("ff657b")

# Parry
const PARRY_KEY := KEY_SPACE
const STRIKE_START := 0.9        # segundos hasta el primer golpe
const STRIKE_GAP := 0.8          # separación entre golpes
const PERFECT_WINDOW := 0.12
const GOOD_WINDOW := 0.25
const PARRY_COUNTER := 4         # daño al atacante con un parry perfecto
const END_DELAY := 0.6
const MENU_FONT := preload("res://assets/fonts/modern_dos_8x16.ttf")
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
@onready var combo_view: Control = %ComboView
@onready var combo_bar: ProgressBar = %ComboBar
@onready var battle_effects: BattleEffects = $Stage/BattleEffects

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
var _player_idle_tween: Tween

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
	var turn_settings := turn_label.label_settings.duplicate() as LabelSettings
	turn_settings.font_size = 8
	turn_label.label_settings = turn_settings

	# ESPACIO es el parry: que no pulse botones por accidente
	defend_button.focus_mode = Control.FOCUS_NONE
	power_button.focus_mode = Control.FOCUS_NONE
	defend_button.pressed.connect(_on_defend_pressed)
	power_button.pressed.connect(_on_power_pressed)
	combo_box.add_theme_stylebox_override("panel", StyleBoxEmpty.new())
	if available_powers.is_empty():
		power_button.text = "Poder"

	parry_view.lead = STRIKE_GAP
	parry_view.perfect = PERFECT_WINDOW
	parry_view.visible = false
	combo_bar.max_value = KEY_TIME_LIMIT
	combo_box.visible = false
	combo_box.pivot_offset = combo_box.size / 2.0
	_setup_button_motion(defend_button)
	_setup_button_motion(power_button)
	_setup_button_motion(combo_menu)

	# Una fila por enemigo, creada desde combatant_row.tscn
	var slots := foe_slots.get_children()
	for i in foes.size():
		var foe: Combatant = foes[i]
		var view: FoeView = FOE_VIEW.instantiate()
		slots[mini(i, slots.size() - 1)].add_child(view)
		view.setup(foe.display_name, foe.max_hp, foe.hp,
				foe.get_meta("sprite_frames"), battle_effects)
		view.attack_button.focus_mode = Control.FOCUS_NONE
		view.attack_button.pressed.connect(_on_attack_pressed.bind(foe))
		_setup_button_motion(view.attack_button)
		foe_rows[foe] = {"view": view, "button": view.attack_button}
	log_label.get_v_scroll_bar().modulate.a = 0.0   # oculta la barra de scroll
	_player_idle_tween = _idle_bob(player_sprite)
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
func _lose_combo_turn(reason: String, failure_label: String = "ROTO",
		failure_color: Color = COMBO_BREAK_COLOR) -> void:
	combo_mode = false
	combo_box.visible = true
	combo_view.call("show_failure", combo_buffer.to_array(), failure_label, failure_color)
	_log(reason)
	var origin := combo_box.position
	var shake := create_tween()
	for offset in [Vector2(-8, 0), Vector2(8, 0), Vector2(-6, 0), Vector2(0, 0)]:
		shake.tween_property(combo_box, "position", origin + offset, 0.045)
	await shake.finished
	_close_combo_panel()
	await get_tree().create_timer(STEP_DELAY).timeout
	_next_turn()
	
func _refresh() -> void:
	player_bar.value = player.hp
	player_hp_label.text = "%d/%d" % [player.hp, player.max_hp]
	ap_label.text = "PA %d/%d" % [ap, MAX_AP]
	for foe in foes:
		foe_rows[foe]["view"].update_hp(foe.hp)
	_refresh_combo_menu()

func _set_actions(enabled: bool) -> void:
	defend_button.disabled = not enabled
	var no_powers := available_powers.is_empty()
	var no_ap := ap < _min_cost()
	power_button.disabled = (not enabled) or no_powers or no_ap
	if no_powers:
		power_button.text = "Poder"
	elif no_ap:
		power_button.text = "Poder"
	else:
		power_button.text = "Poder"
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
	_damage_foe(foe, dmg)
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
	combo_bar.value = KEY_TIME_LIMIT
	_set_combo_bar_color(Color("f5bd4b"))
	_update_combo_view()

func _close_combo_panel() -> void:
	combo_mode = false
	combo_buffer.clear()
	combo_box.visible = false
	parry_view.visible = attacking

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
	combo_view.call("show_input", combo_buffer.to_array())

func _effect_color(effect: String) -> Color:
	return COMBO_ARROW_SCRIPT.COLORS.get(effect, Color("fff0bc"))

func _set_combo_bar_color(color: Color) -> void:
	var fill := combo_bar.get_theme_stylebox("fill").duplicate() as StyleBoxFlat
	if fill == null:
		return
	fill.bg_color = color
	combo_bar.add_theme_stylebox_override("fill", fill)
	
func _cast_power(id: String) -> void:
	var p: Dictionary = POWERS[id]
	var cost: int = int(p["cost"])
	var value: int = int(p["value"])

	if ap < cost:
		await _lose_combo_turn("¡SIN PA! Pierdes el turno.",
				"SIN PA", Color("bd86ff"))
		return

	combo_mode = false
	combo_bar.value = KEY_TIME_LIMIT
	var effect_color := _effect_color(str(p["effect"]))
	_set_combo_bar_color(effect_color)
	combo_view.call("show_result", combo_buffer.to_array(), effect_color)
	_show_combo_impact(str(p["name"]), effect_color)
	var panel_tween := create_tween()
	panel_tween.tween_property(combo_view, "scale", Vector2(1.12, 1.12), 0.1) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	await panel_tween.finished
	var settle := create_tween()
	settle.tween_property(combo_view, "scale", Vector2.ONE, 0.12)
	await settle.finished
	_close_combo_panel()

	ap -= cost
	match p["kind"]:
		"single":
			_play_player_animation("cast_right", "attack_right")
			var foe := _first_alive_foe()
			_damage_foe(foe, value, StringName(p["effect"]))
			_log("%s: %d de daño a %s." % [p["name"], value, foe.display_name])
		"all":
			_play_player_animation("cast_right", "attack_right")
			for f in foes:
				if f.is_alive():
					_damage_foe(f, value, StringName(p["effect"]))
			_log("%s: %d de daño a todos." % [p["name"], value])
		"heal":
			var restored := _heal_player(value)
			_log("%s: recuperas %d de vida." % [p["name"], restored])
	_refresh()
	await get_tree().create_timer(STEP_DELAY).timeout
	_play_player_animation("idle_right")
	_next_turn()

func _show_combo_impact(power_name: String, color: Color) -> void:
	var ui := combo_box.get_parent() as Control
	var viewport_size := get_viewport().get_visible_rect().size
	var center := viewport_size / 2.0
	var overlay := Control.new()
	overlay.set_anchors_preset(Control.PRESET_FULL_RECT)
	overlay.mouse_filter = Control.MOUSE_FILTER_IGNORE
	overlay.z_index = 20
	ui.add_child(overlay)

	var flash := ColorRect.new()
	flash.color = Color(color, 0.34)
	flash.position = Vector2.ZERO
	flash.size = viewport_size
	flash.mouse_filter = Control.MOUSE_FILTER_IGNORE
	overlay.add_child(flash)

	var burst := Polygon2D.new()
	burst.position = center
	burst.polygon = _star_polygon(106.0, 62.0, 12)
	burst.color = Color(color, 0.8)
	burst.scale = Vector2(0.25, 0.25)
	overlay.add_child(burst)

	var ring := Line2D.new()
	for i in 33:
		ring.add_point(Vector2.from_angle(TAU * float(i) / 32.0) * 54.0)
	ring.width = 3.0
	ring.default_color = Color(color, 0.95)
	ring.position = center
	ring.scale = Vector2(0.2, 0.2)
	overlay.add_child(ring)

	var title := Label.new()
	title.text = power_name.to_upper()
	title.set_anchors_preset(Control.PRESET_CENTER)
	title.offset_left = -220.0
	title.offset_top = -36.0
	title.offset_right = 220.0
	title.offset_bottom = 36.0
	title.pivot_offset = Vector2(220.0, 36.0)
	title.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	title.vertical_alignment = VERTICAL_ALIGNMENT_CENTER
	title.add_theme_font_override("font", IMPACT_FONT)
	title.add_theme_font_size_override("font_size", 30)
	title.add_theme_color_override("font_color", color)
	title.add_theme_color_override("font_outline_color", Color("160d22"))
	title.add_theme_constant_override("outline_size", 6)
	title.add_theme_color_override("font_shadow_color", Color(color, 0.9))
	title.add_theme_constant_override("shadow_offset_x", 2)
	title.add_theme_constant_override("shadow_offset_y", 3)
	title.scale = Vector2(0.65, 0.65)
	title.modulate.a = 0.0
	overlay.add_child(title)

	var flash_tween := create_tween()
	flash_tween.tween_property(flash, "modulate:a", 0.0, 0.16)

	var burst_tween := create_tween().set_parallel(true)
	burst_tween.tween_property(burst, "scale", Vector2(1.45, 1.45), 0.42) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	burst_tween.tween_property(burst, "modulate:a", 0.0, 0.42)
	burst_tween.tween_property(ring, "scale", Vector2(1.7, 1.7), 0.4) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_OUT)
	burst_tween.tween_property(ring, "modulate:a", 0.0, 0.4)

	var title_tween := create_tween()
	title_tween.set_parallel(true)
	title_tween.tween_property(title, "scale", Vector2(1.08, 1.08), 0.14) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)
	title_tween.tween_property(title, "modulate:a", 1.0, 0.07)
	title_tween.chain().tween_interval(0.55)
	title_tween.chain().tween_property(title, "modulate:a", 0.0, 0.3)
	title_tween.finished.connect(overlay.queue_free)

func _star_polygon(outer_radius: float, inner_radius: float,
		point_count: int) -> PackedVector2Array:
	var polygon := PackedVector2Array()
	for i in point_count * 2:
		var radius := outer_radius if i % 2 == 0 else inner_radius
		polygon.append(Vector2.from_angle(TAU * float(i) / (point_count * 2)) * radius)
	return polygon

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
	parry_view.visible = true
	parry_view.active = true
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
		parry_view.visible = false
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
	parry_view.show_result(grade)
	var dmg: int = _roll(attacker.attack)
	if player.defending:
		dmg = ceili(dmg / 2.0)
	match grade:
		"perfect":
			dmg = 0
			_damage_foe(attacker, PARRY_COUNTER, &"parry")
			_log("¡PARRY perfecto! Contraatacas por %d." % PARRY_COUNTER)
		"good":
			dmg = ceili(dmg / 2.0)
			_log("Bloqueo parcial: %d de daño." % dmg)
		_:
			_log("%s te golpea: %d de daño." % [attacker.display_name, dmg])
	if dmg > 0:
		_damage_player(dmg)
	_refresh()
	# Si alguien cae, se cancelan los golpes que faltan
	if not player.is_alive() or not attacker.is_alive():
		strikes.clear()

func _end_enemy_phase() -> void:
	await get_tree().create_timer(END_DELAY).timeout
	_next_turn()

func _finish(won: bool) -> void:
	if over:
		return
	over = true
	_set_actions(false)
	attacking = false
	parry_view.active = false
	parry_view.visible = false
	_log("¡Victoria!" if won else "Has caído...")
	if won:
		await get_tree().create_timer(1.0).timeout
	else:
		await _animate_player_death()
	finished.emit(won, player.hp)

var _player_hit_stopped: bool = false

func _setup_button_motion(button: BaseButton) -> void:
	button.pivot_offset = button.size / 2.0
	button.mouse_entered.connect(func():
		if button.disabled:
			return
		_animate_button(button, Vector2(1.055, 1.055))
	)
	button.mouse_exited.connect(func():
		_animate_button(button, Vector2.ONE)
	)
	button.button_down.connect(func():
		_animate_button(button, Vector2(0.96, 0.96))
	)
	button.button_up.connect(func():
		_animate_button(button, Vector2.ONE)
	)

func _animate_button(button: BaseButton, target: Vector2) -> void:
	var tween := create_tween()
	tween.tween_property(button, "scale", target, 0.09) \
			.set_trans(Tween.TRANS_BACK).set_ease(Tween.EASE_OUT)

func _idle_bob(node: Node2D) -> Tween:
	var base := node.position.y
	var t := create_tween().set_loops()
	t.tween_property(node, "position:y", base - 1.5, 0.5).set_trans(Tween.TRANS_SINE)
	t.tween_property(node, "position:y", base + 1.5, 0.5).set_trans(Tween.TRANS_SINE)
	return t

func _animate_player_death() -> void:
	if _player_idle_tween != null and _player_idle_tween.is_running():
		_player_idle_tween.kill()
	var origin := player_sprite.position
	var base_scale := player_sprite.scale
	player_sprite.stop()
	player_sprite.modulate = Color.WHITE
	battle_effects.death(player_sprite.global_position)

	var collapse := create_tween()
	collapse.tween_property(player_sprite, "modulate", Color(2.2, 0.35, 0.4), 0.07)
	collapse.tween_property(player_sprite, "modulate", Color(1.0, 0.16, 0.24), 0.12)
	collapse.tween_property(player_sprite, "position", origin + Vector2(-5.0, 7.0), 0.18) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_IN)
	collapse.parallel().tween_property(player_sprite, "scale",
			base_scale * Vector2(1.3, 0.55), 0.18)
	collapse.parallel().tween_property(player_sprite, "rotation", deg_to_rad(-24.0), 0.18)
	collapse.tween_interval(0.12)
	collapse.tween_property(player_sprite, "position", origin + Vector2(8.0, 14.0), 0.55) \
			.set_trans(Tween.TRANS_CUBIC).set_ease(Tween.EASE_IN)
	collapse.parallel().tween_property(player_sprite, "scale",
			base_scale * Vector2(1.4, 0.28), 0.55)
	collapse.parallel().tween_property(player_sprite, "rotation", deg_to_rad(82.0), 0.55)
	collapse.parallel().tween_property(player_sprite, "modulate",
			Color(0.35, 0.22, 0.33, 0.0), 0.48).set_delay(0.07)
	await collapse.finished

func _lunge(node: Node2D, dx: float) -> void:
	var x0 := node.position.x
	var base_scale := node.scale
	var t := create_tween()
	t.tween_property(node, "position:x", x0 + dx, 0.09).set_trans(Tween.TRANS_BACK)
	t.parallel().tween_property(node, "scale", base_scale * Vector2(1.35, 0.76), 0.09)
	t.tween_property(node, "position:x", x0 - 4.0, 0.06).set_trans(Tween.TRANS_BACK)
	t.parallel().tween_property(node, "scale", base_scale * Vector2(0.82, 1.25), 0.06)
	t.tween_property(node, "position:x", x0, 0.16).set_trans(Tween.TRANS_ELASTIC)
	t.parallel().tween_property(node, "scale", base_scale, 0.16).set_trans(Tween.TRANS_ELASTIC)

func _damage_foe(foe: Combatant, amount: int, effect: StringName = &"physical") -> void:
	if not foe.is_alive() or amount <= 0:
		return
	foe.take_damage(amount)
	var view: FoeView = foe_rows[foe]["view"]
	view.play_damage(amount, effect, not foe.is_alive())

func _damage_player(amount: int) -> void:
	if amount <= 0 or not player.is_alive():
		return
	player.take_damage(amount)
	_animate_player_hit(amount)

func _heal_player(amount: int) -> int:
	var restored: int = mini(amount, player.max_hp - player.hp)
	if restored <= 0:
		return 0
	player.hp += restored
	battle_effects.heal(player_sprite.global_position, restored)
	_play_player_animation("heal_right", "idle_right")
	return restored

func _animate_player_hit(amount: int) -> void:
	_play_player_animation("hurt_right", "idle_right")
	battle_effects.damage(player_sprite.global_position, amount)
	var x0 := player_sprite.position.x
	var base_scale := player_sprite.scale
	player_sprite.modulate = Color(2.5, 2.5, 2.5)
	var flash := create_tween()
	flash.tween_property(player_sprite, "modulate", Color(1.0, 0.18, 0.16), 0.05)
	flash.tween_property(player_sprite, "modulate", Color.WHITE, 0.24)
	var s := create_tween()
	for dx in [-4.0, 4.0, -3.0, 3.0, 0.0]:
		s.tween_property(player_sprite, "position:x", x0 + dx, 0.04)
	var squash := create_tween()
	squash.tween_property(player_sprite, "scale", base_scale * Vector2(1.35, 0.72), 0.07)
	squash.tween_property(player_sprite, "scale", base_scale * Vector2(0.78, 1.35), 0.08)
	squash.tween_property(player_sprite, "scale", base_scale, 0.16) \
			.set_trans(Tween.TRANS_ELASTIC)
	var tilt := create_tween()
	tilt.tween_property(player_sprite, "rotation", deg_to_rad(-12.0), 0.07)
	tilt.tween_property(player_sprite, "rotation", deg_to_rad(8.0), 0.08)
	tilt.tween_property(player_sprite, "rotation", 0.0, 0.16)
	if not _player_hit_stopped and player_sprite.is_playing():
		_player_hit_stopped = true
		player_sprite.pause()
		_resume_player_after_hitstop()

func _resume_player_after_hitstop() -> void:
	await get_tree().create_timer(0.045, true).timeout
	_player_hit_stopped = false
	if is_instance_valid(player_sprite):
		player_sprite.play()

func _play_player_animation(animation_name: String, fallback: String = "idle_right") -> void:
	if player_sprite.sprite_frames == null:
		return
	if player_sprite.sprite_frames.has_animation(animation_name):
		player_sprite.play(animation_name)
	elif player_sprite.sprite_frames.has_animation(fallback):
		player_sprite.play(fallback)
	elif player_sprite.sprite_frames.has_animation("idle_right"):
		player_sprite.play("idle_right")
