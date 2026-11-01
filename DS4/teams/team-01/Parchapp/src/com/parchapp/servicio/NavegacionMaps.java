package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import java.awt.Desktop;
import java.net.URI;
import java.util.Locale;

// Usa Google Maps URLs, que no necesitan clave de API.
public class NavegacionMaps {

    public static String urlComoLlegar(Lugar lugar) {
        // Locale.ROOT obliga a usar punto decimal. Con el sistema en español, %f escribiría
        // "4,638060" y Google recibiría cuatro números en vez de dos coordenadas.
        return String.format(Locale.ROOT, "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f",
                lugar.getLatitud(), lugar.getLongitud());
    }

    // Devuelve false si el sistema no deja abrir el navegador; en ese caso se muestra el enlace
    public static boolean abrirEnNavegador(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
