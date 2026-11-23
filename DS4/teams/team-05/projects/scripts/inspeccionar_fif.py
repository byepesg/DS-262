
import mne
import numpy as np
from ProcesadorEMG import ProcesadorEMG

# ==============================================================================
# PROYECTO: Simulación de Prótesis
# Script de Inspección de Metadatos FIF
# ==============================================================================

def inspeccionar_metadatos(ruta_archivo):
    """
    Carga el archivo .fif y extrae su información general y canales.
    """
    print(f"--- Inspeccionando: {ruta_archivo} ---")
    try:
        # Cargar el archivo sin precargar todos los datos en RAM para ahorrar memoria[cite: 1]
        raw = mne.io.read_raw_fif(ruta_archivo, preload=False, verbose=False)
        
        # Obtener la información general (frecuencia, número de canales, etc.)[cite: 1]
        info_general = raw.info
        print("\n[INFO GENERAL DEL ARCHIVO]")
        print(info_general)
        
        # Extraer únicamente los nombres de las columnas/canales[cite: 1]
        nombres_canales = raw.info['ch_names']
        print(f"\n[TOTAL DE CANALES: {len(nombres_canales)}]")
        print("[LISTA DE CANALES]")
        
        # Imprimir los canales numerados para facilitar la lectura
        for i, canal in enumerate(nombres_canales):
            print(f"  {i+1}. {canal}")
            
    except FileNotFoundError:
        print(f"[Error] El archivo '{ruta_archivo}' no existe en la carpeta actual.")
    except Exception as e:
        print(f"[Error] No se pudo leer el archivo: {e}")
def analizar_actividad_canales(ruta_archivo):
    # 1. Cargar archivo y frecuencia de muestreo
    raw = mne.io.read_raw_fif(ruta_archivo, preload=True, verbose=False)
    fs = raw.info['sfreq']
    procesador = ProcesadorEMG(frecuencia_muestreo=fs)
    
    canales_emg = [f"EMG {i}" for i in range(1, 9)]
    resultados = []

    print(f"Analizando 8 canales EMG en {ruta_archivo}...\n")

    for canal in canales_emg:
        # Extraer señal completa del canal
        datos = raw.get_data(picks=[canal])[0]
        
        # Dividir en ventanas de 200 muestras (400 ms) para simular tu cola circular
        tamano_ventana = 200
        lista_rms = []
        
        for i in range(0, len(datos) - tamano_ventana, 50): # Avanza de 50 en 50
            ventana = datos[i : i + tamano_ventana]
            rms = procesador.calcular_rms(ventana)
            lista_rms.append(rms)
            
        rms_promedio = np.mean(lista_rms)
        rms_maximo = np.max(lista_rms)
        # El rango dinámico (Max - Min) mide qué tanto reacciona el músculo
        rango_dinamico = rms_maximo - np.min(lista_rms) 
        
        resultados.append((canal, rms_promedio, rms_maximo, rango_dinamico))

    # Ordenar de mayor a menor según el Rango Dinámico (actividad real)
    resultados.sort(key=lambda x: x[3], reverse=True)

    print(f"{'CANAL':<10} | {'RMS PROMEDIO':<15} | {'RMS MÁXIMO':<15} | {'ACTIVIDAD (RANGO)':<15}")
    print("-" * 65)
    for canal, prom, maximo, rango in resultados:
        print(f"{canal:<10} | {prom:<15.6f} | {maximo:<15.6f} | {rango:<15.6f}")

  
if __name__ == "__main__":
    # Ruta estática para pruebas (Asegúrate de tener un archivo .fif con este nombre)
    archivo_prueba = r"c:\Users\CASA\Documents\courses\Estructura de datos\Proyeto\ArchivosFif\Subject_01.fif"
    
    inspeccionar_metadatos(archivo_prueba)
    analizar_actividad_canales(archivo_prueba)

