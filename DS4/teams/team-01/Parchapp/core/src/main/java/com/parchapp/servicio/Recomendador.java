package com.parchapp.servicio;

import com.parchapp.dominio.Compania;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.estructuras.MonticuloMax;

// Recomendaciones de "¿Qué hacemos hoy?".
// Se descarta lo que tiene algo a evitar o se pasa del presupuesto; el resto se puntúa
// (10 por etiqueta que coincide, +6 si encaja con la compañía, menos por distancia)
// y del montículo se sacan los k mejores. O(n log n + k log n), sin ordenar todo.
public class Recomendador {

    static final int PUNTOS_POR_ETIQUETA = 10;
    static final int PUNTOS_POR_COMPANIA = 6;

    public static ListaEnlazada<Recomendacion> recomendar(Iterable<Lugar> lugares, Preferencias preferencias,
                                                          Ubicacion desde, int cuantos) {
        MonticuloMax<Recomendacion> mejores = new MonticuloMax<>(Recomendador::comparar);
        for (Lugar lugar : lugares) {
            Recomendacion candidata = evaluar(lugar, preferencias, desde);
            if (candidata != null) {
                mejores.insertar(candidata);
            }
        }
        ListaEnlazada<Recomendacion> resultado = new ListaEnlazada<>();
        while (resultado.tamano() < cuantos && !mejores.estaVacio()) {
            resultado.agregarAlFinal(mejores.extraerMax());
        }
        return resultado;
    }

    // null si el lugar no sirve para estas preferencias
    static Recomendacion evaluar(Lugar lugar, Preferencias preferencias, Ubicacion desde) {
        if ((lugar.getEtiquetas() & preferencias.getEvitadas()) != 0) {
            return null;
        }
        if (lugar.getPrecio() > preferencias.getPresupuestoMaximo()) {
            return null;
        }
        int coincidencias = lugar.getEtiquetas() & preferencias.getBuscadas();
        if (coincidencias == 0) {
            return null;
        }
        double km = desde.distanciaKm(lugar);
        int puntaje = Integer.bitCount(coincidencias) * PUNTOS_POR_ETIQUETA
                + bonoPorCompania(lugar, preferencias)
                - (int) Math.round(Math.min(km, 40) / 2);
        return new Recomendacion(lugar, puntaje, coincidencias, km);
    }

    private static int bonoPorCompania(Lugar lugar, Preferencias preferencias) {
        Compania compania = preferencias.getCompania();
        boolean encaja = (compania == Compania.SOLO && lugar.tiene(Etiqueta.IDEAL_SOLO))
                || (compania == Compania.PAREJA && lugar.tiene(Etiqueta.ROMANTICO))
                || (compania == Compania.FAMILIA && lugar.tiene(Etiqueta.FAMILIAR))
                || (compania == Compania.AMIGOS && preferencias.getPersonas() >= 5 && lugar.tiene(Etiqueta.GRUPOS))
                || (compania == Compania.AMIGOS && preferencias.getPersonas() < 5 && lugar.tiene(Etiqueta.SOCIAL));
        return encaja ? PUNTOS_POR_COMPANIA : 0;
    }

    // Mayor puntaje primero; si empatan, el más cercano
    private static int comparar(Recomendacion a, Recomendacion b) {
        if (a.getPuntaje() != b.getPuntaje()) {
            return Integer.compare(a.getPuntaje(), b.getPuntaje());
        }
        return Double.compare(b.getDistanciaKm(), a.getDistanciaKm());
    }
}
