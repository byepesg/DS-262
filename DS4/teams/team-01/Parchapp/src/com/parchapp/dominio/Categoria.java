package com.parchapp.dominio;

public enum Categoria {

    ESTUDIO("Estudio"),
    CULTURA("Cultura"),
    DEPORTE("Deporte"),
    PARQUE("Parques");

    private final String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
