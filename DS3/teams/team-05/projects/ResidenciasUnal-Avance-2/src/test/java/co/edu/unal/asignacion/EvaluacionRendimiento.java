package co.edu.unal.asignacion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

public final class EvaluacionRendimiento {
    private EvaluacionRendimiento() { }

    public static void main(String[] args) throws IOException {
        Path destino = Path.of(args.length == 0 ? "rendimiento.csv" : args[0]);
        StringBuilder csv = new StringBuilder("n,registro_ms,asignacion_ms,asignados,en_espera\n");
        
        for (int n : new int[] {100, 1000, 10000}) {
            long[] registro = new long[5];
            long[] asignacion = new long[5];
            for (int intento = 0; intento < 8; intento++) {
                int[] puntajes = new Random(262).ints(n, 0, 101).toArray();
                GestorAsignacion sistema = new GestorAsignacion();
                
                long inicio = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    sistema.registrarCandidato(i + 1, "Candidato " + (i + 1), puntajes[i]);
                }
                long finRegistro = System.nanoTime();
                
                sistema.definirCupos(n / 4);
                long inicioAsignacion = System.nanoTime();
                ResultadoAsignacion resultado = sistema.asignarCupos();
                long finAsignacion = System.nanoTime();
                
                if (resultado.asignados().size() != n / 4 || resultado.enEspera().size() != n - n / 4) {
                    throw new IllegalStateException("Resultado inconsistente en n=" + n);
                }
                
                Candidato anterior = null;
                for (Candidato e : sistema.listarPorPuntaje()) {
                    if (anterior != null && (anterior.getPuntaje() > e.getPuntaje()
                            || anterior.getPuntaje() == e.getPuntaje() && anterior.getId() > e.getId())) {
                        throw new IllegalStateException("Orden incorrecto en n=" + n);
                    }
                    anterior = e;
                }
                
                if (intento >= 3) {
                    registro[intento - 3] = finRegistro - inicio;
                    asignacion[intento - 3] = finAsignacion - inicioAsignacion;
                }
            }
            Arrays.sort(registro);
            Arrays.sort(asignacion);
            csv.append(String.format(Locale.ROOT, "%d,%.6f,%.6f,%d,%d%n",
                    n, registro[2] / 1_000_000.0, asignacion[2] / 1_000_000.0, n / 4, n - n / 4));
        }
        Files.writeString(destino, csv.toString(), StandardCharsets.UTF_8);
        System.out.println(csv);
    }
}