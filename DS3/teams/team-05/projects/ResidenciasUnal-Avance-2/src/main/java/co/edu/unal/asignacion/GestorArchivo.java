package co.edu.unal.asignacion;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class GestorArchivo {
    private GestorArchivo() { }

    public static void guardar(Path ruta, DatosGuardados datos) throws IOException {
        Properties propiedades = new Properties();
        propiedades.setProperty("version", "2");
        propiedades.setProperty("cupos", Integer.toString(datos.cupos()));
        propiedades.setProperty("total", Integer.toString(datos.candidatos().size()));
        
        for (int i = 0; i < datos.candidatos().size(); i++) {
            Candidato c = datos.candidatos().get(i);
            String prefijo = "registro." + i + "."; // <--- Completamente genérico
            propiedades.setProperty(prefijo + "id", Integer.toString(c.getId()));
            propiedades.setProperty(prefijo + "nombre", c.getNombre());
            propiedades.setProperty(prefijo + "puntaje", Integer.toString(c.getPuntaje()));
        }
        
        Path destino = ruta.toAbsolutePath();
        if (destino.getParent() == null || Files.isDirectory(destino)) {
            throw new IllegalArgumentException("La ruta debe identificar un archivo.");
        }
        
        Path temporal = Files.createTempFile(destino.getParent(), ".datos-", ".tmp");
        try {
            try (Writer writer = Files.newBufferedWriter(temporal, StandardCharsets.UTF_8)) {
                propiedades.store(writer, "Motor Genérico de Asignación");
            }
            try {
                Files.move(temporal, destino, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    public static DatosGuardados cargar(Path ruta) throws IOException {
        Properties propiedades = new Properties();
        try (Reader reader = Files.newBufferedReader(ruta, StandardCharsets.UTF_8)) {
            propiedades.load(reader);
        }
        if (!"2".equals(requerida(propiedades, "version"))) {
            throw new IllegalArgumentException("Versión de archivo no compatible.");
        }
        
        int cupos = entero(propiedades, "cupos");
        int total = entero(propiedades, "total");
        
        if (total < 0 || 3L + 3L * total != propiedades.size()) {
            throw new IllegalArgumentException("El total no coincide con los datos del archivo.");
        }
        
        List<Candidato> candidatos = new ArrayList<>(total);
        Set<Integer> ids = new HashSet<>(); 
        
        for (int i = 0; i < total; i++) {
            String prefijo = "registro." + i + ".";
            Candidato candidato = new Candidato(entero(propiedades, prefijo + "id"),
                    requerida(propiedades, prefijo + "nombre"),
                    entero(propiedades, prefijo + "puntaje"));
            if (!ids.add(candidato.getId())) {
                throw new IllegalArgumentException("El archivo repite el ID " + candidato.getId() + ".");
            }
            candidatos.add(candidato);
        }
        return new DatosGuardados(cupos, candidatos);
    }

    private static String requerida(Properties propiedades, String clave) {
        String valor = propiedades.getProperty(clave);
        if (valor == null) throw new IllegalArgumentException("Falta el dato " + clave + ".");
        return valor;
    }

    private static int entero(Properties propiedades, String clave) {
        try {
            return Integer.parseInt(requerida(propiedades, clave).trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El dato " + clave + " debe ser un entero.");
        }
    }
}