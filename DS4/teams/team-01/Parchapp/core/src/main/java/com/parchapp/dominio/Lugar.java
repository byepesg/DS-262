package com.parchapp.dominio;

// La dirección, la zona y la descripción pueden ser null: si no tenemos el dato, no lo inventamos.
// precio: 0 = gratis, 1 = $, 2 = $$, 3 = $$$. etiquetas: bits de Etiqueta.
public class Lugar {

    public static final int PRECIO_MAXIMO = 3;

    private final String id;
    private final String nombre;
    private final Categoria categoria;
    private final String direccion;
    private final double latitud;
    private final double longitud;
    private final String descripcion;
    private final String zona;
    private final int precio;
    private final int etiquetas;

    public Lugar(String id, String nombre, Categoria categoria, String direccion,
                 double latitud, double longitud, String descripcion) {
        this(id, nombre, categoria, direccion, null, latitud, longitud, 0, 0, descripcion);
    }

    public Lugar(String id, String nombre, Categoria categoria, String direccion, String zona,
                 double latitud, double longitud, int precio, int etiquetas, String descripcion) {
        if (precio < 0 || precio > PRECIO_MAXIMO) {
            throw new IllegalArgumentException("Precio invalido para " + nombre);
        }
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
        this.zona = zona;
        this.precio = precio;
        this.etiquetas = etiquetas;
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

    public String getZona() {
        return zona;
    }

    public int getPrecio() {
        return precio;
    }

    public int getEtiquetas() {
        return etiquetas;
    }

    public boolean tiene(Etiqueta etiqueta) {
        return (etiquetas & etiqueta.bit()) != 0;
    }

    // mismo id = mismo lugar (lo usan contiene() y eliminar())
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
