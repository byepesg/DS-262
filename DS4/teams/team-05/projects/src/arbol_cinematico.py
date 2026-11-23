"""
Modulo 4: Modulo Cinematico y de Representacion 3D (RF-04 y RF-05).
Modela la topologia de la protesis transradial como un Arbol N-ario cuya raiz es la muneca
y cuyas ramas representan las cadenas falangicas de los cinco dedos (17 articulaciones).

Restriccion cumplida: 100% libre de dict, set o colecciones nativas de Python.
"""

import time
from typing import Optional
from matriz4x4 import Matriz4x4
from nodo_articulacion import NodoArticulacion


class _NodoColaBFS:
    """Nodo de cola enlazada propia (FIFO) para recorrido por niveles (BFS)."""

    __slots__ = ("dato", "siguiente")

    def __init__(self, dato: NodoArticulacion) -> None:
        self.dato: NodoArticulacion = dato
        self.siguiente: Optional["_NodoColaBFS"] = None


class ArbolCinematico:
    """Arbol N-ario de transformaciones homogeneas 4x4 para protesis transradial."""

    def __init__(self, raiz_muneca: NodoArticulacion) -> None:
        if raiz_muneca is None:
            raise ValueError("La raiz del arbol cinematico no puede ser None.")
        self.raiz_muneca: NodoArticulacion = raiz_muneca
        self._matriz_mundo_identidad = Matriz4x4()
        self._visor_3d = None
        self._modo_visual_activo: bool = False
        self._headless_detectado: bool = False

        self.ultimo_tiempo_propagacion_ns: int = 0
        self.nodos_visitados_ultimo_ciclo: int = 0
        self.actualizar_topologia()

    @classmethod
    def crear_protesis_transradial_estandar(cls) -> "ArbolCinematico":
        """
        Construye el modelo anatomico estandar de la protesis transradial (17 articulaciones):
          1 Muneca + 1 Palma + 5 dedos x 3 falanges (Proximal, Media, Distal) = 17 nodos.
        """
        muneca = NodoArticulacion(
            "MUNECA", -45.0, 45.0, max_hijos=1, eje_rotacion="X",
            offset_x=0.0, offset_y=0.0, offset_z=0.0, longitud_segmento=25.0
        )

        palma = NodoArticulacion(
            "PALMA", -15.0, 15.0, max_hijos=5, eje_rotacion="Z",
            offset_x=0.0, offset_y=0.0, offset_z=0.0, longitud_segmento=65.0
        )
        muneca.agregar_hijo(palma)

        nombres_dedos = ("PULGAR", "INDICE", "MEDIO", "ANULAR", "MENIQUE")
        geometria_dedos = (
            (-36.0, -28.0, 10.0, 26.0, 20.0, 16.0),
            (-24.0,   0.0,  0.0, 34.0, 22.0, 16.0),
            ( -8.0,   4.0,  0.0, 38.0, 25.0, 17.0),
            (  8.0,   1.0,  0.0, 35.0, 23.0, 16.0),
            ( 24.0,  -4.0,  0.0, 28.0, 18.0, 14.0),
        )

        for d in range(5):
            dedo = nombres_dedos[d]
            g = geometria_dedos[d]
            eje_prox = "Z" if d == 0 else "X"

            proximal = NodoArticulacion(
                f"{dedo}_PROXIMAL", 0.0, 90.0, max_hijos=1, eje_rotacion=eje_prox,
                offset_x=g[0], offset_y=g[1], offset_z=g[2], longitud_segmento=g[3]
            )
            media = NodoArticulacion(
                f"{dedo}_MEDIA", 0.0, 110.0, max_hijos=1, eje_rotacion="X",
                offset_x=0.0, offset_y=0.0, offset_z=0.0, longitud_segmento=g[4]
            )
            distal = NodoArticulacion(
                f"{dedo}_DISTAL", 0.0, 80.0, max_hijos=0, eje_rotacion="X",
                offset_x=0.0, offset_y=0.0, offset_z=0.0, longitud_segmento=g[5]
            )

            palma.agregar_hijo(proximal)
            proximal.agregar_hijo(media)
            media.agregar_hijo(distal)

        return cls(muneca)

    def actualizar_topologia(self) -> None:
        """Propaga matrices 4x4 desde la muneca en preorden. Complejidad: Theta(V)."""
        self.nodos_visitados_ultimo_ciclo = 0
        inicio = time.perf_counter_ns()
        self.recorrido_preorden(self.raiz_muneca, self._matriz_mundo_identidad)
        self.ultimo_tiempo_propagacion_ns = time.perf_counter_ns() - inicio

    def recorrido_preorden(self, nodo: Optional[NodoArticulacion], matriz_padre_global: Matriz4x4) -> None:
        if nodo is None:
            return
        nodo.calcular_transformacion_global(matriz_padre_global)
        self.nodos_visitados_ultimo_ciclo += 1

        matriz_actual_global = nodo.matriz_global
        for i in range(nodo.num_hijos):
            self.recorrido_preorden(nodo.get_hijo(i), matriz_actual_global)

    def buscar_nodo(self, id_nodo: str) -> Optional[NodoArticulacion]:
        if not id_nodo:
            return None
        return self._buscar_recursivo(self.raiz_muneca, id_nodo)

    def _buscar_recursivo(self, actual: Optional[NodoArticulacion], id_nodo: str) -> Optional[NodoArticulacion]:
        if actual is None:
            return None
        if actual.id == id_nodo:
            return actual
        for i in range(actual.num_hijos):
            encontrado = self._buscar_recursivo(actual.get_hijo(i), id_nodo)
            if encontrado is not None:
                return encontrado
        return None

    def calcular_altura(self) -> int:
        return self._calcular_altura_recursivo(self.raiz_muneca)

    def _calcular_altura_recursivo(self, nodo: Optional[NodoArticulacion]) -> int:
        if nodo is None or nodo.es_hoja:
            return 0
        max_h = 0
        for i in range(nodo.num_hijos):
            h = self._calcular_altura_recursivo(nodo.get_hijo(i))
            if h > max_h:
                max_h = h
        return 1 + max_h

    def contar_nodos(self) -> int:
        return self._contar_nodos_recursivo(self.raiz_muneca)

    def _contar_nodos_recursivo(self, nodo: Optional[NodoArticulacion]) -> int:
        if nodo is None:
            return 0
        total = 1
        for i in range(nodo.num_hijos):
            total += self._contar_nodos_recursivo(nodo.get_hijo(i))
        return total

    def contar_hojas(self) -> int:
        return self._contar_hojas_recursivo(self.raiz_muneca)

    def _contar_hojas_recursivo(self, nodo: Optional[NodoArticulacion]) -> int:
        if nodo is None:
            return 0
        if nodo.es_hoja:
            return 1
        hojas = 0
        for i in range(nodo.num_hijos):
            hojas += self._contar_hojas_recursivo(nodo.get_hijo(i))
        return hojas

    def generar_reporte_por_niveles(self) -> str:
        """Recorrido por niveles (BFS) usando cola enlazada propia en Theta(V)."""
        lineas_cap = self.contar_nodos()
        lineas: list[Optional[str]] = [None] * lineas_cap
        idx_linea = 0

        frente: Optional[_NodoColaBFS] = _NodoColaBFS(self.raiz_muneca)
        fin: Optional[_NodoColaBFS] = frente

        while frente is not None:
            actual = frente.dato
            frente = frente.siguiente
            if frente is None:
                fin = None

            lineas[idx_linea] = (
                f"{actual.id:<16s} | Angulo: {actual.angulo_actual:6.2f} deg | "
                f"Extremo 3D: ({actual.extremo_x:7.2f}, {actual.extremo_y:7.2f}, {actual.extremo_z:7.2f})"
            )
            idx_linea += 1

            for i in range(actual.num_hijos):
                nuevo = _NodoColaBFS(actual.get_hijo(i))
                if fin is None:
                    frente = nuevo
                    fin = nuevo
                else:
                    fin.siguiente = nuevo
                    fin = nuevo

        return "\n".join(lineas[i] for i in range(idx_linea))  # type: ignore[misc]

    def habilitar_visualizacion_3d(self, activar: bool) -> None:
        self._modo_visual_activo = activar
        if activar and self._visor_3d is None and not self._headless_detectado:
            from visor_protesis_3d import VisorProtesis3D
            self._visor_3d = VisorProtesis3D.crear_si_disponible(self)
            if self._visor_3d is None:
                self._headless_detectado = True

    def renderizar_3d(self, ciclo: int = -1, canal: str = "", rms: float = 0.0) -> None:
        """Actualiza la escena 3D interactiva o emite telemetria estructurada en modo headless."""
        if self._modo_visual_activo and self._visor_3d is not None:
            self.visor_3d.registrar_telemetria(ciclo, canal, rms)
            self._visor_3d.actualizar_escena()
        else:
            if ciclo >= 0:
                print(
                    f"[3D-TELEMETRIA] Ciclo {ciclo:02d} | Canal={canal} | RMS={rms:.3f} mV | "
                    f"Nodos={self.nodos_visitados_ultimo_ciclo} | Propagacion={self.ultimo_tiempo_propagacion_ns} ns"
                )
            else:
                print(
                    f"[3D-TELEMETRIA] Raiz={self.raiz_muneca.id} | "
                    f"Nodos={self.nodos_visitados_ultimo_ciclo} | Propagacion={self.ultimo_tiempo_propagacion_ns} ns"
                )

    @property
    def visor_3d(self):
        return self._visor_3d
