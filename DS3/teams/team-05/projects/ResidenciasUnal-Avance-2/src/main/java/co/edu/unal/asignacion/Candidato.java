package co.edu.unal.asignacion;
import java.util.Objects;

public final class Candidato {
    private final int id;
    private final String nombre;
    private int puntaje;

    public Candidato(int id, String nombre, int puntaje) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        validarPuntaje(puntaje);
        this.id = id;
        this.nombre = nombre.trim();
        this.puntaje = puntaje;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPuntaje() { return puntaje; }

    public void actualizarPuntaje(int nuevoPuntaje) {
        validarPuntaje(nuevoPuntaje);
        this.puntaje = nuevoPuntaje;
    }

    public Candidato copiar() { return new Candidato(id, nombre, puntaje); }

    private static void validarPuntaje(int p) {
        if (p < 0 || p > 100) throw new IllegalArgumentException("El puntaje debe estar entre 0 y 100.");
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Candidato otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s | Puntaje: %d", id, nombre, puntaje);
    }
}