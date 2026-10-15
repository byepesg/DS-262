package co.edu.unal.residencias;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SistemaResidenciasTest {
    private SistemaResidencias sistema;

    @BeforeEach
    void preparar() {
        sistema = new SistemaResidencias();
        sistema.registrarEstudiante(103, "Jhon Berdugo", 20);
        sistema.registrarEstudiante(101, "Laura Flores", 8);
        sistema.registrarEstudiante(102, "Juan Manrique", 8);
        sistema.registrarEstudiante(104, "Maria Perez", 4);
    }

    @Test
    void consultaPorId() {
        assertEquals("Laura Flores", sistema.buscarPorId(101).orElseThrow().getNombreCompleto());
        assertTrue(sistema.buscarPorId(999).isEmpty());
    }

    @Test
    void ordenaPorPuntajeYDesempataPorId() {
        assertEquals(List.of(104, 101, 102, 103), ids(sistema.listarPorPuntaje()));
    }

    @Test
    void modificarPuntajeActualizaLaPrioridad() {
        sistema.modificarPuntaje(103, 2);
        assertEquals(103, sistema.listarPorPuntaje().get(0).getId());
    }

    @Test
    void asignaCuposSinDestruirSolicitudes() {
        sistema.definirCupos(2);
        ResultadoAsignacion resultado = sistema.asignarCupos();
        assertEquals(List.of(104, 101), ids(resultado.beneficiarios()));
        assertEquals(List.of(102, 103), ids(resultado.noBeneficiarios()));
        assertEquals(4, sistema.cantidadEstudiantes());
    }

    @Test
    void eliminaDeAmbasEstructuras() {
        sistema.eliminarEstudiante(104);
        assertTrue(sistema.buscarPorId(104).isEmpty());
        assertEquals(List.of(101, 102, 103), ids(sistema.listarPorPuntaje()));
    }

    @Test
    void rechazaDatosInvalidos() {
        assertThrows(IllegalArgumentException.class,
                () -> sistema.registrarEstudiante(101, "Duplicado", 1));
        assertThrows(IllegalArgumentException.class,
                () -> sistema.registrarEstudiante(200, "", 10));
        assertThrows(IllegalArgumentException.class,
                () -> sistema.registrarEstudiante(201, "Prueba", 101));
        assertThrows(IllegalArgumentException.class,
                () -> sistema.definirCupos(-1));
    }

    private static List<Integer> ids(List<Estudiante> estudiantes) {
        return estudiantes.stream().map(Estudiante::getId).toList();
    }
}
