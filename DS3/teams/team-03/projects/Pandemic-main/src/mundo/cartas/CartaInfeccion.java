package mundo.cartas;

import mundo.Ciudad;

/**
 * Representa una carta del mazo de infección asociada a una ciudad específica.
 */
public class CartaInfeccion extends Carta {

    private final Ciudad ciudadAsociada;

    public CartaInfeccion(Ciudad ciudadAsociada) {
        super(ciudadAsociada.obtenerNombre(), "Infeccion");
        this.ciudadAsociada = ciudadAsociada;
    }

    public Ciudad obtenerCiudad() {
        return ciudadAsociada;
    }
}