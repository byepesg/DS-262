import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final RedTransmilenio red;
    private final CalculadorRutas calculador;

    public Main(RedTransmilenio red) {
        this.red = red;
        this.calculador = new CalculadorRutas(red);
    }

    public static void main(String[] args) {
        RedTransmilenio red;
        try {
            red = CargadorDatos.cargar(Path.of(System.getProperty("datos", "data")));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error cargando datos: " + e.getMessage());
            System.err.println("Ejecute desde la carpeta del proyecto o use -Ddatos=RUTA.");
            System.exit(1);
            return;
        }
        Main app = new Main(red);
        if (args.length == 0) {
            app.menu();
            return;
        }
        String cmd = args[0].toLowerCase();
        if (cmd.equals("demo")) {
            app.demo();
        } else if (args.length == 3 && (cmd.equals("ruta") || cmd.equals("transbordos") || cmd.equals("paradas"))) {
            app.consultar(args[1], args[2], cmd);
        } else if (args.length == 2 && cmd.equals("estacion")) {
            app.rutasQuePasan(args[1]);
        } else if (args.length == 2 && cmd.equals("servicio")) {
            app.recorrido(args[1]);
        } else {
            System.out.println("Uso: Main [demo | ruta|transbordos|paradas ORIGEN DESTINO | estacion NOMBRE | servicio CODIGO]");
        }
    }

    private void encabezado() {
        System.out.println("=====================================================");
        System.out.println(" GESTOR DE RUTAS TRANSMILENIO - Corredor Calle 26");
        System.out.println(" Estructuras de Datos 2026-2 - UNAL - DS3 team-06");
        System.out.println(" Jeicob Stiven Murillo Ramos (1000334659)");
        System.out.println(" Andres Santiago Morales Robayo (1011080401)");
        System.out.println("=====================================================");
        System.out.printf(" Red: %d estaciones, %d tramos de vía, %d servicios, %d arcos de servicio%n",
                red.numeroEstaciones(), red.numeroTramos(), red.numeroServicios(), red.numeroArcosServicio());
    }

    private void menu() {
        encabezado();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println();
            System.out.println("1. Listar estaciones y rutas que pasan");
            System.out.println("2. Buscar estación");
            System.out.println("3. Rutas que pasan por una estación");
            System.out.println("4. Recorrido de una ruta (servicio)");
            System.out.println("5. Rutas directas entre dos estaciones");
            System.out.println("6. Ruta más rápida");
            System.out.println("7. Ruta con menos transbordos");
            System.out.println("8. Ruta con menos paradas (BFS)");
            System.out.println("9. Estadísticas de la red");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            if (!sc.hasNextLine()) return;
            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> listar();
                case "2" -> buscar(pedir(sc, "Texto a buscar: "));
                case "3" -> rutasQuePasan(pedir(sc, "Estación: "));
                case "4" -> recorrido(pedir(sc, "Código de la ruta (ej. K16): "));
                case "5" -> directas(pedir(sc, "Origen: "), pedir(sc, "Destino: "));
                case "6" -> consultar(pedir(sc, "Origen: "), pedir(sc, "Destino: "), "ruta");
                case "7" -> consultar(pedir(sc, "Origen: "), pedir(sc, "Destino: "), "transbordos");
                case "8" -> consultar(pedir(sc, "Origen: "), pedir(sc, "Destino: "), "paradas");
                case "9" -> estadisticas();
                case "0" -> {
                    System.out.println("Hasta luego.");
                    return;
                }
                default -> System.out.println("Opción no válida.");
            }
        }
    }

    private static String pedir(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.hasNextLine() ? sc.nextLine() : "";
    }

    private Estacion resolver(String texto) {
        Estacion e = red.resolver(texto);
        if (e == null) {
            System.out.println("No existe la estación '" + texto.trim() + "'.");
            List<Estacion> s = red.buscarPorFragmento(texto);
            if (!s.isEmpty()) {
                System.out.println("Quizás quiso decir:");
                s.stream().limit(5).forEach(x -> System.out.println("  " + x));
            }
        }
        return e;
    }

    private void listar() {
        for (Estacion e : red.estacionesOrdenadas()) {
            List<String> s = red.serviciosQuePasan(e.getId());
            System.out.printf("  %-26s %2d rutas: %s%n", e, s.size(), String.join(", ", s));
        }
    }

    private void buscar(String texto) {
        List<Estacion> r = red.buscarPorFragmento(texto);
        if (r.isEmpty()) System.out.println("Sin coincidencias.");
        r.forEach(e -> System.out.println("  " + e + (e.getAlias().isEmpty() ? "" : "  alias: " + String.join(", ", e.getAlias()))));
    }

    private void rutasQuePasan(String texto) {
        Estacion e = resolver(texto);
        if (e == null) return;
        System.out.println("Rutas que paran en " + e.getNombre() + ":");
        for (Servicio s : red.serviciosEn(e.getId())) {
            System.out.printf("  %-4s %s -> %s%n", s.getCodigo(), s.getOrigen(), s.getDestino());
        }
    }

    private void recorrido(String codigo) {
        List<Servicio> lista = red.buscarServicios(codigo);
        if (lista.isEmpty()) {
            System.out.println("No existe la ruta '" + codigo.trim() + "' en el corredor.");
            return;
        }
        for (Servicio s : lista) {
            System.out.println("Ruta " + s + " - paradas en el corredor:");
            for (String id : s.getParadas()) {
                System.out.println("  - " + red.obtenerEstacion(id).getNombre());
            }
        }
    }


    private void directas(String o, String d) {
        Estacion a = resolver(o);
        Estacion b = resolver(d);
        if (a == null || b == null) return;
        List<Servicio> r = red.serviciosDirectos(a.getId(), b.getId());
        System.out.println("Rutas directas " + a.getNombre() + " -> " + b.getNombre() + ":");
        if (r.isEmpty()) System.out.println("  Ninguna. Se requiere transbordo.");
        for (Servicio s : r) {
            System.out.printf("  %-4s %d parada(s)  (%s -> %s)%n",
                    s.getCodigo(), s.paradasEntre(a.getId(), b.getId()), s.getOrigen(), s.getDestino());
        }
    }

    private void consultar(String o, String d, String criterio) {
        Estacion a = resolver(o);
        Estacion b = resolver(d);
        if (a == null || b == null) return;
        Ruta r;
        String titulo;
        switch (criterio) {
            case "transbordos" -> {
                r = calculador.rutaMenosTransbordos(a.getId(), b.getId());
                titulo = "RUTA CON MENOS TRANSBORDOS";
            }
            case "paradas" -> {
                r = calculador.rutaMenosParadas(a.getId(), b.getId());
                titulo = "RUTA CON MENOS PARADAS";
            }
            default -> {
                r = calculador.rutaMasRapida(a.getId(), b.getId());
                titulo = "RUTA MÁS RÁPIDA";
            }
        }
        System.out.println();
        System.out.println(titulo + ": " + a.getNombre() + " -> " + b.getNombre());
        System.out.println(r == null ? "  No existe una ruta entre esas estaciones." : r.describir());
    }

    private void estadisticas() {
        Estacion max = null;
        int maxRutas = -1;
        for (Estacion e : red.estacionesOrdenadas()) {
            int n = red.serviciosQuePasan(e.getId()).size();
            if (n > maxRutas) {
                maxRutas = n;
                max = e;
            }
        }
        System.out.println("Estaciones:            " + red.numeroEstaciones());
        System.out.println("Tramos de vía:         " + red.numeroTramos());
        System.out.println("Servicios (rutas):     " + red.numeroServicios());
        System.out.println("Arcos de servicio:     " + red.numeroArcosServicio());
        System.out.println("Estación con más rutas: " + (max == null ? "-" : max.getNombre() + " (" + maxRutas + ")"));
    }

    private void demo() {
        encabezado();
        System.out.println();
        rutasQuePasan("CAN");
        System.out.println();
        rutasQuePasan("Universidades");
        System.out.println();
        recorrido("K43");
        System.out.println();
        directas("Portal Eldorado", "Corferias");
        String[][] consultas = {
                {"Portal Eldorado", "Universidades"},
                {"Normandía", "Concejo de Bogotá"},
                {"Centro Memoria", "Modelia"},
        };
        for (String[] q : consultas) {
            consultar(q[0], q[1], "ruta");
            consultar(q[0], q[1], "transbordos");
            consultar(q[0], q[1], "paradas");
        }
        System.out.println();
        System.out.println("¿POR QUÉ PENALIZAR TRANSBORDOS? Portal Eldorado -> Universidades sin penalización:");
        Ruta sinPen = calculador.rutaOptima("E01", "E14", 0);
        System.out.println(sinPen.describir());
        System.out.println();
        System.out.println("ALIAS Y NOMBRES SIN TILDE: \"maloka\", \"consejo de bogota\", \"cityu\"");
        for (String t : new String[]{"maloka", "consejo de bogota", "cityu"}) {
            System.out.println("  " + t + " -> " + red.resolver(t));
        }
        System.out.println();
        System.out.println("NOMBRE INEXISTENTE: \"Salitr\"");
        resolver("Salitr");
    }
}
