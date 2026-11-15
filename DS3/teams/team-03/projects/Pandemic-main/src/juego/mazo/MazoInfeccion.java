package juego.mazo;

import datosObjetos.InfoCartasInfeccion;
import estructurasDatos.FixedArrayStack;
import estructurasDatos.SinglyLinkedListQueue;
import mundo.Ciudad;
import mundo.cartas.CartaInfeccion;

/**
 * Administra el mazo principal de infección utilizando la pila de capacidad fija
 * FixedArrayStack y una cola SinglyLinkedListQueue para el mazo de descarte.
 */
public class MazoInfeccion {

    private final FixedArrayStack<CartaInfeccion> mazoPrincipal;
    private final SinglyLinkedListQueue<CartaInfeccion> colaDescarte;

    public MazoInfeccion(Ciudad[] ciudades) {
        CartaInfeccion[] creadas = InfoCartasInfeccion.crearCartas(ciudades);
        
        // 1. Barajar el arreglo inicial de cartas
        barajarArreglo(creadas);

        // 2. Cargar en la estructura FixedArrayStack
        this.mazoPrincipal = new FixedArrayStack<>(creadas.length);
        for (CartaInfeccion carta : creadas) {
            this.mazoPrincipal.push(carta);
        }

        this.colaDescarte = new SinglyLinkedListQueue<>();
    }

    private void barajarArreglo(CartaInfeccion[] arreglo) {
        for (int i = arreglo.length - 1; i > 0; i--) {
            int aleatorio = (int) (Math.random() * (i + 1));
            CartaInfeccion temp = arreglo[i];
            arreglo[i] = arreglo[aleatorio];
            arreglo[aleatorio] = temp;
        }
    }

    public CartaInfeccion robarCarta() {
        if (!mazoPrincipal.isEmpty()) {
            CartaInfeccion robada = mazoPrincipal.pop();
            colaDescarte.enqueue(robada); // Encola automáticamente en el descarte de infecciones.
            return robada;
        }
        return null;
    }

    public CartaInfeccion extraerCartaDescarteFIFO() {
        return colaDescarte.dequeue();
    }

    public void prepararInfeccionInicial() {
        for (int cantidadCubos = 3; cantidadCubos >= 1; cantidadCubos--) {
            for (int i = 0; i < 3; i++) {
                CartaInfeccion robada = robarCarta();
                if (robada != null) {
                    Ciudad c = robada.obtenerCiudad();
                    for (int k = 0; k < cantidadCubos; k++) {
                        c.agregarCuboDeInfeccion();
                    }
                }
            }
        }
    }

    public int cartasRestantes() {
        return mazoPrincipal.obtenerTamano();
    }
}