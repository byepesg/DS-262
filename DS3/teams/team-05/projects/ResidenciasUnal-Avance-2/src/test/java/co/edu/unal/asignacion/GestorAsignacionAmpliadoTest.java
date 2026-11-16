package co.edu.unal.asignacion;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorAsignacionAmpliadoTest {
    private GestorAsignacion sistema;

    @BeforeEach
    void preparar() {
        sistema = new GestorAsignacion();
        sistema.registrarCandidato(30, "Ana", 5);
        sistema.registrarCandidato(10, "Beto", 5);
        sistema.registrarCandidato(20, "Carla", 9);
        sistema.registrarCandidato(40, "Diego", 7);
        sistema.definirCupos(2);
    }

    @Test
    void puntajeInvalidoNoRetiraCandidatoDelHeap() {
        int movimientos = sistema.historialOperaciones().size();
        assertThrows(IllegalArgumentException.class, () -> sistema.modificarPuntaje(10, 101));
        assertEquals(List.of(10, 30, 40, 20), ids(sistema.listarPorPuntaje()));
        assertEquals(5, sistema.buscarPorId(10).orElseThrow().getPuntaje());
        assertEquals(4, sistema.cantidadCandidatos());
        assertEquals(movimientos, sistema.historialOperaciones().size());
    }

    @Test
    void duplicadoNoCambiaNingunaEstructura() {
        assertThrows(IllegalArgumentException.class, () -> sistema.registrarCandidato(10, "Otra", 0));
        assertEquals("Beto", sistema.buscarPorId(10).orElseThrow().getNombre());
        assertEquals(List.of(10, 30, 40, 20), ids(sistema.listarPorPuntaje()));
    }

    @Test
    void modificarOEliminarInexistenteNoCambiaElEstado() {
        assertThrows(IllegalArgumentException.class, () -> sistema.modificarPuntaje(999, 0));
        assertThrows(IllegalArgumentException.class, () -> sistema.eliminarCandidato(999));
    }

    @Test
    void ceroCuposDejaTodasLasSolicitudesEnEspera() {
        sistema.definirCupos(0);
        assertTrue(sistema.asignarCupos().asignados().isEmpty());
        assertEquals(List.of(10, 30, 40, 20), ids(sistema.listarEspera()));
        assertFalse(sistema.consultarEstado(10).asignado());
    }

    @Test
    void estadoIncluyePosicionYClasificacion() {
        EstadoSolicitud primero = sistema.consultarEstado(10);
        assertEquals(1, primero.posicion());
        assertTrue(primero.asignado());
        assertEquals("ASIGNADO", primero.descripcion());
        
        EstadoSolicitud tercero = sistema.consultarEstado(40);
        assertEquals(3, tercero.posicion());
        assertFalse(tercero.asignado());
        assertEquals("EN ESPERA", tercero.descripcion());
    }

    @Test
    void consultaDevuelveCopiaDefensiva() {
        Candidato consulta = sistema.buscarPorId(20).orElseThrow();
        consulta.actualizarPuntaje(0);
        assertEquals(9, sistema.buscarPorId(20).orElseThrow().getPuntaje());
    }

    @Test
    void resultadoAnteriorNoCambiaAlModificarElSistema() {
        ResultadoAsignacion anterior = sistema.asignarCupos();
        sistema.modificarPuntaje(10, 100);
        assertEquals(5, anterior.asignados().get(0).getPuntaje());
    }

    @Test
    void resumenPresentaTotalesConsistentes() {
        assertEquals(new ResumenSistema(4, 2, 2, 2, 0, 6.5), sistema.generarResumen());
    }

    private static List<Integer> ids(List<Candidato> lista) {
        return lista.stream().map(Candidato::getId).toList();
    }
}