package co.edu.unal.asignacion;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ArchivosTest {
    @TempDir Path carpeta;
    private GestorAsignacion sistema;

    @BeforeEach
    void preparar() {
        sistema = new GestorAsignacion();
        sistema.registrarCandidato(10, "María; \"López\"\\Apellido\nGarcía", 5);
        sistema.registrarCandidato(20, "José", 9);
        sistema.definirCupos(1);
    }

    @Test
    void guardarYCargarConservaTodosLosCamposYCupos() throws IOException {
        Path archivo = carpeta.resolve("datos.properties");
        sistema.guardarDatos(archivo);
        
        GestorAsignacion restaurado = new GestorAsignacion();
        restaurado.cargarDatos(archivo);
        
        assertEquals(1, restaurado.getCuposDisponibles());
        assertEquals(2, restaurado.cantidadCandidatos());
        
        assertEquals(sistema.buscarPorId(10).orElseThrow().getNombre(),
                restaurado.buscarPorId(10).orElseThrow().getNombre());
                
        assertEquals(List.of(10, 20), ids(restaurado.listarPorPuntaje()));
        
        assertEquals(9, restaurado.buscarPorId(20).orElseThrow().getPuntaje());
    }

    @Test
    void cargarReemplazaEnLugarDeDuplicarRegistros() throws IOException {
        Path archivo = carpeta.resolve("datos.properties");
        sistema.guardarDatos(archivo);
        sistema.registrarCandidato(30, "Otra", 0);
        sistema.definirCupos(100);
        sistema.cargarDatos(archivo);
        
        assertEquals(List.of(10, 20), ids(sistema.listarPorPuntaje()));
        assertTrue(sistema.buscarPorId(30).isEmpty());
        assertEquals(1, sistema.getCuposDisponibles());
    }

    @Test
    void estadoVacioTambienSeGuarda() throws IOException {
        GestorAsignacion vacio = new GestorAsignacion();
        vacio.definirCupos(3);
        Path archivo = carpeta.resolve("vacio.properties");
        vacio.guardarDatos(archivo);
        sistema.cargarDatos(archivo);
        
        assertEquals(0, sistema.cantidadCandidatos());
        assertEquals(3, sistema.getCuposDisponibles());
    }

    @Test
    void cargaInvalidaNoPierdeElEstadoAnterior() throws IOException {
        rechazarSinCambios("version=2\ncupos=1\ntotal=2\n"
                + "candidato.0.id=40\ncandidato.0.nombre=Válido\ncandidato.0.puntaje=5\n"
                + "candidato.1.id=50\ncandidato.1.nombre=Inválido\ncandidato.1.puntaje=101\n");
    }

    @Test
    void cargaRechazaIdsDuplicadosSinAplicarCambios() throws IOException {
        rechazarSinCambios("version=2\ncupos=1\ntotal=2\n"
                + "candidato.0.id=40\ncandidato.0.nombre=Uno\ncandidato.0.puntaje=5\n"
                + "candidato.1.id=40\ncandidato.1.nombre=Dos\ncandidato.1.puntaje=6\n");
    }

    @Test
    void cargaRechazaVersionDesconocida() throws IOException {
        rechazarSinCambios("version=1\ncupos=1\ntotal=0\n");
    }

    @Test
    void cargaRechazaCuposNegativos() throws IOException {
        rechazarSinCambios("version=2\ncupos=-1\ntotal=0\n");
    }

    @Test
    void cargaRechazaEnteroInvalido() throws IOException {
        rechazarSinCambios("version=2\ncupos=abc\ntotal=0\n");
    }

    @Test
    void cargaRechazaDatosIncompletos() throws IOException {
        rechazarSinCambios("version=2\ncupos=1\ntotal=1\n"
                + "candidato.0.id=40\ncandidato.0.puntaje=5\n");
    }

    @Test
    void totalDesproporcionadoSeRechazaAntesDeReservarMemoria() throws IOException {
        rechazarSinCambios("version=2\ncupos=1\ntotal=2147483647\n");
    }

    @Test
    void archivoInexistenteNoCambiaDatosNiHistorial() {
        int cantidad = sistema.historialOperaciones().size();
        assertThrows(IOException.class, () -> sistema.cargarDatos(carpeta.resolve("no-existe")));
        assertEquals(2, sistema.cantidadCandidatos());
        assertEquals(cantidad, sistema.historialOperaciones().size());
    }

    @Test
    void falloDeGuardadoNoAgregaMovimiento() {
        int cantidad = sistema.historialOperaciones().size();
        assertThrows(IOException.class,
                () -> sistema.guardarDatos(carpeta.resolve("carpeta-inexistente/datos.properties")));
        assertEquals(cantidad, sistema.historialOperaciones().size());
    }

    @Test
    void historialNoSeImportaComoSiFueraDeLaNuevaSesion() throws IOException {
        Path archivo = carpeta.resolve("datos.properties");
        sistema.guardarDatos(archivo);
        GestorAsignacion restaurado = new GestorAsignacion();
        restaurado.cargarDatos(archivo);
        assertEquals(1, restaurado.historialOperaciones().size());
        assertEquals("CARGAR", restaurado.historialOperaciones().get(0).operacion());
    }

    @Test
    void csvConservaOrdenEstadoYCaracteresEspeciales() throws IOException {
        Path archivo = carpeta.resolve("reporte.csv");
        sistema.exportarReporte(archivo);
        
        String csv = Files.readString(archivo, StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\uFEFFPOSICION;ID;NOMBRE;PUNTAJE;ESTADO"));
        assertTrue(csv.contains("1;10;\"María; \"\"López\"\"\\Apellido\nGarcía\";5;ASIGNADO"));
        assertTrue(csv.contains("2;20;\"José\";9;EN ESPERA"));
        assertTrue(csv.indexOf("1;10;") < csv.indexOf("2;20;"));
        assertEquals(2, sistema.cantidadCandidatos());
    }

    @Test
    void csvVacioContieneSolamenteCabecera() throws IOException {
        Path archivo = carpeta.resolve("vacio.csv");
        new GestorAsignacion().exportarReporte(archivo);
        assertEquals(1, Files.readAllLines(archivo, StandardCharsets.UTF_8).size());
    }

    @Test
    void csvExportaNombresComoTexto() throws IOException {
        sistema.registrarCandidato(30, "=1+2", 0);
        Path archivo = carpeta.resolve("texto.csv");
        sistema.exportarReporte(archivo);
        assertTrue(Files.readString(archivo).contains("\"'=1+2\""));
    }

    private void rechazarSinCambios(String contenido) throws IOException {
        Path archivo = carpeta.resolve("invalido.properties");
        Files.writeString(archivo, contenido, StandardCharsets.UTF_8);
        int movimientos = sistema.historialOperaciones().size();
        assertThrows(IllegalArgumentException.class, () -> sistema.cargarDatos(archivo));
        assertEquals(List.of(10, 20), ids(sistema.listarPorPuntaje()));
        assertEquals(1, sistema.getCuposDisponibles());
        assertEquals(movimientos, sistema.historialOperaciones().size());
    }

    private static List<Integer> ids(List<Candidato> lista) {
        return lista.stream().map(Candidato::getId).toList();
    }
}