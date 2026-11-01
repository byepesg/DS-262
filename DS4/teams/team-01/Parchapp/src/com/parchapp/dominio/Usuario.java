package com.parchapp.dominio;

public class Usuario {

    private final String id;
    private final String nombre;

    public Usuario(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object otro) {
        if (!(otro instanceof Usuario)) {
            return false;
        }
        return id.equals(((Usuario) otro).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
