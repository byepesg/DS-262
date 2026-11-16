package mundo.roles;

import juego.InterfazJuego;
import juego.LogicaJuego;
import mundo.Ciudad;
import mundo.Personaje;
import java.util.Scanner;

public class Despachador extends Rol {

    public Despachador() {
        super("Despachador",
                "Puede mover los peones de otros jugadores como si fueran el suyo (usando sus propias cartas) o mover cualquier peón a una ciudad donde haya otro peón.");
    }

    @Override
    protected void imprimirOpcionesBase() {
        super.imprimirOpcionesBase();
        InterfazJuego.opcion(10, "Mover peón de otro jugador");
        InterfazJuego.opcion(11, "Reunir dos peones");
    }

    @Override
    protected int procesarOpcionBase(int opcion, Personaje personaje, LogicaJuego juego) {
        switch (opcion) {
            case 10:
                return ejecutarMoverOtroPeon(personaje, juego) ? 1 : 0;
            case 11:
                return ejecutarMoverPeonHaciaOtroPeon(personaje, juego) ? 1 : 0;
            default:
                return super.procesarOpcionBase(opcion, personaje, juego);
        }
    }

    private boolean ejecutarMoverOtroPeon(Personaje despachador, LogicaJuego juego) {
        Scanner scanner = despachador.obtenerScanner();
        Personaje[] jugadores = juego.obtenerJugadores();

        System.out.println("\nSeleccione el peón que desea mover:");
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i] != despachador) {
                InterfazJuego.opcion(i, jugadores[i].obtenerNombre()
                        + " (en " + jugadores[i].obtenerCiudadActual().obtenerNombre() + ")");
            }
        }
        InterfazJuego.pregunta("lista", "Índice del jugador: ");

        int idxJugador = leerEntero(scanner);
        if (idxJugador < 0 || idxJugador >= jugadores.length || jugadores[idxJugador] == despachador) {
            System.out.println("Selección de jugador inválida.");
            return false;
        }

        Personaje peonObjetivo = jugadores[idxJugador];

        System.out.println("\nTipo de movimiento para " + peonObjetivo.obtenerNombre() + ":");
        InterfazJuego.opcion(1, "Mover a ciudad vecina");
        InterfazJuego.opcion(2, "Vuelo directo (descartando carta de tu mano)");
        InterfazJuego.opcion(3, "Vuelo chárter (descartando carta de " + peonObjetivo.obtenerCiudadActual().obtenerNombre() + " de tu mano)");
        InterfazJuego.opcion(4, "Mover entre centros de investigación");
        InterfazJuego.pregunta("lista", "Opción: ");

        int tipo = leerEntero(scanner);
        switch (tipo) {
            case 1:
                return ejecutarMoverVecino(despachador, peonObjetivo, juego);
            case 2:
                return ejecutarVueloDirecto(despachador, peonObjetivo, juego);
            case 3:
                return ejecutarVueloCharter(despachador, peonObjetivo, juego);
            case 4:
                return ejecutarTransCenterMove(peonObjetivo, juego);
            default:
                System.out.println("Opción inválida.");
                return false;
        }
    }

    private boolean ejecutarMoverPeonHaciaOtroPeon(Personaje despachador, LogicaJuego juego) {
        Scanner scanner = despachador.obtenerScanner();
        Personaje[] jugadores = juego.obtenerJugadores();

        System.out.println("\nSeleccione el peón que desea trasladar:");
        for (int i = 0; i < jugadores.length; i++) {
            InterfazJuego.opcion(i, jugadores[i].obtenerNombre()
                    + " (en " + jugadores[i].obtenerCiudadActual().obtenerNombre() + ")");
        }
        InterfazJuego.pregunta("lista", "Índice del peón a mover: ");

        int idxOrigen = leerEntero(scanner);
        if (idxOrigen < 0 || idxOrigen >= jugadores.length) {
            System.out.println("Índice inválido.");
            return false;
        }

        Personaje peonAMover = jugadores[idxOrigen];

        System.out.println("\nSeleccione el peón destino al cual reunirse:");
        for (int i = 0; i < jugadores.length; i++) {
            if (jugadores[i] != peonAMover) {
                InterfazJuego.opcion(i, jugadores[i].obtenerNombre()
                        + " (en " + jugadores[i].obtenerCiudadActual().obtenerNombre() + ")");
            }
        }
        InterfazJuego.pregunta("lista", "Índice del peón destino: ");

        int idxDestino = leerEntero(scanner);
        if (idxDestino < 0 || idxDestino >= jugadores.length || jugadores[idxDestino] == peonAMover) {
            System.out.println("Índice inválido.");
            return false;
        }

        Ciudad ciudadDestino = jugadores[idxDestino].obtenerCiudadActual();
        if (ciudadDestino == peonAMover.obtenerCiudadActual()) {
            System.out.println(peonAMover.obtenerNombre() + " ya se encuentra en " + ciudadDestino.obtenerNombre() + ".");
            return false;
        }

        juego.cambiarUbicacion(peonAMover, ciudadDestino);
        return true;
    }
}