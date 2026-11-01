package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import com.parchapp.estructuras.ListaEnlazada;

// Lugares vistos recientemente: el más reciente va primero, sin repetidos y con un máximo.
// Se usa una lista enlazada porque la operación principal es insertar al inicio, que es O(1);
// en un arreglo habría que correr todos los elementos una posición (O(n)).
public class HistorialVistas {

    private final ListaEnlazada<Lugar> vistos = new ListaEnlazada<>();
    private final int capacidad;

    public HistorialVistas(int capacidad) {
        this.capacidad = capacidad;
    }

    public void registrarVista(Lugar lugar) {
        // Si ya estaba, se quita de donde estaba para que suba al primer puesto
        vistos.eliminar(lugar);
        vistos.agregarAlInicio(lugar);
        if (vistos.tamano() > capacidad) {
            vistos.eliminarUltimo();
        }
    }

    // Copia, para que la pantalla no pueda modificar el historial
    public ListaEnlazada<Lugar> recientes() {
        ListaEnlazada<Lugar> copia = new ListaEnlazada<>();
        for (Lugar lugar : vistos) {
            copia.agregarAlFinal(lugar);
        }
        return copia;
    }

    public void vaciar() {
        vistos.vaciar();
    }

    public int tamano() {
        return vistos.tamano();
    }

    public int getCapacidad() {
        return capacidad;
    }

    public boolean invariantesValidos() {
        return vistos.invariantesValidos() && vistos.tamano() <= capacidad;
    }

    public String dibujarEnlaces() {
        return vistos.dibujarEnlaces(Lugar::getId);
    }
}
