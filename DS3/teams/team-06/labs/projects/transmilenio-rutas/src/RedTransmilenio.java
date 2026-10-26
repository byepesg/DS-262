import java.text.Normalizer;
import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class RedTransmilenio {
    public static final int DETENCION_SEG = 30;

    private final Map<String, Estacion> estaciones = new HashMap<>();
    private final Map<String, String> indiceNombre = new HashMap<>();
    private final Map<String, List<Conexion>> infraestructura = new HashMap<>();
    private final Map<String, List<Conexion>> redServicios = new HashMap<>();
    private final Map<String, Servicio> servicios = new HashMap<>();
    private final Map<String, TreeSet<String>> serviciosPorEstacion = new HashMap<>();
    private int numeroTramos = 0;
    private int numeroArcosServicio = 0;

    public void agregarEstacion(Estacion e) {
        if (estaciones.containsKey(e.getId())) {
            throw new IllegalArgumentException("Estación duplicada: " + e.getId());
        }
        estaciones.put(e.getId(), e);
        infraestructura.put(e.getId(), new ArrayList<>());
        redServicios.put(e.getId(), new ArrayList<>());
        serviciosPorEstacion.put(e.getId(), new TreeSet<>());
        indexarNombre(e.getNombre(), e.getId());
        for (String a : e.getAlias()) {
            indexarNombre(a, e.getId());
        }
    }

    private void indexarNombre(String nombre, String id) {
        String clave = normalizar(nombre);
        String previo = indiceNombre.putIfAbsent(clave, id);
        if (previo != null && !previo.equals(id)) {
            throw new IllegalArgumentException("Nombre ambiguo '" + nombre + "' para " + previo + " y " + id);
        }
    }

    public void agregarTramo(String a, String b, int segundos) {
        exigirEstacion(a);
        exigirEstacion(b);
        if (a.equals(b)) throw new IllegalArgumentException("No se permiten lazos: " + a);
        infraestructura.get(a).add(new Conexion(b, segundos, null));
        infraestructura.get(b).add(new Conexion(a, segundos, null));
        numeroTramos++;
    }

    // crea los arcos de la ruta usando el tiempo de la vía
    public void agregarServicio(Servicio s) {
        if (servicios.containsKey(s.getId())) {
            throw new IllegalArgumentException("Servicio duplicado: " + s);
        }
        List<String> p = s.getParadas();
        for (String id : p) exigirEstacion(id);
        List<Conexion> nuevos = new ArrayList<>();
        for (int i = 0; i + 1 < p.size(); i++) {
            int recorrido = tiempoFisico(p.get(i), p.get(i + 1));
            if (recorrido < 0) {
                throw new IllegalArgumentException("El servicio " + s.getCodigo() + " no tiene vía física entre "
                        + p.get(i) + " y " + p.get(i + 1));
            }
            nuevos.add(new Conexion(p.get(i + 1), recorrido + DETENCION_SEG, s.getId()));
        }
        // solo se modifica la red si todo el servicio es válido
        servicios.put(s.getId(), s);
        for (int i = 0; i < nuevos.size(); i++) {
            redServicios.get(p.get(i)).add(nuevos.get(i));
            numeroArcosServicio++;
        }
        for (String id : p) serviciosPorEstacion.get(id).add(s.getId());
    }

    // tiempo mínimo por la vía (Dijkstra)
    public int tiempoFisico(String origen, String destino) {
        exigirEstacion(origen);
        exigirEstacion(destino);
        Map<String, Integer> dist = new HashMap<>();
        MinHeap<Par> heap = new MinHeap<>();
        dist.put(origen, 0);
        heap.insertar(new Par(origen, 0));
        while (!heap.estaVacio()) {
            Par actual = heap.extraerMin();
            if (actual.costo > dist.get(actual.id)) continue;
            if (actual.id.equals(destino)) return actual.costo;
            for (Conexion c : infraestructura.get(actual.id)) {
                int nuevo = actual.costo + c.getSegundos();
                if (nuevo < dist.getOrDefault(c.getDestino(), Integer.MAX_VALUE)) {
                    dist.put(c.getDestino(), nuevo);
                    heap.insertar(new Par(c.getDestino(), nuevo));
                }
            }
        }
        return -1;
    }

    private static final class Par implements Comparable<Par> {
        final String id;
        final int costo;

        Par(String id, int costo) {
            this.id = id;
            this.costo = costo;
        }

        @Override
        public int compareTo(Par o) {
            return Integer.compare(costo, o.costo);
        }
    }

    public Estacion obtenerEstacion(String id) {
        return estaciones.get(id);
    }

    public boolean contieneEstacion(String id) {
        return estaciones.containsKey(id);
    }

    public List<Servicio> buscarServicios(String codigo) {
        List<Servicio> r = new ArrayList<>();
        if (codigo == null) return r;
        for (Servicio s : serviciosOrdenados()) {
            if (s.getCodigo().equalsIgnoreCase(codigo.trim())) r.add(s);
        }
        return r;
    }

    // busca por id, nombre o alias
    public Estacion resolver(String texto) {
        if (texto == null) return null;
        Estacion e = estaciones.get(texto.trim().toUpperCase());
        if (e != null) return e;
        String id = indiceNombre.get(normalizar(texto));
        return id == null ? null : estaciones.get(id);
    }

    public List<Estacion> buscarPorFragmento(String fragmento) {
        List<Estacion> r = new ArrayList<>();
        if (fragmento == null || fragmento.isBlank()) return r;
        String f = normalizar(fragmento);
        for (Estacion e : estacionesOrdenadas()) {
            boolean coincide = normalizar(e.getNombre()).contains(f);
            for (String a : e.getAlias()) coincide |= normalizar(a).contains(f);
            if (coincide) r.add(e);
        }
        return r;
    }

    public List<Servicio> serviciosEn(String idEstacion) {
        exigirEstacion(idEstacion);
        List<Servicio> r = new ArrayList<>();
        for (String id : serviciosPorEstacion.get(idEstacion)) r.add(servicios.get(id));
        return r;
    }

    // códigos de las rutas que paran en la estación
    public List<String> serviciosQuePasan(String idEstacion) {
        List<String> r = new ArrayList<>();
        for (Servicio s : serviciosEn(idEstacion)) r.add(s.getCodigo());
        return r;
    }

    // rutas que van de a hasta b sin cambiar de bus
    public List<Servicio> serviciosDirectos(String a, String b) {
        exigirEstacion(a);
        exigirEstacion(b);
        List<Servicio> r = new ArrayList<>();
        for (String id : serviciosPorEstacion.get(a)) {
            Servicio s = servicios.get(id);
            if (s.vaDe(a, b)) r.add(s);
        }
        r.sort((x, y) -> Integer.compare(x.paradasEntre(a, b), y.paradasEntre(a, b)));
        return r;
    }

    public List<Conexion> arcosDeServicio(String id) {
        exigirEstacion(id);
        return Collections.unmodifiableList(redServicios.get(id));
    }

    public List<Conexion> tramosFisicos(String id) {
        exigirEstacion(id);
        return Collections.unmodifiableList(infraestructura.get(id));
    }

    public List<Estacion> estacionesOrdenadas() {
        List<Estacion> r = new ArrayList<>(estaciones.values());
        r.sort((a, b) -> a.getId().compareTo(b.getId()));
        return r;
    }

    public List<Servicio> serviciosOrdenados() {
        List<Servicio> r = new ArrayList<>(servicios.values());
        r.sort((a, b) -> a.getId().compareTo(b.getId()));
        return r;
    }

    public int numeroEstaciones() {
        return estaciones.size();
    }

    public int numeroTramos() {
        return numeroTramos;
    }

    public int numeroServicios() {
        return servicios.size();
    }

    public int numeroArcosServicio() {
        return numeroArcosServicio;
    }

    private void exigirEstacion(String id) {
        if (!estaciones.containsKey(id)) {
            throw new IllegalArgumentException("Estación inexistente: " + id);
        }
    }

    public static String normalizar(String texto) {
        String sinTildes = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinTildes.toLowerCase().replaceAll("\\s+", " ");
    }
}
