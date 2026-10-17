import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculadorRutas {
    static final int PENALIZACION_LEXICOGRAFICA = 1_000_000;

    private final RedTransmilenio red;

    public CalculadorRutas(RedTransmilenio red) {
        this.red = red;
    }

    public Ruta rutaMasRapida(String origen, String destino) {
        return rutaOptima(origen, destino, Ruta.ESPERA_TRANSBORDO_SEG);
    }

    public Ruta rutaMenosTransbordos(String origen, String destino) {
        return rutaOptima(origen, destino, PENALIZACION_LEXICOGRAFICA);
    }

    private static final class Estado implements Comparable<Estado> {
        final String estacion;
        final String servicio;
        final long costo;

        Estado(String estacion, String servicio, long costo) {
            this.estacion = estacion;
            this.servicio = servicio;
            this.costo = costo;
        }

        String clave() {
            return clave(estacion, servicio);
        }

        static String clave(String estacion, String servicio) {
            return estacion + "|" + (servicio == null ? "" : servicio);
        }

        @Override
        public int compareTo(Estado o) {
            return Long.compare(costo, o.costo);
        }
    }

    // Dijkstra: el estado es (estación, ruta) para poder cobrar el transbordo
    public Ruta rutaOptima(String origen, String destino, int penalizacionTransbordo) {
        validar(origen, destino);
        if (penalizacionTransbordo < 0) {
            throw new IllegalArgumentException("La penalización no puede ser negativa");
        }

        Map<String, Long> costo = new HashMap<>();
        Map<String, String> predecesor = new HashMap<>();
        MinHeap<Estado> heap = new MinHeap<>();

        Estado inicio = new Estado(origen, null, 0);
        costo.put(inicio.clave(), 0L);
        heap.insertar(inicio);

        Estado fin = null;
        while (!heap.estaVacio()) {
            Estado actual = heap.extraerMin();
            if (actual.costo > costo.get(actual.clave())) continue; // entrada obsoleta
            if (actual.estacion.equals(destino)) {
                fin = actual;
                break;
            }
            for (Conexion c : red.arcosDeServicio(actual.estacion)) {
                boolean transbordo = actual.servicio != null && !actual.servicio.equals(c.getServicio());
                long nuevo = actual.costo + c.getSegundos() + (transbordo ? penalizacionTransbordo : 0);
                String clave = Estado.clave(c.getDestino(), c.getServicio());
                if (nuevo < costo.getOrDefault(clave, Long.MAX_VALUE)) {
                    costo.put(clave, nuevo);
                    predecesor.put(clave, actual.clave());
                    heap.insertar(new Estado(c.getDestino(), c.getServicio(), nuevo));
                }
            }
        }
        if (fin == null) return null;
        return reconstruir(fin.clave(), predecesor);
    }

    // arma el camino desde el final usando una pila
    private Ruta reconstruir(String claveFinal, Map<String, String> predecesor) {
        Deque<String> pila = new ArrayDeque<>();
        for (String c = claveFinal; c != null; c = predecesor.get(c)) {
            pila.push(c);
        }
        List<Estacion> paradas = new ArrayList<>();
        List<String> servicios = new ArrayList<>();
        int segundos = 0;
        String anterior = null;
        while (!pila.isEmpty()) {
            String[] partes = pila.pop().split("\\|", -1);
            String estacion = partes[0];
            String servicio = partes[1];
            paradas.add(red.obtenerEstacion(estacion));
            if (anterior != null) {
                servicios.add(servicio);
                segundos += tiempoArco(anterior, estacion, servicio);
            }
            anterior = estacion;
        }
        return new Ruta(paradas, servicios, segundos);
    }

    private int tiempoArco(String a, String b, String servicio) {
        for (Conexion c : red.arcosDeServicio(a)) {
            if (c.getDestino().equals(b) && c.getServicio().equals(servicio)) return c.getSegundos();
        }
        throw new IllegalStateException("Arco inexistente " + a + "->" + b + " por " + servicio);
    }

    // BFS: menos paradas
    public Ruta rutaMenosParadas(String origen, String destino) {
        validar(origen, destino);
        Map<String, Conexion> llegada = new HashMap<>();
        Map<String, String> predecesor = new HashMap<>();
        Deque<String> cola = new ArrayDeque<>();
        predecesor.put(origen, null);
        cola.addLast(origen);
        while (!cola.isEmpty()) {
            String u = cola.pollFirst();
            if (u.equals(destino)) break;
            for (Conexion c : red.arcosDeServicio(u)) {
                if (!predecesor.containsKey(c.getDestino())) {
                    predecesor.put(c.getDestino(), u);
                    llegada.put(c.getDestino(), c);
                    cola.addLast(c.getDestino());
                }
            }
        }
        if (!predecesor.containsKey(destino)) return null;

        Deque<String> pila = new ArrayDeque<>();
        for (String v = destino; v != null; v = predecesor.get(v)) pila.push(v);
        List<Estacion> paradas = new ArrayList<>();
        List<String> servicios = new ArrayList<>();
        int segundos = 0;
        while (!pila.isEmpty()) {
            String v = pila.pop();
            paradas.add(red.obtenerEstacion(v));
            Conexion c = llegada.get(v);
            if (c != null && !v.equals(origen)) {
                servicios.add(c.getServicio());
                segundos += c.getSegundos();
            }
        }
        return new Ruta(paradas, servicios, segundos);
    }

    private void validar(String origen, String destino) {
        if (!red.contieneEstacion(origen)) {
            throw new IllegalArgumentException("Estación de origen inexistente: " + origen);
        }
        if (!red.contieneEstacion(destino)) {
            throw new IllegalArgumentException("Estación de destino inexistente: " + destino);
        }
    }
}
