package juego.mazo;

import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaEvento;

public class MazoAuxiliarEvento {

    private final CartaEvento[] cartasEvento;
    private final Personaje[] portadores;
    private int numEventosDisponibles;

    private final CartaEvento[] eventosDescartados;
    private int numEventosDescartados;

    public MazoAuxiliarEvento() {
        this.cartasEvento = new CartaEvento[10];
        this.portadores = new Personaje[10];
        this.numEventosDisponibles = 0;

        this.eventosDescartados = new CartaEvento[10];
        this.numEventosDescartados = 0;
    }

    public void agregarEvento(Carta carta, Personaje portador) {
        if (!(carta instanceof CartaEvento) || portador == null) {
            return;
        }

        CartaEvento cartaEvento = (CartaEvento) carta;
        int indiceExistente = buscarIndiceEvento(cartaEvento);
        if (indiceExistente != -1) {
            portadores[indiceExistente] = portador;
            return;
        }

        if (numEventosDisponibles < cartasEvento.length) {
            cartasEvento[numEventosDisponibles] = cartaEvento;
            portadores[numEventosDisponibles] = portador;
            numEventosDisponibles++;
        }
    }

    public boolean eliminarEvento(int indice) {
        if (indice < 0 || indice >= numEventosDisponibles) {
            return false;
        }

        CartaEvento carta = cartasEvento[indice];
        Personaje portador = portadores[indice];

        for (int i = indice; i < numEventosDisponibles - 1; i++) {
            cartasEvento[i] = cartasEvento[i + 1];
            portadores[i] = portadores[i + 1];
        }
        cartasEvento[numEventosDisponibles - 1] = null;
        portadores[numEventosDisponibles - 1] = null;
        numEventosDisponibles--;

        if (carta != null && numEventosDescartados < eventosDescartados.length) {
            eventosDescartados[numEventosDescartados++] = carta;
        }

        if (portador != null) {
            Carta[] mano = portador.obtenerMano();
            for (int i = 0; i < portador.obtenerNumCartas(); i++) {
                if (mano[i] == carta) {
                    portador.removerCartaPorIndice(i);
                    break;
                }
            }
        }

        return true;
    }

    public boolean eliminarEvento(Carta carta) {
        int indice = buscarIndiceEvento(carta);
        if (indice != -1) {
            return eliminarEvento(indice);
        }
        return false;
    }

    private int buscarIndiceEvento(Carta carta) {
        if (carta == null) {
            return -1;
        }
        for (int i = 0; i < numEventosDisponibles; i++) {
            if (cartasEvento[i] == carta) {
                return i;
            }
        }
        return -1;
    }

    public boolean hayEventosDisponibles() {
        return numEventosDisponibles > 0;
    }

    public int obtenerNumEventosDisponibles() {
        return numEventosDisponibles;
    }

    public CartaEvento obtenerCartaEvento(int indice) {
        if (indice >= 0 && indice < numEventosDisponibles) {
            return cartasEvento[indice];
        }
        return null;
    }

    public Personaje obtenerPortador(int indice) {
        if (indice >= 0 && indice < numEventosDisponibles) {
            return portadores[indice];
        }
        return null;
    }

    public int obtenerNumEventosDescartados() {
        return numEventosDescartados;
    }

    public CartaEvento obtenerEventoDescartado(int indice) {
        if (indice >= 0 && indice < numEventosDescartados) {
            return eventosDescartados[indice];
        }
        return null;
    }

    public CartaEvento retirarEventoDescartado(int indice) {
        if (indice < 0 || indice >= numEventosDescartados) {
            return null;
        }
        CartaEvento retirada = eventosDescartados[indice];
        for (int i = indice; i < numEventosDescartados - 1; i++) {
            eventosDescartados[i] = eventosDescartados[i + 1];
        }
        eventosDescartados[--numEventosDescartados] = null;
        return retirada;
    }
}