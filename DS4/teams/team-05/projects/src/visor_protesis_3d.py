"""
Modulo 5 (RF-05): Simulador Grafico 3D de la Protesis Transradial.
Autor de modulo: Gestion grafica 3D e integracion (DS-262 / team-05).

Adaptacion directa a los modulos del equipo:
- Cumple el contrato de ArbolCinematico:
    * VisorProtesis3D.crear_si_disponible(arbol)
    * visor.actualizar_escena()
- Lee exclusivamente los resultados ya propagados en preorden por NodoArticulacion
  y Matriz4x4 (nunca muta el arbol ni toca Matriz4x4._SCRATCH).
- 100% libre de dict y set en toda la logica de datos.
"""

from __future__ import annotations

import os
import sys
import threading
import time
from typing import Callable, Optional

import numpy as np

from arbol_cinematico import ArbolCinematico
from nodo_articulacion import NodoArticulacion


class BufferEscena3D:
    """
    Contenedor preasignado (zero-allocation por frame) para el doble bufer
    Productor-Consumidor. Soporta V articulaciones en espacio Theta(V).
    """

    __slots__ = (
        "pos_segmentos",
        "col_segmentos",
        "pos_articulaciones",
        "col_articulaciones",
        "pos_ejes",
        "col_ejes",
        "num_segmentos",
        "num_nodos",
        "num_ejes",
        "tiempo_propagacion_ns",
        "tiempo_extraccion_ns",
    )

    def __init__(self, max_nodos: int) -> None:
        max_segmentos = max_nodos * 2  # Eslabon fisico + conector padre->offset
        self.pos_segmentos = np.zeros((max_segmentos * 2, 3), dtype=np.float32)
        self.col_segmentos = np.ones((max_segmentos * 2, 4), dtype=np.float32)

        self.pos_articulaciones = np.zeros((max_nodos + 1, 3), dtype=np.float32)
        self.col_articulaciones = np.ones((max_nodos + 1, 4), dtype=np.float32)

        max_ejes = max_nodos * 3  # 3 ejes ortogonales (X, Y, Z)
        self.pos_ejes = np.zeros((max_ejes * 2, 3), dtype=np.float32)
        self.col_ejes = np.ones((max_ejes * 2, 4), dtype=np.float32)

        self.num_segmentos: int = 0
        self.num_nodos: int = 0
        self.num_ejes: int = 0
        self.tiempo_propagacion_ns: int = 0
        self.tiempo_extraccion_ns: int = 0


class ExtractorEscenaPreorden:
    """
    Desacopla la extraccion cinematica en preorden Theta(V) y el doble bufer
    concurrente del backend grafico OpenGL, permitiendo pruebas unitarias y
    benchmarks en entornos headless (sin GPU).
    """

    _COLOR_MUNECA = (0.20, 0.72, 0.85, 1.0)
    _COLOR_PALMA = (0.30, 0.58, 0.88, 1.0)
    _COLOR_METACARPO = (0.45, 0.52, 0.62, 0.75)
    _COLOR_FALANGE = (0.85, 0.90, 0.95, 1.0)
    _COLOR_EJE_X = (1.0, 0.25, 0.25, 0.9)
    _COLOR_EJE_Y = (0.25, 1.0, 0.35, 0.9)
    _COLOR_EJE_Z = (0.30, 0.55, 1.0, 0.9)

    def __init__(self, arbol: ArbolCinematico) -> None:
        if arbol is None:
            raise ValueError("El ArbolCinematico no puede ser None.")
        self._arbol = arbol
        self._max_nodos = max(1, arbol.contar_nodos())
        self._lock = threading.Lock()
        self._buf_frontal = BufferEscena3D(self._max_nodos)
        self._buf_trasero = BufferEscena3D(self._max_nodos)
        self._frame_pendiente: bool = False
        self.frames_producidos: int = 0
        self.frames_consumidos: int = 0
        self.ultimo_tiempo_swap_ns: int = 0

    def producir_frame(self) -> None:
        """
        Ejecutado en el hilo productor (pipeline EMG/cinematico).
        Recorre el Arbol N-ario en preorden en Theta(V) sobre el bufer trasero
        y realiza un pointer-swap atomico en Theta(1).
        """
        t0 = time.perf_counter_ns()
        buf = self._buf_trasero
        buf.num_segmentos = 0
        buf.num_nodos = 0
        buf.num_ejes = 0
        buf.tiempo_propagacion_ns = self._arbol.ultimo_tiempo_propagacion_ns

        raiz = self._arbol.raiz_muneca
        buf.pos_articulaciones[0] = (raiz.base_x, raiz.base_y, raiz.base_z)
        buf.col_articulaciones[0] = self._COLOR_MUNECA
        buf.num_nodos = 1

        self._poblar_preorden(raiz, None, buf)
        buf.tiempo_extraccion_ns = time.perf_counter_ns() - t0

        t_swap = time.perf_counter_ns()
        with self._lock:
            self._buf_frontal, self._buf_trasero = self._buf_trasero, self._buf_frontal
            self._frame_pendiente = True
            self.frames_producidos += 1
        self.ultimo_tiempo_swap_ns = time.perf_counter_ns() - t_swap

    def consumir_frame(self) -> Optional[tuple]:
        """
        Ejecutado en el hilo consumidor (60 FPS).
        Retorna una instantanea segura para subir a VBOs, o None si no hay cambios.
        """
        with self._lock:
            if not self._frame_pendiente:
                return None
            buf = self._buf_frontal
            n_seg = buf.num_segmentos * 2
            n_nod = buf.num_nodos
            n_eje = buf.num_ejes * 2
            snapshot = (
                buf.pos_segmentos[:n_seg].copy(),
                buf.col_segmentos[:n_seg].copy(),
                buf.pos_articulaciones[:n_nod].copy(),
                buf.col_articulaciones[:n_nod].copy(),
                buf.pos_ejes[:n_eje].copy(),
                buf.col_ejes[:n_eje].copy(),
                buf.tiempo_propagacion_ns,
                buf.tiempo_extraccion_ns,
            )
            self._frame_pendiente = False
            self.frames_consumidos += 1
            return snapshot

    def _poblar_preorden(
        self,
        nodo: Optional[NodoArticulacion],
        padre: Optional[NodoArticulacion],
        buf: BufferEscena3D,
    ) -> None:
        if nodo is None:
            return

        bx, by, bz = nodo.base_x, nodo.base_y, nodo.base_z
        ex, ey, ez = nodo.extremo_x, nodo.extremo_y, nodo.extremo_z

        # Conector estructural padre -> base cuando existe offset geometrico (nudillos)
        if padre is not None:
            px, py, pz = padre.extremo_x, padre.extremo_y, padre.extremo_z
            if (bx - px) ** 2 + (by - py) ** 2 + (bz - pz) ** 2 > 1e-4:
                self._agregar_segmento(buf, px, py, pz, bx, by, bz, self._COLOR_METACARPO)

        # Segmento oseo del eslabon actual
        color_seg = self._color_segmento(nodo)
        self._agregar_segmento(buf, bx, by, bz, ex, ey, ez, color_seg)

        # Marcador de articulacion coloreado por porcentaje de flexion
        if buf.num_nodos < buf.pos_articulaciones.shape[0]:
            buf.pos_articulaciones[buf.num_nodos] = (ex, ey, ez)
            buf.col_articulaciones[buf.num_nodos] = self._color_activacion(nodo)
            buf.num_nodos += 1

        # Triada de orientacion global (rotacion 3x3 de Matriz4x4) en raiz y puntas
        if padre is None or nodo.es_hoja:
            self._agregar_triada(buf, nodo, longitud=10.0)

        for i in range(nodo.num_hijos):
            self._poblar_preorden(nodo.get_hijo(i), nodo, buf)

    @staticmethod
    def _agregar_segmento(
        buf: BufferEscena3D,
        x0: float, y0: float, z0: float,
        x1: float, y1: float, z1: float,
        color: tuple[float, float, float, float],
    ) -> None:
        v = buf.num_segmentos * 2
        if v + 1 >= buf.pos_segmentos.shape[0]:
            return
        buf.pos_segmentos[v] = (x0, y0, z0)
        buf.pos_segmentos[v + 1] = (x1, y1, z1)
        buf.col_segmentos[v] = color
        buf.col_segmentos[v + 1] = color
        buf.num_segmentos += 1

    def _agregar_triada(self, buf: BufferEscena3D, nodo: NodoArticulacion, longitud: float) -> None:
        m = nodo.matriz_global.m  # Row-major 4x4 de Matriz4x4
        ox, oy, oz = m[3], m[7], m[11]
        self._agregar_eje(buf, ox, oy, oz, ox + m[0] * longitud, oy + m[4] * longitud, oz + m[8] * longitud, self._COLOR_EJE_X)
        self._agregar_eje(buf, ox, oy, oz, ox + m[1] * longitud, oy + m[5] * longitud, oz + m[9] * longitud, self._COLOR_EJE_Y)
        self._agregar_eje(buf, ox, oy, oz, ox + m[2] * longitud, oy + m[6] * longitud, oz + m[10] * longitud, self._COLOR_EJE_Z)

    @staticmethod
    def _agregar_eje(
        buf: BufferEscena3D,
        x0: float, y0: float, z0: float,
        x1: float, y1: float, z1: float,
        color: tuple[float, float, float, float],
    ) -> None:
        v = buf.num_ejes * 2
        if v + 1 >= buf.pos_ejes.shape[0]:
            return
        buf.pos_ejes[v] = (x0, y0, z0)
        buf.pos_ejes[v + 1] = (x1, y1, z1)
        buf.col_ejes[v] = color
        buf.col_ejes[v + 1] = color
        buf.num_ejes += 1

    def _color_segmento(self, nodo: NodoArticulacion) -> tuple[float, float, float, float]:
        if nodo.id == "MUNECA":
            return self._COLOR_MUNECA
        if nodo.id == "PALMA":
            return self._COLOR_PALMA
        return self._COLOR_FALANGE

    @staticmethod
    def _color_activacion(nodo: NodoArticulacion) -> tuple[float, float, float, float]:
        rango = nodo.angulo_maximo - nodo.angulo_minimo
        t = 0.0 if rango <= 1e-6 else (nodo.angulo_actual - nodo.angulo_minimo) / rango
        t = max(0.0, min(1.0, t))
        return (0.15 + 0.85 * t, 0.85 - 0.45 * t, 0.65 - 0.55 * t, 1.0)


class VisorProtesis3D:
    """
    Interfaz grafica 3D ligera sobre VisPy (OpenGL VBOs) desacoplada a 60 FPS.
    """

    def __init__(self, arbol: ArbolCinematico, fps: float = 60.0) -> None:
        from vispy import app, scene

        self._app = app
        self._arbol = arbol
        self._extractor = ExtractorEscenaPreorden(arbol)
        self._num_articulaciones = arbol.contar_nodos()
        self._cerrado: bool = False
        self._texto_telemetria: str = "Esperando senal sEMG..."

        self._canvas = scene.SceneCanvas(
            title="Simulacion Cinematica 3D - Protesis Transradial (DS-262 team-05)",
            size=(1024, 720),
            bgcolor="#11161d",
            show=True,
            keys="interactive",
        )
        self._view = self._canvas.central_widget.add_view()
        self._view.camera = scene.TurntableCamera(
            fov=45.0,
            distance=270.0,
            elevation=20.0,
            azimuth=35.0,
            center=(0.0, 85.0, 0.0),
            up="+y",
        )

        scene.visuals.GridLines(color=(0.35, 0.42, 0.50, 0.35), parent=self._view.scene)
        scene.visuals.XYZAxis(parent=self._view.scene)

        self._visual_segmentos = scene.visuals.Line(
            pos=np.zeros((2, 3), dtype=np.float32),
            color=np.ones((2, 4), dtype=np.float32),
            width=5.0,
            connect="segments",
            antialias=True,
            parent=self._view.scene,
        )
        self._visual_articulaciones = scene.visuals.Markers(
            pos=np.zeros((1, 3), dtype=np.float32),
            face_color=np.ones((1, 4), dtype=np.float32),
            edge_color=(0.05, 0.07, 0.10, 1.0),
            size=11.0,
            parent=self._view.scene,
        )
        self._visual_ejes = scene.visuals.Line(
            pos=np.zeros((2, 3), dtype=np.float32),
            color=np.ones((2, 4), dtype=np.float32),
            width=2.0,
            connect="segments",
            parent=self._view.scene,
        )
        self._hud = scene.visuals.Text(
            text="Inicializando...",
            color="#d9e6f2",
            font_size=10,
            anchor_x="left",
            anchor_y="top",
            pos=(16, 16),
            parent=self._canvas.scene,
        )

        self.actualizar_escena()
        self._refrescar_gpu()

        self._timer = app.Timer(interval=1.0 / fps, connect=self._en_tick, start=True)
        self._canvas.events.close.connect(self._en_cierre)

    @classmethod
    def crear_si_disponible(cls, arbol: ArbolCinematico) -> Optional["VisorProtesis3D"]:
        """Metodo de fabrica invocado por ArbolCinematico.habilitar_visualizacion_3d(True)."""
        if os.environ.get("PROTESIS_HEADLESS", "0") == "1":
            return None
        if sys.platform.startswith("linux") and not (
            os.environ.get("DISPLAY") or os.environ.get("WAYLAND_DISPLAY")
        ):
            return None
        try:
            return cls(arbol)
        except Exception as exc:
            print(f"[3D-AVISO] Backend grafico no disponible ({exc}). Usando modo headless.")
            return None

    def registrar_telemetria(self, ciclo: int, canal: str, rms: float) -> None:
        """Actualiza el texto opcional de telemetria EMG mostrado en el HUD."""
        self._texto_telemetria = f"Ciclo: {ciclo} | Canal: {canal} | RMS: {rms:.6f}"

    def actualizar_escena(self) -> None:
        """Contrato invocado por ArbolCinematico.renderizar_3d() desde el hilo productor."""
        if not self._cerrado:
            self._extractor.producir_frame()

    def _en_tick(self, _event) -> None:
        if not self._cerrado:
            self._refrescar_gpu()

    def _refrescar_gpu(self) -> None:
        snapshot = self._extractor.consumir_frame()
        if snapshot is None:
            return

        seg_pos, seg_col, art_pos, art_col, eje_pos, eje_col, prop_ns, ext_ns = snapshot
        if seg_pos.shape[0] > 0:
            self._visual_segmentos.set_data(pos=seg_pos, color=seg_col)
        if art_pos.shape[0] > 0:
            self._visual_articulaciones.set_data(pos=art_pos, face_color=art_col, size=11.0)
        if eje_pos.shape[0] > 0:
            self._visual_ejes.set_data(pos=eje_pos, color=eje_col)

        self._hud.text = (
            f"V={self._num_articulaciones} | Preorden: {prop_ns / 1000.0:.1f} us | "
            f"Extraccion 3D: {ext_ns / 1000.0:.1f} us | {self._texto_telemetria}"
        )
        self._canvas.update()

    def guardar_captura(self, ruta_png: str) -> None:
        """Exporta un pantallazo del lienzo OpenGL para evidencias del informe PDF."""
        from vispy.io import write_png
        imagen = self._canvas.render()
        write_png(ruta_png, imagen)

    def _en_cierre(self, _event) -> None:
        self._cerrado = True
        self._timer.stop()

    @property
    def esta_abierto(self) -> bool:
        return not self._cerrado

    @property
    def extractor(self) -> ExtractorEscenaPreorden:
        return self._extractor

    def ejecutar_con_hilo_simulacion(self, tarea_simulacion: Callable[[], None]) -> None:
        """Corre el pipeline EMG en un hilo trabajador y mantiene VisPy en el hilo principal."""
        hilo = threading.Thread(target=tarea_simulacion, name="PipelineEMG", daemon=True)
        hilo.start()
        self._app.run()