"""
Modulo 4 (Algebra Matricial): Matriz de transformacion homogenea 4x4 en almacenamiento
lineal contiguo (row-major). Opera in-place en Theta(1) sin crear objetos intermedios.
"""

import math


class Matriz4x4:
    """Matriz homogenea 4x4 con multiplicacion zero-allocation."""

    __slots__ = ("m",)
    _SCRATCH = [0.0] * 16

    def __init__(self) -> None:
        self.m: list[float] = [0.0] * 16
        self.identidad()

    def identidad(self) -> None:
        m = self.m
        for i in range(16):
            m[i] = 0.0
        m[0] = 1.0
        m[5] = 1.0
        m[10] = 1.0
        m[15] = 1.0

    def copiar_desde(self, otra: "Matriz4x4") -> None:
        src = otra.m
        dst = self.m
        for i in range(16):
            dst[i] = src[i]

    def set_traslacion(self, tx: float, ty: float, tz: float) -> None:
        self.identidad()
        self.m[3] = float(tx)
        self.m[7] = float(ty)
        self.m[11] = float(tz)

    def set_rotacion_x(self, grados: float) -> None:
        self.identidad()
        rad = math.radians(grados)
        cos_a = math.cos(rad)
        sin_a = math.sin(rad)
        self.m[5] = cos_a
        self.m[6] = -sin_a
        self.m[9] = sin_a
        self.m[10] = cos_a

    def set_rotacion_y(self, grados: float) -> None:
        self.identidad()
        rad = math.radians(grados)
        cos_a = math.cos(rad)
        sin_a = math.sin(rad)
        self.m[0] = cos_a
        self.m[2] = sin_a
        self.m[8] = -sin_a
        self.m[10] = cos_a

    def set_rotacion_z(self, grados: float) -> None:
        self.identidad()
        rad = math.radians(grados)
        cos_a = math.cos(rad)
        sin_a = math.sin(rad)
        self.m[0] = cos_a
        self.m[1] = -sin_a
        self.m[4] = sin_a
        self.m[5] = cos_a

    @classmethod
    def multiplicar(cls, a: "Matriz4x4", b: "Matriz4x4", destino: "Matriz4x4") -> None:
        """Calcula destino = A * B en Theta(1) (64 multiplicaciones escalares)."""
        am = a.m
        bm = b.m
        r = cls._SCRATCH
        for fila in range(4):
            off = fila * 4
            a0, a1, a2, a3 = am[off], am[off + 1], am[off + 2], am[off + 3]
            for col in range(4):
                r[off + col] = (
                    a0 * bm[col]
                    + a1 * bm[4 + col]
                    + a2 * bm[8 + col]
                    + a3 * bm[12 + col]
                )
        dm = destino.m
        for i in range(16):
            dm[i] = r[i]

    @property
    def posicion_x(self) -> float:
        return self.m[3]

    @property
    def posicion_y(self) -> float:
        return self.m[7]

    @property
    def posicion_z(self) -> float:
        return self.m[11]
