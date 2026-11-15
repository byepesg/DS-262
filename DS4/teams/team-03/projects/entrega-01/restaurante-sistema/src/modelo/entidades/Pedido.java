package modelo.entidades;

public class Pedido{
    private int numeroMesa;
    private Plato plato;
    
    public Pedido(){
        this.numeroMesa=0;
        this.plato=null;
    }
    
    public void setNumeroMesa(int NumeroMesa){
        numeroMesa=NumeroMesa;
    }
    
    public void setPlato(Plato inPlato){
        plato=inPlato;
    }
    
    public int getNumeroMesa(){
        return numeroMesa;
    }
    
    public Plato getPlato(){
        return plato;
    }
}