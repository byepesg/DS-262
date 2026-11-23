package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;

// Un lugar sugerido, con su puntaje y las etiquetas que coincidieron con lo que se buscaba
public class Recomendacion {

    private final Lugar lugar;
    private final int puntaje;
    private final int coincidencias;
    private final double distanciaKm;

    Recomendacion(Lugar lugar, int puntaje, int coincidencias, double distanciaKm) {
        this.lugar = lugar;
        this.puntaje = puntaje;
        this.coincidencias = coincidencias;
        this.distanciaKm = distanciaKm;
    }

    public Lugar getLugar() {
        return lugar;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public int getCoincidencias() {
        return coincidencias;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }
}
