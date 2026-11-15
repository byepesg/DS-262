package mundo.cartas;

/**
 * Representa una carta de epidemia contenida en el mazo de jugador.
 */
public class CartaEpidemia extends Carta {

    private final String descripcion;

    public CartaEpidemia(String descripcion) {
        super("Epidemia", "Epidemia");
        this.descripcion = descripcion;
    }

    public String obtenerDescripcion() {
        return descripcion;
    }
}