"""
Modulo 3: Modulo de Mapeo Logico (RF-03).
Tabla Hash implementada desde cero con encadenamiento separado y redimensionamiento
dinamico (rehashing) a numeros primos. Asocia identificadores de canales sEMG con una o
multiples articulaciones del arbol cinematico, interpolando el valor RMS hacia angulos
biomecanicos acotados en tiempo promedio O(1).

Restriccion cumplida: 100% libre de dict, set o colecciones nativas de Python.
"""

import math
from typing import Optional


class DestinoArticular:
    """
    Nodo de lista enlazada simple propia que representa una articulacion acoplada a un canal.
    Permite modelar sinergias musculares (un canal sEMG puede accionar k falanges a la vez).
    """

    __slots__ = ("min_rms", "max_rms", "umbral_ruido", "nodo_destino", "siguiente")

    def __init__(
        self,
        min_rms: float,
        max_rms: float,
        umbral_ruido: float,
        nodo_destino,
    ) -> None:
        if nodo_destino is None:
            raise ValueError("El nodo articular destino no puede ser None.")
        MapeadorHash.validar_parametros_rms(min_rms, max_rms, umbral_ruido)
        self.min_rms: float = float(min_rms)
        self.max_rms: float = float(max_rms)
        self.umbral_ruido: float = float(umbral_ruido)
        self.nodo_destino = nodo_destino
        self.siguiente: Optional["DestinoArticular"] = None


class _EntradaHash:
    """Nodo de cubeta (bucket) para resolucion de colisiones mediante encadenamiento separado."""

    __slots__ = ("id_canal", "cabeza_destinos", "siguiente")

    def __init__(self, id_canal: str, primer_destino: DestinoArticular) -> None:
        self.id_canal: str = id_canal
        self.cabeza_destinos: DestinoArticular = primer_destino
        self.siguiente: Optional["_EntradaHash"] = None


class MapeadorHash:
    """Diccionario de dispersion propio para el mapeo canal sEMG -> articulaciones (RF-03)."""

    FACTOR_CARGA_MAXIMO: float = 0.75
    CAPACIDAD_INICIAL_DEFECTO: int = 17

    def __init__(self, capacidad_inicial: int = CAPACIDAD_INICIAL_DEFECTO) -> None:
        if capacidad_inicial <= 0:
            raise ValueError("La capacidad inicial debe ser estrictamente positiva.")
        self._capacidad: int = self._siguiente_primo(capacidad_inicial)
        self._tabla: list[Optional[_EntradaHash]] = [None] * self._capacidad
        self._num_canales: int = 0

        self._total_colisiones_insercion: int = 0
        self._total_rehashes: int = 0

    def _funcion_hash(self, clave: str, modulo: int) -> int:
        """Funcion de dispersion polinomial (base prima 31). Complejidad: Theta(L)."""
        h = 0
        for caracter in clave:
            h = (31 * h + ord(caracter)) & 0x7FFFFFFF
        return h % modulo

    def registrar_canal(
        self,
        id_canal: str,
        min_rms: float,
        max_rms: float,
        nodo_destino,
        umbral_ruido: Optional[float] = None,
    ) -> None:
        """
        Registra un canal sEMG con su rango de activacion y articulacion objetivo.
        - Si el canal ya existe y el nodo es nuevo: acopla sinergia muscular (1-a-k).
        - Si el par (id_canal, nodo) ya existe: recalibra sus cotas RMS.
        Complejidad: O(1) amortizado; O(m) en el peor caso cuando dispara _rehash().
        """
        self._validar_clave(id_canal)
        if nodo_destino is None:
            raise ValueError("El nodo articular destino no puede ser None.")

        umbral = float(min_rms) if umbral_ruido is None else float(umbral_ruido)
        self.validar_parametros_rms(min_rms, max_rms, umbral)

        indice = self._funcion_hash(id_canal, self._capacidad)
        actual = self._tabla[indice]

        while actual is not None:
            if actual.id_canal == id_canal:
                dest = actual.cabeza_destinos
                while dest is not None:
                    if dest.nodo_destino is nodo_destino:
                        dest.min_rms = float(min_rms)
                        dest.max_rms = float(max_rms)
                        dest.umbral_ruido = umbral
                        return
                    dest = dest.siguiente

                nuevo_destino = DestinoArticular(min_rms, max_rms, umbral, nodo_destino)
                nuevo_destino.siguiente = actual.cabeza_destinos
                actual.cabeza_destinos = nuevo_destino
                return
            actual = actual.siguiente

        if (self._num_canales + 1) / self._capacidad > self.FACTOR_CARGA_MAXIMO:
            self._rehash()
            indice = self._funcion_hash(id_canal, self._capacidad)

        if self._tabla[indice] is not None:
            self._total_colisiones_insercion += 1

        primer_destino = DestinoArticular(min_rms, max_rms, umbral, nodo_destino)
        nueva_entrada = _EntradaHash(id_canal, primer_destino)
        nueva_entrada.siguiente = self._tabla[indice]
        self._tabla[indice] = nueva_entrada
        self._num_canales += 1

    def buscar_canal(self, id_canal: str) -> Optional[DestinoArticular]:
        """Recupera en O(1) promedio la lista enlazada de destinos articulares."""
        if not id_canal:
            return None
        indice = self._funcion_hash(id_canal, self._capacidad)
        actual = self._tabla[indice]
        while actual is not None:
            if actual.id_canal == id_canal:
                return actual.cabeza_destinos
            actual = actual.siguiente
        return None

    def eliminar_canal(self, id_canal: str) -> bool:
        """Elimina un canal preservando la cadena de colisiones."""
        if not id_canal:
            return False
        indice = self._funcion_hash(id_canal, self._capacidad)
        actual = self._tabla[indice]
        previo: Optional[_EntradaHash] = None

        while actual is not None:
            if actual.id_canal == id_canal:
                if previo is None:
                    self._tabla[indice] = actual.siguiente
                else:
                    previo.siguiente = actual.siguiente
                self._num_canales -= 1
                return True
            previo = actual
            actual = actual.siguiente
        return False

    def mapear_a_grados(self, rms: float, destino: DestinoArticular) -> float:
        """Interpola linealmente un valor RMS hacia grados biomecanicos en Theta(1)."""
        if destino is None:
            raise ValueError("El destino articular no puede ser None.")

        nodo = destino.nodo_destino
        if math.isnan(rms) or math.isinf(rms):
            return nodo.angulo_actual

        angulo_min = nodo.angulo_minimo
        angulo_max = nodo.angulo_maximo

        if rms <= destino.umbral_ruido:
            return angulo_min
        if rms >= destino.max_rms:
            return angulo_max

        proporcion = (rms - destino.umbral_ruido) / (destino.max_rms - destino.umbral_ruido)
        return angulo_min + proporcion * (angulo_max - angulo_min)

    def procesar_y_actualizar_articulaciones(self, id_canal: str, valor_rms: float) -> int:
        """
        Busca el canal en O(1) promedio, interpola el RMS a grados usando
        angulo_minimo y angulo_maximo de NodoArticulacion y actualiza los nodos acoplados.
        """
        indice = self._funcion_hash(id_canal, self._capacidad)
        actual = self._tabla[indice]
        
        while actual is not None:
            if actual.id_canal == id_canal:
                destino = actual.cabeza_destinos
                actualizadas = 0
                while destino is not None:
                    nodo = destino.nodo_destino
                    if valor_rms < destino.umbral_ruido:
                        # Retorna a reposo (0.0 si está dentro del rango, o angulo_minimo)
                        angulo = (
                            0.0
                            if (nodo.angulo_minimo <= 0.0 <= nodo.angulo_maximo)
                            else nodo.angulo_minimo
                        )
                    else:
                        rango_rms = max(1e-9, destino.max_rms - destino.min_rms)
                        norm = (valor_rms - destino.min_rms) / rango_rms
                        norm = max(0.0, min(1.0, norm))
                        angulo = nodo.angulo_minimo + norm * (nodo.angulo_maximo - nodo.angulo_minimo)
                        
                    nodo.actualizar_angulo(angulo)
                    actualizadas += 1
                    destino = destino.siguiente
                return actualizadas
            actual = actual.siguiente
            
        return 0
    def _rehash(self) -> None:
        nueva_capacidad = self._siguiente_primo(self._capacidad * 2)
        nueva_tabla: list[Optional[_EntradaHash]] = [None] * nueva_capacidad
        nuevas_colisiones = 0

        for i in range(self._capacidad):
            actual = self._tabla[i]
            while actual is not None:
                siguiente_en_cadena = actual.siguiente
                nuevo_indice = self._funcion_hash(actual.id_canal, nueva_capacidad)

                if nueva_tabla[nuevo_indice] is not None:
                    nuevas_colisiones += 1

                actual.siguiente = nueva_tabla[nuevo_indice]
                nueva_tabla[nuevo_indice] = actual
                actual = siguiente_en_cadena

        self._tabla = nueva_tabla
        self._capacidad = nueva_capacidad
        self._total_colisiones_insercion = nuevas_colisiones
        self._total_rehashes += 1

    @staticmethod
    def _validar_clave(id_canal: str) -> None:
        if not isinstance(id_canal, str) or not id_canal.strip():
            raise ValueError("El identificador del canal EMG no puede ser nulo ni vacio.")

    @staticmethod
    def validar_parametros_rms(min_rms: float, max_rms: float, umbral_ruido: float) -> None:
        if math.isnan(min_rms) or math.isnan(max_rms) or math.isnan(umbral_ruido):
            raise ValueError("Los umbrales RMS no pueden ser NaN.")
        if min_rms >= max_rms:
            raise ValueError("Rango RMS invalido: min_rms debe ser menor que max_rms.")
        if umbral_ruido < min_rms or umbral_ruido >= max_rms:
            raise ValueError("El umbral de ruido debe pertenecer a [min_rms, max_rms).")

    def _siguiente_primo(self, n: int) -> int:
        if n <= 2:
            return 2
        candidato = n + 1 if n % 2 == 0 else n
        while not self._es_primo(candidato):
            candidato += 2
        return candidato

    @staticmethod
    def _es_primo(num: int) -> bool:
        if num < 2:
            return False
        if num in (2, 3):
            return True
        if num % 2 == 0 or num % 3 == 0:
            return False
        i = 5
        while i * i <= num:
            if num % i == 0 or num % (i + 2) == 0:
                return False
            i += 6
        return True

    @property
    def num_canales(self) -> int:
        return self._num_canales

    @property
    def capacidad(self) -> int:
        return self._capacidad

    @property
    def factor_de_carga(self) -> float:
        return self._num_canales / self._capacidad

    @property
    def total_colisiones_insercion(self) -> int:
        return self._total_colisiones_insercion

    @property
    def total_rehashes(self) -> int:
        return self._total_rehashes

    def obtener_longitud_maxima_cadena(self) -> int:
        max_len = 0
        for i in range(self._capacidad):
            longitud = 0
            actual = self._tabla[i]
            while actual is not None:
                longitud += 1
                actual = actual.siguiente
            if longitud > max_len:
                max_len = longitud
        return max_len
