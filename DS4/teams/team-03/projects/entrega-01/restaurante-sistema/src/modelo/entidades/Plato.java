package modelo.entidades;

public class Plato{
    private String nombre;
    private double precio;
    
    public Plato(){
        this.nombre=null;
        this.precio=0;
    }
    
    public void setNombre(String Nombre){
        nombre=Nombre;
    }
    
    public void setPrecio(double Precio){
        precio=Precio;
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public double getPrecio(){
        return precio;
    }
}
