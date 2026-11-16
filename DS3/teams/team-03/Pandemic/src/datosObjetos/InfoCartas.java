package datosObjetos;

import mundo.Ciudad;
import mundo.cartas.CartaCiudad;
import mundo.cartas.CartaEpidemia;
import mundo.cartas.CartaEvento;

/**
 * Fábrica para la creación de cartas del mazo de jugador.
 * Reutiliza las referencias físicas de las ciudades del tablero para evitar duplicidad de memoria.
 */
public class InfoCartas {

    public static CartaCiudad[] crearCartasCiudad(Ciudad[] ciudadesTablero) {
        CartaCiudad[] cartas = new CartaCiudad[ciudadesTablero.length];
        for (int i = 0; i < ciudadesTablero.length; i++) {
            cartas[i] = new CartaCiudad(ciudadesTablero[i]);
        }
        return cartas;
    }

    public static CartaEpidemia[] crearCartasEpidemia(int cantidad) {
        CartaEpidemia[] epidemias = new CartaEpidemia[cantidad];
        for (int i = 0; i < cantidad; i++) {
            epidemias[i] = new CartaEpidemia("Aumentar tasa, infectar ciudad del descarte FIFO e intensificar.");
        }
        return epidemias;
    }

    public static CartaEvento[] crearCartasEvento() {
        return new CartaEvento[]{
            new CartaEvento("Pronóstico", "Examina las primeras 6 cartas del mazo de infección."),
            new CartaEvento("Puente Aéreo", "Mueve un personaje a cualquier ciudad."),
            new CartaEvento("Noche Tranquila", "Sáltate la siguiente fase de infección."),
            new CartaEvento("Subvención del Gobierno", "Añade una estación de investigación."),
            new CartaEvento("Población Resistente", "Retira una carta de la pila de descartes de infección.")
        };
    }
}