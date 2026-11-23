package com.parchapp.dominio;

// Punto desde donde el usuario explora. Mientras no usemos GPS, el usuario lo elige a mano.
public class Ubicacion {

    private static final double RADIO_TIERRA_KM = 6371;

    private final String nombre;
    private final double latitud;
    private final double longitud;

    public Ubicacion(String nombre, double latitud, double longitud) {
        if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Coordenadas fuera de rango");
        }
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Ubicacion(Lugar lugar) {
        this(lugar.getNombre(), lugar.getLatitud(), lugar.getLongitud());
    }

    public double distanciaKm(Lugar lugar) {
        return distanciaKm(latitud, longitud, lugar.getLatitud(), lugar.getLongitud());
    }

    // Fórmula de Haversine: distancia en línea recta sobre la Tierra, no por calles
    public static double distanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double difLatitud = Math.toRadians(lat2 - lat1);
        double difLongitud = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(difLatitud / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(difLongitud / 2), 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(a));
    }

    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
