package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import java.util.Locale;

// Usa Google Maps URLs, que no necesitan clave de API.
// Aquí solo se arma el enlace; abrirlo depende de la interfaz (navegador en consola, Intent en Android).
public class NavegacionMaps {

    public static String urlComoLlegar(Lugar lugar) {
        // Locale.ROOT obliga a usar punto decimal. Con el sistema en español, %f escribiría
        // "4,638060" y Google recibiría cuatro números en vez de dos coordenadas.
        return String.format(Locale.ROOT, "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f",
                lugar.getLatitud(), lugar.getLongitud());
    }
}
