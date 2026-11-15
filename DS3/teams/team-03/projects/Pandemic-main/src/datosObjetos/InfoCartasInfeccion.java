package datosObjetos;

import mundo.Ciudad;
import mundo.cartas.CartaInfeccion;

/**
 * Crear los objetos de las 48 cartas de infección asociadas a las ciudades del tablero.
 */
public class InfoCartasInfeccion {

    public static CartaInfeccion[] crearCartas(Ciudad[] ciudades) {
        CartaInfeccion[] cartas = new CartaInfeccion[ciudades.length];
        for (int i = 0; i < ciudades.length; i++) {
            cartas[i] = new CartaInfeccion(ciudades[i]);
        }
        return cartas;
    }
}