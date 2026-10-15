package mundo.cartas;

import mundo.Ciudad;

/**
 * Representa una carta de ciudad utilizada por los jugadores para desplazarse.
 */
public class CartaCiudad extends Carta {

    private final Ciudad ciudadAsociada;

    public CartaCiudad(Ciudad ciudadAsociada) {
        super(ciudadAsociada.obtenerNombre(), "Ciudad");
        this.ciudadAsociada = ciudadAsociada;
    }

    public Ciudad obtenerCiudad() {
        return ciudadAsociada;
    }
}