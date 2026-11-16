package mundo;

import juego.InterfazJuego;
import juego.LogicaJuego;
import juego.mazo.MazoAuxiliarEvento;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;
import mundo.cartas.CartaEvento;
import mundo.roles.Rol;
import java.util.Scanner;

public class Personaje {

    private final Rol rol;
    private Ciudad ciudadActual;
    private final Carta[] mano;
    private int numCartas;
    private static final int LIMITE_MANO = 7;
    private final Scanner scanner;
    private MazoAuxiliarEvento mazoAuxiliarEvento;

    public Personaje(Rol rol, Ciudad ciudadInicial) {
        this.rol = rol;
        this.ciudadActual = ciudadInicial;
        this.mano = new Carta[LIMITE_MANO + 1];
        this.numCartas = 0;
        this.scanner = InterfazJuego.entrada();
    }

    public void establecerMazoAuxiliarEvento(MazoAuxiliarEvento mazoAuxiliarEvento) {
        this.mazoAuxiliarEvento = mazoAuxiliarEvento;
    }

    public String obtenerNombre() {
        return rol.obtenerNombre();
    }

    public Rol obtenerRol() {
        return rol;
    }

    public Ciudad obtenerCiudadActual() {
        return ciudadActual;
    }

    public void establecerCiudadActual(Ciudad ciudadActual) {
        this.ciudadActual = ciudadActual;
    }

    public Scanner obtenerScanner() {
        return scanner;
    }

    public Carta[] obtenerMano() {
        return mano;
    }

    public int obtenerNumCartas() {
        return numCartas;
    }

    public void agarrarCarta(Carta carta) {
        if (carta == null) {
            return;
        }
        mano[numCartas++] = carta;

        while (numCartas > LIMITE_MANO) {
            System.out.println("\n" + obtenerNombre() + " supera el límite de " + LIMITE_MANO + " cartas.");
            imprimirMano();
            InterfazJuego.preguntaPara(this, "lista", "Elige el índice de la carta a descartar: ");
            if (scanner.hasNextInt()) {
                int idx = scanner.nextInt();
                scanner.nextLine();
                if (idx >= 0 && idx < numCartas) {
                    System.out.println("Carta descartada: " + mano[idx].obtenerNombre());
                    removerCartaPorIndice(idx);
                } else {
                    System.out.println("Índice fuera de rango.");
                }
            } else {
                scanner.nextLine();
                System.out.println("Entrada inválida.");
            }
        }
    }

    public void removerCartaPorIndice(int indice) {
        if (indice < 0 || indice >= numCartas) {
            return;
        }
        Carta cartaRemovida = mano[indice];

        for (int i = indice; i < numCartas - 1; i++) {
            mano[i] = mano[i + 1];
        }
        mano[--numCartas] = null;

        if (mazoAuxiliarEvento != null
                && (cartaRemovida instanceof CartaEvento
                || "Evento".equalsIgnoreCase(cartaRemovida.obtenerTipo()))) {
            mazoAuxiliarEvento.eliminarEvento(cartaRemovida);
        }
    }

    public boolean descartarCartasDeCiudad(String color, int cantidad) {
        int[] indices = new int[cantidad];
        int encontradas = 0;

        for (int i = 0; i < numCartas && encontradas < cantidad; i++) {
            if (mano[i] instanceof CartaCiudad) {
                CartaCiudad cc = (CartaCiudad) mano[i];
                if (cc.obtenerCiudad().obtenerColor().equalsIgnoreCase(color)) {
                    indices[encontradas++] = i;
                }
            }
        }

        if (encontradas < cantidad) {
            return false;
        }

        for (int i = cantidad - 1; i >= 0; i--) {
            removerCartaPorIndice(indices[i]);
        }
        return true;
    }

    public void imprimirMano() {
        System.out.println("Mano de " + obtenerNombre() + " (" + numCartas + "/" + LIMITE_MANO + "):");
        if (numCartas == 0) {
            System.out.println("  (Sin cartas)");
            return;
        }
        for (int i = 0; i < numCartas; i++) {
            InterfazJuego.opcion(i, mano[i].obtenerNombre() + " [" + mano[i].obtenerTipo() + "]");
        }
    }

    public void ejecutarAccion(LogicaJuego juego) {
        int accionesDisponibles = 4;
        while (accionesDisponibles > 0 && !juego.hayVictoria()) {
            System.out.println("\n--- Acciones restantes: " + accionesDisponibles + " | Ubicación: "
                    + ciudadActual.obtenerNombre() + " ---");
            InterfazJuego.accionesRestantes(accionesDisponibles);
            if (!InterfazJuego.esGrafica()) {
                imprimirMano();
            }

            int resultado = rol.ejecutarMenu(this, juego);
            if (resultado == -1) {
                break;
            } else if (resultado > 0) {
                accionesDisponibles -= resultado;
            }
        }
    }
}