"""
Modulo 4 (Nodo Articular): Nodo del Arbol N-ario de la protesis transradial.
Gestiona sus hijos mediante un arreglo dinamico propio (crecimiento amortizado O(1))
y calcula sus matrices homogeneas 4x4 local y global en Theta(1).
"""

import math
from typing import Optional
from matriz4x4 import Matriz4x4


class NodoArticulacion:
    EJES_VALIDOS = ("X", "Y", "Z")

    def __init__(
        self,
        id_nodo: str,
        angulo_minimo: float,
        angulo_maximo: float,
        max_hijos: int = 2,
        eje_rotacion: str = "X",
        offset_x: float = 0.0,
        offset_y: float = 0.0,
        offset_z: float = 0.0,
        longitud_segmento: float = 25.0,
    ) -> None:
        if not isinstance(id_nodo, str) or not id_nodo.strip():
            raise ValueError("El identificador de la articulacion no puede ser vacio.")
        if math.isnan(angulo_minimo) or math.isnan(angulo_maximo) or angulo_minimo > angulo_maximo:
            raise ValueError("Rango angular invalido: angulo_minimo debe ser <= angulo_maximo.")
        if eje_rotacion not in self.EJES_VALIDOS:
            raise ValueError(f"Eje de rotacion invalido '{eje_rotacion}'. Use 'X', 'Y' o 'Z'.")
        if longitud_segmento < 0.0:
            raise ValueError("La longitud del segmento no puede ser negativa.")

        self.id: str = id_nodo
        self.angulo_minimo: float = float(angulo_minimo)
        self.angulo_maximo: float = float(angulo_maximo)
        self.angulo_actual: float = (
            0.0 if (angulo_minimo <= 0.0 <= angulo_maximo) else float(angulo_minimo)
        )
        self.eje_rotacion: str = eje_rotacion

        self.offset_x: float = float(offset_x)
        self.offset_y: float = float(offset_y)
        self.offset_z: float = float(offset_z)
        self.longitud_segmento: float = float(longitud_segmento)

        cap = max(1, max_hijos)
        self._hijos: list[Optional["NodoArticulacion"]] = [None] * cap
        self._capacidad_hijos: int = cap
        self._num_hijos: int = 0
        self.padre: Optional["NodoArticulacion"] = None

        self._matriz_traslacion_offset = Matriz4x4()
        self._matriz_traslacion_offset.set_traslacion(self.offset_x, self.offset_y, self.offset_z)

        self._matriz_rotacion = Matriz4x4()
        self._matriz_traslacion_segmento = Matriz4x4()
        self._matriz_traslacion_segmento.set_traslacion(0.0, self.longitud_segmento, 0.0)

        self._matriz_temp = Matriz4x4()
        self.matriz_base_global = Matriz4x4()
        self.matriz_global = Matriz4x4()

    def actualizar_angulo(self, nuevo_angulo: float) -> None:
        """Actualiza el angulo local acotado estrictamente a [min, max]. Complejidad: Theta(1)."""
        if math.isnan(nuevo_angulo) or math.isinf(nuevo_angulo):
            return
        if nuevo_angulo < self.angulo_minimo:
            self.angulo_actual = self.angulo_minimo
        elif nuevo_angulo > self.angulo_maximo:
            self.angulo_actual = self.angulo_maximo
        else:
            self.angulo_actual = float(nuevo_angulo)

    def calcular_transformacion_global(self, matriz_padre_global: Matriz4x4) -> None:
        """
        Compone la cadena cinematica en Theta(1):
          M_base   = M_padre * T(offset)
          M_global = M_base * R_eje(angulo_actual) * T(0, longitud_segmento, 0)
        """
        if self.eje_rotacion == "X":
            self._matriz_rotacion.set_rotacion_x(self.angulo_actual)
        elif self.eje_rotacion == "Y":
            self._matriz_rotacion.set_rotacion_y(self.angulo_actual)
        else:
            self._matriz_rotacion.set_rotacion_z(self.angulo_actual)

        Matriz4x4.multiplicar(
            matriz_padre_global, self._matriz_traslacion_offset, self.matriz_base_global
        )
        Matriz4x4.multiplicar(
            self.matriz_base_global, self._matriz_rotacion, self._matriz_temp
        )
        Matriz4x4.multiplicar(
            self._matriz_temp, self._matriz_traslacion_segmento, self.matriz_global
        )

    def agregar_hijo(self, nodo: "NodoArticulacion") -> None:
        """
        Inserta un nodo hijo en el arreglo dinamico propio (O(1) amortizado).
        Previene ciclos estructurales y doble paternidad.
        """
        if nodo is None:
            raise ValueError("El nodo hijo no puede ser None.")
        if nodo is self or self._es_ancestro(nodo):
            raise ValueError("Operacion invalida: se detecto un ciclo en el arbol cinematico.")
        if nodo.padre is not None:
            raise RuntimeError(f"El nodo '{nodo.id}' ya pertenece a otro padre.")

        if self._num_hijos == self._capacidad_hijos:
            nueva_cap = self._capacidad_hijos * 2
            nuevo_arreglo: list[Optional["NodoArticulacion"]] = [None] * nueva_cap
            for i in range(self._num_hijos):
                nuevo_arreglo[i] = self._hijos[i]
            self._hijos = nuevo_arreglo
            self._capacidad_hijos = nueva_cap

        nodo.padre = self
        self._hijos[self._num_hijos] = nodo
        self._num_hijos += 1

    def _es_ancestro(self, candidato: "NodoArticulacion") -> bool:
        actual = self.padre
        while actual is not None:
            if actual is candidato:
                return True
            actual = actual.padre
        return False

    @property
    def num_hijos(self) -> int:
        return self._num_hijos

    @property
    def es_hoja(self) -> bool:
        return self._num_hijos == 0

    def get_hijo(self, indice: int) -> "NodoArticulacion":
        if indice < 0 or indice >= self._num_hijos:
            raise IndexError(f"Indice de hijo fuera de rango: {indice}")
        hijo = self._hijos[indice]
        assert hijo is not None
        return hijo

    @property
    def base_x(self) -> float:
        return self.matriz_base_global.posicion_x

    @property
    def base_y(self) -> float:
        return self.matriz_base_global.posicion_y

    @property
    def base_z(self) -> float:
        return self.matriz_base_global.posicion_z

    @property
    def extremo_x(self) -> float:
        return self.matriz_global.posicion_x

    @property
    def extremo_y(self) -> float:
        return self.matriz_global.posicion_y

    @property
    def extremo_z(self) -> float:
        return self.matriz_global.posicion_z
