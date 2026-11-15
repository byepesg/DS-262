import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// mide tiempos de construcción y de consulta con redes de distinto tamaño
public class Rendimiento {
    static final int CONSULTAS = 300;

    public static void main(String[] args) throws Exception {
        System.out.println("Red real (datos/):");
        long t0 = System.nanoTime();
        RedTransmilenio real = CargadorDatos.cargar(Path.of(System.getProperty("datos", "data")));
        double carga = (System.nanoTime() - t0) / 1e6;
        imprimirEncabezado();
        medir("real", real, carga);

        System.out.println();
        System.out.println("Redes sintéticas (troncales en paralelo, servicios corrientes y expresos):");
        imprimirEncabezado();
        int[][] tamanos = {{2, 30}, {4, 50}, {8, 60}, {10, 100}, {20, 100}, {25, 200}};
        for (int[] t : tamanos) {
            long ini = System.nanoTime();
            RedTransmilenio r = sintetica(t[0], t[1]);
            double ms = (System.nanoTime() - ini) / 1e6;
            medir(t[0] + "x" + t[1], r, ms);
        }
    }

    static void imprimirEncabezado() {
        System.out.printf("%-8s %6s %6s %7s %10s %10s %10s %10s%n",
                "red", "V", "serv", "arcos", "carga ms", "rápida ms", "transb ms", "BFS ms");
    }

    // promedio por consulta sobre pares aleatorios (semilla fija)
    static void medir(String nombre, RedTransmilenio r, double cargaMs) {
        CalculadorRutas c = new CalculadorRutas(r);
        List<Estacion> est = r.estacionesOrdenadas();
        Random rnd = new Random(262);
        String[][] pares = new String[CONSULTAS][2];
        for (String[] p : pares) {
            p[0] = est.get(rnd.nextInt(est.size())).getId();
            p[1] = est.get(rnd.nextInt(est.size())).getId();
        }
        // calentamiento para que la JVM compile el código
        for (int i = 0; i < 2; i++) ronda(c, pares);
        // se toma el mejor de 5 rondas para reducir el ruido
        double[] mejor = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};
        for (int i = 0; i < 5; i++) {
            double[] t = ronda(c, pares);
            for (int k = 0; k < 3; k++) mejor[k] = Math.min(mejor[k], t[k]);
        }
        System.out.printf("%-8s %6d %6d %7d %10.1f %10.3f %10.3f %10.3f%n", nombre, r.numeroEstaciones(),
                r.numeroServicios(), r.numeroArcosServicio(), cargaMs,
                mejor[0], mejor[1], mejor[2]);
    }

    // tiempo promedio por consulta (ms) de cada criterio
    static double[] ronda(CalculadorRutas c, String[][] pares) {
        long a = System.nanoTime();
        for (String[] p : pares) c.rutaMasRapida(p[0], p[1]);
        long b = System.nanoTime();
        for (String[] p : pares) c.rutaMenosTransbordos(p[0], p[1]);
        long d = System.nanoTime();
        for (String[] p : pares) c.rutaMenosParadas(p[0], p[1]);
        long e = System.nanoTime();
        return new double[]{(b - a) / 1e6 / pares.length, (d - b) / 1e6 / pares.length, (e - d) / 1e6 / pares.length};
    }

    // t troncales de l estaciones, unidas cada 10 estaciones
    static RedTransmilenio sintetica(int t, int l) {
        RedTransmilenio r = new RedTransmilenio();
        Random rnd = new Random(t * 1000L + l);
        for (int i = 0; i < t; i++)
            for (int j = 0; j < l; j++) r.agregarEstacion(new Estacion(id(i, j), "T" + i + " E" + j, List.of()));
        for (int i = 0; i < t; i++)
            for (int j = 0; j + 1 < l; j++) r.agregarTramo(id(i, j), id(i, j + 1), 90 + rnd.nextInt(61));
        for (int i = 0; i + 1 < t; i++)
            for (int j = 0; j < l; j += 10) r.agregarTramo(id(i, j), id(i + 1, j), 150);

        for (int i = 0; i < t; i++) {
            servicioIdaYRegreso(r, "L" + i, troncal(i, 0, l, 1));     // corriente
            servicioIdaYRegreso(r, "X" + i, troncal(i, 0, l, 3));     // expreso
            servicioIdaYRegreso(r, "Y" + i, troncal(i, 1, l, 4));     // expreso
            if (i + 1 < t) {
                // pasa de la troncal i a la i+1 por un cruce
                int cruce = 10 * (1 + rnd.nextInt(Math.max(1, (l - 1) / 10)));
                if (cruce >= l) cruce = 0;
                List<String> p = new ArrayList<>();
                for (int j = 0; j <= cruce; j += 2) p.add(id(i, j));
                if (!p.get(p.size() - 1).equals(id(i, cruce))) p.add(id(i, cruce));
                for (int j = cruce; j < l; j += 2) p.add(id(i + 1, j));
                servicioIdaYRegreso(r, "C" + i, p);
            }
        }
        return r;
    }

    static List<String> troncal(int i, int desde, int l, int paso) {
        List<String> p = new ArrayList<>();
        for (int j = desde; j < l; j += paso) p.add(id(i, j));
        if (!p.get(p.size() - 1).equals(id(i, l - 1))) p.add(id(i, l - 1));
        return p;
    }

    static void servicioIdaYRegreso(RedTransmilenio r, String cod, List<String> p) {
        r.agregarServicio(new Servicio(cod, "ini", "fin", p));
        List<String> inv = new ArrayList<>(p);
        java.util.Collections.reverse(inv);
        r.agregarServicio(new Servicio(cod, "fin", "ini", inv));
    }

    static String id(int i, int j) {
        return "T" + i + "-" + j;
    }
}
