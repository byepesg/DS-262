package juego;

import datosObjetos.Ciudades;
import datosObjetos.Roles;
import estructurasDatos.SinglyLinkedListQueue;
import estructurasDatos.AVL;
import juego.mazo.MazoAuxiliarEvento;
import juego.mazo.MazoInfeccion;
import juego.mazo.MazoJugador;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaEvento;
import mundo.cartas.CartaInfeccion;
import mundo.roles.Rol;

public class LogicaJuego {

    static final int MIN_JUGADORES = 2;
    static final int MAX_JUGADORES = 3;
    static final String RANGO_JUGADORES = MIN_JUGADORES + " a " + MAX_JUGADORES;

    private final Ciudad[] tableroCiudades;
    private final AVL<String, Ciudad> directorioCiudades;
    private final Personaje[] jugadores;
    private int indiceJugadorActual;

    private final MazoInfeccion mazoInfeccion;
    private final MazoJugador mazoJugador;
    private final MazoAuxiliarEvento mazoAuxiliarEvento;

    private int tasaInfeccion;
    private final Ciudad atlanta;

    private static final int MAX_CENTROS = 6;
    private int centrosConstruidos;

    // Marcador de brotes: avanza una casilla por brote. Con MAX_BROTES los jugadores pierden.
    private static final int MAX_BROTES = 8;
    private int contadorBrotes;

    public static final String[] COLORES = {"Azul", "Amarillo", "Negro", "Rojo"};
    private final boolean[] curas;
    private boolean nocheTranquila;
    private boolean victoriaAlcanzada;

    public LogicaJuego() {
        this(3);
    }

    static boolean cantidadJugadoresValida(int cantidad) {
        return cantidad >= MIN_JUGADORES && cantidad <= MAX_JUGADORES;
    }

    public LogicaJuego(int numJugadores) {
        if (!cantidadJugadoresValida(numJugadores)) {
            throw new IllegalArgumentException("Cantidad de jugadores permitida: " + RANGO_JUGADORES + ".");
        }
        Rol[] rolesDisponibles = Roles.crearRoles();

        this.tableroCiudades = Ciudades.crearCiudades();
        this.directorioCiudades = new AVL<>();
        for (Ciudad ciudad : this.tableroCiudades) {
            this.directorioCiudades.insertar(ciudad.obtenerNombre().trim().toLowerCase(), ciudad);
        }
        this.atlanta = buscarCiudad("Atlanta");
        this.centrosConstruidos = 0;
        registrarCentroDeInvestigacion(this.atlanta);

        this.curas = new boolean[COLORES.length];
        this.victoriaAlcanzada = false;
        this.mazoAuxiliarEvento = new MazoAuxiliarEvento();

        // Barajar roles y asignar sin repetición
        for (int i = rolesDisponibles.length - 1; i > 0; i--) {
            int aleatorio = (int) (Math.random() * (i + 1));
            Rol temp = rolesDisponibles[i];
            rolesDisponibles[i] = rolesDisponibles[aleatorio];
            rolesDisponibles[aleatorio] = temp;
        }

        this.jugadores = new Personaje[numJugadores];
        for (int i = 0; i < numJugadores; i++) {
            this.jugadores[i] = new Personaje(rolesDisponibles[i], atlanta);
            this.jugadores[i].establecerMazoAuxiliarEvento(this.mazoAuxiliarEvento);
            atlanta.agregarPersonaje(this.jugadores[i]);
            System.out.println("Jugador " + (i + 1) + " -> " + this.jugadores[i].obtenerNombre()
                    + ": " + this.jugadores[i].obtenerRol().obtenerDescripcion());
        }
        this.indiceJugadorActual = 0;

        this.mazoInfeccion = new MazoInfeccion(tableroCiudades);
        this.mazoInfeccion.prepararInfeccionInicial();

        this.mazoJugador = new MazoJugador();
        Carta[] mazoInicial = mazoJugador.crearMazoInicialSinEpidemias(tableroCiudades);
        mazoJugador.barajarArreglo(mazoInicial);
        int cartasUsadas = mazoJugador.repartirManosIniciales(mazoInicial, jugadores, mazoAuxiliarEvento);
        mazoJugador.ensamblarYConvertirAStack(mazoInicial, cartasUsadas, 4);

        this.tasaInfeccion = 2;
        this.contadorBrotes = 0;
        this.nocheTranquila = false;
    }

    // ==========================================
    // GESTIÓN DE CENTROS DE INVESTIGACIÓN (MÁX. 6)
    // ==========================================

    public boolean puedeConstruirCentro() {
        return centrosConstruidos < MAX_CENTROS;
    }

    public boolean registrarCentroDeInvestigacion(Ciudad ciudad) {
        if (ciudad == null || ciudad.tenerCentroDeInvestigacion() || !puedeConstruirCentro()) {
            return false;
        }
        ciudad.construirCentroDeInvestigacion();
        centrosConstruidos++;
        return true;
    }

    public boolean moverCentroDeInvestigacion(Ciudad origen, Ciudad destino) {
        if (origen == null || destino == null
                || !origen.tenerCentroDeInvestigacion()
                || destino.tenerCentroDeInvestigacion()) {
            return false;
        }
        origen.removerCentroDeInvestigacion();
        destino.construirCentroDeInvestigacion();
        return true;
    }

    public Ciudad[] obtenerCiudadesConCentro() {
        Ciudad[] resultado = new Ciudad[centrosConstruidos];
        int idx = 0;
        for (Ciudad c : tableroCiudades) {
            if (c.tenerCentroDeInvestigacion() && idx < resultado.length) {
                resultado[idx++] = c;
            }
        }
        return resultado;
    }

    public int obtenerMaximoCentros() {
        return MAX_CENTROS;
    }

    public int obtenerCentrosConstruidos() {
        return centrosConstruidos;
    }

    // ==========================================
    // MÉTODOS DE BÚSQUEDA, TRASLADO Y PROTECCIÓN
    // ==========================================

    public Ciudad buscarCiudad(String nombre) {
        if (nombre == null) {
            return null;
        }
        return directorioCiudades.buscar(nombre.trim().toLowerCase());
    }

    public void cambiarUbicacion(Personaje personaje, Ciudad ciudadDestino) {
        Ciudad ciudadOrigen = personaje.obtenerCiudadActual();
        if (ciudadOrigen != null) {
            ciudadOrigen.removerPersonaje(personaje);
        }
        personaje.establecerCiudadActual(ciudadDestino);
        ciudadDestino.agregarPersonaje(personaje);
        System.out.println(personaje.obtenerNombre() + " se ha movido a " + ciudadDestino.obtenerNombre());

        personaje.obtenerRol().alEntrarACiudad(personaje, ciudadDestino, this);
    }

    public boolean estaProtegidaDeInfeccion(Ciudad ciudad) {
        for (Personaje p : jugadores) {
            if (p.obtenerRol().protegeCiudad(p, ciudad, this)) {
                return true;
            }
        }
        return false;
    }

    // ==========================================
    // GESTIÓN DE CURAS Y CONDICIÓN DE VICTORIA
    // ==========================================

    public int indiceColor(String color) {
        for (int i = 0; i < COLORES.length; i++) {
            if (COLORES[i].equalsIgnoreCase(color.trim())) {
                return i;
            }
        }
        return -1;
    }

    public boolean estaCurada(int indice) {
        return indice >= 0 && indice < curas.length && curas[indice];
    }

    public void registrarCura(int indice) {
        if (indice >= 0 && indice < curas.length) {
            curas[indice] = true;
            for (Personaje p : jugadores) {
                p.obtenerRol().alDescubrirCura(p, this);
            }
            verificarVictoria();
        }
    }

    private void verificarVictoria() {
        for (boolean curada : curas) {
            if (!curada) {
                return;
            }
        }
        victoriaAlcanzada = true;
        System.out.println("\n==========================================");
        System.out.println("¡¡¡ VICTORIA !!! SE HAN DESCUBIERTO LAS 4 CURAS.");
        System.out.println("==========================================");
    }

    public boolean hayVictoria() {
        return victoriaAlcanzada;
    }

    // ==========================================
    // DINÁMICA DEL TURNO, INFECCIÓN Y BROTES
    // ==========================================
    public void activarNocheTranquila() {
        this.nocheTranquila = true;
    }

    public void ejecutarInfeccionAutomatica() {
        System.out.println("\n--- FASE DE INFECCIÓN AUTOMÁTICA ---");
        if (nocheTranquila) {
            System.out.println("--- FASE SALTADA POR NOCHE TRANQUILA ---");
            nocheTranquila = false;
            return;
        }

        for (int i = 0; i < this.tasaInfeccion; i++) {
            CartaInfeccion robada = this.mazoInfeccion.robarCarta();
            if (robada != null) {
                infectarCiudad(robada.obtenerCiudad(), 1);
                if (hayDerrotaPorBrotes()) {
                    return;
                }
            }
        }
    }

    public void infectarCiudad(Ciudad ciudad, int cantidad) {
        if (estaProtegidaDeInfeccion(ciudad)) {
            System.out.println("[Protección Activa] " + ciudad.obtenerNombre()
                    + " está protegida y no recibe cubos de infección.");
            return;
        }

        for (int i = 0; i < cantidad; i++) {
            boolean hayBrote = ciudad.agregarCuboDeInfeccion();
            if (hayBrote) {
                resolverBrotes(ciudad);
                return;
            }
        }
        System.out.println("¡Infección! " + ciudad.obtenerNombre()
                + " ahora tiene " + ciudad.obtenerCubosDeInfeccion() + " cubo(s).");
    }

    private void resolverBrotes(Ciudad ciudadInicial) {
        SinglyLinkedListQueue<Ciudad> colaBrotes = new SinglyLinkedListQueue<>();
        Ciudad[] yaExplotaron = new Ciudad[tableroCiudades.length];
        int numExplotadas = 0;

        colaBrotes.enqueue(ciudadInicial);
        yaExplotaron[numExplotadas++] = ciudadInicial;

        while (!colaBrotes.estaVacia()) {
            Ciudad ciudad = colaBrotes.dequeue();
            contadorBrotes++;
            System.out.println("\n*** ¡BROTE en " + ciudad.obtenerNombre() + "! ***");
            imprimirMarcadorBrotes();

            for (Ciudad vecina : ciudad.obtenerVecinos()) {
                if (contiene(yaExplotaron, numExplotadas, vecina)) {
                    continue;
                }

                if (estaProtegidaDeInfeccion(vecina)) {
                    System.out.println("   [Protección Activa] " + vecina.obtenerNombre()
                            + " no recibe cubos del brote.");
                    continue;
                }

                boolean vecinaExplota = vecina.agregarCuboDeInfeccion();
                if (vecinaExplota) {
                    System.out.println("   " + vecina.obtenerNombre()
                            + " ya tenía 3 cubos: ¡reacción en cadena!");
                    colaBrotes.enqueue(vecina);
                    yaExplotaron[numExplotadas++] = vecina;
                } else {
                    System.out.println("   " + vecina.obtenerNombre() + " recibe 1 cubo (ahora tiene "
                            + vecina.obtenerCubosDeInfeccion() + ").");
                }
            }
        }
    }

    private boolean contiene(Ciudad[] ciudades, int cantidad, Ciudad buscada) {
        for (int i = 0; i < cantidad; i++) {
            if (ciudades[i] == buscada) {
                return true;
            }
        }
        return false;
    }

    public boolean hayDerrotaPorBrotes() {
        return contadorBrotes >= MAX_BROTES;
    }

    public int obtenerContadorBrotes() {
        return contadorBrotes;
    }

    public int obtenerMaximoBrotes() {
        return MAX_BROTES;
    }

    public void imprimirMarcadorBrotes() {
        StringBuilder marcador = new StringBuilder("Marcador de brotes:  ");
        int casillaActual = Math.min(contadorBrotes, MAX_BROTES);

        for (int i = 0; i <= MAX_BROTES; i++) {
            if (i == casillaActual) {
                marcador.append("[").append(i).append("]");
            } else {
                marcador.append(" ").append(i).append(" ");
            }
        }
        marcador.append(" (").append(MAX_BROTES).append(" = derrota)");

        System.out.println(marcador);
    }

    public boolean dinamicaTurno() {
        Personaje jugadorActual = jugadores[indiceJugadorActual];
        InterfazJuego.estadoTurno(indiceJugadorActual, 0);
        System.out.println("\n==========================================");
        System.out.println("TURNO DE: " + jugadorActual.obtenerNombre());
        System.out.println("==========================================");
        imprimirMarcadorBrotes();

        jugadorActual.ejecutarAccion(this);
        InterfazJuego.accionesRestantes(0);
        if (victoriaAlcanzada) {
            return false;
        }

        System.out.println("\n--- Robando 2 cartas del mazo de jugador ---");
        for (int i = 0; i < 2; i++) {
            Carta robada = mazoJugador.robarCarta();
            if (robada != null) {
                System.out.println("Carta robada: " + robada.obtenerNombre() + " [" + robada.obtenerTipo() + "]");
                if (robada.obtenerTipo().equals("Epidemia")) {
                    resolverEpidemia();
                    if (hayDerrotaPorBrotes()) {
                        anunciarDerrotaPorBrotes();
                        return false;
                    }
                } else {
                    if (robada instanceof CartaEvento || "Evento".equalsIgnoreCase(robada.obtenerTipo())) {
                        mazoAuxiliarEvento.agregarEvento(robada, jugadorActual);
                    }
                    jugadorActual.agarrarCarta(robada);
                }
            } else {
                System.out.println("\n==========================================");
                System.out.println("¡¡¡ DERROTA !!! EL MAZO DE JUGADOR SE HA AGOTADO.");
                System.out.println("==========================================");
                return false;
            }
        }

        ejecutarInfeccionAutomatica();
        if (hayDerrotaPorBrotes()) {
            anunciarDerrotaPorBrotes();
            return false;
        }

        if (!InterfazJuego.esGrafica()) {
            System.out.print("\n¿Desea consultar el estado del mapa actual? (s/n): ");
            if (InterfazJuego.entrada().nextLine().trim().equalsIgnoreCase("s")) {
                imprimirEstadoMapa();
            }
        }

        indiceJugadorActual = (indiceJugadorActual + 1) % jugadores.length;
        return true;
    }

    private void anunciarDerrotaPorBrotes() {
        System.out.println("\n==========================================");
        System.out.println("¡¡¡ DERROTA !!! SE ALCANZARON " + MAX_BROTES + " BROTES.");
        System.out.println("==========================================");
    }

    public void imprimirEstadoMapa() {
        System.out.println("\n==========================================");
        System.out.println("              MAPA ACTUAL                 ");
        imprimirMarcadorBrotes();
        System.out.println("Tasa de infección: " + tasaInfeccion
                + " | Centros: " + centrosConstruidos + "/" + MAX_CENTROS);
        System.out.println("==========================================");

        for (Ciudad ciudad : obtenerCiudadesOrdenadas()) {
            int cubos = ciudad.obtenerCubosDeInfeccion();
            String centro = ciudad.tenerCentroDeInvestigacion() ? " [Centro de Inv.]" : "";

            if (cubos > 0) {
                System.out.println("!!! " + ciudad.obtenerNombre()
                        + " (Cubos de infección: " + cubos + ")" + centro + " !!!");
            } else {
                System.out.println(ciudad.obtenerNombre()
                        + " (Cubos de infección: " + cubos + ")" + centro);
            }
        }
    }

    public void resolverEpidemia() {
        System.out.println("\n!!! ALERTA DE EPIDEMIA !!!");
        tasaInfeccion++;
        System.out.println("1. Tasa de infección incrementada a: " + tasaInfeccion);

        CartaInfeccion robadaDescarte = mazoInfeccion.extraerCartaDescarteFIFO();
        if (robadaDescarte != null) {
            Ciudad ciudadEpidemia = robadaDescarte.obtenerCiudad();
            System.out.println("2. Ciudad infectada desde mazo de descarte de infección: "
                    + ciudadEpidemia.obtenerNombre() + " (3 cubos).");
            infectarCiudad(ciudadEpidemia, 3);
            mazoInfeccion.agregarADescartes(robadaDescarte);
        } else {
            System.out.println("2. La pila de descartes de infección estaba vacía.");
        }

        int cartasRecicladas = mazoInfeccion.reciclarDescartesAlMazo();
        System.out.println("3. Intensificar: Se barajaron " + cartasRecicladas
                + " carta(s) del descarte y se colocaron sobre el mazo de infección.");
    }

    public MazoInfeccion obtenerMazoInfeccion() {
        return mazoInfeccion;
    }

    public Ciudad[] obtenerCiudadesOrdenadas() {
        Ciudad[] ordenadas = new Ciudad[directorioCiudades.obtenerTamano()];
        directorioCiudades.inOrder(ordenadas);
        return ordenadas;
    }

    public Ciudad[] obtenerTableroCiudades() {
        return tableroCiudades;
    }

    public Personaje[] obtenerJugadores() {
        return jugadores;
    }

    public int obtenerTasaInfeccion() {
        return tasaInfeccion;
    }

    public MazoAuxiliarEvento obtenerMazoAuxiliarEvento() {
        return mazoAuxiliarEvento;
    }
}