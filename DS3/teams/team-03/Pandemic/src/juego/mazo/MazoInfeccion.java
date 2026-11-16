package juego.mazo;

import estructurasDatos.FixedArrayStack;
import mundo.Ciudad;
import mundo.cartas.CartaInfeccion;

public class MazoInfeccion {

    private final FixedArrayStack<CartaInfeccion> mazo;
    private final CartaInfeccion[] descarte;
    private int numDescartes;

    public MazoInfeccion(Ciudad[] ciudades) {
        this.mazo = new FixedArrayStack<>(ciudades.length);
        this.descarte = new CartaInfeccion[ciudades.length];
        this.numDescartes = 0;

        CartaInfeccion[] cartas = new CartaInfeccion[ciudades.length];
        for (int i = 0; i < ciudades.length; i++) {
            cartas[i] = new CartaInfeccion(ciudades[i]);
        }

        // Barajar mazo de infección
        for (int i = cartas.length - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            CartaInfeccion temp = cartas[i];
            cartas[i] = cartas[j];
            cartas[j] = temp;
        }

        for (CartaInfeccion c : cartas) {
            this.mazo.push(c);
        }
    }

    public void prepararInfeccionInicial() {
        System.out.println("\n--- PREPARANDO INFECCIÓN INICIAL DEL TABLERO ---");
        int[] cubosPorGrupo = {3, 2, 1};

        for (int cubos : cubosPorGrupo) {
            for (int i = 0; i < 3; i++) {
                CartaInfeccion robada = robarCarta();
                if (robada != null) {
                    Ciudad ciudad = robada.obtenerCiudad();
                    for (int c = 0; c < cubos; c++) {
                        ciudad.agregarCuboDeInfeccion();
                    }
                    System.out.println("Infección inicial: " + ciudad.obtenerNombre()
                            + " recibe " + cubos + " cubo(s).");
                }
            }
        }
    }

    public CartaInfeccion robarCarta() {
        if (mazo.isEmpty()) {
            return null;
        }
        CartaInfeccion robada = mazo.pop();
        agregarADescartes(robada);
        return robada;
    }

    public void agregarADescartes(CartaInfeccion carta) {
        if (carta != null && numDescartes < descarte.length) {
            descarte[numDescartes++] = carta;
        }
    }

    public CartaInfeccion extraerCartaDescarteFIFO() {
        return retirarDeDescartes(0);
    }

    /**
     * Paso 3 de Epidemia (Intensificar): baraja todas las cartas en la pila de descartes,
     * las apila encima del mazo de infección mediante push y vacía el descarte.
     */
    public int reciclarDescartesAlMazo() {
        if (numDescartes == 0) {
            return 0;
        }

        // Barajar únicamente la porción ocupada del arreglo de descartes
        for (int i = numDescartes - 1; i > 0; i--) {
            int j = (int) (Math.random() * (i + 1));
            CartaInfeccion temp = descarte[i];
            descarte[i] = descarte[j];
            descarte[j] = temp;
        }

        int totalRecicladas = numDescartes;
        for (int i = 0; i < totalRecicladas; i++) {
            mazo.push(descarte[i]);
            descarte[i] = null;
        }
        numDescartes = 0;

        return totalRecicladas;
    }

    // ==========================================
    // MÉTODOS DE APOYO PARA EVENTOS
    // ==========================================

    /**
     * Extrae hasta 'cantidad' cartas desde el tope del mazo de infección
     * sin enviarlas al descarte (usado por Pronóstico).
     */
    public CartaInfeccion[] extraerSuperiores(int cantidad) {
        CartaInfeccion[] temporal = new CartaInfeccion[cantidad];
        int extraidas = 0;

        while (extraidas < cantidad && !mazo.isEmpty()) {
            temporal[extraidas++] = mazo.pop();
        }

        CartaInfeccion[] resultado = new CartaInfeccion[extraidas];
        for (int i = 0; i < extraidas; i++) {
            resultado[i] = temporal[i];
        }
        return resultado;
    }

    /**
     * Devuelve un arreglo de cartas al tope del mazo de infección.
     * La posición 0 del arreglo quedará en el tope (será la primera en robarse).
     */
    public void devolverAlTope(CartaInfeccion[] cartasOrdenadas) {
        if (cartasOrdenadas == null) {
            return;
        }
        for (int i = cartasOrdenadas.length - 1; i >= 0; i--) {
            if (cartasOrdenadas[i] != null) {
                mazo.push(cartasOrdenadas[i]);
            }
        }
    }

    /**
     * Retira y devuelve la carta ubicada en 'indice' dentro del arreglo de descartes,
     * compactando las posiciones restantes hacia la izquierda.
     */
    public CartaInfeccion retirarDeDescartes(int indice) {
        if (indice < 0 || indice >= numDescartes) {
            return null;
        }
        CartaInfeccion retirada = descarte[indice];
        for (int i = indice; i < numDescartes - 1; i++) {
            descarte[i] = descarte[i + 1];
        }
        descarte[--numDescartes] = null;
        return retirada;
    }

    public int obtenerNumDescartes() {
        return numDescartes;
    }

    public CartaInfeccion obtenerDescarte(int indice) {
        if (indice >= 0 && indice < numDescartes) {
            return descarte[indice];
        }
        return null;
    }
}