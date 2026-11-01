package com.parchapp.consola;

import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.NavegacionMaps;
import java.time.LocalDateTime;
import java.util.Locale;

class VistaLugares {

    private static final String[] ORDENES = {"nombre", "cercanía", "categoría"};

    private final Pantalla pantalla;
    private final Sesion sesion;
    private final VistaParches vistaParches;

    VistaLugares(Pantalla pantalla, Sesion sesion, VistaParches vistaParches) {
        this.pantalla = pantalla;
        this.sesion = sesion;
        this.vistaParches = vistaParches;
    }

    void explorar() {
        String busqueda = "";
        Categoria categoria = null;
        int orden = 0;

        while (true) {
            ListaEnlazada<Lugar> lugares = sesion.catalogo.buscar(busqueda, categoria);
            ordenar(lugares, orden);

            pantalla.nueva("Lugares", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita("Explorar lugares") + pantalla.gris("   "
                    + lugares.tamano() + " de " + sesion.catalogo.cantidad() + " · ordenados por " + ORDENES[orden]));
            if (!busqueda.isEmpty() || categoria != null) {
                String filtros = "Filtros:";
                if (!busqueda.isEmpty()) {
                    filtros += " texto \"" + busqueda + "\"";
                }
                if (categoria != null) {
                    filtros += " categoría " + categoria.getNombre();
                }
                pantalla.linea("  " + pantalla.cian(filtros));
            }
            pantalla.vacio();
            if (lugares.estaVacia()) {
                pantalla.linea("  " + pantalla.amarillo("Ningún lugar coincide. Prueba con otro texto o quita los filtros."));
            } else {
                imprimirTabla(lugares);
            }

            pantalla.titulo("Opciones");
            pantalla.nota("Escribe el número de un lugar para ver su detalle.");
            pantalla.opcion("B", "Buscar por nombre");
            pantalla.opcion("C", "Filtrar por categoría");
            pantalla.opcion("O", "Ordenar por " + ORDENES[(orden + 1) % ORDENES.length]);
            if (!busqueda.isEmpty() || categoria != null) {
                pantalla.opcion("L", "Quitar filtros");
            }
            pantalla.opcion("0", "Volver al inicio");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("B")) {
                busqueda = pantalla.preguntar("¿Qué buscas? (Enter para ver todos):");
            } else if (respuesta.equals("C")) {
                categoria = elegirCategoria();
            } else if (respuesta.equals("O")) {
                orden = (orden + 1) % ORDENES.length;
            } else if (respuesta.equals("L")) {
                busqueda = "";
                categoria = null;
            } else if (!respuesta.isEmpty()) {
                Lugar elegido = pantalla.elegir(lugares, respuesta);
                if (elegido != null) {
                    detalle(elegido);
                }
            }
        }
    }

    private void ordenar(ListaEnlazada<Lugar> lugares, int orden) {
        if (orden == 0) {
            CatalogoLugares.ordenarPorNombre(lugares);
        } else if (orden == 1) {
            CatalogoLugares.ordenarPorCercania(lugares, sesion.ubicacion);
        } else {
            CatalogoLugares.ordenarPorCategoria(lugares);
        }
    }

    // Devuelve null para "todas las categorías"
    private Categoria elegirCategoria() {
        pantalla.nueva("Lugares » Categoría", sesion.resumen());
        pantalla.linea("  " + pantalla.negrita("¿Qué tipo de lugar buscas?"));
        pantalla.vacio();
        Categoria[] categorias = Categoria.values();
        for (int i = 0; i < categorias.length; i++) {
            int cantidad = sesion.catalogo.contarPorCategoria(categorias[i]);
            pantalla.opcion(String.valueOf(i + 1), categorias[i].getNombre(), Pantalla.contar(cantidad, "lugar", "lugares"));
        }
        pantalla.opcion("0", "Todas las categorías");
        while (true) {
            String respuesta = pantalla.preguntar("Categoría:");
            if (respuesta.equals("0") || respuesta.isEmpty()) {
                return null;
            }
            try {
                int numero = Integer.parseInt(respuesta);
                if (numero >= 1 && numero <= categorias.length) {
                    return categorias[numero - 1];
                }
            } catch (NumberFormatException e) {
                // se avisa abajo
            }
            pantalla.error("Escribe un número del 0 al " + categorias.length + ".");
        }
    }

    void imprimirTabla(ListaEnlazada<Lugar> lugares) {
        pantalla.linea(pantalla.gris("    #  " + Pantalla.ajustar("Lugar", 42) + " "
                + Pantalla.ajustar("Categoría", 14) + Pantalla.ajustarDerecha("Distancia", 12)));
        pantalla.linea("  " + pantalla.gris("─".repeat(Pantalla.ANCHO - 2)));
        int numero = 1;
        for (Lugar lugar : lugares) {
            String distancia = Pantalla.distancia(sesion.ubicacion.distanciaKm(lugar));
            pantalla.linea("  " + pantalla.cian(Pantalla.ajustarDerecha(String.valueOf(numero), 3)) + "  "
                    + Pantalla.ajustar(lugar.getNombre(), 42) + " "
                    + pantalla.gris(Pantalla.ajustar(lugar.getCategoria().getNombre(), 14))
                    + Pantalla.ajustarDerecha(distancia, 12));
            numero++;
        }
    }

    void detalle(Lugar lugar) {
        sesion.historial.registrarVista(lugar);
        while (true) {
            ListaEnlazada<Parche> parchesAqui = sesion.gestor.activosEn(lugar, LocalDateTime.now());
            double km = sesion.ubicacion.distanciaKm(lugar);

            pantalla.nueva("Lugares » " + lugar.getId(), sesion.resumen());
            pantalla.linea("  " + pantalla.negrita(lugar.getNombre()) + "  "
                    + pantalla.etiquetaGris(lugar.getCategoria().getNombre()));
            pantalla.vacio();
            pantalla.dato("Dirección", textoOpcional(lugar.getDireccion()));
            pantalla.dato("Distancia", Pantalla.distancia(km) + " en línea recta desde " + sesion.ubicacion.getNombre());
            pantalla.dato("Coordenadas", String.format(Locale.ROOT, "%.5f, %.5f", lugar.getLatitud(), lugar.getLongitud()));
            pantalla.dato("Descripción", textoOpcional(lugar.getDescripcion()));
            if (parchesAqui.estaVacia()) {
                pantalla.dato("Parches", "Ninguno activo por ahora");
            } else {
                pantalla.dato("Parches", pantalla.verde(Pantalla.contar(parchesAqui.tamano(), "activo", "activos") + " en este lugar"));
            }

            pantalla.titulo("¿Qué quieres hacer?");
            pantalla.opcion("M", "Cómo llegar", "abre Google Maps");
            if (sesion.ruta.contiene(lugar)) {
                pantalla.opcion("R", "Agregar a mi ruta del día", "ya está en tu ruta");
            } else {
                pantalla.opcion("R", "Agregar a mi ruta del día");
            }
            pantalla.opcion("P", "Crear un parche aquí");
            if (!parchesAqui.estaVacia()) {
                pantalla.opcion("V", "Ver los parches de este lugar", Pantalla.contar(parchesAqui.tamano(), "activo", "activos"));
            }
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("M")) {
                abrirComoLlegar(pantalla, lugar);
            } else if (respuesta.equals("R")) {
                if (sesion.ruta.agregar(lugar)) {
                    pantalla.exito("Agregado a tu ruta del día como parada #" + sesion.ruta.tamano() + ".");
                } else {
                    pantalla.aviso("Este lugar ya está en tu ruta del día.");
                }
            } else if (respuesta.equals("P")) {
                vistaParches.crear(lugar);
            } else if (respuesta.equals("V") && !parchesAqui.estaVacia()) {
                vistaParches.verEnLugar(lugar);
            } else if (!respuesta.isEmpty()) {
                pantalla.error("\"" + respuesta + "\" no es una opción válida.");
            }
        }
    }

    void historial() {
        while (true) {
            ListaEnlazada<Lugar> recientes = sesion.historial.recientes();
            pantalla.nueva("Lugares » Vistos", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita("Vistos recientemente") + pantalla.gris("   "
                    + recientes.tamano() + " de " + sesion.historial.getCapacidad()));
            pantalla.nota("Del más reciente al más antiguo. Si vuelves a abrir un lugar, sube al primer puesto.");
            pantalla.vacio();
            if (recientes.estaVacia()) {
                pantalla.linea("  " + pantalla.amarillo("Todavía no has abierto ningún lugar. Entra a Explorar lugares."));
            } else {
                imprimirTabla(recientes);
            }

            pantalla.titulo("Opciones");
            if (!recientes.estaVacia()) {
                pantalla.nota("Escribe el número de un lugar para volver a verlo.");
                pantalla.opcion("B", "Borrar el historial");
            }
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("B")) {
                sesion.historial.vaciar();
                pantalla.exito("Historial borrado.");
            } else if (!respuesta.isEmpty()) {
                Lugar elegido = pantalla.elegir(recientes, respuesta);
                if (elegido != null) {
                    detalle(elegido);
                }
            }
        }
    }

    // Lo usan también las vistas de parches y de la ruta
    static void abrirComoLlegar(Pantalla pantalla, Lugar lugar) {
        String url = NavegacionMaps.urlComoLlegar(lugar);
        if (NavegacionMaps.abrirEnNavegador(url)) {
            pantalla.exito("Abrimos Google Maps en tu navegador con la ruta a " + lugar.getNombre() + ".");
        } else {
            pantalla.aviso("No se pudo abrir el navegador. Copia este enlace: " + url);
        }
    }

    private String textoOpcional(String texto) {
        if (texto == null) {
            return pantalla.gris("No disponible");
        }
        return texto;
    }
}
