import time
import numpy as np
from LectorFIF import LectorFIF
from ColaCircular import ColaCircular
from ProcesadorEMG import ProcesadorEMG
from mapeador_hash import MapeadorHash
from arbol_cinematico import ArbolCinematico


class ControladorPrincipal:
    def __init__(
        self,
        ruta_archivo: str,
        tamano_ventana: int = 150,
        paso_bloque: int = 15,
        activar_3d: bool = True,
    ):
        self.ruta_archivo = ruta_archivo
        self.tamano_ventana = tamano_ventana
        self.paso_bloque = paso_bloque
        self.activar_3d = activar_3d

        self.inicializar_componentes()
        self.configurar_mapeo_canales()

    def inicializar_componentes(self):
        # 1. Lector multicanal (carga todos los canales del .fif una sola vez)
        self.lector = LectorFIF(self.ruta_archivo)
        self.nombres_canales: list[str] = self.lector.obtener_nombres_canales()
        self.num_canales: int = len(self.nombres_canales)

        # 2. Arreglo paralelo de Colas Circulares (una por canal, sin dict ni set)
        self.colas_por_canal: list[ColaCircular] = [
            ColaCircular(capacidad=self.tamano_ventana) for _ in range(self.num_canales)
        ]

        # 3. Procesador EMG
        self.procesador = ProcesadorEMG(frecuencia_muestreo=self.lector.frecuencia_muestreo)

        # 4. Tabla Hash y Árbol Cinemático de 17 nodos
        self.mapeador = MapeadorHash(capacidad_inicial=max(17, self.num_canales * 2))
        self.arbol = ArbolCinematico.crear_protesis_transradial_estandar()

        # 5. Inicialización del motor gráfico 3D (con fallback automático a headless)
        if self.activar_3d:
            self.arbol.habilitar_visualizacion_3d(True)
            if self.arbol.visor_3d is not None:
                print("[INFO] Visor 3D OpenGL/VisPy inicializado correctamente (60 FPS).")
            else:
                print("[INFO] Ejecutando en modo headless (sin ventana gráfica).")

    def configurar_mapeo_canales(self):
        """
        Calibra automáticamente los umbrales RMS (reposo vs. contracción)
        para cada canal EMG y los vincula con las 3 falanges de su dedo.
        """
        asignaciones = (
            ("EMG 1", "PULGAR"),
            ("EMG 2", "INDICE"),
            ("EMG 3", "MEDIO"),
            ("EMG 4", "ANULAR"),
            ("EMG 5", "MENIQUE"),
        )
        falanges = ("PROXIMAL", "MEDIA", "DISTAL")

        matriz_datos = None
        for val in vars(self.lector).values():
            arr = np.asarray(val) if not hasattr(val, "get_data") else val.get_data()
            if getattr(arr, "ndim", 0) == 2 and arr.shape[0] == self.num_canales:
                matriz_datos = arr
                break

        for nombre_canal, nombre_dedo in asignaciones:
            if nombre_canal not in self.nombres_canales:
                continue

            idx_canal = self.nombres_canales.index(nombre_canal)
            min_rms, umbral_ruido, max_rms = 35.0, 45.0, 130.0

            # Auto-calibración por ventanas sobre los datos cargados del .fif
            if matriz_datos is not None:
                senal = np.asarray(matriz_datos[idx_canal], dtype=np.float64)
                num_ventanas = len(senal) // self.tamano_ventana
                if num_ventanas > 10:
                    recorte = senal[: num_ventanas * self.tamano_ventana]
                    bloques = recorte.reshape(num_ventanas, self.tamano_ventana)
                    rms_hist = np.sqrt(np.mean(bloques ** 2, axis=1))
                    p15 = float(np.percentile(rms_hist, 15))
                    p30 = float(np.percentile(rms_hist, 30))
                    p95 = float(np.percentile(rms_hist, 95))
                    if p95 > p30 * 1.15:
                        min_rms = p15
                        umbral_ruido = p30
                        max_rms = p95

            print(
                f"[CALIBRACION] {nombre_canal} -> {nombre_dedo} | "
                f"Reposo={umbral_ruido:.2f} | Max={max_rms:.2f}"
            )

            for sufijo in falanges:
                id_nodo = f"{nombre_dedo}_{sufijo}"
                nodo = self.arbol.buscar_nodo(id_nodo)
                if nodo is not None:
                    self.mapeador.registrar_canal(
                        id_canal=nombre_canal,
                        min_rms=min_rms,
                        max_rms=max_rms,
                        nodo_destino=nodo,
                        umbral_ruido=umbral_ruido,
                    )

    def iniciar(self):
        """
        Ejecuta el pipeline EMG en un hilo trabajador y mantiene
        el bucle de eventos OpenGL/VisPy en el hilo principal.
        """
        visor = self.arbol.visor_3d
        if self.activar_3d and visor is not None:
            visor.ejecutar_con_hilo_simulacion(self.ejecutar_ciclo_principal)
        else:
            self.ejecutar_ciclo_principal()

    def ejecutar_ciclo_principal(self):
        print(f"[INFO] Iniciando simulación multicanal ({self.num_canales} canales)...")
        ciclo = 0

        while True:
            if self.arbol.visor_3d is not None and not self.arbol.visor_3d.esta_abierto:
                print("[INFO] Ventana 3D cerrada por el usuario. Finalizando pipeline...")
                break

            try:
                # 1. Ingesta simultánea de todos los canales
                matriz_bloque = self.lector.obtener_siguiente_bloque(self.paso_bloque)
                if not matriz_bloque:
                    print("[INFO] Fin del archivo .fif alcanzado.")
                    break

                ciclo += 1
                ventanas_listas: list[list[float]] = []
                indices_listos: list[int] = []
                min_muestras = getattr(self.procesador, "longitud_minima_filtro", 30)

                # 2. Actualizar ventana deslizante por cada canal
                for idx in range(self.num_canales):
                    cola = self.colas_por_canal[idx]
                    for muestra in matriz_bloque[idx]:
                        if cola.esta_llena():
                            cola.desencolar()
                        cola.encolar(muestra)

                    if cola.tamano_actual() > min_muestras:
                        ventanas_listas.append(cola.obtener_ventana())
                        indices_listos.append(idx)

                if not ventanas_listas:
                    continue

                # 3. Cálculo RMS multicanal
                valores_rms = self.procesador.calcular_rms_multicanal(ventanas_listas)

                # 4. Consulta O(1) en Tabla Hash y actualización angular en los nodos
                articulaciones_actualizadas = 0
                canal_dominante = "-"
                rms_dominante = 0.0

                for pos, idx_canal in enumerate(indices_listos):
                    id_canal = self.nombres_canales[idx_canal]
                    rms_canal = valores_rms[pos]
                    movidas = self.mapeador.procesar_y_actualizar_articulaciones(
                        id_canal, rms_canal
                    )
                    if movidas > 0:
                        articulaciones_actualizadas += movidas
                        if rms_canal > rms_dominante:
                            rms_dominante = rms_canal
                            canal_dominante = id_canal

                # 5. Propagación de matrices 4x4 en preorden Theta(V) y renderizado 3D
                if articulaciones_actualizadas > 0:
                    self.arbol.actualizar_topologia()
                    self.arbol.renderizar_3d(
                        ciclo=ciclo,
                        canal=canal_dominante,
                        rms=rms_dominante,
                    )

                if ciclo % 50 == 0:
                    nodo_prueba = self.arbol.buscar_nodo("INDICE_PROXIMAL")
                    ang = nodo_prueba.angulo_actual if nodo_prueba else 0.0
                    print(
                        f"[MOVIMIENTO] Ciclo={ciclo} | Dominante={canal_dominante} "
                        f"| RMS={rms_dominante:.2f} | Angulo Indice={ang:.1f} deg"
                    )

                time.sleep(self.paso_bloque / self.lector.frecuencia_muestreo)

            except KeyboardInterrupt:
                print("\n[INFO] Simulación detenida por el usuario.")
                break
            except Exception as e:
                print(f"[Error] Fallo en el ciclo principal: {e}")
                break


if __name__ == "__main__":
    import argparse
    import os
    import sys

    parser = argparse.ArgumentParser(description="Simulador 3D Protesis Transradial (team-05)")
    parser.add_argument(
        "--archivo",
        type=str,
        default=os.path.join("data", "Subject_01.fif"),
        help="Ruta al archivo .fif (por defecto: data/Subject_01.fif)",
    )
    parser.add_argument("--headless", action="store_true", help="Ejecutar sin ventana 3D")
    args = parser.parse_args()

    if not os.path.isfile(args.archivo):
        print(f"[ERROR] No se encontró el archivo: {args.archivo}")
        print("Coloca 'Subject_01.fif' dentro de la carpeta 'data/' o pásalo con --archivo <ruta>.")
        sys.exit(1)

    simulador = ControladorPrincipal(ruta_archivo=args.archivo, activar_3d=not args.headless)
    simulador.iniciar()