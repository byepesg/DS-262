package com.parchapp.dominio;

// Rasgos de un lugar para el cuestionario. Cada uno es un bit de un int, así que cruzar
// lo que tiene un lugar con lo que se busca es un AND.
public enum Etiqueta {
    ROMANTICO("romántico"),
    EMOCIONANTE("emocionante"),
    PAISAJE("con vista"),
    TRANQUILO("tranquilo"),
    SOCIAL("para socializar"),
    ALCOHOL("con tragos"),
    COMIDA("para comer"),
    ACTIVIDAD("activo"),
    CULTURAL("cultural"),
    AIRE_LIBRE("al aire libre"),
    GRUPOS("para grupos"),
    FAMILIAR("en familia"),
    IDEAL_SOLO("ideal solo"),
    NOCTURNO("de noche");

    private final String descripcion;

    Etiqueta(String descripcion) {
        this.descripcion = descripcion;
    }

    public int bit() {
        return 1 << ordinal();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public static int mascara(Etiqueta... etiquetas) {
        int mascara = 0;
        for (Etiqueta etiqueta : etiquetas) {
            mascara |= etiqueta.bit();
        }
        return mascara;
    }

    // "romántico · con vista": para explicar por qué se recomienda un lugar
    public static String describir(int mascara) {
        String texto = "";
        for (Etiqueta etiqueta : values()) {
            if ((mascara & etiqueta.bit()) != 0) {
                texto += (texto.isEmpty() ? "" : " · ") + etiqueta.descripcion;
            }
        }
        return texto;
    }
}
