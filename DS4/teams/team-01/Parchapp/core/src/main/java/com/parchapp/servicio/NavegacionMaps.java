package com.parchapp.servicio;

import com.parchapp.dominio.Lugar;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

// Arma el enlace de Google Maps (no necesita API key). Cada interfaz lo abre a su manera.
public class NavegacionMaps {

    public static String urlComoLlegar(Lugar lugar) {
        // Con zona se busca por nombre: las coordenadas son aproximadas y así Maps atina a la entrada
        if (lugar.getZona() != null) {
            return "https://www.google.com/maps/dir/?api=1&destination="
                    + codificar(lugar.getNombre() + ", " + lugar.getZona() + ", Bogotá");
        }
        // Locale.ROOT por el punto decimal: en español %f escribe "4,638060" y se daña el enlace
        return String.format(Locale.ROOT, "https://www.google.com/maps/dir/?api=1&destination=%.6f,%.6f",
                lugar.getLatitud(), lugar.getLongitud());
    }

    private static String codificar(String texto) {
        try {
            return URLEncoder.encode(texto, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 siempre existe en Java", e);
        }
    }
}
