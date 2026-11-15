import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Servicio {
    private final String codigo;
    private final String origen;
    private final String destino;
    private final List<String> paradas;
    private final Map<String, Integer> posicion = new HashMap<>();

    public Servicio(String codigo, String origen, String destino, List<String> paradas) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código del servicio no puede ser vacío");
        }
        if (paradas == null || paradas.isEmpty()) {
            throw new IllegalArgumentException("El servicio " + codigo + " no tiene paradas");
        }
        this.codigo = codigo.trim();
        this.origen = origen;
        this.destino = destino;
        this.paradas = List.copyOf(paradas);
        for (int i = 0; i < this.paradas.size(); i++) {
            if (posicion.put(this.paradas.get(i), i) != null) {
                throw new IllegalArgumentException("El servicio " + codigo + " repite la parada " + this.paradas.get(i));
            }
        }
    }

    public String getCodigo() {
        return codigo;
    }

    // id único: el mismo código puede ir en los dos sentidos
    public String getId() {
        return codigo + ">" + destino;
    }

    public static String codigoDe(String id) {
        int i = id.indexOf('>');
        return i < 0 ? id : id.substring(0, i);
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public List<String> getParadas() {
        return paradas;
    }

    public boolean paraEn(String idEstacion) {
        return posicion.containsKey(idEstacion);
    }

    public boolean vaDe(String a, String b) {
        Integer pa = posicion.get(a);
        Integer pb = posicion.get(b);
        return pa != null && pb != null && pa < pb;
    }

    public int paradasEntre(String a, String b) {
        return posicion.get(b) - posicion.get(a);
    }

    @Override
    public String toString() {
        return codigo + " (" + origen + " -> " + destino + ")";
    }
}
