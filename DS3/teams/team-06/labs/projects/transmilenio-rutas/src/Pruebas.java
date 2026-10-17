import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

public class Pruebas {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("== MinHeap ==");
        heapVacio();
        heapUnElemento();
        heapOrdenaAleatorios();

        System.out.println("== Construcción de la red ==");
        redRechazaDatosInvalidos();
        servicioExpresoUsaTiempoFisico();
        busquedaPorNombreYAlias();

        System.out.println("== Consultas de servicios (datos reales) ==");
        redRealCargaCompleta();
        rutasQuePasanPorCan();
        rutasDirectas();

        System.out.println("== Rutas (red pequeña) ==");
        penalizacionCambiaLaRuta();
        rutaMismaEstacionEInalcanzable();

        System.out.println("== Rutas (corredor Calle 26) ==");
        todasLasEstacionesAlcanzables();
        dijkstraCoincideConBellmanFord();
        criteriosSonCoherentes();
        rutasUsanArcosExistentes();

        System.out.printf("%nResultado: %d aprobadas, %d fallidas%n", aprobadas, fallidas);
        if (fallidas > 0) System.exit(1);
    }

    static void heapVacio() {
        MinHeap<Integer> h = new MinHeap<>();
        verificar("heap nuevo está vacío", h.estaVacio() && h.tamano() == 0);
        verificar("extraerMin en vacío lanza excepción", lanza(h::extraerMin, NoSuchElementException.class));
    }

    static void heapUnElemento() {
        MinHeap<Integer> h = new MinHeap<>();
        h.insertar(7);
        verificar("un elemento: verMin = 7", h.verMin() == 7);
        verificar("un elemento: extraerMin = 7 y queda vacío", h.extraerMin() == 7 && h.estaVacio());
    }

    static void heapOrdenaAleatorios() {
        Random r = new Random(262);
        int[] v = new int[1000];
        MinHeap<Integer> h = new MinHeap<>();
        for (int i = 0; i < v.length; i++) {
            v[i] = r.nextInt(500) - 250;
            h.insertar(v[i]);
        }
        Arrays.sort(v);
        boolean ok = true;
        for (int x : v) ok &= h.extraerMin() == x;
        verificar("1000 aleatorios salen en orden (y el arreglo crece)", ok && h.estaVacio());
    }

    static RedTransmilenio redPequena() {
        RedTransmilenio r = new RedTransmilenio();
        for (String id : List.of("A", "B", "C", "D", "E")) {
            r.agregarEstacion(new Estacion(id, "Estacion " + id, List.of()));
        }
        r.agregarTramo("A", "B", 60);
        r.agregarTramo("B", "C", 60);
        r.agregarTramo("C", "D", 60);
        r.agregarServicio(new Servicio("X", "-", "-", List.of("A", "B", "C")));
        r.agregarServicio(new Servicio("Y", "-", "-", List.of("B", "D")));
        r.agregarServicio(new Servicio("W", "-", "-", List.of("C", "D")));
        r.agregarServicio(new Servicio("Z", "-", "-", List.of("A", "D")));
        return r;
    }

    static void redRechazaDatosInvalidos() {
        RedTransmilenio r = redPequena();
        verificar("estación duplicada es rechazada",
                lanza(() -> r.agregarEstacion(new Estacion("A", "Otra", List.of())), IllegalArgumentException.class));
        verificar("tramo a estación inexistente es rechazado",
                lanza(() -> r.agregarTramo("A", "Q", 60), IllegalArgumentException.class));
        verificar("servicio sin vía física es rechazado (E aislada)",
                lanza(() -> r.agregarServicio(new Servicio("V", "-", "-", List.of("A", "E"))), IllegalArgumentException.class));
        verificar("servicio que repite parada es rechazado",
                lanza(() -> new Servicio("R", "-", "-", List.of("A", "B", "A")), IllegalArgumentException.class));
        verificar("servicio inválido no deja arcos a medias", r.numeroServicios() == 4 && r.numeroArcosServicio() == 5);
    }

    static void servicioExpresoUsaTiempoFisico() {
        RedTransmilenio r = redPequena();
        int yBD = r.arcosDeServicio("B").stream().filter(c -> Servicio.codigoDe(c.getServicio()).equals("Y")).findFirst().get().getSegundos();
        verificar("arco expreso B->D = 120 s de vía + 30 s detención", yBD == 150);
        verificar("tiempo físico A->D = 180 s", r.tiempoFisico("A", "D") == 180);
    }

    static void penalizacionCambiaLaRuta() {
        CalculadorRutas c = new CalculadorRutas(redPequena());
        Ruta sinPen = c.rutaOptima("A", "D", 0);
        verificar("A->D sin penalización: toma Z directo (210 s)", sinPen.getSegundosEnVehiculo() == 210
                && sinPen.getTransbordos() == 0);
        Ruta menosParadas = c.rutaMenosParadas("A", "D");
        verificar("A->D con menos paradas: 1 parada", menosParadas.getNumeroParadas() == 1);
        Ruta ac = c.rutaMasRapida("A", "C");
        verificar("A->C usa X sin transbordo (180 s)", ac.getSegundosEnVehiculo() == 180 && ac.getTransbordos() == 0);
        Ruta bd = c.rutaOptima("B", "D", 0);
        verificar("B->D prefiere expreso Y (150 s) a X+W (180 s)", bd.getSegundosEnVehiculo() == 150);
    }

    static void rutaMismaEstacionEInalcanzable() {
        CalculadorRutas c = new CalculadorRutas(redPequena());
        Ruta r = c.rutaMasRapida("B", "B");
        verificar("origen = destino: 0 s y sin paradas", r.getSegundosTotales() == 0 && r.getNumeroParadas() == 0);
        verificar("estación aislada: Dijkstra y BFS retornan null",
                c.rutaMasRapida("A", "E") == null && c.rutaMenosParadas("A", "E") == null);
        verificar("los servicios son dirigidos: D->A no tiene ruta", c.rutaMasRapida("D", "A") == null);
        verificar("origen inexistente lanza excepción",
                lanza(() -> c.rutaMasRapida("Q", "A"), IllegalArgumentException.class));
    }

    static RedTransmilenio real() throws Exception {
        return CargadorDatos.cargar(Path.of("data"));
    }

    static void busquedaPorNombreYAlias() throws Exception {
        RedTransmilenio r = real();
        verificar("'normandia' (sin tilde) -> Normandía", "E03".equals(id(r.resolver("normandia"))));
        verificar("alias 'Maloka' -> El Tiempo - Maloka", "E05".equals(id(r.resolver("Maloka"))));
        verificar("alias 'CityU' -> Universidades", "E14".equals(id(r.resolver("CityU"))));
        verificar("'consejo de bogota' (alias, sin tilde) -> Concejo de Bogotá", "E12".equals(id(r.resolver("consejo de bogota"))));
        verificar("id en minúscula 'e07' -> CAN", "E07".equals(id(r.resolver("e07"))));
        verificar("fragmento 'univ' -> Ciudad Universitaria y Universidades", r.buscarPorFragmento("univ").size() == 2);
    }

    static void redRealCargaCompleta() throws Exception {
        RedTransmilenio r = real();
        verificar("corredor: 14 estaciones, 13 tramos, 18 rutas",
                r.numeroEstaciones() == 14 && r.numeroTramos() == 13 && r.numeroServicios() == 18);
    }

    static void rutasQuePasanPorCan() throws Exception {
        RedTransmilenio r = real();
        List<String> esperadas = List.of("1", "1", "B16", "B23", "G53", "H54", "K10", "K16", "K23", "K53", "K54",
                "K86", "L10", "M86");
        verificar("rutas que paran en CAN = 14 (la 1 cuenta ida y regreso)",
                new ArrayList<>(r.serviciosQuePasan("E07")).equals(esperadas));
        verificar("en Universidades solo para la ruta 1 (ida y regreso)",
                new ArrayList<>(r.serviciosQuePasan("E14")).equals(List.of("1", "1")));
        verificar("rutas que paran en Avenida Rojas no incluyen K10 ni L10",
                !r.serviciosQuePasan("E04").contains("K10") && !r.serviciosQuePasan("E04").contains("L10"));
    }

    static void rutasDirectas() throws Exception {
        RedTransmilenio r = real();
        List<String> d = new ArrayList<>();
        for (Servicio s : r.serviciosDirectos("E01", "E10")) d.add(s.getCodigo());
        verificar("directas Portal Eldorado -> Corferias = M86, S43, B16, H54, 1 (ordenadas por paradas)",
                d.equals(List.of("M86", "S43", "B16", "H54", "1")));
        verificar("directa Portal Eldorado -> Universidades = solo ruta 1", r.serviciosDirectos("E01", "E14").size() == 1);
        verificar("directas respetan sentido: Universidades -> Portal = solo ruta 1",
                r.serviciosDirectos("E14", "E01").size() == 1);
    }

    static void todasLasEstacionesAlcanzables() throws Exception {
        RedTransmilenio r = real();
        CalculadorRutas c = new CalculadorRutas(r);
        boolean ok = true;
        for (Estacion a : r.estacionesOrdenadas())
            for (Estacion b : r.estacionesOrdenadas())
                ok &= c.rutaMasRapida(a.getId(), b.getId()) != null;
        verificar("red fuertemente conexa: hay ruta entre los 196 pares", ok);
    }

    static void dijkstraCoincideConBellmanFord() throws Exception {
        RedTransmilenio r = real();
        CalculadorRutas c = new CalculadorRutas(r);
        int errores = 0;
        int comparaciones = 0;
        for (int pen : new int[]{0, Ruta.ESPERA_TRANSBORDO_SEG, 1_000_000}) {
            for (Estacion a : r.estacionesOrdenadas()) {
                Map<String, Long> dist = bellmanFord(r, a.getId(), pen);
                for (Estacion b : r.estacionesOrdenadas()) {
                    long esperado = Long.MAX_VALUE;
                    for (Map.Entry<String, Long> e : dist.entrySet()) {
                        if (e.getKey().startsWith(b.getId() + "|")) esperado = Math.min(esperado, e.getValue());
                    }
                    Ruta ruta = c.rutaOptima(a.getId(), b.getId(), pen);
                    long obtenido = ruta.getSegundosEnVehiculo() + (long) pen * ruta.getTransbordos();
                    if (obtenido != esperado) errores++;
                    comparaciones++;
                }
            }
        }
        verificar("Dijkstra por estados = Bellman-Ford en " + comparaciones + " consultas", errores == 0);
    }

    static Map<String, Long> bellmanFord(RedTransmilenio r, String origen, int pen) {
        Map<String, Long> d = new HashMap<>();
        d.put(origen + "|", 0L);
        boolean cambio = true;
        while (cambio) {
            cambio = false;
            for (Map.Entry<String, Long> e : new ArrayList<>(d.entrySet())) {
                String[] p = e.getKey().split("\\|", -1);
                for (Conexion c : r.arcosDeServicio(p[0])) {
                    boolean tb = !p[1].isEmpty() && !p[1].equals(c.getServicio());
                    long nuevo = e.getValue() + c.getSegundos() + (tb ? pen : 0);
                    String k = c.getDestino() + "|" + c.getServicio();
                    if (nuevo < d.getOrDefault(k, Long.MAX_VALUE)) {
                        d.put(k, nuevo);
                        cambio = true;
                    }
                }
            }
        }
        return d;
    }

    static void criteriosSonCoherentes() throws Exception {
        RedTransmilenio r = real();
        CalculadorRutas c = new CalculadorRutas(r);
        boolean tiempoOk = true, transbordosOk = true, paradasOk = true;
        for (Estacion a : r.estacionesOrdenadas()) {
            for (Estacion b : r.estacionesOrdenadas()) {
                Ruta rapida = c.rutaMasRapida(a.getId(), b.getId());
                Ruta pocos = c.rutaMenosTransbordos(a.getId(), b.getId());
                Ruta cortas = c.rutaMenosParadas(a.getId(), b.getId());
                tiempoOk &= rapida.getSegundosTotales() <= pocos.getSegundosTotales()
                        && rapida.getSegundosTotales() <= cortas.getSegundosTotales();
                transbordosOk &= pocos.getTransbordos() <= rapida.getTransbordos()
                        && pocos.getTransbordos() <= cortas.getTransbordos();
                paradasOk &= cortas.getNumeroParadas() <= rapida.getNumeroParadas()
                        && cortas.getNumeroParadas() <= pocos.getNumeroParadas();
            }
        }
        verificar("la ruta más rápida nunca es más lenta que las otras", tiempoOk);
        verificar("la de menos transbordos nunca tiene más transbordos que las otras", transbordosOk);
        verificar("la de menos paradas (BFS) nunca tiene más paradas que las otras", paradasOk);
    }

    static void rutasUsanArcosExistentes() throws Exception {
        RedTransmilenio r = real();
        CalculadorRutas c = new CalculadorRutas(r);
        Ruta ruta = c.rutaMasRapida("E03", "E14");
        boolean ok = true;
        int suma = 0;
        for (int i = 1; i < ruta.getParadas().size(); i++) {
            String a = ruta.getParadas().get(i - 1).getId();
            String b = ruta.getParadas().get(i).getId();
            String s = ruta.getServicioPorTramo().get(i - 1);
            Conexion arco = r.arcosDeServicio(a).stream()
                    .filter(x -> x.getDestino().equals(b) && x.getServicio().equals(s)).findFirst().orElse(null);
            ok &= arco != null;
            if (arco != null) suma += arco.getSegundos();
        }
        verificar("ruta Normandía -> Universidades usa arcos reales y suma el tiempo reportado",
                ok && suma == ruta.getSegundosEnVehiculo());
    }

    static String id(Estacion e) {
        return e == null ? null : e.getId();
    }

    static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            aprobadas++;
            System.out.println("  [OK]    " + descripcion);
        } else {
            fallidas++;
            System.out.println("  [FALLA] " + descripcion);
        }
    }

    static boolean lanza(Runnable accion, Class<? extends Throwable> tipo) {
        try {
            accion.run();
            return false;
        } catch (Throwable t) {
            return tipo.isInstance(t);
        }
    }
}
