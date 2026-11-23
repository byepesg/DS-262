package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.estructuras.Cola;

// Lugares por visitar en el orden en que se agregaron: la primera parada que entra es la
// primera que se visita (FIFO), por eso se usa una cola.
public class RutaDelDia {

    private final Cola<Lugar> paradas = new Cola<>();

    // false si el lugar ya estaba en la ruta
    public boolean agregar(Lugar lugar) {
        if (paradas.contiene(lugar)) {
            return false;
        }
        paradas.encolar(lugar);
        return true;
    }

    public Lugar avanzar() {
        return paradas.desencolar();
    }

    public Lugar proxima() {
        return paradas.verFrente();
    }

    public boolean contiene(Lugar lugar) {
        return paradas.contiene(lugar);
    }

    public Iterable<Lugar> paradas() {
        return paradas;
    }

    // Suma de los tramos: desde la salida a la primera parada y luego de parada en parada
    public double distanciaTotalKm(Ubicacion salida) {
        double total = 0;
        Ubicacion anterior = salida;
        for (Lugar parada : paradas) {
            total += anterior.distanciaKm(parada);
            anterior = new Ubicacion(parada);
        }
        return total;
    }

    public void vaciar() {
        paradas.vaciar();
    }

    public boolean estaVacia() {
        return paradas.estaVacia();
    }

    public int tamano() {
        return paradas.tamano();
    }

    public boolean invariantesValidos() {
        return paradas.invariantesValidos();
    }

    public String dibujarEnlaces() {
        return paradas.dibujarEnlaces(Lugar::getId);
    }
}
