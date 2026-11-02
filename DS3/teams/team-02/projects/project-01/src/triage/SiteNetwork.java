package triage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Red de sedes de atencion modelada como un grafo no dirigido y ponderado:
 * cada vertice es una sede (con su propio {@link TriageSystem} y una
 * capacidad de pendientes) y cada arista es una via con su tiempo de
 * traslado en minutos.
 *
 * Representacion: listas de adyacencia (un arreglo de listas enlazadas de
 * aristas). Espacio Theta(V + E). Se prefirio sobre la matriz de adyacencia
 * (Theta(V^2)) porque la red es dispersa: cada sede se conecta con pocas.
 *
 * Algoritmos:
 * - BFS: numero minimo de saltos desde una sede a las demas, O(V + E).
 * - Dijkstra con min-heap: tiempo minimo de traslado, O((V + E) log V).
 *   Se usa para derivar una solicitud a la sede con cupo mas cercana.
 */
public class SiteNetwork {

    /** Sede de atencion: vertice del grafo. */
    public static final class Site {

        private final String name;
        private final int capacity;
        private final TriageSystem system;

        Site(String name, int capacity, TriageSystem system) {
            this.name = name;
            this.capacity = capacity;
            this.system = system;
        }

        public String name() {
            return name;
        }

        public int capacity() {
            return capacity;
        }

        public TriageSystem system() {
            return system;
        }

        /** Una sede esta saturada si sus pendientes alcanzan su capacidad. */
        public boolean isSaturated() {
            return system.pendingCount() >= capacity;
        }
    }

    /** Resultado de una derivacion: sede destino, minutos y camino. */
    public static final class Route {

        private final int site;
        private final long minutes;
        private final List<String> path;

        Route(int site, long minutes, List<String> path) {
            this.site = site;
            this.minutes = minutes;
            this.path = path;
        }

        public int site() {
            return site;
        }

        public long minutes() {
            return minutes;
        }

        public List<String> path() {
            return path;
        }
    }

    private static final class Edge {

        final int to;
        final int minutes;
        final Edge next;

        Edge(int to, int minutes, Edge next) {
            this.to = to;
            this.minutes = minutes;
            this.next = next;
        }
    }

    /** Entrada del heap de Dijkstra: sede y distancia tentativa. */
    private static final class Entry {

        final int site;
        final long distance;

        Entry(int site, long distance) {
            this.site = site;
            this.distance = distance;
        }
    }

    public static final long UNREACHABLE = Long.MAX_VALUE;

    private Site[] sites = new Site[4];
    private Edge[] adjacency = new Edge[4];
    private int count = 0;
    private int edgeCount = 0;

    // ------------------------------------------------------------------
    // Construccion del grafo
    // ------------------------------------------------------------------

    /** @return el indice de la nueva sede */
    public int addSite(String name, int capacity, TriageSystem.Policy policy) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la sede no puede estar vacio");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        if (indexOf(name) >= 0) {
            throw new IllegalArgumentException("Ya existe la sede: " + name);
        }
        if (count == sites.length) {
            Site[] s = new Site[count * 2];
            Edge[] e = new Edge[count * 2];
            System.arraycopy(sites, 0, s, 0, count);
            System.arraycopy(adjacency, 0, e, 0, count);
            sites = s;
            adjacency = e;
        }
        sites[count] = new Site(name.trim(), capacity, new TriageSystem(policy));
        adjacency[count] = null;
        return count++;
    }

    /** Agrega una via en ambos sentidos. Theta(1). */
    public void addRoad(int a, int b, int minutes) {
        checkSite(a);
        checkSite(b);
        if (a == b) {
            throw new IllegalArgumentException("Una via debe unir dos sedes distintas");
        }
        if (minutes <= 0) {
            throw new IllegalArgumentException("El tiempo de traslado debe ser positivo");
        }
        adjacency[a] = new Edge(b, minutes, adjacency[a]);
        adjacency[b] = new Edge(a, minutes, adjacency[b]);
        edgeCount++;
    }

    public int siteCount() {
        return count;
    }

    public int roadCount() {
        return edgeCount;
    }

    public Site site(int i) {
        checkSite(i);
        return sites[i];
    }

    /** Busqueda lineal por nombre, O(V). */
    public int indexOf(String name) {
        for (int i = 0; i < count; i++) {
            if (sites[i].name.equalsIgnoreCase(name.trim())) {
                return i;
            }
        }
        return -1;
    }

    /** Vecinos de una sede con el tiempo de cada via, como texto. */
    public List<String> neighbors(int i) {
        checkSite(i);
        List<String> out = new ArrayList<>();
        for (Edge e = adjacency[i]; e != null; e = e.next) {
            out.add(sites[e.to].name + " (" + e.minutes + " min)");
        }
        return out;
    }

    // ------------------------------------------------------------------
    // BFS
    // ------------------------------------------------------------------

    /**
     * Menor numero de vias (saltos) desde from hasta cada sede; -1 si no es
     * alcanzable. Usa una cola FIFO: los vertices se descubren por niveles.
     */
    public int[] bfsHops(int from) {
        checkSite(from);
        int[] hops = new int[count];
        for (int i = 0; i < count; i++) {
            hops[i] = -1;
        }
        LinkedQueue<Integer> queue = new LinkedQueue<>();
        hops[from] = 0;
        queue.enqueue(from);
        while (!queue.isEmpty()) {
            int u = queue.dequeue();
            for (Edge e = adjacency[u]; e != null; e = e.next) {
                if (hops[e.to] == -1) {
                    hops[e.to] = hops[u] + 1;
                    queue.enqueue(e.to);
                }
            }
        }
        return hops;
    }

    // ------------------------------------------------------------------
    // Dijkstra
    // ------------------------------------------------------------------

    /**
     * Tiempo minimo de traslado desde from a cada sede (UNREACHABLE si no hay
     * camino). Si parent no es null, guarda el predecesor de cada sede en el
     * camino minimo. Usa un min-heap con eliminacion perezosa: una entrada
     * con distancia mayor a la ya conocida se descarta al salir.
     */
    public long[] dijkstra(int from, int[] parent) {
        checkSite(from);
        long[] dist = new long[count];
        boolean[] done = new boolean[count];
        for (int i = 0; i < count; i++) {
            dist[i] = UNREACHABLE;
            if (parent != null) {
                parent[i] = -1;
            }
        }
        MinHeap<Entry> heap = new MinHeap<>(Comparator.comparingLong((Entry x) -> x.distance));
        dist[from] = 0;
        heap.insert(new Entry(from, 0));
        while (!heap.isEmpty()) {
            Entry current = heap.removeMin();
            int u = current.site;
            if (done[u]) {
                continue;
            }
            done[u] = true;
            for (Edge e = adjacency[u]; e != null; e = e.next) {
                long candidate = dist[u] + e.minutes;
                if (candidate < dist[e.to]) {
                    dist[e.to] = candidate;
                    if (parent != null) {
                        parent[e.to] = u;
                    }
                    heap.insert(new Entry(e.to, candidate));
                }
            }
        }
        return dist;
    }

    /**
     * Sede no saturada (distinta de from) con menor tiempo de traslado.
     *
     * @return la ruta o null si ninguna sede alcanzable tiene cupo
     */
    public Route nearestAvailable(int from) {
        int[] parent = new int[count];
        long[] dist = dijkstra(from, parent);
        int best = -1;
        for (int i = 0; i < count; i++) {
            if (i != from && dist[i] != UNREACHABLE && !sites[i].isSaturated()
                    && (best == -1 || dist[i] < dist[best])) {
                best = i;
            }
        }
        if (best == -1) {
            return null;
        }
        return new Route(best, dist[best], buildPath(parent, best));
    }

    /** Ruta minima entre dos sedes; null si no hay camino. */
    public Route shortestRoute(int from, int to) {
        checkSite(to);
        int[] parent = new int[count];
        long[] dist = dijkstra(from, parent);
        if (dist[to] == UNREACHABLE) {
            return null;
        }
        return new Route(to, dist[to], buildPath(parent, to));
    }

    /** Reconstruye el camino siguiendo predecesores; la pila lo invierte. */
    private List<String> buildPath(int[] parent, int target) {
        LinkedStack<Integer> stack = new LinkedStack<>();
        for (int v = target; v != -1; v = parent[v]) {
            stack.push(v);
        }
        List<String> path = new ArrayList<>();
        while (!stack.isEmpty()) {
            path.add(sites[stack.pop()].name);
        }
        return path;
    }

    private void checkSite(int i) {
        if (i < 0 || i >= count) {
            throw new IllegalArgumentException("Sede inexistente: " + i);
        }
    }

    // ------------------------------------------------------------------
    // Red de ejemplo
    // ------------------------------------------------------------------

    /** Red de ejemplo con seis sedes ficticias en Bogota. */
    public static SiteNetwork defaultNetwork(TriageSystem.Policy policy) {
        SiteNetwork net = new SiteNetwork();
        int central = net.addSite("Central", 5, policy);
        int chapinero = net.addSite("Chapinero", 4, policy);
        int norte = net.addSite("Norte", 4, policy);
        int occidente = net.addSite("Occidente", 3, policy);
        int sur = net.addSite("Sur", 4, policy);
        int suba = net.addSite("Suba", 3, policy);
        net.addRoad(central, chapinero, 15);
        net.addRoad(central, occidente, 25);
        net.addRoad(central, sur, 20);
        net.addRoad(chapinero, norte, 20);
        net.addRoad(chapinero, occidente, 30);
        net.addRoad(norte, suba, 15);
        net.addRoad(occidente, suba, 30);
        net.addRoad(occidente, sur, 25);
        return net;
    }
}
