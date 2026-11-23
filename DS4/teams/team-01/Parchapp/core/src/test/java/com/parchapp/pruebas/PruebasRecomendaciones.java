package com.parchapp.pruebas;

import static com.parchapp.pruebas.MiniTest.caso;
import static com.parchapp.pruebas.MiniTest.grupo;
import static com.parchapp.pruebas.MiniTest.iguales;
import static com.parchapp.pruebas.MiniTest.lanza;
import static com.parchapp.pruebas.MiniTest.verificar;

import com.parchapp.datos.DatosDemo;
import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Compania;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.estructuras.MonticuloMax;
import com.parchapp.servicio.Preferencias;
import com.parchapp.servicio.Recomendacion;
import com.parchapp.servicio.Recomendador;
import java.util.NoSuchElementException;
import java.util.Random;

final class PruebasRecomendaciones {

    private static final Ubicacion CENTRO = new Ubicacion("Centro", 4.60, -74.07);

    private static Lugar lugar(String id, int precio, double latitud, Etiqueta... etiquetas) {
        return new Lugar(id, "Lugar " + id, Categoria.PARQUE, null, null, latitud, -74.07, precio,
                Etiqueta.mascara(etiquetas), null);
    }

    static void ejecutar() {
        grupo("MonticuloMax");

        caso("vacio: ver o extraer lanza excepcion y no cambia nada", () -> {
            MonticuloMax<Integer> m = new MonticuloMax<>(Integer::compare);
            verificar(m.estaVacio(), "deberia estar vacio");
            lanza(NoSuchElementException.class, m::extraerMax);
            lanza(NoSuchElementException.class, m::verMax);
            iguales(0, m.tamano());
            verificar(m.invariantesValidos(), "invariantes");
        });

        caso("un elemento: es el maximo y al sacarlo queda vacio", () -> {
            MonticuloMax<Integer> m = new MonticuloMax<>(Integer::compare);
            m.insertar(7);
            iguales(7, m.verMax());
            iguales(7, m.extraerMax());
            verificar(m.estaVacio(), "deberia quedar vacio");
        });

        caso("con repetidos y negativos saca en orden de mayor a menor", () -> {
            MonticuloMax<Integer> m = new MonticuloMax<>(Integer::compare);
            int[] valores = {5, -2, 9, 5, 0, 13, -8, 9};
            for (int v : valores) {
                m.insertar(v);
                verificar(m.invariantesValidos(), "invariantes despues de insertar " + v);
            }
            String orden = "";
            while (!m.estaVacio()) {
                orden += m.extraerMax() + (m.estaVacio() ? "" : ",");
                verificar(m.invariantesValidos(), "invariantes despues de extraer");
            }
            iguales("13,9,9,5,5,0,-2,-8", orden);
        });

        caso("crece mas alla de la capacidad inicial sin perder datos", () -> {
            MonticuloMax<Integer> m = new MonticuloMax<>(Integer::compare);
            Random azar = new Random(1);
            for (int i = 0; i < 1000; i++) {
                m.insertar(azar.nextInt(10000));
            }
            iguales(1000, m.tamano());
            verificar(m.invariantesValidos(), "invariantes con 1000 elementos");
            int anterior = Integer.MAX_VALUE;
            while (!m.estaVacio()) {
                int actual = m.extraerMax();
                verificar(actual <= anterior, "salio " + actual + " despues de " + anterior);
                anterior = actual;
            }
        });

        grupo("Etiquetas y recomendaciones");

        caso("cada etiqueta usa un bit distinto", () -> {
            int todas = 0;
            for (Etiqueta e : Etiqueta.values()) {
                verificar((todas & e.bit()) == 0, "bit repetido en " + e);
                todas |= e.bit();
            }
            iguales(Etiqueta.values().length, Integer.bitCount(todas));
        });

        caso("se descarta lo que se quiere evitar y lo que pasa del presupuesto", () -> {
            ListaEnlazada<Lugar> lugares = new ListaEnlazada<>();
            lugares.agregarAlFinal(lugar("BAR", 2, 4.60, Etiqueta.SOCIAL, Etiqueta.ALCOHOL));
            lugares.agregarAlFinal(lugar("CARO", 3, 4.60, Etiqueta.SOCIAL));
            lugares.agregarAlFinal(lugar("OK", 1, 4.60, Etiqueta.SOCIAL));
            Preferencias p = new Preferencias().con(Compania.AMIGOS, 4).buscar(Etiqueta.SOCIAL)
                    .evitar(Etiqueta.ALCOHOL).presupuesto(2);
            ListaEnlazada<Recomendacion> r = Recomendador.recomendar(lugares, p, CENTRO, 10);
            iguales(1, r.tamano());
            iguales("OK", r.obtenerPrimero().getLugar().getId());
        });

        caso("gana quien cumple mas etiquetas; si empatan, el mas cercano", () -> {
            ListaEnlazada<Lugar> lugares = new ListaEnlazada<>();
            lugares.agregarAlFinal(lugar("UNA", 0, 4.60, Etiqueta.PAISAJE));
            lugares.agregarAlFinal(lugar("DOS_LEJOS", 0, 4.64, Etiqueta.PAISAJE, Etiqueta.ROMANTICO));
            lugares.agregarAlFinal(lugar("DOS_CERCA", 0, 4.601, Etiqueta.PAISAJE, Etiqueta.ROMANTICO));
            Preferencias p = new Preferencias().con(Compania.SOLO, 1).buscar(Etiqueta.PAISAJE, Etiqueta.ROMANTICO);
            ListaEnlazada<Recomendacion> r = Recomendador.recomendar(lugares, p, CENTRO, 3);
            iguales("DOS_CERCA", r.obtener(0).getLugar().getId());
            iguales("DOS_LEJOS", r.obtener(1).getLugar().getId());
            iguales("UNA", r.obtener(2).getLugar().getId());
        });

        caso("devuelve como maximo k y sin lugares que no coinciden", () -> {
            Preferencias p = new Preferencias().con(Compania.PAREJA, 2).buscar(Etiqueta.ROMANTICO, Etiqueta.PAISAJE);
            ListaEnlazada<Recomendacion> r = Recomendador.recomendar(DatosDemo.lugares(), p, CENTRO, 5);
            iguales(5, r.tamano());
            int anterior = Integer.MAX_VALUE;
            for (Recomendacion rec : r) {
                verificar(rec.getCoincidencias() != 0, "sin coincidencias: " + rec.getLugar().getId());
                verificar(rec.getPuntaje() <= anterior, "puntajes desordenados");
                anterior = rec.getPuntaje();
            }
        });

        caso("sin ningun lugar que sirva devuelve una lista vacia", () -> {
            Preferencias p = new Preferencias().buscar(Etiqueta.ALCOHOL).evitar(Etiqueta.ALCOHOL);
            verificar(Recomendador.recomendar(DatosDemo.lugares(), p, CENTRO, 5).estaVacia(), "deberia estar vacia");
        });
    }
}
