package juego;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;

/** Conecta las preguntas del motor con la consola o con el cliente gráfico. */
public final class InterfazJuego {
    private static final List<Map<String, Object>> opciones = new ArrayList<>();
    private static Scanner entrada;
    private static boolean grafica;
    private static LogicaJuego partida;
    private static int turno;
    private static int jugador;
    private static int acciones;

    private InterfazJuego() {}

    public static Scanner entrada() {
        if (entrada == null) {
            entrada = new Scanner(System.in, StandardCharsets.UTF_8);
        }
        return entrada;
    }

    public static void modoGrafico() {
        grafica = true;
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
    }

    public static boolean esGrafica() {
        return grafica;
    }

    public static void establecerPartida(LogicaJuego juego) {
        partida = juego;
    }

    public static void iniciarTurno(int numero) {
        turno = numero;
    }

    public static void estadoTurno(int indice, int restantes) {
        jugador = indice;
        acciones = restantes;
    }

    public static void accionesRestantes(int restantes) {
        acciones = restantes;
    }

    public static void opcion(int valor, String etiqueta) {
        if (grafica) {
            opciones.add(Map.of("valor", String.valueOf(valor), "etiqueta", etiqueta));
        } else {
            System.out.println("  [" + valor + "] " + etiqueta);
        }
    }

    public static void opcionesCartasCiudad(Personaje personaje) {
        if (!grafica) {
            return;
        }
        for (int i = 0; i < personaje.obtenerNumCartas(); i++) {
            Carta carta = personaje.obtenerMano()[i];
            if (carta instanceof CartaCiudad) {
                opcion(i, carta.obtenerNombre());
            }
        }
    }

    public static void pregunta(String tipo, String texto) {
        pregunta(tipo, texto, jugador);
    }

    public static void preguntaPara(Personaje personaje, String tipo, String texto) {
        int indice = grafica && partida != null
                ? Arrays.asList(partida.obtenerJugadores()).indexOf(personaje) : jugador;
        pregunta(tipo, texto, indice >= 0 ? indice : jugador);
    }

    private static void pregunta(String tipo, String texto, int responsable) {
        if (!grafica) {
            System.out.print(texto);
            return;
        }
        List<Map<String, Object>> alternativas = new ArrayList<>(opciones);
        if (texto.contains("-1 para cancelar")) {
            alternativas.add(Map.of("valor", "-1", "etiqueta", "Cancelar"));
        }
        emitir(Map.of("evento", "pregunta", "tipo", tipo, "texto", texto.trim(),
                "opciones", alternativas, "estado", estado(), "responsable", responsable));
        opciones.clear();
    }

    public static void finalizar(String resultado) {
        if (grafica) {
            emitir(Map.of("evento", "fin", "resultado", resultado, "estado", estado()));
        }
    }

    private static Map<String, Object> estado() {
        if (partida == null) {
            return Map.of();
        }
        List<Map<String, Object>> ciudades = new ArrayList<>();
        for (Ciudad ciudad : partida.obtenerCiudadesOrdenadas()) {
            List<String> vecinos = Arrays.stream(ciudad.obtenerVecinos())
                    .map(Ciudad::obtenerNombre).toList();
            ciudades.add(Map.of("nombre", ciudad.obtenerNombre(), "color", ciudad.obtenerColor(),
                    "cubos", ciudad.obtenerCubosDeInfeccion(), "centro", ciudad.tenerCentroDeInvestigacion(),
                    "vecinos", vecinos));
        }
        List<Map<String, Object>> jugadores = new ArrayList<>();
        for (Personaje personaje : partida.obtenerJugadores()) {
            List<Map<String, Object>> mano = new ArrayList<>();
            for (int i = 0; i < personaje.obtenerNumCartas(); i++) {
                Carta carta = personaje.obtenerMano()[i];
                mano.add(Map.of("nombre", carta.obtenerNombre(), "tipo", carta.obtenerTipo(),
                        "color", carta instanceof CartaCiudad
                                ? ((CartaCiudad) carta).obtenerCiudad().obtenerColor() : ""));
            }
            jugadores.add(Map.of("rol", personaje.obtenerNombre(), "ciudad", personaje.obtenerCiudadActual().obtenerNombre(),
                    "descripcion", personaje.obtenerRol().obtenerDescripcion(), "mano", mano));
        }
        List<Map<String, Object>> curas = new ArrayList<>();
        for (int i = 0; i < LogicaJuego.COLORES.length; i++) {
            curas.add(Map.of("color", LogicaJuego.COLORES[i], "descubierta", partida.estaCurada(i)));
        }
        return Map.ofEntries(
                Map.entry("turno", turno), Map.entry("jugador", jugador), Map.entry("acciones", acciones),
                Map.entry("brotes", partida.obtenerContadorBrotes()), Map.entry("maxBrotes", partida.obtenerMaximoBrotes()),
                Map.entry("tasa", partida.obtenerTasaInfeccion()), Map.entry("centros", partida.obtenerCentrosConstruidos()),
                Map.entry("maxCentros", partida.obtenerMaximoCentros()), Map.entry("ciudades", ciudades),
                Map.entry("jugadores", jugadores), Map.entry("curas", curas));
    }

    private static void emitir(Map<String, Object> mensaje) {
        System.out.println("@P " + json(mensaje));
        System.out.flush();
    }

    private static String json(Object valor) {
        if (valor instanceof Map<?, ?> mapa) {
            List<String> partes = new ArrayList<>();
            mapa.forEach((clave, dato) -> partes.add(json(clave) + ":" + json(dato)));
            return "{" + String.join(",", partes) + "}";
        }
        if (valor instanceof List<?> lista) {
            return "[" + String.join(",", lista.stream().map(InterfazJuego::json).toList()) + "]";
        }
        if (valor instanceof String texto) {
            StringBuilder literal = new StringBuilder("\"");
            for (int i = 0; i < texto.length(); i++) {
                char c = texto.charAt(i);
                if (c == '"' || c == '\\') {
                    literal.append('\\').append(c);
                } else if (c < 32) {
                    literal.append(String.format("\\u%04x", (int) c));
                } else {
                    literal.append(c);
                }
            }
            return literal.append('"').toString();
        }
        return valor.toString();
    }
}
