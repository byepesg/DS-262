from typing import Optional
import mne


class LectorFIF:
    def __init__(self, ruta_archivo: str, canales_objetivo: Optional[list[str]] = None):
        """
        Inicializa el lector, abre el archivo .fif y extrae la matriz multicanal completa.
        """
        self.ruta_archivo: str = ruta_archivo
        self.canales_objetivo: Optional[list[str]] = canales_objetivo
        self.indice_actual: int = 0

        self.datos_crudos = None  # Matriz NumPy 2D: (num_canales, total_muestras)
        self.nombres_canales: list[str] = []
        self.num_canales: int = 0
        self.total_muestras: int = 0
        self.frecuencia_muestreo: float = 0.0

        self.cargar_archivo()

    def cargar_archivo(self) -> None:
        """Abre el archivo .fif y guarda todos los canales seleccionados en memoria."""
        print(f"[LectorFIF] Cargando archivo: {self.ruta_archivo}")
        try:
            raw = mne.io.read_raw_fif(self.ruta_archivo, preload=True, verbose=False)

            if self.canales_objetivo:
                canales_validos = [c for c in self.canales_objetivo if c in raw.ch_names]
                if canales_validos:
                    raw.pick(canales_validos)

            self.datos_crudos = raw.get_data()
            self.nombres_canales = list(raw.ch_names)
            self.num_canales = len(self.nombres_canales)
            self.total_muestras = int(self.datos_crudos.shape[1])
            self.frecuencia_muestreo = float(raw.info["sfreq"])

            print(
                f"[LectorFIF] Éxito. {self.num_canales} canales | "
                f"{self.total_muestras} muestras cargadas a {self.frecuencia_muestreo} Hz."
            )
        except Exception as e:
            print(f"[Error LectorFIF] No se pudo cargar el archivo: {e}")

    def obtener_nombres_canales(self) -> list[str]:
        return self.nombres_canales

    def obtener_siguiente_bloque(self, tamano_bloque: int = 15) -> list[list[float]]:
        """
        Lee el siguiente fragmento simultáneo de todos los canales.
        Retorna una matriz [num_canales][tamano_bloque] o [] al finalizar el archivo.
        """
        if self.datos_crudos is None or self.indice_actual >= self.total_muestras:
            return []

        indice_fin = min(self.indice_actual + tamano_bloque, self.total_muestras)
        bloque = self.datos_crudos[:, self.indice_actual : indice_fin]
        self.indice_actual = indice_fin

        return bloque.tolist()