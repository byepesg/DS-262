"""
Suite de pruebas para RF-05 (visor_protesis_3d.py) e integracion M1-M5.
Ejecutar con: PYTHONPATH=src pytest -v tests/test_visor_3d.py
"""

import math
import os
import threading
import time

import numpy as np
import pytest

from ColaCircular import ColaCircular
from ProcesadorEMG import ProcesadorEMG
from arbol_cinematico import ArbolCinematico
from mapeador_hash import MapeadorHash
from visor_protesis_3d import ExtractorEscenaPreorden, VisorProtesis3D


def test_extractor_rechaza_arbol_nulo():
    """Caso invalido: debe lanzar ValueError si el arbol es None."""
    with pytest.raises(ValueError):
        ExtractorEscenaPreorden(None)  # type: ignore[arg-type]


def test_extraccion_preorden_17_articulaciones_y_orientacion():
    """Verifica que el recorrido Theta(V) proyecta los 17 nodos, conectores y triadas."""
    arbol = ArbolCinematico.crear_protesis_transradial_estandar()
    extractor = ExtractorEscenaPreorden(arbol)

    extractor.producir_frame()
    snap = extractor.consumir_frame()
    assert snap is not None

    seg_pos, _seg_col, art_pos, _art_col, eje_pos, _eje_col, _prop_ns, _ext_ns = snap
    # 17 extremos + 1 anclaje de muneca = 18 puntos articulares
    assert art_pos.shape == (18, 3)
    # 17 eslabones + 5 conectores metacarpianos (palma -> base con offset de cada dedo) = 22 segmentos = 44 vertices
    assert seg_pos.shape == (44, 3)
    # 1 raiz + 5 hojas distales = 6 triadas x 3 ejes x 2 vertices = 36 vertices
    assert eje_pos.shape == (36, 3)
    # No deben existir NaN ni infinitos
    assert np.all(np.isfinite(seg_pos))
    assert np.all(np.isfinite(art_pos))
    assert np.all(np.isfinite(eje_pos))


def test_propagacion_rotacion_muneca_afecta_todas_las_falanges():
    """Verifica que rotar la raiz (MUNECA) modifica la geometria extraida de las hojas."""
    arbol = ArbolCinematico.crear_protesis_transradial_estandar()
    extractor = ExtractorEscenaPreorden(arbol)

    extractor.producir_frame()
    art_inicial = extractor.consumir_frame()[2]  # type: ignore[index]

    arbol.buscar_nodo("MUNECA").actualizar_angulo(35.0)
    arbol.actualizar_topologia()
    extractor.producir_frame()
    art_rotada = extractor.consumir_frame()[2]  # type: ignore[index]

    # El anclaje base de la muneca (indice 0) permanece en (0,0,0), los demas nodos se desplazan
    assert np.allclose(art_inicial[0], art_rotada[0])
    assert not np.allclose(art_inicial[1:], art_rotada[1:])


def test_fallback_headless_desde_arbol_cinematico(monkeypatch):
    """Verifica el contrato crear_si_disponible() cuando no hay entorno grafico."""
    monkeypatch.setenv("PROTESIS_HEADLESS", "1")
    arbol = ArbolCinematico.crear_protesis_transradial_estandar()
    arbol.habilitar_visualizacion_3d(True)
    assert arbol.visor_3d is None
    # renderizar_3d debe continuar funcionando sin lanzar excepciones
    arbol.renderizar_3d(ciclo=1, canal="EMG 2", rms=0.00025)


def test_concurrencia_doble_bufer_sin_bloqueo():
    """Prueba de estres Productor-Consumidor: alta frecuencia de escritura vs lectura 60 Hz."""
    arbol = ArbolCinematico.crear_protesis_transradial_estandar()
    extractor = ExtractorEscenaPreorden(arbol)
    detener = threading.Event()
    errores: list[Exception] = []

    def productor():
        try:
            nodo_idx = arbol.buscar_nodo("INDICE_PROXIMAL")
            for k in range(300):
                nodo_idx.actualizar_angulo(45.0 * (1.0 + math.sin(k * 0.1)))
                arbol.actualizar_topologia()
                extractor.producir_frame()
        except Exception as exc:
            errores.append(exc)
        finally:
            detener.set()

    hilo = threading.Thread(target=productor)
    hilo.start()
    while not detener.is_set():
        snap = extractor.consumir_frame()
        if snap is not None:
            assert np.all(np.isfinite(snap[2]))
        time.sleep(0.001)
    hilo.join()

    assert not errores
    assert extractor.frames_producidos == 300
    assert extractor.frames_consumidos >= 1
    assert extractor.ultimo_tiempo_swap_ns < 5_000_000  # Swap < 5 ms (tipicamente < 50 us)


def test_integracion_completa_m1_a_m5():
    """Integracion real: ColaCircular -> ProcesadorEMG -> MapeadorHash -> ArbolCinematico -> Extractor 3D."""
    cola = ColaCircular(capacidad=200)
    proc = ProcesadorEMG(frecuencia_muestreo=500.0)
    arbol = ArbolCinematico.crear_protesis_transradial_estandar()
    mapeador = MapeadorHash()
    extractor = ExtractorEscenaPreorden(arbol)

    nodo_indice = arbol.buscar_nodo("INDICE_PROXIMAL")
    mapeador.registrar_canal("EMG 2", 0.00001, 0.0005, nodo_indice, umbral_ruido=0.00002)

    for k in range(250):
        muestra = 0.0004 * math.sin(2.0 * math.pi * 80.0 * (k / 500.0))
        if cola.esta_llena():
            cola.desencolar()
        cola.encolar(muestra)

    rms = proc.calcular_rms(cola.obtener_ventana())
    actualizadas = mapeador.procesar_y_actualizar_articulaciones("EMG 2", rms)
    arbol.actualizar_topologia()
    extractor.producir_frame()
    snap = extractor.consumir_frame()

    assert actualizadas == 1
    assert nodo_indice.angulo_actual > 0.0
    assert snap is not None
    assert snap[2].shape == (18, 3)