package modelo.entidades;

import java.util.ArrayList;
public class Factura{
    private int numeroMesa;
    private double totalPagado;
    private ArrayList<Pedido> orden;
    
    public Factura(){
        this.numeroMesa=0;
        this.totalPagado=0;
        this.orden=null;
    }
    
    public void setNumeroMesa(int NumeroMesa){
        numeroMesa=NumeroMesa;
    }
    
    public void setTotalPagado(double TotalPagado){
        totalPagado=TotalPagado;
    }
    
    public void setOrden(ArrayList<Pedido> Orden){
        orden=Orden;
    }
    
    public int getNumeroMesa(){
        return numeroMesa;
    }
    
    public double getTotalPagado(){
        return totalPagado;
    }
    
    public ArrayList<Pedido> getOrden(){
        return orden;
    }
}
