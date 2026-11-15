package mundo;

/**
 * Representa una entidad de ciudad en el tablero de juego.
 * Gestiona sus conexiones (vecinos), cubos de infección y jugadores presentes.
 */
public class Ciudad {

    private final String nombre;
    private final String color;
    private int cubosDeInfeccion;
    private boolean centroDeInvestigacion;

    // Relaciones del mapa y control de personajes
    private Ciudad[] vecinos;
    private int numVecinos;
    private Personaje[] jugadoresEnCiudad;
    private int numJugadores;

    public Ciudad(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.cubosDeInfeccion = 0;
        this.centroDeInvestigacion = false;

        // Capacidad inicial para conexiones y jugadores en la ciudad
        this.vecinos = new Ciudad[6];
        this.numVecinos = 0;
        this.jugadoresEnCiudad = new Personaje[3];
        this.numJugadores = 0;
    }

    // Getters y modificadores básicos
    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerColor() {
        return color;
    }

    public int obtenerCubosDeInfeccion() {
        return cubosDeInfeccion;
    }

    public boolean tenerCentroDeInvestigacion() {
        return centroDeInvestigacion;
    }

    public void agregarCuboDeInfeccion() {
        if (cubosDeInfeccion < 3) {
            cubosDeInfeccion++;
        }
    }

    public void quitarCuboDeInfeccion() {
        if (cubosDeInfeccion > 0) {
            cubosDeInfeccion--;
        }
    }

    public void construirCentroDeInvestigacion() {
        centroDeInvestigacion = true;
    }

    // Gestión de vecinos (Grafo de adyacencia)
    public void agregarVecino(Ciudad vecino) {
        if (numVecinos < vecinos.length) {
            vecinos[numVecinos] = vecino;
            numVecinos++;
        }
    }

    public boolean esVecino(Ciudad otraCiudad) {
        for (int i = 0; i < numVecinos; i++) {
            if (vecinos[i].obtenerNombre().equals(otraCiudad.obtenerNombre())) {
                return true;
            }
        }
        return false;
    }

    public Ciudad[] obtenerVecinos() {
        Ciudad[] resultado = new Ciudad[numVecinos];
        for (int i = 0; i < numVecinos; i++) {
            resultado[i] = vecinos[i];
        }
        return resultado;
    }

    // Registro de presencia de personajes
    public void agregarPersonaje(Personaje personaje) {
        if (numJugadores < jugadoresEnCiudad.length) {
            jugadoresEnCiudad[numJugadores] = personaje;
            numJugadores++;
        }
    }

    public void removerPersonaje(Personaje personaje) {
        int indice = -1;
        for (int i = 0; i < numJugadores; i++) {
            if (jugadoresEnCiudad[i] == personaje) {
                indice = i;
                break;
            }
        }
        if (indice != -1) {
            for (int i = indice; i < numJugadores - 1; i++) {
                jugadoresEnCiudad[i] = jugadoresEnCiudad[i + 1];
            }
            jugadoresEnCiudad[numJugadores - 1] = null;
            numJugadores--;
        }
    }

    public void imprimirCiudad() {
        System.out.println("Ciudad: " + nombre + " | Color: " + color 
                + " | Cubos: " + cubosDeInfeccion + " | Centro: " + centroDeInvestigacion);
    }
}