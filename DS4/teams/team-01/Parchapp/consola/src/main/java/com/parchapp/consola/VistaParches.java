package com.parchapp.consola;

import com.parchapp.dominio.EstadoParche;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.ResultadoUnion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

class VistaParches {

    private static final Locale ESPANOL = Locale.forLanguageTag("es-CO");
    private static final DateTimeFormatter FORMATO_DIA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ESPANOL);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("H:mm");
    // STRICT rechaza fechas que no existen, como 31/02/2026
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("d/M/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final Pantalla pantalla;
    private final Sesion sesion;

    VistaParches(Pantalla pantalla, Sesion sesion) {
        this.pantalla = pantalla;
        this.sesion = sesion;
    }

    void verDisponibles() {
        listar("Parches disponibles", null, false);
    }

    void verMios() {
        listar("Mis parches", null, true);
    }

    void verEnLugar(Lugar lugar) {
        listar("Parches en " + lugar.getNombre(), lugar, false);
    }

    // ---------------------------------------------------------------- listado

    private void listar(String titulo, Lugar lugar, boolean soloMios) {
        boolean porCercania = false;
        while (true) {
            LocalDateTime ahora = LocalDateTime.now();
            ListaEnlazada<Parche> parches;
            if (soloMios) {
                parches = sesion.gestor.delUsuario(sesion.usuario, ahora);
            } else if (lugar != null) {
                parches = sesion.gestor.activosEn(lugar, ahora);
            } else {
                parches = sesion.gestor.activos(ahora);
            }
            if (porCercania) {
                GestorParches.ordenarPorCercania(parches, sesion.ubicacion);
            } else {
                GestorParches.ordenarPorFecha(parches);
            }

            pantalla.nueva("Parches", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita(titulo) + pantalla.gris("   " + Pantalla.contar(parches.tamano(), "parche", "parches")
                    + " · ordenados por " + (porCercania ? "cercanía" : "fecha")));
            pantalla.vacio();
            if (parches.estaVacia()) {
                if (soloMios) {
                    pantalla.linea("  " + pantalla.amarillo("Todavía no tienes parches. Crea uno o únete a alguno."));
                } else {
                    pantalla.linea("  " + pantalla.amarillo("No hay parches activos por ahora. ¡Crea el primero!"));
                }
            }
            int numero = 1;
            for (Parche parche : parches) {
                imprimirTarjeta(numero, parche, ahora);
                numero++;
            }

            pantalla.titulo("Opciones");
            if (!parches.estaVacia()) {
                pantalla.nota("Escribe el número de un parche para ver el detalle y unirte.");
            }
            pantalla.opcion("N", "Crear un parche nuevo");
            pantalla.opcion("O", "Ordenar por " + (porCercania ? "fecha" : "cercanía"));
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("N")) {
                crear(lugar);
            } else if (respuesta.equals("O")) {
                porCercania = !porCercania;
            } else if (!respuesta.isEmpty()) {
                Parche elegido = pantalla.elegir(parches, respuesta);
                if (elegido != null) {
                    detalle(elegido);
                }
            }
        }
    }

    private void imprimirTarjeta(int numero, Parche parche, LocalDateTime ahora) {
        String estado = etiquetaEstado(parche);
        int anchoEstado = parche.getEstado().name().length() + 2;
        int anchoTitulo = Pantalla.ANCHO - 8 - anchoEstado;
        pantalla.linea("  " + pantalla.cian(Pantalla.ajustarDerecha(String.valueOf(numero), 3)) + "  "
                + pantalla.negrita(Pantalla.ajustar(parche.getTitulo(), anchoTitulo)) + " " + estado);

        String distancia = Pantalla.distancia(sesion.ubicacion.distanciaKm(parche.getLugar()));
        String cuandoYDonde = describirFecha(parche.getFechaHora(), ahora) + " · "
                + parche.getLugar().getNombre() + " · " + distancia;
        pantalla.linea("       " + pantalla.gris(Pantalla.recortar(cuandoYDonde, Pantalla.ANCHO - 7)));

        String cupos = parche.cantidadParticipantes() + "/" + parche.getCapacidad() + " personas";
        if (parche.cantidadEnEspera() > 0) {
            cupos += " · " + parche.cantidadEnEspera() + " en espera";
        }
        String miRol = describirMiRol(parche);
        if (!miRol.isEmpty()) {
            cupos += " · " + pantalla.cian(miRol);
        }
        pantalla.linea("       " + pantalla.barra(parche.cantidadParticipantes(), parche.getCapacidad()) + "  " + cupos);
        pantalla.vacio();
    }

    // ---------------------------------------------------------------- detalle

    void detalle(Parche parche) {
        while (true) {
            LocalDateTime ahora = LocalDateTime.now();
            parche.finalizarSiYaPaso(ahora);
            Usuario yo = sesion.usuario;

            pantalla.nueva("Parches » " + parche.getId(), sesion.resumen());
            pantalla.linea("  " + pantalla.negrita(parche.getTitulo()) + "  " + etiquetaEstado(parche));
            pantalla.vacio();
            pantalla.dato("Cuándo", describirFecha(parche.getFechaHora(), ahora));
            pantalla.dato("Dónde", parche.getLugar().getNombre() + " · "
                    + Pantalla.distancia(sesion.ubicacion.distanciaKm(parche.getLugar())) + " de " + sesion.ubicacion.getNombre());
            pantalla.dato("Organiza", parche.getCreador().getNombre());
            pantalla.dato("Cupos", pantalla.barra(parche.cantidadParticipantes(), parche.getCapacidad()) + "  "
                    + parche.cantidadParticipantes() + " de " + parche.getCapacidad() + " · " + describirCuposLibres(parche));
            pantalla.dato("Asistentes", listarNombres(parche.getParticipantes(), false));
            if (parche.cantidadEnEspera() > 0) {
                pantalla.dato("En espera", listarNombres(parche.getListaEspera(), true));
            }
            String miRol = describirMiRol(parche);
            pantalla.dato("Tú", miRol.isEmpty() ? "No estás en este parche" : miRol);

            boolean soyCreador = parche.esCreador(yo);
            boolean participo = parche.esParticipante(yo);
            boolean espero = parche.posicionEnEspera(yo) > 0;
            boolean puedoUnirme = parche.estaActivo() && !participo && !espero;
            boolean puedoSalir = parche.estaActivo() && participo && !soyCreador;
            boolean puedoCancelar = parche.estaActivo() && soyCreador;

            pantalla.titulo("¿Qué quieres hacer?");
            if (puedoUnirme && parche.getEstado() == EstadoParche.LLENO) {
                pantalla.opcion("U", "Anotarme en la lista de espera", "está lleno");
            } else if (puedoUnirme) {
                pantalla.opcion("U", "Unirme a este parche", describirCuposLibres(parche));
            }
            if (puedoSalir) {
                pantalla.opcion("S", "Salir del parche");
            }
            if (puedoCancelar) {
                pantalla.opcion("C", "Cancelar el parche");
            }
            pantalla.opcion("M", "Cómo llegar", "abre Google Maps");
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("U") && puedoUnirme) {
                unirse(parche);
            } else if (respuesta.equals("S") && puedoSalir) {
                salir(parche);
            } else if (respuesta.equals("C") && puedoCancelar) {
                cancelar(parche);
            } else if (respuesta.equals("M")) {
                VistaLugares.abrirComoLlegar(pantalla, parche.getLugar());
            } else if (!respuesta.isEmpty()) {
                pantalla.error("\"" + respuesta + "\" no es una opción válida.");
            }
        }
    }

    private void unirse(Parche parche) {
        ResultadoUnion resultado = parche.unirse(sesion.usuario);
        switch (resultado) {
            case UNIDO -> {
                pantalla.exito("¡Listo! Ahora haces parte de \"" + parche.getTitulo() + "\".");
                if (parche.getEstado() == EstadoParche.LLENO) {
                    pantalla.info("Ocupaste el último cupo: el parche quedó lleno.");
                }
            }
            case EN_LISTA_DE_ESPERA -> pantalla.aviso("El parche está lleno. Quedaste de #"
                    + parche.posicionEnEspera(sesion.usuario) + " en la lista de espera; si alguien sale, entras automáticamente.");
            case YA_PARTICIPA -> pantalla.aviso("Ya haces parte de este parche.");
            case YA_EN_ESPERA -> pantalla.aviso("Ya estás en la lista de espera.");
            case NO_DISPONIBLE -> pantalla.error("Este parche ya no recibe participantes.");
        }
    }

    private void salir(Parche parche) {
        if (!pantalla.confirmar("¿Seguro que quieres salir de \"" + parche.getTitulo() + "\"?")) {
            return;
        }
        Usuario quienEntra = parche.salir(sesion.usuario);
        pantalla.exito("Saliste del parche.");
        if (quienEntra != null) {
            pantalla.info(quienEntra.getNombre() + " estaba primero en la lista de espera y tomó tu cupo.");
        }
    }

    private void cancelar(Parche parche) {
        if (!pantalla.confirmar("¿Seguro que quieres cancelar el parche? No se puede deshacer")) {
            return;
        }
        parche.cancelar(sesion.usuario);
        pantalla.exito("Cancelaste \"" + parche.getTitulo() + "\". Ya no recibe participantes.");
    }

    // ---------------------------------------------------------------- crear

    void crear(Lugar lugarElegido) {
        Lugar lugar = lugarElegido;
        if (lugar == null) {
            lugar = elegirLugar();
            if (lugar == null) {
                return;
            }
        }

        pantalla.nueva("Parches » Nuevo", sesion.resumen());
        pantalla.linea("  " + pantalla.negrita("Nuevo parche en " + lugar.getNombre()));
        pantalla.nota("Responde cada pregunta. Escribe 0 en cualquier momento para cancelar.");

        String titulo = pedirTitulo(lugar);
        if (titulo == null) {
            return;
        }
        LocalDate dia = pedirDia();
        if (dia == null) {
            return;
        }
        LocalTime hora = pedirHora(dia);
        if (hora == null) {
            return;
        }
        int capacidad = pedirCapacidad();
        if (capacidad == 0) {
            return;
        }
        LocalDateTime fechaHora = dia.atTime(hora);

        pantalla.titulo("Revisa tu parche");
        pantalla.dato("Título", titulo);
        pantalla.dato("Lugar", lugar.getNombre());
        pantalla.dato("Cuándo", describirFecha(fechaHora, LocalDateTime.now()));
        pantalla.dato("Capacidad", capacidad + " personas, contándote a ti");
        if (!pantalla.confirmar("¿Crear el parche?")) {
            pantalla.aviso("No se creó el parche.");
            return;
        }

        // La pantalla ya validó los datos, pero las reglas de verdad están en el modelo:
        // si algo no cumple, el gestor o el parche lanzan la excepción con el motivo
        try {
            Parche parche = sesion.gestor.crear(sesion.usuario, lugar, titulo, fechaHora, capacidad, LocalDateTime.now());
            pantalla.exito("Creaste el parche " + parche.getId() + ". Ya aparece en Parches disponibles.");
            detalle(parche);
        } catch (IllegalArgumentException e) {
            pantalla.error("No se pudo crear el parche: " + e.getMessage());
            pantalla.pausa();
        }
    }

    private Lugar elegirLugar() {
        ListaEnlazada<Lugar> lugares = sesion.catalogo.todos();
        CatalogoLugares.ordenarPorNombre(lugares);
        while (true) {
            pantalla.nueva("Parches » Nuevo", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita("¿Dónde será el parche?"));
            pantalla.vacio();
            int numero = 1;
            for (Lugar lugar : lugares) {
                pantalla.linea("  " + pantalla.cian(Pantalla.ajustarDerecha(String.valueOf(numero), 3)) + "  "
                        + Pantalla.ajustar(lugar.getNombre(), 44) + pantalla.gris(lugar.getCategoria().getNombre()));
                numero++;
            }
            String respuesta = pantalla.preguntar("Número del lugar (0 para cancelar):");
            if (respuesta.equals("0")) {
                return null;
            }
            if (!respuesta.isEmpty()) {
                Lugar elegido = pantalla.elegir(lugares, respuesta);
                if (elegido != null) {
                    return elegido;
                }
            }
        }
    }

    private String pedirTitulo(Lugar lugar) {
        String porDefecto = Pantalla.recortar("Parche en " + lugar.getNombre(), Parche.LARGO_MAXIMO_TITULO);
        while (true) {
            String titulo = pantalla.preguntar("Título (Enter para \"" + porDefecto + "\"):");
            if (titulo.equals("0")) {
                return null;
            }
            if (titulo.isEmpty()) {
                return porDefecto;
            }
            if (titulo.length() <= Parche.LARGO_MAXIMO_TITULO) {
                return titulo;
            }
            pantalla.error("El título puede tener máximo " + Parche.LARGO_MAXIMO_TITULO + " caracteres.");
        }
    }

    private LocalDate pedirDia() {
        LocalDate hoy = LocalDate.now();
        while (true) {
            String texto = CatalogoLugares.normalizar(pantalla.preguntar("Día (hoy, mañana o dd/mm/aaaa):"));
            if (texto.equals("0")) {
                return null;
            }
            if (texto.equals("hoy")) {
                return hoy;
            }
            if (texto.equals("manana")) {
                return hoy.plusDays(1);
            }
            try {
                LocalDate dia = LocalDate.parse(texto, FORMATO_FECHA);
                if (!dia.isBefore(hoy)) {
                    return dia;
                }
                pantalla.error("Esa fecha ya pasó.");
            } catch (DateTimeParseException e) {
                pantalla.error("No entendí la fecha. Ejemplos: hoy, mañana, 25/10/2026.");
            }
        }
    }

    private LocalTime pedirHora(LocalDate dia) {
        while (true) {
            String texto = pantalla.preguntar("Hora en formato 24 h (por ejemplo 14:30):");
            if (texto.equals("0")) {
                return null;
            }
            try {
                LocalTime hora = LocalTime.parse(texto, FORMATO_HORA);
                if (dia.atTime(hora).isAfter(LocalDateTime.now())) {
                    return hora;
                }
                pantalla.error("Esa hora ya pasó. Elige una hora más tarde.");
            } catch (DateTimeParseException e) {
                pantalla.error("No entendí la hora. Usa el formato HH:MM, por ejemplo 9:00 o 18:30.");
            }
        }
    }

    // Devuelve 0 si el usuario cancela
    private int pedirCapacidad() {
        while (true) {
            String texto = pantalla.preguntar("¿Cuántas personas en total, contándote? (" + Parche.CAPACIDAD_MINIMA
                    + " a " + Parche.CAPACIDAD_MAXIMA + "):");
            if (texto.equals("0")) {
                return 0;
            }
            try {
                int capacidad = Integer.parseInt(texto);
                if (capacidad >= Parche.CAPACIDAD_MINIMA && capacidad <= Parche.CAPACIDAD_MAXIMA) {
                    return capacidad;
                }
            } catch (NumberFormatException e) {
                // se avisa abajo
            }
            pantalla.error("Escribe un número entre " + Parche.CAPACIDAD_MINIMA + " y " + Parche.CAPACIDAD_MAXIMA + ".");
        }
    }

    // ---------------------------------------------------------------- textos

    private String etiquetaEstado(Parche parche) {
        String texto = parche.getEstado().name();
        return switch (parche.getEstado()) {
            case ABIERTO -> pantalla.etiquetaVerde(texto);
            case LLENO -> pantalla.etiquetaAmarilla(texto);
            case CANCELADO -> pantalla.etiquetaRoja(texto);
            case FINALIZADO -> pantalla.etiquetaGris(texto);
        };
    }

    private String describirMiRol(Parche parche) {
        Usuario yo = sesion.usuario;
        if (parche.esCreador(yo)) {
            return "Tú lo organizas";
        }
        if (parche.esParticipante(yo)) {
            return "Estás inscrito";
        }
        int posicion = parche.posicionEnEspera(yo);
        if (posicion > 0) {
            return "Estás #" + posicion + " en la lista de espera";
        }
        return "";
    }

    private String describirCuposLibres(Parche parche) {
        int libres = parche.cuposLibres();
        if (libres == 0) {
            return "sin cupos";
        }
        if (libres == 1) {
            return "queda 1 cupo";
        }
        return "quedan " + libres + " cupos";
    }

    private String listarNombres(Iterable<Usuario> usuarios, boolean numerados) {
        String texto = "";
        int posicion = 1;
        for (Usuario usuario : usuarios) {
            if (!texto.isEmpty()) {
                texto += ", ";
            }
            if (numerados) {
                texto += posicion + ". ";
            }
            texto += usuario.getNombre();
            posicion++;
        }
        return texto;
    }

    static String describirFecha(LocalDateTime fecha, LocalDateTime ahora) {
        LocalDate dia = fecha.toLocalDate();
        LocalDate hoy = ahora.toLocalDate();
        String textoDia;
        if (dia.equals(hoy)) {
            textoDia = "Hoy";
        } else if (dia.equals(hoy.plusDays(1))) {
            textoDia = "Mañana";
        } else {
            textoDia = FORMATO_DIA.format(dia);
            textoDia = textoDia.substring(0, 1).toUpperCase() + textoDia.substring(1);
        }
        return textoDia + ", " + FORMATO_HORA.format(fecha);
    }
}
