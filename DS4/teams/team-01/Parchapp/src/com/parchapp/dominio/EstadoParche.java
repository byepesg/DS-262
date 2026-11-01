package com.parchapp.dominio;

public enum EstadoParche {

    ABIERTO("Abierto"),
    LLENO("Lleno"),
    CANCELADO("Cancelado"),
    FINALIZADO("Finalizado");

    private final String nombre;

    EstadoParche(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
