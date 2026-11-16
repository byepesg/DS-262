package juego;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = InterfazJuego.entrada();
        int cantidadJugadores;
        if (args.length == 2 && args[0].equals("--grafico")) {
            InterfazJuego.modoGrafico();
            cantidadJugadores = Integer.parseInt(args[1]);
        } else {
            cantidadJugadores = elegirCantidad(scanner);
        }

        LogicaJuego juego = new LogicaJuego(cantidadJugadores);
        InterfazJuego.establecerPartida(juego);
        if (!InterfazJuego.esGrafica()) {
            juego.imprimirEstadoMapa();
        }

        String resultado;
        int numeroTurno = 1;
        while (true) {
            InterfazJuego.iniciarTurno(numeroTurno);
            System.out.println("\nINICIO DEL TURNO #" + numeroTurno);

            if (!juego.dinamicaTurno()) {
                resultado = juego.hayVictoria() ? "Victoria" : "Derrota";
                if (!juego.hayVictoria()) {
                    System.out.println("\nFin de la partida: Los jugadores han sido derrotados.");
                }
                break;
            }

            InterfazJuego.pregunta("turno", "\n¿Desean continuar al siguiente turno? (s/n): ");
            if (scanner.nextLine().trim().equalsIgnoreCase("n")) {
                resultado = "Partida finalizada";
                System.out.println("Partida pausada/finalizada por decisión de los jugadores.");
                break;
            }
            numeroTurno++;
        }
        InterfazJuego.finalizar(resultado);
    }

    private static int elegirCantidad(Scanner scanner) {
        System.out.println("██████   █████  ███╗  ██╗██████  ███████╗███╗   ███╗██╗ ██████");
        System.out.println("██   ██ ██   ██ ████╗ ██║██   ██ ██      ████╗ ████║██║██     ");
        System.out.println("██████  ███████ ██╔██╗██║██   ██ █████   ██╔████╔██║██║██     ");
        System.out.println("██      ██   ██ ██║╚████║██   ██ ██      ██║╚██╔╝██║██║██     ");
        System.out.println("██      ██   ██ ██║ ╚███║██████  ███████╗██║ ╚═╝ ██║██║ ██████");
        System.out.println();

        int cantidadJugadores = 0;
        while (!LogicaJuego.cantidadJugadoresValida(cantidadJugadores)) {
            System.out.print("Ingrese la cantidad de jugadores para la partida (" + LogicaJuego.RANGO_JUGADORES + "): ");
            if (scanner.hasNextInt()) {
                cantidadJugadores = scanner.nextInt();
                scanner.nextLine();
                if (!LogicaJuego.cantidadJugadoresValida(cantidadJugadores)) {
                    System.out.println("Número fuera de rango. Cantidad permitida: " + LogicaJuego.RANGO_JUGADORES + ".");
                }
            } else {
                scanner.nextLine();
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
        return cantidadJugadores;
    }
}
