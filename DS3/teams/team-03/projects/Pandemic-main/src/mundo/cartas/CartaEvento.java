package mundo.cartas;

/**
 * Representa una carta de evento especial que confiere habilidades únicas.
 */
public class CartaEvento extends Carta {

    private final String descripcion;

    public CartaEvento(String nombre, String descripcion) {
        super(nombre, "Evento");
        this.descripcion = descripcion;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }
}