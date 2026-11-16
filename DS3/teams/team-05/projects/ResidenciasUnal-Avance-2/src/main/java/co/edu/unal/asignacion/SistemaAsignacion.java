package co.edu.unal.asignacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

/**
 * Motor de asignación general.
 * T representa una solicitud; la subclase define el ID, las copias y la prioridad.
 */
public abstract class SistemaAsignacion<T> {
    private final Comparator<T> ordenPrioridad;
    private Map<Integer, T> solicitudesPorId = new HashMap<>();
    private PriorityQueue<T> colaPrioridad;
    private int cuposDisponibles;

    protected SistemaAsignacion(Comparator<T> ordenPrioridad) {
        this.ordenPrioridad = ordenPrioridad;
        colaPrioridad = new PriorityQueue<>(ordenPrioridad);
    }

    protected abstract int obtenerId(T solicitud);
    protected abstract T copiarSolicitud(T solicitud);

    protected final void registrarSolicitud(T solicitud) {
        int id = obtenerId(solicitud);
        if (solicitudesPorId.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe una solicitud con el ID " + id + ".");
        }
        T copia = copiarSolicitud(solicitud);
        colaPrioridad.offer(copia);
        solicitudesPorId.put(id, copia);
    }

    protected final Optional<T> buscarSolicitud(int id) {
        return Optional.ofNullable(solicitudesPorId.get(id)).map(this::copiarSolicitud);
    }

    protected final T obtenerObligatoria(int id) {
        return buscarSolicitud(id).orElseThrow(() ->
                new IllegalArgumentException("No existe una solicitud con el ID " + id + "."));
    }

    protected final void reemplazarSolicitud(T nuevaSolicitud) {
        int id = obtenerId(nuevaSolicitud);
        T anterior = solicitudesPorId.get(id);
        if (anterior == null) {
            throw new IllegalArgumentException("No existe una solicitud con el ID " + id + ".");
        }
        T copia = copiarSolicitud(nuevaSolicitud);
        colaPrioridad.remove(anterior);
        colaPrioridad.offer(copia);
        solicitudesPorId.put(id, copia);
    }

    protected final void eliminarSolicitud(int id) {
        T eliminado = solicitudesPorId.get(id);
        if (eliminado == null) {
            throw new IllegalArgumentException("No existe una solicitud con el ID " + id + ".");
        }
        colaPrioridad.remove(eliminado);
        solicitudesPorId.remove(id);
    }

    protected final void configurarCupos(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        cuposDisponibles = cantidad;
    }

    public final int getCuposDisponibles() { return cuposDisponibles; }
    protected final int cantidadSolicitudes() { return solicitudesPorId.size(); }

    protected final List<T> listarSolicitudesOrdenadas() {
        PriorityQueue<T> copia = new PriorityQueue<>(colaPrioridad);
        List<T> ordenados = new ArrayList<>(copia.size());
        while (!copia.isEmpty()) ordenados.add(copiarSolicitud(copia.poll()));
        return List.copyOf(ordenados);
    }

    protected final Distribucion<T> distribuirCupos() {
        List<T> ordenados = listarSolicitudesOrdenadas();
        int limite = Math.min(cuposDisponibles, ordenados.size());
        return new Distribucion<>(cuposDisponibles,
                ordenados.subList(0, limite), ordenados.subList(limite, ordenados.size()));
    }

    protected record Distribucion<E>(int cuposConfigurados,
            List<E> asignados, List<E> enEspera) {
        public Distribucion {
            asignados = List.copyOf(asignados);
            enEspera = List.copyOf(enEspera);
        }
    }

    protected final void restaurarSolicitudes(List<T> solicitudes, int cupos) {
        if (cupos < 0) throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        Map<Integer, T> nuevoIndice = new HashMap<>();
        PriorityQueue<T> nuevaCola = new PriorityQueue<>(ordenPrioridad);
        for (T solicitud : solicitudes) {
            T copia = copiarSolicitud(solicitud);
            int id = obtenerId(copia);
            if (nuevoIndice.putIfAbsent(id, copia) != null) {
                throw new IllegalArgumentException("El archivo repite el ID " + id + ".");
            }
            nuevaCola.offer(copia);
        }
        solicitudesPorId = nuevoIndice;
        colaPrioridad = nuevaCola;
        cuposDisponibles = cupos;
    }
}