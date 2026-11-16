"""Vista esquemática de las conexiones del tablero."""
import tkinter as tk

# Posiciones de dibujo. Las ciudades y aristas se cargan desde la partida.
POSICIONES = {
    "San Francisco": (68, 245), "Los Angeles": (63, 345), "Chicago": (160, 183),
    "Montreal": (270, 129), "Nueva York": (355, 182), "Washington": (336, 264),
    "Atlanta": (207, 268), "Miami": (277, 348), "Ciudad de Mexico": (113, 405),
    "Bogota": (232, 471), "Lima": (172, 554), "Santiago": (177, 688),
    "Buenos Aires": (274, 749), "Sao Paulo": (339, 627),
    "Londres": (467, 148), "Essen": (565, 118), "San Petersburgo": (690, 78),
    "Madrid": (447, 274), "Paris": (518, 217), "Milan": (608, 222),
    "Moscú": (805, 118), "Estambul": (701, 228),
    "Argel": (487, 360), "El Cairo": (636, 366), "Lagos": (482, 502),
    "Jartum": (626, 453), "Kinsasa": (532, 582), "Johannesburgo": (606, 703),
    "Bagdad": (753, 317), "Riad": (729, 432), "Teheran": (851, 273),
    "Karachi": (866, 408), "Bombay": (904, 517), "Nueva Delhi": (962, 328),
    "Calcuta": (1004, 413), "Chennai": (992, 610),
    "Pekin": (1071, 223), "Seul": (1193, 146), "Tokio": (1340, 182),
    "Shangai": (1120, 310), "Osaka": (1300, 271), "Taipéi": (1232, 371),
    "Hong Kong": (1132, 400), "Bangkok": (1058, 498), "Ho Chi Minh": (1155, 526),
    "Manila": (1265, 474), "Yakarta": (1191, 655), "Sídney": (1369, 720),
}
COLORES = {"Azul": "#78cbfa", "Amarillo": "#f4c66d", "Negro": "#c5c0d5", "Rojo": "#ff8d98"}
FONDO = "#0d121a"
TRANS_PACIFICO = {
    frozenset(("San Francisco", "Tokio")): 54,
    frozenset(("San Francisco", "Manila")): 32,
    frozenset(("Los Angeles", "Sídney")): 790,
}


class Mapa(tk.Canvas):
    def __init__(self, parent, seleccionar, **kwargs):
        super().__init__(parent, bg=FONDO, borderwidth=0, highlightthickness=0, **kwargs)
        self.seleccionar = seleccionar
        self.ciudades = {}
        self.peones = {}
        self.visible = set()
        self.elegida = None
        self.bind("<Configure>", lambda _: self.dibujar())

    def mostrar(self, estado, visibles, elegida):
        self.ciudades = {c["nombre"]: c for c in estado["ciudades"]}
        self.peones = {}
        for p in estado["jugadores"]:
            self.peones[p["ciudad"]] = self.peones.get(p["ciudad"], 0) + 1
        self.visible = set(visibles)
        self.elegida = elegida
        self.dibujar()

    def dibujar(self):
        self.delete("all")
        w, h = self.winfo_width(), self.winfo_height()
        if not self.ciudades or min(w, h) <= 10:
            return
        sx, sy = (w - 16) / 1450, (h - 16) / 840
        scale = min(sx, sy)
        radius = max(3, int(11 * scale))
        size = max(8, min(15, int(scale * 24)))
        selected = self.ciudades.get(self.elegida, {})
        highlighted = {self.elegida, *selected.get("vecinos", [])} if selected else set()

        def xy(x, y):
            return 8 + x * sx, 8 + y * sy

        for x in range(35, 1440, 55):
            for y in range(35, 820, 55):
                px, py = xy(x, y)
                self.create_rectangle(px, py, px + 1, py + 1, fill="#233147", outline="")

        edges = set()
        for a, ciudad in self.ciudades.items():
            for b in ciudad["vecinos"]:
                edge = frozenset((a, b))
                if edge in edges:
                    continue
                edges.add(edge)
                ax, ay = POSICIONES[a]
                bx, by = POSICIONES[b]
                passthrough = TRANS_PACIFICO.get(edge)
                is_selected = self.elegida in edge
                route = [*xy(ax, ay), *xy(bx, by)]
                if passthrough:
                    route[2:2] = [*xy(ax, passthrough), *xy(bx, passthrough)]
                self.create_line(route, fill="#9ebadb" if is_selected else "#455875",
                                 width=2 if is_selected else 1,
                                 dash=(4, 3) if passthrough else ())

        for nombre, c in self.ciudades.items():
            x, y = xy(*POSICIONES[nombre])
            shown = nombre in self.visible
            color = COLORES[c["color"]] if shown else "#59677a"
            is_selected = nombre == self.elegida
            tag = f"ciudad{len(self.find_all())}"
            if is_selected or c["centro"]:
                r = radius + 4
                shape = self.create_oval if is_selected else self.create_rectangle
                shape(x-r, y-r, x+r, y+r, outline="#c1d5ed" if is_selected else "#d9e1ee",
                      width=2 if is_selected else 1, tags=tag)
            self.create_oval(x-radius, y-radius, x+radius, y+radius, fill=color, outline=FONDO, tags=tag)
            if c["cubos"]:
                self.create_text(x + radius + 5, y - radius - 1, text=str(c["cubos"]),
                                 fill=color, font=("Segoe UI", -size, "bold"), tags=tag)
            if nombre in self.peones:
                rr = max(2, radius - 1)
                px = x + 4 + radius
                self.create_oval(px-rr, y-rr, px+rr, y+rr, fill="#e0eaf8", outline=FONDO, tags=tag)
            label_y = y + radius + 4
            label = nombre
            if w < 740 and nombre == "Ciudad de Mexico":
                label = "Ciudad de Mex."
            self.create_text(x, label_y, text=label, fill="#f1f4f8" if shown else "#768499",
                             font=("Segoe UI", -size, "bold" if nombre in highlighted else "normal"),
                             anchor="n", tags=tag)
            self.tag_bind(tag, "<Button-1>", lambda _, n=nombre: self.seleccionar(n))
            self.tag_bind(tag, "<Enter>", lambda _: self.configure(cursor="hand2"))
            self.tag_bind(tag, "<Leave>", lambda _: self.configure(cursor=""))
