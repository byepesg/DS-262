package co.edu.unal.asignacion;

/** Operación exitosa, numerada dentro de la sesión actual. */
public record Movimiento(long numero, String operacion, String detalle) {
    @Override
    public String toString() { return numero + ". " + operacion + " | " + detalle; }
}
