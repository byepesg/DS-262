package co.edu.unal.residencias;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

public final class SistemaResidencias {
    // Menor puntaje = mayor prioridad. En empate, menor ID primero.
    private static final Comparator<Estudiante> ORDEN_PRIORIDAD =
            Comparator.comparingInt(Estudiante::getPuntajeSocioeconomico)
                    .thenComparingInt(Estudiante::getId);

    private final Map<Integer, Estudiante> estudiantesPorId = new HashMap<>();
    private final PriorityQueue<Estudiante> colaPrioridad =
            new PriorityQueue<>(ORDEN_PRIORIDAD);
    private int cuposDisponibles;

    public void registrarEstudiante(int id, String nombre, int puntaje) {
        if (estudiantesPorId.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un estudiante con el ID " + id + ".");
        }
        Estudiante estudiante = new Estudiante(id, nombre, puntaje);
        estudiantesPorId.put(id, estudiante);
        colaPrioridad.offer(estudiante);
    }

    public Optional<Estudiante> buscarPorId(int id) {
        return Optional.ofNullable(estudiantesPorId.get(id));
    }

    public void modificarPuntaje(int id, int nuevoPuntaje) {
        Estudiante estudiante = obtenerObligatorio(id);
        // Se retira antes de mutar porque PriorityQueue no se reordena sola.
        colaPrioridad.remove(estudiante);
        estudiante.actualizarPuntaje(nuevoPuntaje);
        colaPrioridad.offer(estudiante);
    }

    public void eliminarEstudiante(int id) {
        Estudiante eliminado = estudiantesPorId.remove(id);
        if (eliminado == null) {
            throw new IllegalArgumentException("No existe un estudiante con el ID " + id + ".");
        }
        colaPrioridad.remove(eliminado);
    }

    public void definirCupos(int cantidad) {
        if (cantidad < 0) throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        cuposDisponibles = cantidad;
    }

    public int getCuposDisponibles() { return cuposDisponibles; }
    public int cantidadEstudiantes() { return estudiantesPorId.size(); }

    public List<Estudiante> listarPorPuntaje() {
        PriorityQueue<Estudiante> copia = new PriorityQueue<>(colaPrioridad);
        List<Estudiante> ordenados = new ArrayList<>(copia.size());
        while (!copia.isEmpty()) ordenados.add(copia.poll());
        return List.copyOf(ordenados);
    }

    public ResultadoAsignacion asignarCupos() {
        List<Estudiante> ordenados = listarPorPuntaje();
        int limite = Math.min(cuposDisponibles, ordenados.size());
        List<Estudiante> beneficiarios = new ArrayList<>(ordenados.subList(0, limite));
        List<Estudiante> noBeneficiarios = new ArrayList<>(ordenados.subList(limite, ordenados.size()));
        return new ResultadoAsignacion(cuposDisponibles, beneficiarios, noBeneficiarios);
    }

    private Estudiante obtenerObligatorio(int id) {
        Estudiante estudiante = estudiantesPorId.get(id);
        if (estudiante == null) {
            throw new IllegalArgumentException("No existe un estudiante con el ID " + id + ".");
        }
        return estudiante;
    }
}
