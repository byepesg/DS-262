package mundo;

public class Ciudad {

    private final String nombre;
    private final String color;
    private int cubosDeInfeccion;
    private boolean tieneCentroDeInvestigacion;

    private final Ciudad[] vecinos;
    private int numVecinos;

    private final Personaje[] personajes;
    private int numPersonajes;

    public Ciudad(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
        this.cubosDeInfeccion = 0;
        this.tieneCentroDeInvestigacion = false;
        this.vecinos = new Ciudad[10];
        this.numVecinos = 0;
        this.personajes = new Personaje[7];
        this.numPersonajes = 0;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public String obtenerColor() {
        return color;
    }

    public int obtenerCubosDeInfeccion() {
        return cubosDeInfeccion;
    }

    /**
     * Agrega 1 cubo de infección. Si ya tiene 3, retorna true para detonar un brote.
     */
    public boolean agregarCuboDeInfeccion() {
        if (cubosDeInfeccion < 3) {
            cubosDeInfeccion++;
            return false;
        }
        return true;
    }

    public void quitarCuboDeInfeccion() {
        if (cubosDeInfeccion > 0) {
            cubosDeInfeccion--;
        }
    }

    public boolean tenerCentroDeInvestigacion() {
        return tieneCentroDeInvestigacion;
    }

    public void construirCentroDeInvestigacion() {
        this.tieneCentroDeInvestigacion = true;
    }

    public void removerCentroDeInvestigacion() {
        this.tieneCentroDeInvestigacion = false;
    }

    public void agregarVecino(Ciudad vecino) {
        if (vecino != null && !esVecino(vecino) && numVecinos < vecinos.length) {
            vecinos[numVecinos++] = vecino;
        }
    }

    public void establecerVecinos(Ciudad[] listaVecinos) {
        if (listaVecinos == null) return;
        for (Ciudad v : listaVecinos) {
            agregarVecino(v);
        }
    }

    public Ciudad[] obtenerVecinos() {
        Ciudad[] resultado = new Ciudad[numVecinos];
        for (int i = 0; i < numVecinos; i++) {
            resultado[i] = vecinos[i];
        }
        return resultado;
    }

    public boolean esVecino(Ciudad ciudad) {
        if (ciudad == null) return false;
        for (int i = 0; i < numVecinos; i++) {
            if (vecinos[i] == ciudad) {
                return true;
            }
        }
        return false;
    }

    public void agregarPersonaje(Personaje personaje) {
        if (personaje != null && numPersonajes < personajes.length) {
            personajes[numPersonajes++] = personaje;
        }
    }

    public void removerPersonaje(Personaje personaje) {
        for (int i = 0; i < numPersonajes; i++) {
            if (personajes[i] == personaje) {
                for (int j = i; j < numPersonajes - 1; j++) {
                    personajes[j] = personajes[j + 1];
                }
                personajes[--numPersonajes] = null;
                return;
            }
        }
    }

    public Personaje[] obtenerPersonajes() {
        Personaje[] resultado = new Personaje[numPersonajes];
        for (int i = 0; i < numPersonajes; i++) {
            resultado[i] = personajes[i];
        }
        return resultado;
    }
}