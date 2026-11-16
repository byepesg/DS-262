package co.edu.unal.asignacion;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public final class GestorAsignacion extends SistemaAsignacion<Candidato> {
    
    // Regla general: menor puntaje (u otra lógica que se requiera); en empate, menor ID.
    private static final Comparator<Candidato> ORDEN_PRIORIDAD =
            Comparator.comparingInt(Candidato::getPuntaje)
                    .thenComparingInt(Candidato::getId);
                    
    public static final int LIMITE_HISTORIAL = 100;
    private final Deque<Movimiento> historial = new ArrayDeque<>();
    private long numeroMovimiento;

    public GestorAsignacion() { super(ORDEN_PRIORIDAD); }

    @Override
    protected int obtenerId(Candidato candidato) { return candidato.getId(); }

    @Override
    protected Candidato copiarSolicitud(Candidato candidato) { return candidato.copiar(); }

    public void registrarCandidato(int id, String nombre, int puntaje) {
        registrarSolicitud(new Candidato(id, nombre, puntaje));
        registrarMovimiento("REGISTRAR", "ID " + id + ", puntaje " + puntaje);
    }

    public Optional<Candidato> buscarPorId(int id) { return buscarSolicitud(id); }

    public void modificarPuntaje(int id, int nuevoPuntaje) {
        Candidato candidato = obtenerObligatoria(id);
        candidato.actualizarPuntaje(nuevoPuntaje);
        reemplazarSolicitud(candidato);
        registrarMovimiento("MODIFICAR", "ID " + id + ", nuevo puntaje " + nuevoPuntaje);
    }

    public void eliminarCandidato(int id) {
        eliminarSolicitud(id);
        registrarMovimiento("ELIMINAR", "ID " + id);
    }

    public void definirCupos(int cantidad) {
        configurarCupos(cantidad);
        registrarMovimiento("CUPOS", "Cupos configurados: " + cantidad);
    }

    public int cantidadCandidatos() { return cantidadSolicitudes(); }
    public List<Candidato> listarPorPuntaje() { return listarSolicitudesOrdenadas(); }

    public ResultadoAsignacion asignarCupos() {
        ResultadoAsignacion resultado = calcularResultado();
        registrarMovimiento("ASIGNAR", resultado.asignados().size() + " asignados; "
                + resultado.enEspera().size() + " en espera");
        return resultado;
    }

    private ResultadoAsignacion calcularResultado() {
        Distribucion<Candidato> distribucion = distribuirCupos();
        return new ResultadoAsignacion(distribucion.cuposConfigurados(),
                distribucion.asignados(), distribucion.enEspera());
    }

    public List<Candidato> listarEspera() { return calcularResultado().enEspera(); }

    public EstadoSolicitud consultarEstado(int id) {
        obtenerObligatoria(id);
        List<Candidato> ordenados = listarPorPuntaje();
        for (int i = 0; i < ordenados.size(); i++) {
            Candidato candidato = ordenados.get(i);
            if (candidato.getId() == id) {
                return new EstadoSolicitud(candidato, i + 1, i < getCuposDisponibles());
            }
        }
        throw new IllegalStateException("El índice y la cola de prioridad no coinciden.");
    }

    public ResumenSistema generarResumen() {
        List<Candidato> ordenados = listarPorPuntaje();
        int total = ordenados.size();
        int asignados = Math.min(getCuposDisponibles(), total);
        double promedio = ordenados.stream().mapToInt(Candidato::getPuntaje)
                .average().orElse(0.0);
        return new ResumenSistema(total, getCuposDisponibles(), asignados,
                total - asignados, getCuposDisponibles() - asignados, promedio);
    }

    public void guardarDatos(Path ruta) throws IOException {
        GestorArchivo.guardar(ruta,
                new DatosGuardados(getCuposDisponibles(), listarPorPuntaje()));
        registrarMovimiento("GUARDAR", "Estado guardado en " + ruta.getFileName());
    }

    public void cargarDatos(Path ruta) throws IOException {
        DatosGuardados datos = GestorArchivo.cargar(ruta);
        restaurarSolicitudes(datos.candidatos(), datos.cupos());
        registrarMovimiento("CARGAR", datos.candidatos().size() + " registros cargados");
    }

    public void exportarReporte(Path ruta) throws IOException {
        ExportadorReporte.exportar(ruta, calcularResultado(), "BENEFICIARIO", "EN ESPERA");
        registrarMovimiento("EXPORTAR", "Reporte exportado a " + ruta.getFileName());
    }

    public List<Movimiento> historialOperaciones() { return List.copyOf(historial); }

    private void registrarMovimiento(String operacion, String detalle) {
        if (historial.size() == LIMITE_HISTORIAL) historial.removeFirst();
        historial.addLast(new Movimiento(++numeroMovimiento, operacion, detalle));
    }
}