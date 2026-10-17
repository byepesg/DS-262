// Función: punto de entrada. Lee el registro, detecta los picos R con DeteccionQRS,
// imprime latidos, intervalos RR y frecuencia cardíaca, y genera la gráfica con GraficadorECG.

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Path DIRECTORIO_DATOS = Path.of("Muestras_Pruebas");

    public static void main(String[] args) {
        try {
            // Número del registro (por argumento o por consola, sin Scanner)
            String numero;
            if (args.length > 0) {
                numero = args[0].trim();
            } else {
                System.out.print("Ingrese el numero del registro (000-999): ");
                System.out.flush();
                StringBuilder sb = new StringBuilder();
                int c;
                while ((c = System.in.read()) != -1 && c != '\n') {
                    if (c != '\r') {
                        sb.append((char) c);
                    }
                }
                numero = sb.toString().trim();
            }

            boolean valido = numero.length() == 3;
            for (int i = 0; valido && i < 3; i++) {
                valido = numero.charAt(i) >= '0' && numero.charAt(i) <= '9';
            }
            if (!valido) {
                System.out.println("Error: debe ingresar exactamente tres digitos.");
                return;
            }

            // Rutas: permite ejecutar desde AnalizadorECG o desde su carpeta padre.
            Path directorioDatos = resolverDirectorioDatos();
            Path carpeta = directorioDatos.resolve("REF_" + numero);
            Path cabecera = carpeta.resolve(numero + ".hea");
            Path anotaciones = carpeta.resolve(numero + ".atr");
            Path auxiliar = carpeta.resolve(numero + ".xws");

            if (!Files.isDirectory(carpeta)) {
                System.out.println("Error: no existe la carpeta " + carpeta);
                return;
            }
            if (!Files.isRegularFile(cabecera)) {
                System.out.println("Error: no existe el archivo " + cabecera);
                return;
            }

            CabeceraECG datos = LectorCabecera.leer(cabecera);

            Path sen = carpeta.resolve(datos.archivosen).normalize();
            if (!sen.startsWith(carpeta.normalize()) || !Files.isRegularFile(sen)) {
                throw new IOException("No se encuentra el archivo de señal: " + sen);
            }
            if (!Files.isRegularFile(anotaciones)) {
                throw new IOException("No se encuentra el archivo .atr: " + anotaciones);
            }

            // Detección de picos R (canal 0), muestra a muestra
            double fs = datos.frecuenciaMuestreo;
            double[] x = LectorSen.leer(sen, datos)[0];
            DeteccionQRS detector = new DeteccionQRS(fs);

            for (int i = 0; i < x.length; i++) {
                detector.decidir(detector.procesarMuestra(x[i]));
            }

            // Resultados: se recorre la lista de latidos (circular, totalLatidos nodos)
            int latidos = detector.totalLatidos;
            System.out.println();
            System.out.println("Registro " + datos.nombreRegistro + " | canal 0 (" + datos.canales.get(0).descripcion + ")");
            System.out.printf("Duracion: %.1f s | fs: %.0f Hz%n", x.length / fs, fs);
            System.out.println("Latidos detectados: " + latidos);

            if (latidos >= 2) {
                double sumaRR = 0;
                double minRR = Double.MAX_VALUE;
                double maxRR = 0;
                int previo = detector.latidos.head.key;
                CircularLinkedList.Node nodo = detector.latidos.head.nextNode;

                for (int k = 1; k < latidos; k++) {
                    double rr = nodo.key - previo;
                    sumaRR += rr;
                    minRR = Math.min(minRR, rr);
                    maxRR = Math.max(maxRR, rr);
                    previo = nodo.key;
                    nodo = nodo.nextNode;
                }

                double rrProm = sumaRR / (latidos - 1);
                System.out.printf("RR promedio: %.3f s (min %.3f s, max %.3f s)%n", rrProm / fs, minRR / fs, maxRR / fs);
                System.out.printf("FC promedio: %.1f lpm (min %.1f, max %.1f)%n", 60.0 * fs / rrProm, 60.0 * fs / maxRR, 60.0 * fs / minRR);
            }
            System.out.println();

            // Gráfica (la hace GraficadorECG, igual que antes)
            var aux = Files.isRegularFile(auxiliar) ? LectorAuxiliar.leer(auxiliar) : null;
            GraficadorECG graficador = new GraficadorECG(datos, sen, anotaciones, aux);
            graficador.mostrar(Path.of("salida", "REF_" + numero + ".png"));

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error de lectura: " + e.getMessage());
        }
    }

    private static Path resolverDirectorioDatos() throws IOException {
        Path desdeDirectorioActual = DIRECTORIO_DATOS.toAbsolutePath().normalize();
        if (Files.isDirectory(desdeDirectorioActual)) {
            return desdeDirectorioActual;
        }

        Path desdeCarpetaProyecto = Path.of("AnalizadorECG")
                .resolve(DIRECTORIO_DATOS)
                .toAbsolutePath()
                .normalize();
        if (Files.isDirectory(desdeCarpetaProyecto)) {
            return desdeCarpetaProyecto;
        }

        throw new IOException("no existe la carpeta de datos. Se busco en "
                + desdeDirectorioActual + " y " + desdeCarpetaProyecto);
    }
}