package bd;

public class Permiso {
    private int id;
    private String nombre;

    public Permiso(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}