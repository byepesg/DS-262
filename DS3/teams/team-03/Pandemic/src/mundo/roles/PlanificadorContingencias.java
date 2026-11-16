package mundo.roles;
import mundo.cartas.CartaEvento;
import juego.InterfazJuego;
import juego.LogicaJuego;
import juego.mazo.MazoAuxiliarEvento;
import mundo.Personaje;
import mundo.cartas.Carta;
import java.util.Scanner;

public class PlanificadorContingencias extends Rol {

    private CartaEvento eventoAlmacenado;

    public PlanificadorContingencias() {
        super("Planificador de Contingencias",
                "Como acción, puede tomar una carta de Evento descartada y guardarla en su carta de rol (máx. 1). Al jugarla, se retira del juego.");
        this.eventoAlmacenado = null;
    }

    @Override
    protected void imprimirOpcionesBase() {
        super.imprimirOpcionesBase();
        InterfazJuego.opcion(10, "Recuperar evento descartado");
    }

    @Override
    protected int procesarOpcionBase(int opcion, Personaje personaje, LogicaJuego juego) {
        if (opcion == 10) {
            return ejecutarRecuperarEvento(personaje, juego) ? 1 : 0;
        }
        return super.procesarOpcionBase(opcion, personaje, juego);
    }

    @Override
    public boolean jugarEvento(Personaje jugadorEnTurno, LogicaJuego juego) {
        if (eventoAlmacenado == null) {
            return super.jugarEvento(jugadorEnTurno, juego);
        }

        MazoAuxiliarEvento mazoEventos = juego.obtenerMazoAuxiliarEvento();
        int totalEnMano = mazoEventos.obtenerNumEventosDisponibles();

        System.out.println("\nCartas de Evento disponibles:");
        for (int i = 0; i < totalEnMano; i++) {
            CartaEvento evento = mazoEventos.obtenerCartaEvento(i);
            InterfazJuego.opcion(i, evento.obtenerNombre()
                    + " (en mano de " + mazoEventos.obtenerPortador(i).obtenerNombre() + "): "
                    + evento.obtenerDescripcion());
        }
        InterfazJuego.opcion(totalEnMano, eventoAlmacenado.obtenerNombre()
                + " (almacenada en " + nombre + "): "
                + eventoAlmacenado.obtenerDescripcion());
        InterfazJuego.pregunta("lista", "Seleccione el índice de la carta de evento a jugar (o -1 para cancelar): ");

        Scanner scanner = jugadorEnTurno.obtenerScanner();
        int seleccion = leerEntero(scanner);
        if (seleccion < 0 || seleccion > totalEnMano) {
            System.out.println("Ejecución de evento cancelada.");
            return false;
        }

        if (seleccion == totalEnMano) {
            boolean ejecutado = resolverCartaEvento(eventoAlmacenado, jugadorEnTurno, juego);
            if (ejecutado) {
                System.out.println("Carta de evento '" + eventoAlmacenado.obtenerNombre()
                        + "' retirada definitivamente de la partida.");
                eventoAlmacenado = null;
                return true;
            }
            return false;
        }

        Carta cartaElegida = mazoEventos.obtenerCartaEvento(seleccion);
        Personaje portador = mazoEventos.obtenerPortador(seleccion);

        boolean ejecutado = resolverCartaEvento(cartaElegida, jugadorEnTurno, juego);
        if (ejecutado) {
            mazoEventos.eliminarEvento(seleccion);
            System.out.println("Carta de evento '" + cartaElegida.obtenerNombre()
                    + "' descartada de la mano de " + portador.obtenerNombre() + ".");
            return true;
        }

        return false;
    }

    private boolean ejecutarRecuperarEvento(Personaje personaje, LogicaJuego juego) {
        if (eventoAlmacenado != null) {
            System.out.println("Ya tienes un evento almacenado (" + eventoAlmacenado.obtenerNombre()
                    + "). Juégalo antes de recuperar otro.");
            return false;
        }

        MazoAuxiliarEvento mazoEventos = juego.obtenerMazoAuxiliarEvento();
        int totalDescartados = mazoEventos.obtenerNumEventosDescartados();

        if (totalDescartados == 0) {
            System.out.println("No hay cartas de evento descartadas disponibles para recuperar.");
            return false;
        }

        System.out.println("\nCartas de Evento en el descarte:");
        for (int i = 0; i < totalDescartados; i++) {
            InterfazJuego.opcion(i, mazoEventos.obtenerEventoDescartado(i).obtenerNombre());
        }
        InterfazJuego.pregunta("lista", "Seleccione el índice del evento a recuperar (o -1 para cancelar): ");

        Scanner scanner = personaje.obtenerScanner();
        int idx = leerEntero(scanner);
        if (idx < 0 || idx >= totalDescartados) {
            System.out.println("Recuperación de evento cancelada.");
            return false;
        }

        eventoAlmacenado = mazoEventos.retirarEventoDescartado(idx);
        System.out.println("¡Éxito! Has almacenado '" + eventoAlmacenado.obtenerNombre()
                + "' en tu carta de rol.");
        return true;
    }
}