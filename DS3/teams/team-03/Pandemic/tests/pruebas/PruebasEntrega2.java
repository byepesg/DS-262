package pruebas;

import estructurasDatos.AVL;
import estructurasDatos.FixedArrayStack;
import estructurasDatos.SinglyLinkedListQueue;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import juego.LogicaJuego;
import juego.mazo.MazoInfeccion;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.CartaInfeccion;

public class PruebasEntrega2 {
    private static int total;
    private static int aprobadas;

    public static void main(String[] args) {
        prueba("Pila LIFO, desbordamiento y extracción vacía", PruebasEntrega2::pila);
        prueba("Cola FIFO y reutilización después de vaciarla", PruebasEntrega2::cola);
        prueba("AVL balanceado con claves insertadas en orden", PruebasEntrega2::avl);
        prueba("Grafo conexo de 48 ciudades y 93 conexiones", PruebasEntrega2::grafo);
        prueba("Búsqueda y recorrido ordenado de ciudades", PruebasEntrega2::directorio);
        prueba("Configuración inicial y reparto para 2 o 3 jugadores", PruebasEntrega2::inicio);
        prueba("Rechazo de cantidades de jugadores fuera de rango", PruebasEntrega2::limiteJugadores);
        prueba("Descarte de infección con retirada por índice", PruebasEntrega2::descarte);
        prueba("Reordenamiento de las seis cartas de Pronóstico", PruebasEntrega2::pronostico);
        prueba("Cadena de dos brotes sin repetir ciudad", PruebasEntrega2::brotes);
        prueba("Límite y traslado de centros de investigación", PruebasEntrega2::centros);
        prueba("Victoria al registrar las cuatro curas", PruebasEntrega2::curas);
        System.out.printf("Resultado: %d/%d%n", aprobadas, total);
        if (aprobadas != total) {
            System.exit(1);
        }
    }

    private static void prueba(String nombre, Runnable caso) {
        total++;
        PrintStream salida = System.out;
        try (PrintStream silencio = new PrintStream(OutputStream.nullOutputStream())) {
            System.setOut(silencio);
            caso.run();
            aprobadas++;
            salida.println("[OK] " + nombre);
        } catch (AssertionError | RuntimeException error) {
            salida.println("[FALLA] " + nombre + " — " + error.getMessage());
        } finally {
            System.setOut(salida);
        }
    }

    private static void exigir(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    private static void pila() {
        FixedArrayStack<Integer> pila = new FixedArrayStack<>(2);
        pila.push(10);
        pila.push(20);
        try {
            pila.push(30);
            throw new AssertionError("Se esperaba un error de capacidad.");
        } catch (IllegalStateException esperado) {
            exigir(pila.isFull(), "La pila debe seguir llena.");
        }
        exigir(pila.pop() == 20 && pila.pop() == 10 && pila.pop() == null, "Orden LIFO incorrecto.");
    }

    private static void cola() {
        SinglyLinkedListQueue<Integer> cola = new SinglyLinkedListQueue<>();
        cola.enqueue(10);
        cola.enqueue(20);
        exigir(cola.dequeue() == 10 && cola.dequeue() == 20 && cola.dequeue() == null, "Orden FIFO incorrecto.");
        cola.enqueue(30);
        exigir(cola.dequeue() == 30 && cola.estaVacia(), "La cola vacía no se reutilizó correctamente.");
    }

    private static void avl() {
        AVL<Integer, Integer> arbol = new AVL<>();
        Integer[] orden = new Integer[1023];
        for (int i = 0; i < orden.length; i++) {
            arbol.insertar(i, i);
        }
        exigir(arbol.obtenerAlturaArbol() == 9, "Altura inesperada para 1023 claves consecutivas.");
        exigir(arbol.inOrder(orden) == orden.length, "Recorrido incompleto.");
        for (int i = 0; i < orden.length; i++) {
            exigir(orden[i] == i, "Posición fuera de orden: " + i);
        }
        exigir(arbol.buscar(670) == 670 && arbol.buscar(-1) == null, "Búsqueda AVL incorrecta.");
    }

    private static void grafo() {
        LogicaJuego juego = new LogicaJuego(2);
        Ciudad[] ciudades = juego.obtenerTableroCiudades();
        int conexionesOrientadas = 0;
        for (Ciudad ciudad : ciudades) {
            for (Ciudad vecina : ciudad.obtenerVecinos()) {
                exigir(ciudad != vecina && vecina.esVecino(ciudad), "Bucle o arista asimétrica.");
                conexionesOrientadas++;
            }
        }
        Set<Ciudad> visitadas = new HashSet<>();
        ArrayDeque<Ciudad> pendientes = new ArrayDeque<>();
        visitadas.add(ciudades[0]);
        pendientes.add(ciudades[0]);
        while (!pendientes.isEmpty()) {
            for (Ciudad vecina : pendientes.remove().obtenerVecinos()) {
                if (visitadas.add(vecina)) {
                    pendientes.add(vecina);
                }
            }
        }
        exigir(ciudades.length == 48 && visitadas.size() == 48 && conexionesOrientadas == 186,
                "Cantidad de ciudades, conexiones o componente conexa incorrecta.");
    }

    private static void directorio() {
        LogicaJuego juego = new LogicaJuego(2);
        Ciudad atlanta = juego.buscarCiudad("  AtLaNtA  ");
        exigir(atlanta != null && juego.buscarCiudad("Ciudad inexistente") == null, "Búsqueda por nombre incorrecta.");
        Set<String> vecinas = new HashSet<>();
        for (Ciudad ciudad : atlanta.obtenerVecinos()) {
            vecinas.add(ciudad.obtenerNombre());
        }
        exigir(vecinas.equals(Set.of("Chicago", "Miami", "Washington")), "Vecinas de Atlanta incorrectas.");
        Ciudad[] ciudades = juego.obtenerCiudadesOrdenadas();
        for (int i = 1; i < ciudades.length; i++) {
            String previa = ciudades[i - 1].obtenerNombre().toLowerCase();
            String actual = ciudades[i].obtenerNombre().toLowerCase();
            exigir(previa.compareTo(actual) < 0, "Listado de ciudades fuera de orden.");
        }
    }

    private static void inicio() {
        for (int cantidad : new int[] {2, 3}) {
            LogicaJuego juego = new LogicaJuego(cantidad);
            Personaje[] equipo = juego.obtenerJugadores();
            Set<String> roles = new HashSet<>();
            int cartas = cantidad == 2 ? 4 : 3;
            for (Personaje jugador : equipo) {
                exigir(jugador.obtenerCiudadActual() == juego.buscarCiudad("Atlanta"), "Peón inicial fuera de Atlanta.");
                exigir(jugador.obtenerNumCartas() == cartas, "Reparto inicial incorrecto.");
                exigir(roles.add(jugador.obtenerNombre()), "Rol repetido.");
            }
            long infectadas = Arrays.stream(juego.obtenerTableroCiudades())
                    .filter(c -> c.obtenerCubosDeInfeccion() > 0).count();
            int cubos = Arrays.stream(juego.obtenerTableroCiudades()).mapToInt(Ciudad::obtenerCubosDeInfeccion).sum();
            exigir(equipo.length == cantidad && infectadas == 9 && cubos == 18, "Estado inicial incorrecto.");
        }
    }

    private static void limiteJugadores() {
        for (int cantidad : new int[] {1, 4, 7}) {
            try {
                new LogicaJuego(cantidad);
                throw new AssertionError("El motor admitió " + cantidad + " jugadores.");
            } catch (IllegalArgumentException esperado) {
                exigir(esperado.getMessage().contains("2 a 3"), "El mensaje debe indicar el rango de la entrega.");
            }
        }
    }

    private static void descarte() {
        MazoInfeccion mazo = new LogicaJuego(2).obtenerMazoInfeccion();
        CartaInfeccion primera = mazo.obtenerDescarte(0);
        CartaInfeccion segunda = mazo.obtenerDescarte(1);
        CartaInfeccion tercera = mazo.obtenerDescarte(2);
        exigir(mazo.retirarDeDescartes(1) == segunda && mazo.obtenerDescarte(1) == tercera,
                "La retirada por índice no compactó el arreglo.");
        exigir(mazo.extraerCartaDescarteFIFO() == primera && mazo.obtenerNumDescartes() == 7,
                "El primer descarte no se extrajo en orden FIFO.");
    }

    private static void pronostico() {
        MazoInfeccion mazo = new LogicaJuego(2).obtenerMazoInfeccion();
        CartaInfeccion[] seis = mazo.extraerSuperiores(6);
        exigir(seis.length == 6, "Se esperaban seis cartas.");
        CartaInfeccion[] orden = new CartaInfeccion[seis.length];
        for (int i = 0; i < seis.length; i++) {
            orden[i] = seis[seis.length - 1 - i];
        }
        mazo.devolverAlTope(orden);
        exigir(Arrays.equals(orden, mazo.extraerSuperiores(6)), "El nuevo orden del tope no se conservó.");
    }

    private static void brotes() {
        LogicaJuego juego = new LogicaJuego(2);
        for (Ciudad ciudad : juego.obtenerTableroCiudades()) {
            while (ciudad.obtenerCubosDeInfeccion() > 0) {
                ciudad.quitarCuboDeInfeccion();
            }
        }
        Ciudad pekin = juego.buscarCiudad("Pekin");
        Ciudad seul = juego.buscarCiudad("Seul");
        for (int i = 0; i < 3; i++) {
            pekin.agregarCuboDeInfeccion();
            seul.agregarCuboDeInfeccion();
        }
        juego.infectarCiudad(pekin, 1);
        exigir(juego.obtenerContadorBrotes() == 2, "La cadena debe producir exactamente dos brotes.");
        exigir(juego.buscarCiudad("Shangai").obtenerCubosDeInfeccion() == 2
                && juego.buscarCiudad("Tokio").obtenerCubosDeInfeccion() == 1,
                "Propagación incorrecta entre los vecinos de Pekín y Seúl.");
    }

    private static void centros() {
        LogicaJuego juego = new LogicaJuego(2);
        Ciudad atlanta = juego.buscarCiudad("Atlanta");
        exigir(atlanta.tenerCentroDeInvestigacion() && juego.obtenerCentrosConstruidos() == 1,
                "Falta el centro inicial de Atlanta.");
        for (String nombre : List.of("Chicago", "Miami", "Washington", "Londres", "Madrid")) {
            exigir(juego.registrarCentroDeInvestigacion(juego.buscarCiudad(nombre)), "No se construyó el centro en " + nombre);
        }
        Ciudad paris = juego.buscarCiudad("Paris");
        exigir(!juego.registrarCentroDeInvestigacion(paris), "Se admitió un séptimo centro.");
        exigir(juego.moverCentroDeInvestigacion(atlanta, paris) && paris.tenerCentroDeInvestigacion()
                && !atlanta.tenerCentroDeInvestigacion() && juego.obtenerCentrosConstruidos() == 6,
                "Traslado de centro incorrecto.");
    }

    private static void curas() {
        LogicaJuego juego = new LogicaJuego(2);
        for (int i = 0; i < 3; i++) {
            juego.registrarCura(i);
            exigir(!juego.hayVictoria(), "Hubo victoria antes de registrar las cuatro curas.");
        }
        juego.registrarCura(3);
        exigir(juego.hayVictoria(), "La cuarta cura debe producir la victoria.");
    }
}
