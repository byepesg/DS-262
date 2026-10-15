package juego.mazo;

import datosObjetos.InfoCartas;
import estructurasDatos.FixedArrayStack;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;
import mundo.cartas.CartaEpidemia;
import mundo.cartas.CartaEvento;

/**
 * Administra el mazo de jugador.
 * Gestiona el flujo de repartición inicial sin epidemias, el ensamblaje con epidemias
 * y la posterior conversión a un FixedArrayStack para la dinámica del juego.
 */

public class MazoJugador {

    private FixedArrayStack<Carta> mazoStack;

    public MazoJugador() {
        this.mazoStack = null;
    }

    // Paso 1: Crear mazo inicial sin epidemias (48 de ciudad + 5 de evento = 53 cartas)
    public Carta[] crearMazoInicialSinEpidemias(Ciudad[] ciudadesTablero) {
        CartaCiudad[] cartasCiudad = InfoCartas.crearCartasCiudad(ciudadesTablero);
        CartaEvento[] cartasEvento = InfoCartas.crearCartasEvento();

        Carta[] mazoInicial = new Carta[cartasCiudad.length + cartasEvento.length];
        int idx = 0;

        for (CartaCiudad c : cartasCiudad) {
            mazoInicial[idx++] = c;
        }
        for (CartaEvento e : cartasEvento) {
            mazoInicial[idx++] = e;
        }

        return mazoInicial;
    }

    // Paso 2: Barajar mazo en formato arreglo (Fisher-Yates)
    public void barajarArreglo(Carta[] mazo) {
        for (int i = mazo.length - 1; i > 0; i--) {
            int aleatorio = (int) (Math.random() * (i + 1));
            Carta temp = mazo[i];
            mazo[i] = mazo[aleatorio];
            mazo[aleatorio] = temp;
        }
    }

    // Paso 3: Repartir cartas iniciales (3 cartas a cada jugador)
    public int repartirManosIniciales(Carta[] mazoSinEpidemias, Personaje[] jugadores) {
        int cartasRepartidas = 0;
        for (Personaje j : jugadores) {
            for (int k = 0; k < 3; k++) {
                j.agarrarCarta(mazoSinEpidemias[cartasRepartidas]);
                cartasRepartidas++;
            }
        }
        return cartasRepartidas;
    }

    // Pasos 4 y 5: Ensamblar remanente con epidemias, barajar y cargar en FixedArrayStack
    public void ensamblarYConvertirAStack(Carta[] mazoSinEpidemias, int cartasYaUsadas, int numEpidemias) {
        int remanenteTamano = mazoSinEpidemias.length - cartasYaUsadas;
        CartaEpidemia[] epidemias = InfoCartas.crearCartasEpidemia(numEpidemias);

        Carta[] remanenteConEpidemias = new Carta[remanenteTamano + numEpidemias];
        int idx = 0;

        // Copiar remanente no repartido
        for (int i = cartasYaUsadas; i < mazoSinEpidemias.length; i++) {
            remanenteConEpidemias[idx++] = mazoSinEpidemias[i];
        }

        // Agregar cartas de epidemia
        for (CartaEpidemia ep : epidemias) {
            remanenteConEpidemias[idx++] = ep;
        }

        // Barajar el remanente final
        barajarArreglo(remanenteConEpidemias);

        // Guardar en la estructura FixedArrayStack
        this.mazoStack = new FixedArrayStack<>(remanenteConEpidemias.length);
        for (Carta c : remanenteConEpidemias) {
            this.mazoStack.push(c);
        }
    }

    public Carta robarCarta() {
        if (mazoStack != null && !mazoStack.isEmpty()) {
            return mazoStack.pop();
        }
        return null; // Mazo de jugador agotado
    }

    public int cartasRestantes() {
        return (mazoStack != null) ? mazoStack.obtenerTamano() : 0;
    }
}