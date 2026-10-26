package co.edu.unal.residencias;

import java.util.Objects;

public final class Estudiante {
    private final int id;
    private final String nombreCompleto;
    private int puntajeSocioeconomico;

    public Estudiante(int id, String nombreCompleto, int puntajeSocioeconomico) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        validarPuntaje(puntajeSocioeconomico);
        this.id = id;
        this.nombreCompleto = nombreCompleto.trim();
        this.puntajeSocioeconomico = puntajeSocioeconomico;
    }

    public int getId() { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public int getPuntajeSocioeconomico() { return puntajeSocioeconomico; }

    public void actualizarPuntaje(int nuevoPuntaje) {
        validarPuntaje(nuevoPuntaje);
        this.puntajeSocioeconomico = nuevoPuntaje;
    }

    private static void validarPuntaje(int puntaje) {
        if (puntaje < 0 || puntaje > 100) {
            throw new IllegalArgumentException("El puntaje debe estar entre 0 y 100.");
        }
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof Estudiante otro)) return false;
        return id == otro.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("ID: %d | Nombre: %s | Puntaje: %d", id, nombreCompleto, puntajeSocioeconomico);
    }
}
