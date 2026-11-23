package com.parchapp.pruebas;

import static com.parchapp.pruebas.MiniTest.caso;
import static com.parchapp.pruebas.MiniTest.grupo;
import static com.parchapp.pruebas.MiniTest.iguales;
import static com.parchapp.pruebas.MiniTest.verificar;

import com.parchapp.datos.DatosDemo;
import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.HistorialVistas;
import com.parchapp.servicio.NavegacionMaps;
import com.parchapp.servicio.RutaDelDia;
import java.util.Locale;

final class PruebasServicios {

    private static Lugar lugar(String id, double latitud, double longitud) {
        return new Lugar(id, "Lugar " + id, Categoria.ESTUDIO, null, latitud, longitud, null);
    }

    private static String ids(Iterable<Lugar> lugares) {
        String texto = "";
        for (Lugar lugar : lugares) {
            if (!texto.isEmpty()) {
                texto += ",";
            }
            texto += lugar.getId();
        }
        return texto;
    }

    static void ejecutar() {
        grupo("Navegacion Maps (T-04)");

        caso("el enlace usa punto decimal aunque el sistema este en español", () -> {
            Locale original = Locale.getDefault();
            try {
                Locale.setDefault(Locale.forLanguageTag("es-CO"));
                String url = NavegacionMaps.urlComoLlegar(lugar("1", 4.63806, -74.08475));
                iguales("https://www.google.com/maps/dir/?api=1&destination=4.638060,-74.084750", url);
            } finally {
                Locale.setDefault(original);
            }
        });

        grupo("HistorialVistas (lista enlazada)");

        caso("el mas reciente queda primero y no hay repetidos", () -> {
            HistorialVistas historial = new HistorialVistas(5);
            historial.registrarVista(lugar("A", 0, 0));
            historial.registrarVista(lugar("B", 0, 0));
            historial.registrarVista(lugar("C", 0, 0));
            historial.registrarVista(lugar("A", 0, 0));
            iguales("A,C,B", ids(historial.recientes()));
            verificar(historial.invariantesValidos(), "invariantes");
        });

        caso("respeta la capacidad descartando el mas antiguo", () -> {
            HistorialVistas historial = new HistorialVistas(3);
            historial.registrarVista(lugar("A", 0, 0));
            historial.registrarVista(lugar("B", 0, 0));
            historial.registrarVista(lugar("C", 0, 0));
            historial.registrarVista(lugar("D", 0, 0));
            iguales("D,C,B", ids(historial.recientes()));
        });

        grupo("RutaDelDia (cola)");

        caso("se recorre en orden FIFO y no admite paradas repetidas", () -> {
            RutaDelDia ruta = new RutaDelDia();
            verificar(ruta.agregar(lugar("A", 0, 0)), "agrega A");
            verificar(ruta.agregar(lugar("B", 0, 0)), "agrega B");
            verificar(!ruta.agregar(lugar("A", 0, 0)), "A repetido se rechaza");
            iguales("A", ruta.avanzar().getId());
            iguales("B", ruta.proxima().getId());
        });

        caso("la distancia total suma los tramos en orden", () -> {
            RutaDelDia ruta = new RutaDelDia();
            ruta.agregar(lugar("A", 0, 1));
            ruta.agregar(lugar("B", 0, 2));
            double km = ruta.distanciaTotalKm(new Ubicacion("Origen", 0, 0));
            // 2 grados de longitud sobre el ecuador son unos 222,4 km
            verificar(Math.abs(km - 222.39) < 0.5, "se esperaban ~222,4 km y fueron " + km);
        });

        grupo("CatalogoLugares");

        CatalogoLugares catalogo = new CatalogoLugares(DatosDemo.lugares());

        caso("la busqueda ignora mayusculas y tildes", () -> {
            iguales("P02", catalogo.buscar("LEON de greiff", null).obtenerPrimero().getId());
            iguales("P02", catalogo.buscar("león", null).obtenerPrimero().getId());
        });

        caso("una busqueda sin resultados devuelve una lista vacia (T-02)", () -> {
            verificar(catalogo.buscar("zzzz", null).estaVacia(), "deberia estar vacia");
        });

        caso("el filtro por categoria solo devuelve esa categoria", () -> {
            ListaEnlazada<Lugar> parques = catalogo.buscar("", Categoria.PARQUE);
            iguales(catalogo.contarPorCategoria(Categoria.PARQUE), parques.tamano());
            for (Lugar parque : parques) {
                iguales(Categoria.PARQUE, parque.getCategoria());
            }
        });

        caso("ordenar por cercania deja primero el mas cercano", () -> {
            Ubicacion campus = new Ubicacion("Campus", 4.6357, -74.0829);
            ListaEnlazada<Lugar> lugares = catalogo.todos();
            CatalogoLugares.ordenarPorCercania(lugares, campus);
            double anterior = -1;
            for (Lugar actual : lugares) {
                double distancia = campus.distanciaKm(actual);
                verificar(distancia >= anterior, "distancias desordenadas en " + actual.getId());
                anterior = distancia;
            }
        });

        caso("ordenar por categoria deja cada categoria en orden alfabetico", () -> {
            ListaEnlazada<Lugar> lugares = catalogo.todos();
            CatalogoLugares.ordenarPorCategoria(lugares);
            Lugar anterior = null;
            for (Lugar actual : lugares) {
                if (anterior != null && anterior.getCategoria() == actual.getCategoria()) {
                    String a = CatalogoLugares.normalizar(anterior.getNombre());
                    String b = CatalogoLugares.normalizar(actual.getNombre());
                    verificar(a.compareTo(b) <= 0, anterior.getNombre() + " deberia ir antes de " + actual.getNombre());
                }
                anterior = actual;
            }
        });
    }
}
