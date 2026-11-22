package com.parchapp.dominio;

// La dirección y la descripción pueden ser null: si no tenemos el dato, no lo inventamos.
public class Lugar {

    private final String id;
    private final String nombre;
    private final Categoria categoria;
    private final String direccion;
    private final double latitud;
    private final double longitud;
    private final String descripcion;

    public Lugar(String id, String nombre, Categoria categoria, String direccion,
                 double latitud, double longitud, String descripcion) {
        if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
            throw new IllegalArgumentException("Coordenadas invalidas para " + nombre);
        }
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.descripcion = descripcion;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public String getDireccion() {
        return direccion;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getDescripcion() {
        return descripcion;
    }

    // Dos lugares son el mismo si tienen el mismo id. Lo usan contiene() y eliminar() de las estructuras.
    @Override
    public boolean equals(Object otro) {
        if (!(otro instanceof Lugar)) {
            return false;
        }
        return id.equals(((Lugar) otro).id);
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
