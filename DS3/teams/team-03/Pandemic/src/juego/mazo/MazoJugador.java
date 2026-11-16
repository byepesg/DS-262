package juego.mazo;

import datosObjetos.InfoCartas;
import estructurasDatos.FixedArrayStack;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;
import mundo.cartas.CartaEpidemia;
import mundo.cartas.CartaEvento;

public class MazoJugador {

    private FixedArrayStack<Carta> mazo;

    public MazoJugador() {
        this.mazo = new FixedArrayStack<>(100);
    }

    public Carta[] crearMazoInicialSinEpidemias(Ciudad[] ciudades) {
        CartaEvento[] eventos = InfoCartas.crearCartasEvento();
        Carta[] mazoInicial = new Carta[ciudades.length + eventos.length];
        int idx = 0;

        for (Ciudad c : ciudades) {
            mazoInicial[idx++] = new CartaCiudad(c);
        }
        for (CartaEvento e : eventos) {
            mazoInicial[idx++] = e;
        }
        return mazoInicial;
    }

    public void barajarArreglo(Carta[] cartas) {
        for (int i = cartas.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            Carta temp = cartas[i];
            cartas[i] = cartas[j];
            cartas[j] = temp;
        }
    }

    public int repartirManosIniciales(Carta[] mazoInicial, Personaje[] jugadores, MazoAuxiliarEvento mazoAuxiliarEvento) {
        int cartasPorJugador;
        if (jugadores.length == 2) {
            cartasPorJugador = 4;
        } else if (jugadores.length == 3) {
            cartasPorJugador = 3;
        } else {
            cartasPorJugador = 2;
        }

        int indiceMazo = 0;
        for (int i = 0; i < jugadores.length; i++) {
            for (int c = 0; c < cartasPorJugador; c++) {
                Carta cartaRepartida = mazoInicial[indiceMazo++];
                jugadores[i].agarrarCarta(cartaRepartida);

                if (mazoAuxiliarEvento != null
                        && (cartaRepartida instanceof CartaEvento
                        || "Evento".equalsIgnoreCase(cartaRepartida.obtenerTipo()))) {
                    mazoAuxiliarEvento.agregarEvento(cartaRepartida, jugadores[i]);
                }
            }
        }
        return indiceMazo;
    }

    public void ensamblarYConvertirAStack(Carta[] mazoInicial, int cartasUsadas, int numEpidemias) {
        int restantes = mazoInicial.length - cartasUsadas;
        int totalFinal = restantes + numEpidemias;
        this.mazo = new FixedArrayStack<>(totalFinal);

        CartaEpidemia[] epidemias = InfoCartas.crearCartasEpidemia(numEpidemias);
        int baseSize = restantes / numEpidemias;
        int extra = restantes % numEpidemias;

        Carta[][] pilas = new Carta[numEpidemias][];
        int cursor = cartasUsadas;

        for (int p = 0; p < numEpidemias; p++) {
            int tamSubpila = baseSize + (p < extra ? 1 : 0);
            Carta[] subpila = new Carta[tamSubpila + 1];

            for (int k = 0; k < tamSubpila; k++) {
                subpila[k] = mazoInicial[cursor++];
            }
            subpila[tamSubpila] = epidemias[p];
            barajarArreglo(subpila);
            pilas[p] = subpila;
        }

        for (int p = numEpidemias - 1; p >= 0; p--) {
            for (int k = pilas[p].length - 1; k >= 0; k--) {
                this.mazo.push(pilas[p][k]);
            }
        }
    }

    public Carta robarCarta() {
        if (mazo.isEmpty()) {
            return null;
        }
        return mazo.pop();
    }

}