package modelo.entidades;

public class Plato {
    private int id;
    private String nombre;
    private double precio;
    
    // Constructor original (no había id xd)
    public Plato() {
        this.id = 0;
        this.nombre = null;
        this.precio = 0;
    }

    // Constructor para la BD
    public Plato(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String Nombre) {
        nombre = Nombre;
    }
    
    public void setPrecio(double Precio) {
        precio = Precio;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public double getPrecio() {
        return precio;
    }
}