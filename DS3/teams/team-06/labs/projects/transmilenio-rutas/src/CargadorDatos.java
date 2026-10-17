import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class CargadorDatos {
    private CargadorDatos() {
    }

    // lee estaciones, tramos y rutas
    public static RedTransmilenio cargar(Path carpeta) throws IOException {
        RedTransmilenio red = new RedTransmilenio();
        for (String[] c : leer(carpeta.resolve("estaciones.csv"), 3)) {
            List<String> alias = c[2].isBlank() ? List.of() : Arrays.asList(c[2].split("\\|"));
            red.agregarEstacion(new Estacion(c[0].trim(), c[1], alias));
        }
        for (String[] c : leer(carpeta.resolve("tramos.csv"), 3)) {
            red.agregarTramo(c[0].trim(), c[1].trim(), entero(c[2], "tramos.csv"));
        }
        for (String[] c : leer(carpeta.resolve("servicios.csv"), 4)) {
            List<String> paradas = new ArrayList<>();
            for (String p : c[3].split("\\|")) paradas.add(p.trim());
            red.agregarServicio(new Servicio(c[0], c[1].trim(), c[2].trim(), paradas));
        }
        return red;
    }

    private static int entero(String s, String archivo) throws IOException {
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            throw new IOException("Número inválido en " + archivo + ": " + s);
        }
    }

    private static List<String[]> leer(Path archivo, int columnas) throws IOException {
        if (!Files.exists(archivo)) {
            throw new IOException("No se encontró el archivo de datos: " + archivo.toAbsolutePath());
        }
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea = br.readLine();
            int n = 1;
            while ((linea = br.readLine()) != null) {
                n++;
                if (linea.isBlank()) continue;
                String[] campos = linea.split(",", -1);
                if (campos.length != columnas) {
                    throw new IOException(archivo.getFileName() + " línea " + n + ": se esperaban " + columnas + " columnas");
                }
                filas.add(campos);
            }
        }
        return filas;
    }
}
