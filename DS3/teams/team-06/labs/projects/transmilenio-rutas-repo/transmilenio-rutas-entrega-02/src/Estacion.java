import java.util.List;
import java.util.Objects;

public final class Estacion {
    private final String id;
    private final String nombre;
    private final List<String> alias;

    public Estacion(String id, String nombre, List<String> alias) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El id de la estación no puede ser vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la estación no puede ser vacío");
        }
        this.id = id.trim();
        this.nombre = nombre.trim();
        this.alias = alias == null ? List.of() : List.copyOf(alias);
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getAlias() {
        return alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Estacion)) return false;
        return id.equals(((Estacion) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " [" + id + "]";
    }
}
