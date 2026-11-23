package bd;

import java.util.ArrayList;

public class Rol {
    private int id;
    private String nombreRol;
    private ArrayList<Permiso> permisos;

    public Rol(int id, String nombreRol) {
        this.id = id;
        this.nombreRol = nombreRol;
        this.permisos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void agregarPermiso(Permiso permiso) {
        permisos.add(permiso);
    }

    public boolean tienePermiso(String nombrePermiso) {
    for (Permiso p : permisos) {
        if (p.getNombre().equalsIgnoreCase(nombrePermiso)) {
            return true;
        }
    }
    return false;
}
}