package com.parchapp.consola;

import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import java.time.LocalDateTime;
import java.util.Locale;

public class App {

    private final Pantalla pantalla;
    private final Sesion sesion = new Sesion();
    private final VistaParches vistaParches;
    private final VistaLugares vistaLugares;
    private final VistaRuta vistaRuta;

    public App(boolean modoSimple) {
        pantalla = new Pantalla(modoSimple);
        vistaParches = new VistaParches(pantalla, sesion);
        vistaLugares = new VistaLugares(pantalla, sesion, vistaParches);
        vistaRuta = new VistaRuta(pantalla, sesion);
    }

    public void iniciar() {
        elegirUsuario(true);
        while (true) {
            String opcion = mostrarMenu();
            switch (opcion) {
                case "1" -> vistaLugares.explorar();
                case "2" -> vistaLugares.historial();
                case "3" -> vistaParches.verDisponibles();
                case "4" -> vistaParches.verMios();
                case "5" -> vistaParches.crear(null);
                case "6" -> vistaRuta.ver();
                case "7" -> cambiarUbicacion();
                case "8" -> elegirUsuario(false);
                case "9" -> verEstructuras();
                case "0" -> {
                    pantalla.vacio();
                    pantalla.linea("  " + pantalla.cian("¡Hasta pronto, " + sesion.usuario.getNombre() + "! Nos vemos en el próximo parche."));
                    pantalla.vacio();
                    return;
                }
                case "" -> { }
                default -> pantalla.error("\"" + opcion + "\" no es una opción válida. Escribe un número del 0 al 9.");
            }
        }
    }

    private String mostrarMenu() {
        LocalDateTime ahora = LocalDateTime.now();
        int activos = sesion.gestor.activos(ahora).tamano();
        int mios = sesion.gestor.delUsuario(sesion.usuario, ahora).tamano();

        pantalla.nueva("Inicio", sesion.resumen());
        pantalla.linea("  " + pantalla.negrita("Hola, " + sesion.usuario.getNombre() + ".") + " ¿Qué plan hay para hoy?");

        pantalla.grupo("Lugares");
        pantalla.opcion("1", "Explorar lugares", sesion.catalogo.cantidad() + " lugares");
        pantalla.opcion("2", "Vistos recientemente", sesion.historial.tamano() + " de " + sesion.historial.getCapacidad());
        pantalla.grupo("Parches");
        pantalla.opcion("3", "Parches disponibles", Pantalla.contar(activos, "activo", "activos"));
        pantalla.opcion("4", "Mis parches", String.valueOf(mios));
        pantalla.opcion("5", "Crear un parche");
        pantalla.grupo("Tu día");
        pantalla.opcion("6", "Ruta del día", Pantalla.contar(sesion.ruta.tamano(), "parada", "paradas"));
        pantalla.grupo("Ajustes");
        pantalla.opcion("7", "Cambiar mi ubicación", sesion.ubicacion.getNombre());
        pantalla.opcion("8", "Cambiar de usuario", sesion.usuario.getNombre());
        pantalla.opcion("9", "Ver estructuras de datos", "listas, colas e invariantes");
        pantalla.vacio();
        pantalla.opcion("0", "Salir");

        return pantalla.preguntar("Elige una opción:");
    }

    private void elegirUsuario(boolean esInicio) {
        while (true) {
            if (esInicio) {
                pantalla.nueva("Bienvenida", "Datos de demostración");
                pantalla.linea("  " + pantalla.negrita(pantalla.cian("¡Bienvenido a Parchapp!")));
                pantalla.vacio();
                pantalla.nota("Descubre lugares cerca de la universidad, arma planes en grupo (parches) y organiza tu ruta del día.");
                pantalla.nota("Por ahora la app usa usuarios de prueba. Después puedes cambiar de usuario para ver cómo otras personas se unen a tus parches.");
            } else {
                pantalla.nueva("Cambiar de usuario", sesion.resumen());
            }
            pantalla.titulo("¿Quién eres?");
            int numero = 1;
            for (Usuario usuario : sesion.usuarios) {
                int parches = sesion.gestor.delUsuario(usuario, LocalDateTime.now()).tamano();
                pantalla.opcion(String.valueOf(numero), usuario.getNombre(), Pantalla.contar(parches, "parche", "parches"));
                numero++;
            }
            if (!esInicio) {
                pantalla.opcion("0", "Volver");
            }

            String respuesta = pantalla.preguntar("Número de usuario:");
            if (!esInicio && respuesta.equals("0")) {
                return;
            }
            if (respuesta.isEmpty()) {
                continue;
            }
            Usuario elegido = pantalla.elegir(sesion.usuarios, respuesta);
            if (elegido != null) {
                sesion.usuario = elegido;
                if (!esInicio) {
                    pantalla.exito("Ahora estás usando la app como " + elegido.getNombre() + ".");
                }
                return;
            }
        }
    }

    private void cambiarUbicacion() {
        while (true) {
            pantalla.nueva("Ubicación", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita("¿Desde dónde estás explorando?"));
            pantalla.nota("Se usa para calcular distancias y ordenar por cercanía. Ahora: " + sesion.ubicacion.getNombre() + ".");
            pantalla.vacio();
            int numero = 1;
            for (Ubicacion ubicacion : sesion.ubicaciones) {
                pantalla.opcion(String.valueOf(numero), ubicacion.getNombre());
                numero++;
            }
            pantalla.opcion("C", "Escribir coordenadas");
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            }
            if (respuesta.equals("C")) {
                if (leerCoordenadas()) {
                    return;
                }
            } else if (!respuesta.isEmpty()) {
                Ubicacion elegida = pantalla.elegir(sesion.ubicaciones, respuesta);
                if (elegida != null) {
                    sesion.ubicacion = elegida;
                    pantalla.exito("Ahora exploras desde " + elegida.getNombre() + ".");
                    return;
                }
            }
        }
    }

    private boolean leerCoordenadas() {
        String texto = pantalla.preguntar("Latitud y longitud separadas por coma (ej. 4.6097, -74.0817):");
        String[] partes = texto.split(",");
        if (partes.length != 2) {
            pantalla.error("Escribe dos números separados por una coma y usa punto decimal: 4.6097, -74.0817");
            return false;
        }
        try {
            double latitud = Double.parseDouble(partes[0].strip());
            double longitud = Double.parseDouble(partes[1].strip());
            String nombre = String.format(Locale.ROOT, "Punto %.4f, %.4f", latitud, longitud);
            sesion.ubicacion = new Ubicacion(nombre, latitud, longitud);
        } catch (IllegalArgumentException e) {
            // NumberFormatException también es IllegalArgumentException
            pantalla.error("Coordenadas no válidas. La latitud va de -90 a 90 y la longitud de -180 a 180.");
            return false;
        }
        pantalla.exito("Ahora exploras desde " + sesion.ubicacion.getNombre() + ".");

        // Si el punto queda lejísimos, seguramente se escribió "4,6" en vez de "4.6"
        double masCercano = Double.MAX_VALUE;
        for (Lugar lugar : sesion.catalogo.todos()) {
            masCercano = Math.min(masCercano, sesion.ubicacion.distanciaKm(lugar));
        }
        if (masCercano > 50) {
            pantalla.aviso("Ese punto está a " + Pantalla.distancia(masCercano)
                    + " del lugar más cercano. ¿Usaste punto decimal?");
        }
        return true;
    }

    // Pantalla pensada para la sustentación: muestra los nodos reales y si se cumplen los invariantes
    private void verEstructuras() {
        pantalla.nueva("Estructuras de datos", sesion.resumen());
        pantalla.linea("  " + pantalla.negrita("Estructuras propias en uso"));
        pantalla.nota("Todos los datos se guardan en ListaEnlazada y Cola, implementadas por el equipo con nodos."
                + " \"OK\" significa que se cumplen sus invariantes: extremos, tamaño y enlaces consistentes.");

        mostrarEstructura("ListaEnlazada<Lugar>", "catálogo de lugares", sesion.catalogo.invariantesValidos());
        pantalla.dato("Nodos", String.valueOf(sesion.catalogo.cantidad()));

        mostrarEstructura("ListaEnlazada<Lugar>", "historial de vistos", sesion.historial.invariantesValidos());
        pantalla.dato("Nodos", sesion.historial.tamano() + " de máximo " + sesion.historial.getCapacidad());
        pantalla.dato("Enlaces", sesion.historial.dibujarEnlaces());

        mostrarEstructura("Cola<Lugar>", "ruta del día", sesion.ruta.invariantesValidos());
        pantalla.dato("Nodos", String.valueOf(sesion.ruta.tamano()));
        pantalla.dato("Enlaces", sesion.ruta.dibujarEnlaces());

        mostrarEstructura("ListaEnlazada<Parche>", "todos los parches", sesion.gestor.invariantesValidos());
        pantalla.dato("Nodos", String.valueOf(sesion.gestor.cantidad()));

        pantalla.titulo("Dentro de cada parche");
        pantalla.nota("Participantes: ListaEnlazada<Usuario>. Lista de espera: Cola<Usuario>.");
        for (Parche parche : sesion.gestor.todos()) {
            pantalla.vacio();
            String estado = parche.estructurasValidas() ? pantalla.etiquetaVerde("OK") : pantalla.etiquetaRoja("FALLA");
            pantalla.linea("  " + pantalla.negrita(parche.getId()) + " " + estado + " " + pantalla.gris(parche.getTitulo()));
            pantalla.dato("Participantes", parche.dibujarParticipantes());
            if (parche.cantidadEnEspera() > 0) {
                pantalla.dato("En espera", parche.dibujarListaEspera());
            }
        }

        pantalla.titulo("Costo de las operaciones");
        pantalla.dato("O(1)", "agregarAlInicio, agregarAlFinal, eliminarPrimero, encolar, desencolar, verFrente");
        pantalla.dato("O(n)", "obtener(i), contiene, eliminar, eliminarUltimo, recorrer con for-each");
        pantalla.dato("O(n log n)", "ordenar (merge sort sobre los nodos)");
        pantalla.pausa();
    }

    private void mostrarEstructura(String tipo, String uso, boolean valida) {
        String estado = valida ? pantalla.etiquetaVerde("OK") : pantalla.etiquetaRoja("FALLA");
        pantalla.vacio();
        pantalla.linea("  " + estado + " " + pantalla.negrita(tipo) + pantalla.gris("  " + uso));
    }
}
