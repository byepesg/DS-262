import numpy as np
from scipy.signal import butter, filtfilt, iirnotch


class ProcesadorEMG:
    def __init__(
        self,
        frecuencia_muestreo: float,
        freq_baja: float = 20.0,
        freq_alta: float = 200.0,
        freq_notch: float = 60.0,
    ):
        """
        Configura los filtros digitales Notch (60 Hz) y Pasa-Banda (Butterworth orden 4).
        """
        self.fs: float = float(frecuencia_muestreo)
        nyquist: float = 0.5 * self.fs

        # 1. Filtro Pasa-Banda (acotado por debajo de Nyquist = fs / 2)[cite: 4]
        baja = max(1.0, freq_baja) / nyquist
        alta = min(freq_alta / nyquist, 0.95)
        self.b_banda, self.a_banda = butter(4, [baja, alta], btype="band")

        # 2. Filtro Notch para ruido de red eléctrica (50 Hz / 60 Hz)[cite: 4]
        w0 = min(freq_notch / nyquist, 0.95)
        calidad_q = 30.0
        self.b_notch, self.a_notch = iirnotch(w0, calidad_q)

        # Longitud mínima requerida por filtfilt (padlen) para evitar errores en ventanas cortas[cite: 4]
        orden_max = max(
            len(self.a_banda), len(self.b_banda), len(self.a_notch), len(self.b_notch)
        )
        self.longitud_minima_filtro: int = 3 * orden_max

    def filtrar_senal(self, ventana_datos: np.ndarray) -> np.ndarray:
        """Aplica filtrado cero-fase (Notch + Pasa-Banda) sobre un canal[cite: 4]."""
        senal_centrada = ventana_datos - np.mean(ventana_datos)
        senal_sin_ruido_red = filtfilt(self.b_notch, self.a_notch, senal_centrada)
        return filtfilt(self.b_banda, self.a_banda, senal_sin_ruido_red)

    def rectificar_senal(self, senal: np.ndarray) -> np.ndarray:
        """Rectificación de onda completa[cite: 4]."""
        return np.abs(senal)

    def calcular_rms(self, ventana_datos: list[float]) -> float:
        """Procesa la ventana de un canal y retorna su valor RMS[cite: 4]."""
        if not ventana_datos:
            return 0.0

        arreglo_datos = np.array(ventana_datos, dtype=float)
        if len(arreglo_datos) > self.longitud_minima_filtro:
            senal_limpia = self.filtrar_senal(arreglo_datos)
        else:
            senal_limpia = arreglo_datos - np.mean(arreglo_datos)

        senal_rectificada = self.rectificar_senal(senal_limpia)
        rms = np.sqrt(np.mean(np.square(senal_rectificada)))
        return float(rms)

    def calcular_mav(self, ventana_datos: list[float]) -> float:
        """Calcula el Valor Absoluto Medio (MAV) de la ventana de un canal[cite: 4]."""
        if not ventana_datos:
            return 0.0

        arreglo_datos = np.array(ventana_datos, dtype=float)
        if len(arreglo_datos) > self.longitud_minima_filtro:
            arreglo_datos = self.filtrar_senal(arreglo_datos)

        return float(np.mean(self.rectificar_senal(arreglo_datos)))

    def calcular_rms_multicanal(self, matriz_ventanas: list[list[float]]) -> list[float]:
        """Calcula el RMS para todas las ventanas activas en el ciclo actual."""
        num_ventanas = len(matriz_ventanas)
        resultados: list[float] = [0.0] * num_ventanas
        for i in range(num_ventanas):
            resultados[i] = self.calcular_rms(matriz_ventanas[i])
        return resultados