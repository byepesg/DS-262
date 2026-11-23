package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import java.time.LocalDateTime;

// Guarda los parches en memoria. Los métodos reciben "ahora" para poder probarlos con cualquier fecha.
public class GestorParches {

    private final ListaEnlazada<Parche> parches = new ListaEnlazada<>();
    private int consecutivo = 1;

    public Parche crear(Usuario creador, Lugar lugar, String titulo, LocalDateTime fechaHora,
                        int capacidad, LocalDateTime ahora) {
        if (fechaHora == null || !fechaHora.isAfter(ahora)) {
            throw new IllegalArgumentException("La fecha y hora del parche deben ser futuras");
        }
        String id = String.format("PCH-%03d", consecutivo);
        Parche parche = new Parche(id, lugar, creador, titulo, fechaHora, capacidad);
        parches.agregarAlFinal(parche);
        consecutivo++;
        return parche;
    }

    // Búsqueda lineal O(n). Con los parches de una sesión es suficiente;
    // TODO: si se vuelven muchos (Sprint 3), evaluar una tabla hash por id
    public Parche buscarPorId(String id) {
        for (Parche parche : parches) {
            if (parche.getId().equalsIgnoreCase(id)) {
                return parche;
            }
        }
        return null;
    }

    public ListaEnlazada<Parche> activos(LocalDateTime ahora) {
        actualizarEstados(ahora);
        ListaEnlazada<Parche> resultado = new ListaEnlazada<>();
        for (Parche parche : parches) {
            if (parche.estaActivo()) {
                resultado.agregarAlFinal(parche);
            }
        }
        return resultado;
    }

    public ListaEnlazada<Parche> activosEn(Lugar lugar, LocalDateTime ahora) {
        ListaEnlazada<Parche> resultado = new ListaEnlazada<>();
        for (Parche parche : activos(ahora)) {
            if (parche.getLugar().equals(lugar)) {
                resultado.agregarAlFinal(parche);
            }
        }
        return resultado;
    }

    // Incluye cancelados y finalizados, para que el usuario vea su historial
    public ListaEnlazada<Parche> delUsuario(Usuario usuario, LocalDateTime ahora) {
        actualizarEstados(ahora);
        ListaEnlazada<Parche> resultado = new ListaEnlazada<>();
        for (Parche parche : parches) {
            boolean participa = parche.esParticipante(usuario) || parche.posicionEnEspera(usuario) > 0;
            if (participa || parche.esCreador(usuario)) {
                resultado.agregarAlFinal(parche);
            }
        }
        return resultado;
    }

    public void actualizarEstados(LocalDateTime ahora) {
        for (Parche parche : parches) {
            parche.finalizarSiYaPaso(ahora);
        }
    }

    public Iterable<Parche> todos() {
        return parches;
    }

    public int cantidad() {
        return parches.tamano();
    }

    public boolean invariantesValidos() {
        return parches.invariantesValidos();
    }

    public static void ordenarPorFecha(ListaEnlazada<Parche> lista) {
        lista.ordenar((a, b) -> a.getFechaHora().compareTo(b.getFechaHora()));
    }

    public static void ordenarPorCercania(ListaEnlazada<Parche> lista, Ubicacion desde) {
        lista.ordenar((a, b) -> Double.compare(desde.distanciaKm(a.getLugar()), desde.distanciaKm(b.getLugar())));
    }
}
