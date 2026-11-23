package com.parchapp.android;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

// Textos para mostrar: distancias, fechas e iniciales
final class Formato {

    static final Locale ES = Locale.forLanguageTag("es-CO");

    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("h:mm a", ES);
    private static final DateTimeFormatter DIA = DateTimeFormatter.ofPattern("EEE d 'de' MMM", ES);
    private static final DateTimeFormatter DIA_LARGO = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES);
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("MMM", ES);

    private Formato() {
    }

    static String distancia(double km) {
        if (km < 1) {
            return Math.round(km * 100) * 10 + " m";
        }
        return String.format(ES, "%.1f km", km);
    }

    // "Hoy · 4:00 p. m.", "Mañana · 2:00 p. m." o "Sáb 28 de nov · 11:00 a. m."
    static String cuando(LocalDateTime fecha) {
        return diaRelativo(fecha.toLocalDate(), DIA.format(fecha)) + " · " + HORA.format(fecha);
    }

    static String fechaLarga(LocalDateTime fecha) {
        return diaRelativo(fecha.toLocalDate(), DIA_LARGO.format(fecha)) + " · " + HORA.format(fecha);
    }

    static String dia(LocalDate fecha) {
        return diaRelativo(fecha, DateTimeFormatter.ofPattern("EEE d 'de' MMMM", ES).format(fecha));
    }

    static String hora(LocalDateTime fecha) {
        return HORA.format(fecha);
    }

    static String mesCorto(LocalDateTime fecha) {
        return MES.format(fecha).replace(".", "").toUpperCase(ES);
    }

    // "Gratis", "$", "$$" o "$$$"
    static String precio(int nivel) {
        if (nivel == 0) {
            return "Gratis";
        }
        String signos = "";
        for (int i = 0; i < nivel; i++) {
            signos += "$";
        }
        return signos;
    }

    static String saludo(String nombre) {
        int hora = java.time.LocalTime.now().getHour();
        String parte = hora < 12 ? "Buenos días" : hora < 19 ? "Buenas tardes" : "Buenas noches";
        return parte + ", " + nombre.split(" ")[0];
    }

    // "Chapinero · $$ · 1,2 km"
    static String metaLugar(com.parchapp.dominio.Lugar lugar, double km) {
        String zona = lugar.getZona() != null ? lugar.getZona() + " · " : "";
        return zona + precio(lugar.getPrecio()) + " · " + distancia(km);
    }

    static String iniciales(String nombre) {
        String[] partes = nombre.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1);
        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1);
        }
        return iniciales.toUpperCase(ES);
    }

    private static String diaRelativo(LocalDate fecha, String siNoEsCercano) {
        LocalDate hoy = LocalDate.now();
        if (fecha.equals(hoy)) {
            return "Hoy";
        }
        if (fecha.equals(hoy.plusDays(1))) {
            return "Mañana";
        }
        return siNoEsCercano.substring(0, 1).toUpperCase(ES) + siNoEsCercano.substring(1);
    }
}
