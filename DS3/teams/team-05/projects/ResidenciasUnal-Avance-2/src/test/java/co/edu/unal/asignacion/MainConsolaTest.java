package co.edu.unal.asignacion;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainConsolaTest {
    @TempDir Path carpeta;

    @Test
    void consolaIntegraRegistroAsignacionEsperaYValidacion() throws Exception {
        String salida = ejecutar("1\n30\nAna\n5\n1\n10\nBeto\n5\n"
                + "6\n1\n7\n8\n30\n4\n10\n101\n9\n10\n11\n0\n");
        assertTrue(salida.contains("ASIGNADOS"));
        assertTrue(salida.contains("Posición: 2 | Estado: EN ESPERA"));
        assertTrue(salida.contains("El puntaje debe estar entre 0 y 100."));
        assertTrue(salida.contains("Solicitudes totales: 2"));
        assertTrue(salida.contains("Asignados: 1 | En espera: 1"));
        assertTrue(salida.contains("Programa finalizado."));
    }

    private String ejecutar(String entrada) throws IOException, InterruptedException {
        String java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        String clases = Path.of("target", "classes").toAbsolutePath().toString();
        Path archivoSalida = carpeta.resolve("consola.txt");
        Process proceso = new ProcessBuilder(java, "-cp", clases, Main.class.getName())
                .redirectErrorStream(true).redirectOutput(archivoSalida.toFile()).start();
        try {
            proceso.getOutputStream().write(entrada.getBytes(StandardCharsets.UTF_8));
            proceso.getOutputStream().close();
            proceso.waitFor(10, TimeUnit.SECONDS);
            return Files.readString(archivoSalida, StandardCharsets.UTF_8);
        } finally {
            proceso.destroyForcibly();
        }
    }
}