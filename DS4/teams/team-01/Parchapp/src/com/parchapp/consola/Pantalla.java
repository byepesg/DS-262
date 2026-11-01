package com.parchapp.consola;

import com.parchapp.estructuras.ListaEnlazada;
import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Locale;

// Todo lo que se imprime o se lee pasa por aquí.
// En modo simple no se usan colores, ni se limpia la pantalla, ni se usan caracteres especiales,
// para terminales que no los soportan (por ejemplo la salida de NetBeans).
public class Pantalla {

    public static final int ANCHO = 78;
    private static final Locale ESPANOL = Locale.forLanguageTag("es-CO");

    private final boolean simple;
    private final BufferedReader entrada;
    // Los mensajes de una acción se muestran en la siguiente pantalla que se dibuje
    private final ListaEnlazada<String> mensajesPendientes = new ListaEnlazada<>();

    public Pantalla(boolean simple) {
        this.simple = simple;
        // Leer con la codificación de la consola hace que las tildes lleguen bien en Windows
        Console consola = System.console();
        Charset codificacion = consola != null ? consola.charset() : Charset.defaultCharset();
        entrada = new BufferedReader(new InputStreamReader(System.in, codificacion));
    }

    // ---------------------------------------------------------------- partes de una pantalla

    public void nueva(String ruta, String derecha) {
        if (!simple) {
            System.out.print("\033[H\033[2J");
        }
        String marca = "PARCHAPP";
        int espacio = ANCHO - 7 - marca.length() - ruta.length() - derecha.length();
        if (espacio < 1) {
            ruta = recortar(ruta, ruta.length() + espacio - 1);
            espacio = 1;
        }
        String borde = "─".repeat(ANCHO - 2);
        linea(cian("┌" + borde + "┐"));
        linea(cian("│ ") + color("1;36", marca) + gris(" · " + ruta) + " ".repeat(espacio) + derecha + cian(" │"));
        linea(cian("└" + borde + "┘"));
        mostrarMensajes();
        vacio();
    }

    public void titulo(String texto) {
        vacio();
        int resto = ANCHO - 6 - texto.length();
        linea("  " + cian("── ") + negrita(texto) + " " + cian("─".repeat(Math.max(3, resto))));
    }

    public void grupo(String texto) {
        vacio();
        linea("  " + gris(texto.toUpperCase()));
    }

    public void opcion(String tecla, String texto) {
        linea("   " + color("1;36", ajustar(tecla, 2)) + " " + texto);
    }

    public void opcion(String tecla, String texto, String detalle) {
        linea("   " + color("1;36", ajustar(tecla, 2)) + " " + ajustar(texto, 34) + gris(detalle));
    }

    // "Etiqueta      valor", partiendo el valor en varios renglones si es largo
    public void dato(String etiqueta, String valor) {
        String sangria = " ".repeat(17);
        boolean primero = true;
        for (String renglon : partirEnRenglones(valor, ANCHO - sangria.length())) {
            if (primero) {
                linea("  " + gris(ajustar(etiqueta, 15)) + renglon);
                primero = false;
            } else {
                linea(sangria + renglon);
            }
        }
    }

    public void nota(String texto) {
        for (String renglon : partirEnRenglones(texto, ANCHO - 2)) {
            linea("  " + gris(renglon));
        }
    }

    public void linea(String texto) {
        System.out.println(simple ? aTextoSimple(texto) : texto);
    }

    public void vacio() {
        System.out.println();
    }

    // ---------------------------------------------------------------- mensajes

    public void exito(String texto) {
        mensajesPendientes.agregarAlFinal(etiquetaVerde("OK") + " " + texto);
    }

    public void aviso(String texto) {
        mensajesPendientes.agregarAlFinal(etiquetaAmarilla("!") + " " + texto);
    }

    public void error(String texto) {
        mensajesPendientes.agregarAlFinal(etiquetaRoja("X") + " " + texto);
    }

    public void info(String texto) {
        mensajesPendientes.agregarAlFinal(etiquetaCian("i") + " " + texto);
    }

    private void mostrarMensajes() {
        if (mensajesPendientes.estaVacia()) {
            return;
        }
        vacio();
        for (String mensaje : mensajesPendientes) {
            boolean primero = true;
            for (String renglon : partirEnRenglones(mensaje, ANCHO - 6)) {
                linea((primero ? "  " : "      ") + renglon);
                primero = false;
            }
        }
        mensajesPendientes.vaciar();
    }

    // ---------------------------------------------------------------- entrada

    public String preguntar(String pregunta) {
        mostrarMensajes();
        String texto = "\n  " + color("1;36", "»") + " " + pregunta + " ";
        System.out.print(simple ? aTextoSimple(texto) : texto);
        String respuesta;
        try {
            respuesta = entrada.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (respuesta == null) {
            // Se cerró la entrada (Ctrl+D o Ctrl+Z): salimos sin mostrar un error
            System.out.println();
            System.exit(0);
        }
        return respuesta.strip();
    }

    public boolean confirmar(String pregunta) {
        while (true) {
            String respuesta = preguntar(pregunta + " (s/n):").toLowerCase();
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                return true;
            }
            if (respuesta.equals("n") || respuesta.equals("no")) {
                return false;
            }
            error("Responde s (sí) o n (no).");
        }
    }

    public void pausa() {
        preguntar(gris("Presiona Enter para continuar"));
    }

    // Convierte la respuesta "3" en el tercer elemento de la lista; null si no es válida
    public <T> T elegir(ListaEnlazada<T> lista, String respuesta) {
        try {
            int numero = Integer.parseInt(respuesta);
            if (numero >= 1 && numero <= lista.tamano()) {
                return lista.obtener(numero - 1);
            }
        } catch (NumberFormatException e) {
            // no era un número: se avisa abajo
        }
        error("\"" + respuesta + "\" no es una opción válida.");
        return null;
    }

    // ---------------------------------------------------------------- colores y etiquetas

    public String negrita(String texto) {
        return color("1", texto);
    }

    public String gris(String texto) {
        return color("90", texto);
    }

    public String cian(String texto) {
        return color("36", texto);
    }

    public String verde(String texto) {
        return color("32", texto);
    }

    public String amarillo(String texto) {
        return color("33", texto);
    }

    public String etiquetaVerde(String texto) {
        return etiqueta("42;30", texto);
    }

    public String etiquetaAmarilla(String texto) {
        return etiqueta("43;30", texto);
    }

    public String etiquetaRoja(String texto) {
        return etiqueta("41;97", texto);
    }

    public String etiquetaGris(String texto) {
        return etiqueta("100;97", texto);
    }

    public String etiquetaCian(String texto) {
        return etiqueta("46;30", texto);
    }

    // Ocupa siempre el largo del texto + 2, con o sin colores, para que las columnas cuadren
    private String etiqueta(String fondo, String texto) {
        if (simple) {
            return "[" + texto + "]";
        }
        return color(fondo, " " + texto + " ");
    }

    public String barra(int ocupados, int total) {
        // Con capacidades grandes la barra se escala a 10 casillas
        int casillas = Math.min(total, 10);
        int llenas = (int) Math.round(ocupados * casillas / (double) total);
        if (ocupados > 0 && llenas == 0) {
            llenas = 1;
        }
        return verde("█".repeat(llenas)) + gris("░".repeat(casillas - llenas));
    }

    private String color(String codigo, String texto) {
        if (simple) {
            return texto;
        }
        return "\033[" + codigo + "m" + texto + "\033[0m";
    }

    // ---------------------------------------------------------------- utilidades de texto

    public static String recortar(String texto, int ancho) {
        if (texto.length() <= ancho) {
            return texto;
        }
        return texto.substring(0, Math.max(0, ancho - 3)) + "...";
    }

    public static String ajustar(String texto, int ancho) {
        String recortado = recortar(texto, ancho);
        return recortado + " ".repeat(ancho - recortado.length());
    }

    public static String ajustarDerecha(String texto, int ancho) {
        String recortado = recortar(texto, ancho);
        return " ".repeat(ancho - recortado.length()) + recortado;
    }

    // contar(1, "parada", "paradas") -> "1 parada"; contar(3, ...) -> "3 paradas"
    public static String contar(int cantidad, String singular, String plural) {
        if (cantidad == 1) {
            return "1 " + singular;
        }
        return cantidad + " " + plural;
    }

    public static String distancia(double km) {
        if (km < 1) {
            return Math.round(km * 1000) + " m";
        }
        return String.format(ESPANOL, "%.1f km", km);
    }

    private ListaEnlazada<String> partirEnRenglones(String texto, int ancho) {
        ListaEnlazada<String> renglones = new ListaEnlazada<>();
        String actual = "";
        int largoActual = 0;
        for (String palabra : texto.split(" ")) {
            int largoPalabra = largoVisible(palabra);
            if (largoActual > 0 && largoActual + 1 + largoPalabra > ancho) {
                renglones.agregarAlFinal(actual);
                actual = "";
                largoActual = 0;
            }
            if (largoActual > 0) {
                actual += " ";
                largoActual++;
            }
            actual += palabra;
            largoActual += largoPalabra;
        }
        renglones.agregarAlFinal(actual);
        return renglones;
    }

    // Largo sin contar los códigos de color, que no ocupan espacio en la terminal
    private static int largoVisible(String texto) {
        return texto.replaceAll("\033\\[[0-9;]*m", "").length();
    }

    private static String aTextoSimple(String texto) {
        String resultado = texto
                .replace('┌', '+').replace('┐', '+').replace('└', '+').replace('┘', '+')
                .replace('─', '-').replace('│', '|')
                .replace('█', '#').replace('░', '.')
                .replace('·', '-').replace('»', '>')
                .replace("¿", "").replace("¡", "");
        // Quita las tildes: "á" -> "a", "ñ" -> "n"
        return Normalizer.normalize(resultado, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
