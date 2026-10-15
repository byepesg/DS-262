package juego;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("██████   █████  ███╗  ██╗██████  ███████╗███╗   ███╗██╗ ██████");
        System.out.println("██   ██ ██   ██ ████╗ ██║██   ██ ██      ████╗ ████║██║██     ");
        System.out.println("██████  ███████ ██╔██╗██║██   ██ █████   ██╔████╔██║██║██     ");
        System.out.println("██      ██   ██ ██║╚████║██   ██ ██      ██║╚██╔╝██║██║██     ");
        System.out.println("██      ██   ██ ██║ ╚███║██████  ███████╗██║ ╚═╝ ██║██║ ██████");
        System.out.println();

        LogicaJuego juego = new LogicaJuego();
        juego.imprimirEstadoMapa();
        

        boolean juegoActivo = true;
        int numeroTurno = 1;

        while (juegoActivo) {
            System.out.println("\n******************************************");
            System.out.println("          INICIO DEL TURNO #" + numeroTurno);
            System.out.println("******************************************");

            // dinamicaTurno() retorna false cuando el mazo de jugador se agota (Derrota)
            boolean juegoContinua = juego.dinamicaTurno();

            if (!juegoContinua) {
                juegoActivo = false;
                System.out.println("\nFin de la partida: Los jugadores han sido derrotados.");
                break;
            }

            System.out.print("\n¿Desean continuar al siguiente turno? (s/n): ");
            String respuesta = scanner.nextLine().trim();
            if (respuesta.equalsIgnoreCase("n")) {
                juegoActivo = false;
                System.out.println("Partida pausada/finalizada por decisión de los jugadores.");
            }

            numeroTurno++;
        }

        scanner.close();
    }
}