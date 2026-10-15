package mundo.cartas;

/**
 * Clase base que representa una carta genérica dentro del juego.
 */
public class Carta {

    private final String nombre;
    private final String tipo;

    public Carta(String nombre, String tipo) {
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerTipo() {
        return tipo;
    }
}