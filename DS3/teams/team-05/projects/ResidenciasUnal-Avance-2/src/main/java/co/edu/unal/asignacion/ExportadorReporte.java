package co.edu.unal.asignacion;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ExportadorReporte {
    private ExportadorReporte() { }

    // Ahora recibe etiquetas personalizables para el CSV
    public static void exportar(Path ruta, ResultadoAsignacion resultado, 
                                String etiquetaAsignado, String etiquetaEspera) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(ruta, StandardCharsets.UTF_8)) {
            writer.write('\uFEFF');
            writer.write("POSICION;ID;NOMBRE;PUNTAJE;ESTADO");
            writer.newLine();
            int posicion = 1;
            for (Candidato c : resultado.asignados()) {
                escribir(writer, posicion++, c, etiquetaAsignado);
            }
            for (Candidato c : resultado.enEspera()) {
                escribir(writer, posicion++, c, etiquetaEspera);
            }
        }
    }

    private static void escribir(BufferedWriter writer, int posicion,
            Candidato candidato, String estado) throws IOException {
        writer.write(posicion + ";" + candidato.getId() + ";"
                + celdaTexto(candidato.getNombre()) + ";"
                + candidato.getPuntaje() + ";" + estado);
        writer.newLine();
    }

    private static String celdaTexto(String texto) {
        String valor = texto;
        if (!valor.isEmpty() && "=+-@\t\r\n".indexOf(valor.charAt(0)) >= 0) valor = "'" + valor;
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}