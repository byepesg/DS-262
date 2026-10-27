package mundo;

import juego.LogicaJuego;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;
import java.util.Scanner;

/**
 * Representa al jugador en la partida. Administra su posición, mano (FixedArray de 8)
 * y la ejecución interactiva de sus 4 acciones por turno mediante consola.
 */
public class Personaje {

    private final String nombre;
    private Ciudad ciudadActual;
    private final Carta[] mano;
    private int numCartas;
    private int accionesDisponibles;
    private final Scanner scanner;

    public Personaje(String nombre, Ciudad ciudadInicial) {
        this.nombre = nombre;
        this.ciudadActual = ciudadInicial;
        this.mano = new Carta[8]; // FixedArray de capacidad 8
        this.numCartas = 0;
        this.accionesDisponibles = 0;
        this.scanner = new Scanner(System.in);
    }

    public String obtenerNombre() {
        return nombre;
    }

    public Ciudad obtenerCiudadActual() {
        return ciudadActual;
    }

    public void establecerCiudadActual(Ciudad ciudadActual) {
        this.ciudadActual = ciudadActual;
    }

    public Carta[] obtenerMano() {
        return mano;
    }

    public int obtenerNumCartas() {
        return numCartas;
    }

    // ==========================================
    // GESTIÓN DE LA MANO 
    // ==========================================

    public void agarrarCarta(Carta nuevaCarta) {
        if (nuevaCarta == null) return;
        mano[numCartas] = nuevaCarta;
        numCartas++;
        
        while (numCartas > 7) {
            descartarCarta();
        }
    }

    public void descartarCarta() {
        System.out.println("\n------------------------------------------");
        System.out.println("¡Mano llena para " + nombre + "! (Límite: 7 cartas, tienes " + numCartas + ")");
        imprimirMano();
        
        int indiceAEliminar = -1;
        while (indiceAEliminar < 0 || indiceAEliminar >= numCartas) {
            System.out.print("Ingrese el índice de la carta que desea descartar (0 a " + (numCartas - 1) + "): ");
            if (scanner.hasNextInt()) {
                indiceAEliminar = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
            }
        }
        
        System.out.println("Descartaste: " + mano[indiceAEliminar].obtenerNombre());
        removerCartaPorIndice(indiceAEliminar);
    }

    public void removerCartaPorIndice(int indice) {
        if (indice >= 0 && indice < numCartas) {
            for (int i = indice; i < numCartas - 1; i++) {
                mano[i] = mano[i + 1];
            }
            mano[numCartas - 1] = null;
            numCartas--;
        }
    }

    public void imprimirMano() {
        System.out.println("Mano actual de " + nombre + " (" + numCartas + "/7):");
        for (int i = 0; i < numCartas; i++) {
            System.out.println("  [" + i + "] " + mano[i].obtenerNombre() + " (" + mano[i].obtenerTipo() + ")");
        }
    }

    // ==========================================
    // ACCIONES DISPONIBLES
    // ==========================================

    public boolean normalMove(Ciudad ciudadDestino, LogicaJuego juego) {
        if (ciudadActual.esVecino(ciudadDestino)) {
            juego.cambiarUbicacion(this, ciudadDestino);
            return true;
        }
        System.out.println("Movimiento inválido: La ciudad no es adyacente.");
        return false;
    }

    public boolean destinyDirectMove(int indiceCartaDestino, LogicaJuego juego) {
        if (indiceCartaDestino >= 0 && indiceCartaDestino < numCartas) {
            Carta c = mano[indiceCartaDestino];
            if (c instanceof CartaCiudad) {
                CartaCiudad cCiudad = (CartaCiudad) c;
                Ciudad destino = cCiudad.obtenerCiudad();
                removerCartaPorIndice(indiceCartaDestino);
                juego.cambiarUbicacion(this, destino);
                return true;
            }
        }
        System.out.println("Carta inválida para vuelo directo.");
        return false;
    }

    public boolean anyDirectMove(int indiceCartaActual, Ciudad ciudadDestino, LogicaJuego juego) {
        if (indiceCartaActual >= 0 && indiceCartaActual < numCartas) {
            Carta c = mano[indiceCartaActual];
            if (c instanceof CartaCiudad) {
                CartaCiudad cCiudad = (CartaCiudad) c;
                if (cCiudad.obtenerNombre().equalsIgnoreCase(ciudadActual.obtenerNombre())) {
                    removerCartaPorIndice(indiceCartaActual);
                    juego.cambiarUbicacion(this, ciudadDestino);
                    return true;
                }
            }
        }
        System.out.println("Para vuelo chárter debes descartar la carta de la ciudad donde te encuentras.");
        return false;
    }

    public boolean tratarEnfermedad() {
        if (ciudadActual.obtenerCubosDeInfeccion() > 0) {
            ciudadActual.quitarCuboDeInfeccion();
            System.out.println("¡Éxito! Se removió 1 cubo en " + ciudadActual.obtenerNombre() 
                    + ". Cubos restantes: " + ciudadActual.obtenerCubosDeInfeccion());
            return true;
        }
        System.out.println("No hay cubos de infección en " + ciudadActual.obtenerNombre() + ".");
        return false;
    }

    // ==========================================
    // EJECUCIÓN INTERACTIVA DEL TURNO
    // ==========================================

    public void ejecutarAccion(LogicaJuego juego) {
        this.accionesDisponibles = 4;

        while (accionesDisponibles > 0) {
            System.out.println("\n------------------------------------------");
            System.out.println("Ubicación actual: " + ciudadActual.obtenerNombre() 
                    + " (Cubos de infección: " + ciudadActual.obtenerCubosDeInfeccion() + ")");
            System.out.println("Acciones restantes en este turno: " + accionesDisponibles);
            imprimirMano();

            System.out.println("\nSeleccione una acción:");
            System.out.println("  1. Mover a ciudad vecina (normalMove)");
            System.out.println("  2. Vuelo directo (destinyDirectMove - descartar carta del destino)");
            System.out.println("  3. Vuelo chárter (anyDirectMove - descartar carta de ciudad actual)");
            System.out.println("  4. Tratar enfermedad (tratarEnfermedad)");
            System.out.println("  5. Pasar / Finalizar acciones restantes");
            System.out.print("Opción (1-5): ");

            int opcion = -1;
            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } else {
                scanner.nextLine();
                System.out.println("Entrada inválida. Ingrese un número entre 1 y 5.");
                continue;
            }

            boolean exitoAccion = false;

            switch (opcion) {
                case 1:
                    exitoAccion = ejecutarMoverVecino(juego);
                    break;
                case 2:
                    exitoAccion = ejecutarVueloDirecto(juego);
                    break;
                case 3:
                    exitoAccion = ejecutarVueloCharter(juego);
                    break;
                case 4:
                    exitoAccion = tratarEnfermedad();
                    break;
                case 5:
                    System.out.println("Has decidido finalizar tus acciones restantes.");
                    accionesDisponibles = 0;
                    return;
                default:
                    System.out.println("Opción fuera de rango (1-5).");
            }

            if (exitoAccion) {
                accionesDisponibles--;
            }
        }
    }

    private boolean ejecutarMoverVecino(LogicaJuego juego) {
        Ciudad[] vecinos = ciudadActual.obtenerVecinos();
        if (vecinos.length == 0) {
            System.out.println("Esta ciudad no tiene conexiones vecinas.");
            return false;
        }

        System.out.println("Ciudades vecinas disponibles:");
        for (int i = 0; i < vecinos.length; i++) {
            System.out.println("  [" + i + "] " + vecinos[i].obtenerNombre());
        }
        System.out.print("Seleccione el índice del destino: ");

        if (scanner.hasNextInt()) {
            int idx = scanner.nextInt();
            scanner.nextLine();
            if (idx >= 0 && idx < vecinos.length) {
                return normalMove(vecinos[idx], juego);
            }
        } else {
            scanner.nextLine();
        }
        System.out.println("Índice de vecino inválido.");
        return false;
    }

    private boolean ejecutarVueloDirecto(LogicaJuego juego) {
        if (numCartas == 0) {
            System.out.println("No tienes cartas en la mano.");
            return false;
        }
        System.out.print("Ingrese el índice de la Carta Ciudad a descartar para volar a ella: ");
        if (scanner.hasNextInt()) {
            int idx = scanner.nextInt();
            scanner.nextLine();
            return destinyDirectMove(idx, juego);
        }
        scanner.nextLine();
        return false;
    }

    private boolean ejecutarVueloCharter(LogicaJuego juego) {
        int idxCartaActual = obtenerIndiceCartaCiudadActual();
        if (idxCartaActual == -1) {
            System.out.println("No posees la Carta Ciudad de " + ciudadActual.obtenerNombre() + " para realizar vuelo chárter.");
            return false;
        }

        System.out.print("Ingrese el nombre exacto de la ciudad a la cual desea volar: ");
        String nombreDestino = scanner.nextLine().trim();
        Ciudad ciudadDestino = juego.buscarCiudad(nombreDestino);

        if (ciudadDestino != null) {
            return anyDirectMove(idxCartaActual, ciudadDestino, juego);
        } else {
            System.out.println("Ciudad no encontrada en el tablero.");
            return false;
        }
    }

    private int obtenerIndiceCartaCiudadActual() {
        for (int i = 0; i < numCartas; i++) {
            if (mano[i] instanceof CartaCiudad) {
                if (mano[i].obtenerNombre().equalsIgnoreCase(ciudadActual.obtenerNombre())) {
                    return i;
                }
            }
        }
        return -1;
    }
}