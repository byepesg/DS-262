package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import com.parchapp.estructuras.ListaEnlazada;

// Vistos recientemente: el último primero, sin repetidos y con tope.
// Lista enlazada porque casi todo es insertar al inicio (O(1); en un arreglo sería O(n)).
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

    // copia, para que nadie lo cambie desde afuera
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
