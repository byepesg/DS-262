package com.parchapp.servicio;

import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.estructuras.ListaEnlazada;
import java.text.Normalizer;

public class CatalogoLugares {

    private final ListaEnlazada<Lugar> lugares;

    public CatalogoLugares(ListaEnlazada<Lugar> lugares) {
        this.lugares = lugares;
    }

    // Devuelve una lista nueva; texto vacío o categoría null significa "sin ese filtro"
    public ListaEnlazada<Lugar> buscar(String texto, Categoria categoria) {
        String consulta = normalizar(texto);
        ListaEnlazada<Lugar> resultado = new ListaEnlazada<>();
        for (Lugar lugar : lugares) {
            boolean categoriaCoincide = categoria == null || lugar.getCategoria() == categoria;
            boolean textoCoincide = consulta.isEmpty()
                    || normalizar(lugar.getNombre()).contains(consulta)
                    || normalizar(lugar.getDireccion()).contains(consulta)
                    || normalizar(lugar.getDescripcion()).contains(consulta);
            if (categoriaCoincide && textoCoincide) {
                resultado.agregarAlFinal(lugar);
            }
        }
        return resultado;
    }

    public ListaEnlazada<Lugar> todos() {
        return buscar("", null);
    }

    public Lugar buscarPorId(String id) {
        for (Lugar lugar : lugares) {
            if (lugar.getId().equalsIgnoreCase(id)) {
                return lugar;
            }
        }
        return null;
    }

    public int contarPorCategoria(Categoria categoria) {
        int cantidad = 0;
        for (Lugar lugar : lugares) {
            if (lugar.getCategoria() == categoria) {
                cantidad++;
            }
        }
        return cantidad;
    }

    public int cantidad() {
        return lugares.tamano();
    }

    public boolean invariantesValidos() {
        return lugares.invariantesValidos();
    }

    public static void ordenarPorNombre(ListaEnlazada<Lugar> lista) {
        lista.ordenar((a, b) -> normalizar(a.getNombre()).compareTo(normalizar(b.getNombre())));
    }

    public static void ordenarPorCategoria(ListaEnlazada<Lugar> lista) {
        // Primero por nombre y luego por categoría: como el merge sort es estable,
        // dentro de cada categoría los lugares quedan en orden alfabético
        ordenarPorNombre(lista);
        lista.ordenar((a, b) -> a.getCategoria().compareTo(b.getCategoria()));
    }

    public static void ordenarPorCercania(ListaEnlazada<Lugar> lista, Ubicacion desde) {
        lista.ordenar((a, b) -> Double.compare(desde.distanciaKm(a), desde.distanciaKm(b)));
    }

    // Minúsculas y sin tildes, para que "León" y "leon" coincidan
    public static String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().strip();
    }
}
