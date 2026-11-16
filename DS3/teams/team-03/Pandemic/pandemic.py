"""Interfaz gráfica de Pandemic. El estado y las reglas pertenecen al motor Java."""
import ctypes
import json
import queue
import subprocess
import sys
import threading
import tkinter as tk
from pathlib import Path
from tkinter import messagebox, ttk

from musica import MusicaMenu
from tablero import Mapa


BASE = Path(getattr(sys, "_MEIPASS", Path(__file__).resolve().parent))
BG = "#0b0e12"
PAPER = "#131820"
FIELD = "#10151c"
INK = "#f1f4f8"
SUB = "#aeb9c8"
LINE = "#2a3645"
NAVY = "#111722"
DARK = "#090d13"
PRIMARY = "#2b4360"
ACCENT = "#a4c1e1"
SELECT = "#23354a"
PALE = "#192331"
FONT = "Segoe UI"
TONES = {
    "Azul": ("#b1d7ff", "#152131"),
    "Amarillo": ("#efd49d", "#26231e"),
    "Negro": ("#d4cfde", "#211f29"),
    "Rojo": ("#f3b3bb", "#2b2029"),
}


class Motor:
    def __init__(self, cantidad):
        java = BASE / "runtime" / "bin" / "java.exe"
        self.mensajes = queue.Queue()
        self.proceso = subprocess.Popen(
            [str(java), "-Dfile.encoding=UTF-8", "-cp", str(BASE / "motor.jar"),
             "juego.Main", "--grafico", str(cantidad)],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
            encoding="utf-8", errors="replace", bufsize=1,
            creationflags=getattr(subprocess, "CREATE_NO_WINDOW", 0),
            cwd=BASE,
        )
        threading.Thread(target=self.leer, daemon=True).start()

    def leer(self):
        with self.proceso.stdout as salida:
            for linea in salida:
                linea = linea.rstrip("\r\n")
                if linea.startswith("@P "):
                    try:
                        self.mensajes.put(json.loads(linea[3:]))
                    except json.JSONDecodeError:
                        self.mensajes.put({"evento": "registro", "texto": "Mensaje ilegible del motor."})
                else:
                    self.mensajes.put({"evento": "registro", "texto": linea})
        self.mensajes.put({"evento": "cerrado", "codigo": self.proceso.wait()})

    def responder(self, respuesta):
        self.proceso.stdin.write(respuesta + "\n")
        self.proceso.stdin.flush()

    def cerrar(self):
        if self.proceso.poll() is None:
            self.proceso.terminate()
            self.proceso.wait(timeout=3)
        self.proceso.stdin.close()


def texto(parent, value, size=11, weight="normal", fg=INK, bg=PAPER, **kwargs):
    return tk.Label(
        parent, text=value, font=(FONT, size, weight), fg=fg, bg=bg,
        anchor="w", justify="left", **kwargs,
    )


def boton(parent, value, command, kind="secondary", *, wrap=0):
    schemes = {
        "secondary": ("#202b39", INK, "#293b51"),
        "primary": (PRIMARY, INK, "#385775"),
        "dark": ("#1a2432", INK, "#2b3c50"),
    }
    bg, fg, hover = schemes[kind]
    item = tk.Button(
        parent, text=value, command=command, bg=bg, fg=fg, activebackground=hover,
        activeforeground=fg, disabledforeground=SUB, font=(FONT, 10, "bold"),
        highlightthickness=0, borderwidth=0, relief="flat", padx=12, pady=7,
        takefocus=True, cursor="hand2", wraplength=wrap, justify="left", anchor="w",
    )
    def cambiar_color(color):
        if item.cget("state") != "disabled":
            item.configure(bg=color)

    item.bind("<Enter>", lambda _: cambiar_color(hover))
    item.bind("<Leave>", lambda _: cambiar_color(bg))
    item.bind("<FocusIn>", lambda _: cambiar_color(hover))
    item.bind("<FocusOut>", lambda _: cambiar_color(bg))
    return item


class Pandemic:
    def __init__(self, root):
        self.root = root
        self.root.title("Pandemic")
        width = min(1480, self.root.winfo_screenwidth() - 50)
        height = min(920, self.root.winfo_screenheight() - 100)
        self.root.geometry(f"{width}x{height}")
        self.root.minsize(990, 685)
        self.root.configure(bg=BG)
        self.root.protocol("WM_DELETE_WINDOW", self.salir)
        try:
            self.root.iconbitmap(default=str(BASE / "pandemic.ico"))
        except tk.TclError:
            pass
        self.estilo()
        self.motor = None
        self.valores = {}
        self.espera = False
        self.terminada = False
        self.cantidad = tk.IntVar(value=3)
        self.pagina = None
        self.musica = MusicaMenu(self.root, BASE / "musica_menu.mp3")
        self.menu()

    def estilo(self):
        style = ttk.Style(self.root)
        style.theme_use("clam")
        style.configure(".", font=(FONT, 10), background=PAPER, foreground=INK,
                        bordercolor=LINE, lightcolor=LINE, darkcolor=LINE, focuscolor=ACCENT)
        style.configure("TNotebook", background=PAPER, borderwidth=0, tabmargins=(0, 1, 0, 0))
        style.configure("TNotebook.Tab", padding=(11, 5), background=PAPER, foreground=SUB)
        style.map("TNotebook.Tab", background=[("selected", SELECT), ("active", PALE)],
                  foreground=[("selected", INK)])
        style.configure("Treeview", background=FIELD, fieldbackground=FIELD, foreground=INK,
                        borderwidth=0, rowheight=27)
        style.configure("Treeview.Heading", background=PALE, foreground=INK,
                        font=(FONT, 9, "bold"), relief="flat", padding=(8, 5))
        style.map("Treeview", background=[("selected", SELECT)], foreground=[("selected", INK)])
        style.map("Treeview.Heading", background=[("active", SELECT)])
        style.configure("TEntry", fieldbackground=FIELD, foreground=INK, insertcolor=INK, padding=5)
        style.configure("TCombobox", fieldbackground=FIELD, foreground=INK, arrowcolor=ACCENT,
                        background=PALE, selectbackground=SELECT, selectforeground=INK, padding=5)
        style.map("TCombobox", fieldbackground=[("readonly", FIELD)], foreground=[("readonly", INK)],
                  background=[("active", SELECT)], selectbackground=[("readonly", SELECT)],
                  selectforeground=[("readonly", INK)])
        style.configure("TCheckbutton", background=PAPER, foreground=INK,
                        indicatorbackground=FIELD, indicatorforeground=ACCENT)
        style.map("TCheckbutton", background=[("active", PAPER)],
                  indicatorbackground=[("selected", SELECT)])
        style.configure("Vertical.TScrollbar", background=PALE, troughcolor=FIELD, arrowcolor=ACCENT)
        style.map("Vertical.TScrollbar", background=[("active", SELECT)])
        self.root.option_add("*TCombobox*Listbox.background", FIELD)
        self.root.option_add("*TCombobox*Listbox.foreground", INK)
        self.root.option_add("*TCombobox*Listbox.selectBackground", SELECT)
        self.root.option_add("*TCombobox*Listbox.selectForeground", INK)

    def pantalla(self, color):
        if self.pagina is not None:
            self.pagina.destroy()
        self.pagina = tk.Frame(self.root, bg=color)
        self.pagina.pack(fill="both", expand=True)
        return self.pagina

    def menu(self):
        page = self.pantalla(DARK)
        header = tk.Frame(page, bg=NAVY, padx=38, pady=17)
        header.pack(fill="x")
        texto(header, "◈  PANDEMIC", 13, "bold", ACCENT, NAVY).pack(side="left")
        boton(header, "Salir", self.salir, "dark").pack(side="right")

        body = tk.Frame(page, bg=DARK, padx=50, pady=34)
        body.pack(fill="both", expand=True)
        body.columnconfigure(1, weight=1)
        body.rowconfigure(0, weight=1)
        lead = tk.Frame(body, bg=DARK)
        lead.grid(row=0, column=0, sticky="w", padx=(0, 24))
        texto(lead, "Pandemic", 55, "bold", INK, DARK).pack(anchor="w")
        texto(lead, "Juego cooperativo por turnos", 15, fg=SUB, bg=DARK).pack(anchor="w", pady=(0, 32))

        setup = tk.Frame(lead, bg=NAVY, padx=20, pady=20, highlightthickness=1,
                         highlightbackground=LINE)
        setup.pack(fill="x")
        texto(setup, "Jugadores", 12, "bold", INK, NAVY).pack(anchor="w", pady=(0, 12))
        selectors = tk.Frame(setup, bg=NAVY)
        selectors.pack(fill="x")
        for n in (2, 3):
            choice = tk.Radiobutton(
                selectors, text=str(n), variable=self.cantidad, value=n, indicatoron=False,
                font=(FONT, 10, "bold"), bg=PALE, fg=INK, selectcolor=PRIMARY,
                activebackground=SELECT, activeforeground=INK, bd=0, highlightthickness=1,
                highlightcolor=ACCENT, highlightbackground=NAVY, cursor="hand2", padx=11, pady=7,
            )
            choice.pack(side="left", padx=(0, 4))
        start = boton(setup, "Nueva partida  →", self.iniciar, "primary")
        start.pack(fill="x", pady=(18, 0))
        start.focus_set()
        canvas = tk.Canvas(body, bg=DARK, bd=0, highlightthickness=0, width=455)
        canvas.grid(row=0, column=1, sticky="nsew")
        canvas.bind("<Configure>", self.mapa_menu)
        self.musica.iniciar()

    def mapa_menu(self, event):
        c = event.widget
        c.delete("all")
        sx, sy = event.width / 620, event.height / 540
        scale = min(sx, sy)
        ox, oy = (event.width - 620 * scale) / 2, (event.height - 540 * scale) / 2

        def point(x, y):
            return ox + x * scale, oy + y * scale

        for x in range(20, 621, 32):
            for y in range(36, 523, 32):
                px, py = point(x, y)
                c.create_oval(px, py, px + 2, py + 2, fill="#233042", outline="")
        for r in (86, 155, 227):
            c.create_oval(*point(243 - r, 291 - r), *point(243 + r, 291 + r),
                          outline="#1b293b", width=1)
        cities = {
            "San Francisco": (44, 208), "Chicago": (177, 166), "Montreal": (286, 119),
            "Nueva York": (391, 166), "Londres": (507, 95), "Madrid": (555, 218),
            "Atlanta": (243, 291), "Washington": (360, 255), "Miami": (323, 367),
            "Ciudad de Mexico": (123, 341), "Bogota": (237, 446), "Sao Paulo": (405, 474),
        }
        edges = [
            ("San Francisco", "Chicago"), ("Chicago", "Montreal"), ("Chicago", "Atlanta"),
            ("Chicago", "Ciudad de Mexico"), ("Montreal", "Nueva York"),
            ("Montreal", "Washington"), ("Nueva York", "Londres"), ("Nueva York", "Madrid"),
            ("Nueva York", "Washington"), ("Londres", "Madrid"), ("Atlanta", "Miami"),
            ("Washington", "Atlanta"), ("Washington", "Miami"), ("Miami", "Ciudad de Mexico"),
            ("Miami", "Bogota"), ("Ciudad de Mexico", "Bogota"), ("Bogota", "Sao Paulo"),
            ("Madrid", "Sao Paulo"),
        ]
        for a, b in edges:
            active = "Atlanta" in (a, b)
            c.create_line(*point(*cities[a]), *point(*cities[b]),
                          fill=ACCENT if active else "#3c5470", width=2 if active else 1)
        for name, (x, y) in cities.items():
            active = name == "Atlanta"
            px, py = point(x, y)
            if active:
                c.create_oval(px - 14, py - 14, px + 14, py + 14, fill=SELECT, outline=ACCENT)
            accent = "#f3c777" if name in {"Miami", "Ciudad de Mexico", "Bogota", "Sao Paulo"} else "#8ddcfa"
            radius = 6 if active else 4
            c.create_oval(px - radius, py - radius, px + radius, py + radius,
                          fill=ACCENT if active else accent, outline="")
            c.create_text(px, py + (24 if active else 18), text=name, fill=ACCENT if active else SUB,
                          font=(FONT, 11 if active else 9, "bold" if active else "normal"))
        c.create_text(*point(35, 500), text="RED DE CIUDADES  ·  SECTOR ATLÁNTICO",
                      font=(FONT, 8, "bold"), fill=SUB, anchor="w")

    def iniciar(self):
        try:
            self.motor = Motor(self.cantidad.get())
        except OSError as exc:
            messagebox.showerror("No se pudo iniciar la partida", str(exc), parent=self.root)
            return
        self.valores = {}
        self.elegida = None
        self.ampliada = None
        self.terminada = False
        self.espera = False
        self.musica.detener()
        self.juego()
        self.root.after(20, self.recibir, self.motor)

    def juego(self):
        page = self.pantalla(BG)
        self.top = tk.Frame(page, bg=NAVY, padx=18, pady=6)
        self.top.pack(fill="x")
        texto(self.top, "◈  PANDEMIC", 12, "bold", ACCENT, NAVY).pack(side="left")
        self.turn_label = texto(self.top, "Iniciando partida…", 11, "bold", INK, NAVY, padx=18)
        boton(self.top, "Menú principal", self.regresar, "dark").pack(side="right")
        self.turn_label.pack(side="left", fill="x", expand=True)

        body = tk.Frame(page, bg=BG, padx=14, pady=8)
        body.pack(fill="both", expand=True)
        tools = tk.Frame(body, bg=BG)
        tools.pack(fill="x", pady=(0, 7))
        tools.columnconfigure(0, weight=5, minsize=465)
        tools.columnconfigure(1, weight=4)
        self.crear_acciones(tools)
        self.crear_marcadores(tools)

        self.feedback = texto(body, "Preparando el tablero…", 9, fg=SUB, bg=BG)
        self.feedback.pack(side="bottom", fill="x", pady=(4, 0))

        content = tk.Frame(body, bg=BG)
        content.pack(fill="both", expand=True)
        self.tablero = tk.Frame(content, bg=PAPER, padx=8, pady=6, highlightthickness=1,
                                highlightbackground=LINE)
        self.tablero.pack(side="left", fill="both", expand=True)
        self.panel_partida = tk.Frame(content, bg=BG)
        self.crear_pestanas(self.panel_partida)
        self.crear_tablero(self.tablero)

    def crear_marcadores(self, tools):
        counts = tk.Frame(tools, bg=PAPER, padx=7, pady=4, highlightthickness=1,
                          highlightbackground=LINE)
        counts.grid(row=0, column=1, sticky="nsew", padx=(7, 0))
        counts.rowconfigure(0, weight=1)
        self.metricas = {}
        for i, label in enumerate(("Brotes", "Tasa de infección", "Centros", "Curas")):
            tile = tk.Frame(counts, bg=PAPER, padx=5)
            counts.columnconfigure(i, weight=1)
            tile.grid(row=0, column=i)
            texto(tile, label, 9, "bold", SUB).pack(anchor="center")
            item = texto(tile, "—", 14, "bold")
            item.pack(anchor="center")
            self.metricas[label] = item

    def alternar_panel_partida(self):
        if self.panel_partida.winfo_manager():
            self.panel_partida.pack_forget()
            self.boton_panel.configure(text="Mano, equipo y registro")
        else:
            self.panel_partida.pack(side="right", fill="y", padx=(8, 0), before=self.tablero)
            self.boton_panel.configure(text="Ocultar panel")

    def crear_tablero(self, board):
        barra = tk.Frame(board, bg=PAPER)
        barra.pack(fill="x", pady=(0, 5))
        texto(barra, "Tablero", 15, "bold").pack(side="left", padx=(1, 9))
        self.total_ciudades = texto(barra, "", 9, fg=SUB)
        self.total_ciudades.pack(side="left")
        search = tk.Frame(barra, bg=PAPER)
        search.pack(side="right")
        self.filtro = tk.StringVar()
        self.infectadas = tk.BooleanVar()
        self.filtro.trace_add("write", lambda *_: self.actualizar_tablero())
        texto(search, "Buscar ciudad", 9, fg=SUB).pack(side="left", padx=(0, 5))
        ttk.Entry(search, textvariable=self.filtro, width=12).pack(side="left")
        ttk.Checkbutton(search, text="Solo con cubos", variable=self.infectadas,
                        command=self.actualizar_tablero).pack(side="left", padx=(6, 0))

        vistas = ttk.Notebook(board)
        vistas.pack(fill="both", expand=True)
        vista_mapa = tk.Frame(vistas, bg=PAPER)
        table = tk.Frame(vistas, bg=PAPER)
        vistas.add(vista_mapa, text="Mapa")
        vistas.add(table, text="Lista de ciudades")
        self.mapa = Mapa(vista_mapa, self.elegir_ciudad, width=500, height=285)
        self.mapa.pack(fill="both", expand=True)
        cols = [("ciudad", "Ciudad", 166), ("color", "Color", 85),
                ("cubos", "Cubos", 58), ("centro", "Centro", 59), ("peones", "Peones", 61)]
        self.ciudades = ttk.Treeview(table, show="headings", columns=[x[0] for x in cols],
                                     selectmode="browse", height=9)
        for column, title, width in cols:
            self.ciudades.heading(column, text=title, anchor="w" if column == "ciudad" else "center")
            self.ciudades.column(column, width=width, anchor="w" if column == "ciudad" else "center",
                                 minwidth=45, stretch=column in ("ciudad", "color"))
        for name, (fg, bg) in TONES.items():
            self.ciudades.tag_configure(name, foreground=fg, background=bg)
        scroll = ttk.Scrollbar(table, orient="vertical", command=self.ciudades.yview)
        self.ciudades.configure(yscrollcommand=scroll.set)
        scroll.pack(side="right", fill="y")
        self.ciudades.pack(side="left", fill="both", expand=True)
        self.ciudades.bind("<<TreeviewSelect>>", self.elegir_en_lista)
        foot = tk.Frame(board, bg=PAPER)
        foot.pack(fill="x", pady=(5, 0))
        controls = tk.Frame(foot, bg=PAPER)
        controls.pack(side="right", padx=(8, 0))
        botones = tk.Frame(controls, bg=PAPER)
        botones.pack(anchor="e")
        self.boton_panel = boton(botones, "Mano, equipo y registro", self.alternar_panel_partida)
        self.boton_panel.pack(side="right")
        boton(botones, "Ampliar mapa", self.ampliar_mapa).pack(side="right", padx=(0, 5))
        texto(controls, "Cubos (número)    □ Centro    ● Peón", 9, fg=SUB).pack(anchor="e")

        detail = tk.Frame(foot, bg=PALE, padx=7, pady=4)
        detail.pack(side="left", fill="both", expand=True)
        self.nombre_ciudad = texto(detail, "Conexiones", 10, "bold", INK, PALE)
        self.nombre_ciudad.pack(fill="x")
        self.vecinos_ciudad = texto(detail, "Selecciona una ciudad del tablero.", 9, fg=INK,
                                    bg=PALE, wraplength=340)
        self.vecinos_ciudad.pack(fill="x")
        def ajustar_detalle(event):
            ancho = max(120, event.width - 17)
            self.nombre_ciudad.configure(wraplength=ancho)
            self.vecinos_ciudad.configure(wraplength=ancho)

        detail.bind("<Configure>", ajustar_detalle)

    def crear_acciones(self, tools):
        card = tk.Frame(tools, bg=PAPER, padx=8, pady=5, highlightthickness=1,
                        highlightbackground=LINE)
        card.grid(row=0, column=0, sticky="nsew")
        top = tk.Frame(card, bg=PAPER)
        top.pack(fill="x")
        self.accion_titulo = texto(top, "Acciones del turno", 11, "bold")
        self.accion_titulo.pack(side="left")
        self.restantes = texto(top, "", 9, "bold", INK)
        self.restantes.pack(side="right")
        self.pregunta_label = texto(card, "", 9, fg=SUB, wraplength=425)
        self.opciones = tk.Frame(card, bg=PAPER)
        self.opciones.pack(fill="x", pady=(3, 0))
        self.mostrar_pregunta("Esperando al motor de juego…")

    def mostrar_pregunta(self, mensaje):
        if mensaje:
            self.pregunta_label.configure(text=mensaje)
            self.pregunta_label.pack(fill="x", before=self.opciones)
        else:
            self.pregunta_label.pack_forget()

    def crear_pestanas(self, side):
        card = tk.Frame(side, bg=PAPER, padx=5, pady=5, width=320, highlightthickness=1,
                        highlightbackground=LINE)
        card.pack(fill="both", expand=True)
        card.pack_propagate(False)
        tabs = ttk.Notebook(card)
        tabs.pack(fill="both", expand=True)
        player = tk.Frame(tabs, bg=PAPER, padx=8, pady=5)
        team = tk.Frame(tabs, bg=PAPER, padx=7, pady=5)
        log = tk.Frame(tabs, bg=PAPER, padx=6, pady=5)
        tabs.add(player, text="Mano")
        tabs.add(team, text="Equipo y curas")
        tabs.add(log, text="Registro")
        self.mano_title = texto(player, "", 10, "bold", INK, wraplength=285)
        self.mano_title.pack(fill="x", pady=(0, 3))
        self.mano = self.caja_texto(player)
        self.equipo = self.caja_texto(team)
        self.registro = self.caja_texto(log)
        for widget in (self.mano, self.equipo, self.registro):
            for color, (fg, _) in TONES.items():
                widget.tag_configure(color, foreground=fg)
            widget.tag_configure("activo", foreground=INK, font=(FONT, 10, "bold"))

    def caja_texto(self, parent):
        scroll = ttk.Scrollbar(parent)
        scroll.pack(side="right", fill="y")
        box = tk.Text(parent, bg=PAPER, fg=INK, font=(FONT, 10), bd=0, highlightthickness=0,
                      wrap="word", padx=4, height=4, state="disabled", yscrollcommand=scroll.set)
        box.pack(side="left", fill="both", expand=True)
        scroll.configure(command=box.yview)
        return box

    def recibir(self, fuente):
        if self.motor is not fuente:
            return
        try:
            for _ in range(180):
                item = fuente.mensajes.get_nowait()
                evento = item["evento"]
                if evento == "registro":
                    self.anotar(item["texto"])
                elif evento == "pregunta":
                    self.datos(item["estado"])
                    self.pregunta(item)
                elif evento == "fin":
                    self.datos(item["estado"])
                    self.fin(item["resultado"])
                elif evento == "cerrado":
                    if not self.terminada:
                        self.fin("El motor se cerró", "Consulta el registro para ver el último mensaje.")
                    return
        except queue.Empty:
            pass
        self.root.after(35, self.recibir, fuente)

    def anotar(self, linea):
        if not linea.strip() or set(linea.strip()) <= {"=", "*"}:
            return
        self.registro.configure(state="normal")
        self.registro.insert("end", linea.strip() + "\n")
        self.registro.see("end")
        self.registro.configure(state="disabled")
        if not linea.startswith(("Seleccione una acción", "--- Acciones restantes")):
            self.feedback.configure(text=linea.strip())

    def datos(self, datos):
        self.valores = datos
        self.ciudad_por_nombre = {c["nombre"]: c for c in datos["ciudades"]}
        indice = datos["jugador"]
        jugador = datos["jugadores"][indice]
        self.turn_label.configure(
            text=f"Turno {datos['turno']}  ·  Jugador {indice + 1}  ·  {jugador['rol']} en {jugador['ciudad']}"
        )
        self.restantes.configure(text=f"{datos['acciones']} disponibles")
        curas = datos["curas"]
        values = [f"{datos['brotes']} / {datos['maxBrotes']}", datos["tasa"],
                  f"{datos['centros']} / {datos['maxCentros']}", f"{sum(c['descubierta'] for c in curas)} / {len(curas)}"]
        for item, value in zip(self.metricas.values(), values):
            item.configure(text=value)
        self.actualizar_tablero()

        self.mano_title.configure(text=f"Jugador {indice + 1}  ·  {jugador['rol']}  ·  {len(jugador['mano'])} cartas")
        self.mano.configure(state="normal")
        self.mano.delete("1.0", "end")
        for carta in jugador["mano"]:
            self.mano.insert("end", f"{carta['nombre']}  ·  {carta['tipo']}\n", carta["color"])
        if not jugador["mano"]:
            self.mano.insert("end", "Sin cartas")
        self.mano.configure(state="disabled")
        self.equipo.configure(state="normal")
        self.equipo.delete("1.0", "end")
        for i, miembro in enumerate(datos["jugadores"]):
            tag = "activo" if i == datos["jugador"] else ""
            self.equipo.insert("end", f"Jugador {i + 1}  ·  {miembro['rol']}  ·  {miembro['ciudad']}\n", tag)
            self.equipo.insert("end", f"{miembro['descripcion']}\n\n")
        self.equipo.insert("end", "Curas\n", "activo")
        for cura in curas:
            estado = "Descubierta" if cura["descubierta"] else "Pendiente"
            self.equipo.insert("end", f"{cura['color']}  ·  {estado}\n", cura["color"])
        self.equipo.configure(state="disabled")

    def actualizar_tablero(self):
        if not self.valores:
            return
        start = self.ciudades.yview()[0]
        names = self.ciudades.get_children()
        if names:
            self.ciudades.delete(*names)
        filtro = self.filtro.get().strip().casefold()
        for c in self.valores["ciudades"]:
            if filtro not in c["nombre"].casefold() or (self.infectadas.get() and not c["cubos"]):
                continue
            peones = sum(p["ciudad"] == c["nombre"] for p in self.valores["jugadores"])
            cells = (c["nombre"], c["color"], c["cubos"], "Sí" if c["centro"] else "—", peones or "—")
            self.ciudades.insert("", "end", iid=c["nombre"], values=cells, tags=(c["color"],))
        self.visibles = self.ciudades.get_children()
        self.total_ciudades.configure(text=f"{len(self.visibles)} / {len(self.valores['ciudades'])} ciudades")
        if self.elegida is None or (self.visibles and self.elegida not in self.visibles):
            jugador = self.valores["jugadores"][self.valores["jugador"]]["ciudad"]
            self.elegida = jugador if jugador in self.visibles else next(iter(self.visibles), jugador)
        if self.ciudades.exists(self.elegida):
            self.ciudades.selection_set(self.elegida)
            self.ciudades.yview_moveto(start)
            self.ciudades.see(self.elegida)
        self.refrescar_mapa()

    def elegir_en_lista(self, _event):
        sel = self.ciudades.selection()
        if sel and sel[0] != self.elegida:
            self.elegir_ciudad(sel[0])

    def elegir_ciudad(self, nombre):
        self.elegida = nombre
        if self.ciudades.exists(nombre):
            self.ciudades.selection_set(nombre)
            self.ciudades.see(nombre)
        self.refrescar_mapa()

    def refrescar_mapa(self):
        self.mapa.mostrar(self.valores, self.visibles, self.elegida)
        c = self.ciudad_por_nombre[self.elegida]
        detalles = "  ·  ".join(c["vecinos"])
        indicadores = f"{c['color']}  ·  {c['cubos']} cubos"
        if c["centro"]:
            indicadores += "  ·  Centro"
        peones = sum(p["ciudad"] == c["nombre"] for p in self.valores["jugadores"])
        if peones:
            indicadores += f"  ·  {peones} peón" if peones == 1 else f"  ·  {peones} peones"
        self.nombre_ciudad.configure(text=f"{c['nombre']}  ·  {indicadores}")
        self.vecinos_ciudad.configure(text=f"Conexiones: {detalles}")
        if self.ampliada is not None and self.ampliada.winfo_exists():
            self.mapa_grande.mostrar(self.valores, self.visibles, self.elegida)
            self.detalle_grande.configure(text=f"{c['nombre']}  ·  {indicadores}  |  {detalles}")

    def ampliar_mapa(self):
        if not self.valores:
            return
        if self.ampliada is not None and self.ampliada.winfo_exists():
            self.ampliada.lift()
            return
        self.ampliada = tk.Toplevel(self.root, bg=BG)
        self.ampliada.title("Pandemic · Tablero")
        w = min(1510, self.root.winfo_screenwidth() - 100)
        h = min(875, self.root.winfo_screenheight() - 130)
        self.ampliada.geometry(f"{w}x{h}")
        self.ampliada.transient(self.root)
        self.ampliada.bind("<Escape>", lambda _: self.ampliada.destroy())
        barra = tk.Frame(self.ampliada, padx=12, pady=8, bg=BG)
        barra.pack(fill="x")
        self.detalle_grande = texto(barra, "", 10, fg=INK, bg=BG, wraplength=max(360, w - 210))
        self.detalle_grande.pack(side="left", fill="x", expand=True)
        boton(barra, "Cerrar mapa", self.ampliada.destroy).pack(side="right")
        self.mapa_grande = Mapa(self.ampliada, self.elegir_ciudad)
        self.mapa_grande.pack(fill="both", expand=True)
        self.refrescar_mapa()

    def limpiar_opciones(self):
        for child in self.opciones.winfo_children():
            child.destroy()

    def pregunta(self, item):
        self.limpiar_opciones()
        self.espera = True
        tipo = item["tipo"]
        encabezado = "Acciones" if tipo == "accion" else "Decisión"
        self.accion_titulo.configure(text=f"{encabezado}  ·  Jugador {item['responsable'] + 1}")
        titulo = item["texto"].replace(" (s/n)", "").replace(" (o -1 para cancelar)", "").rstrip(": ")
        self.mostrar_pregunta("" if tipo == "accion" else titulo)
        alternativas = [(x["valor"], x["etiqueta"]) for x in item["opciones"]]

        if tipo == "accion":
            self.selector(alternativas, placeholder="Elige una acción", confirmar_texto="Ejecutar")
        elif tipo in ("si_no", "turno", "cura"):
            if tipo == "cura":
                alternativas = [(c["color"], c["color"]) for c in self.valores["curas"]]
            elif tipo == "turno":
                alternativas = [("s", "Siguiente turno"), ("n", "Finalizar partida")]
            else:
                alternativas = [("s", "Sí"), ("n", "No")]
            for value, label in alternativas:
                boton(self.opciones, label, lambda v=value: self.responder(v),
                      "primary" if value == "s" else "secondary").pack(side="left", padx=(0, 5))
        elif tipo == "ciudad":
            self.selector([(c["nombre"], c["nombre"]) for c in self.valores["ciudades"]])
        elif alternativas:
            self.selector(alternativas)
        else:
            self.mostrar_pregunta("No hay opciones disponibles para esta acción.")
            boton(self.opciones, "Volver a acciones", lambda: self.responder("-2")).pack(anchor="w")

    def selector(self, alternativas, *, placeholder=None, confirmar_texto="Confirmar"):
        row = tk.Frame(self.opciones, bg=PAPER)
        row.pack(fill="x")
        box = ttk.Combobox(row, state="readonly", width=24, height=12,
                           values=[label for _, label in alternativas])
        box.pack(side="left", fill="x", expand=True, padx=(0, 6))
        if placeholder is None:
            box.current(0)
        else:
            box.set(placeholder)

        def confirmar():
            idx = box.current()
            if idx >= 0:
                self.responder(alternativas[idx][0])

        aceptar = boton(row, confirmar_texto, confirmar, "primary")
        aceptar.pack(side="right")
        if placeholder is not None:
            aceptar.configure(state="disabled", bg=PALE)

        detalle = None
        if any(len(label) > 45 for _, label in alternativas):
            detalle = texto(self.opciones, box.get(), 9, fg=SUB, wraplength=425)
            detalle.pack(fill="x", pady=(3, 0))

        def cambiar_opcion(_event):
            aceptar.configure(state="normal", bg=PRIMARY)
            if detalle is not None:
                detalle.configure(text=box.get())

        box.bind("<<ComboboxSelected>>", cambiar_opcion)
        box.bind("<Return>", lambda _: confirmar())
        box.focus_set()

    def responder(self, value):
        if not self.espera:
            return
        self.espera = False
        self.limpiar_opciones()
        self.mostrar_pregunta("Procesando…")
        try:
            self.motor.responder(value)
        except (OSError, ValueError):
            self.fin("El motor se cerró", "La respuesta no pudo enviarse.")

    def fin(self, resultado, detail=None):
        self.terminada = True
        self.espera = False
        self.limpiar_opciones()
        self.accion_titulo.configure(text=resultado)
        self.restantes.configure(text="")
        self.mostrar_pregunta(detail or "Puedes consultar el tablero y el registro de la partida.")
        boton(self.opciones, "Menú principal", self.regresar, "primary").pack(anchor="w")

    def regresar(self):
        if not self.terminada and not messagebox.askyesno(
                "Volver al menú", "¿Quieres cerrar la partida actual?", parent=self.root):
            return
        if self.ampliada is not None and self.ampliada.winfo_exists():
            self.ampliada.destroy()
        self.cerrar_motor()
        self.menu()

    def cerrar_motor(self):
        if self.motor is not None:
            motor = self.motor
            self.motor = None
            motor.cerrar()

    def salir(self):
        if self.motor is not None and not self.terminada and not messagebox.askyesno(
                "Salir de Pandemic", "¿Quieres cerrar la partida y salir?", parent=self.root):
            return
        self.cerrar_motor()
        self.musica.detener()
        self.root.destroy()


def establecer_dpi():
    if sys.platform == "win32":
        ctypes.windll.user32.SetProcessDPIAware()


if __name__ == "__main__":
    establecer_dpi()
    root = tk.Tk()
    Pandemic(root)
    root.mainloop()
