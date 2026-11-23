package com.parchapp.dominio;

public enum Categoria {
    COMIDA("Comida"),
    CAFE("Cafés"),
    RUMBA("Rumba"),
    CULTURA("Cultura"),
    MIRADOR("Miradores"),
    PARQUE("Parques"),
    DEPORTE("Deporte"),
    DIVERSION("Diversión"),
    AVENTURA("Aventura"),
    ESTUDIO("Estudio");

    private final String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
