package controlador;

import vista.Ventana;
import modelo.entidades.Plato;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PanelMeseroControlador {

    private Ventana vista;
    private ArrayList<Plato> listaPlatosDisponibles;   
    private List<Double> preciosActuales;
    

    public PanelMeseroControlador(Ventana vista,ArrayList<modelo.entidades.Plato> listaPlatosDisponibles) {
        this.vista = vista;
        this.preciosActuales = new ArrayList<>();
        this.listaPlatosDisponibles = listaPlatosDisponibles; 
        
        inicializarEventos();
    }

    private void inicializarEventos() {
        // Evento para añadir plato
        vista.alAgregarPlato(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarPlato();
            }
        });

        // Evento Quitar
        vista.alQuitarPedido(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                int posicionEliminada = Integer.parseInt(e.getActionCommand());
                quitarPlato(posicionEliminada);
            }
        });

        // Evento para reiniciar la mesa 
        vista.alReiniciarMesa(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarMesa();
            }
        });
    }

    private void agregarPlato() {
        String platoSeleccionado = vista.obtenerPlatoSeleccionado();        
        
        if (platoSeleccionado == null || platoSeleccionado.equals("Escoja el plato")) {
            return;
        }

        double precio = obtenerPrecioDelPlato(platoSeleccionado);        
        preciosActuales.add(precio);        
        vista.agregarPedidoMesero(platoSeleccionado, String.format("%.2f", precio));
        
        recalcularTotal();
    }

    private void quitarPlato(int posicion) {
        
        if (posicion >= 0 && posicion < preciosActuales.size()) {
            
            preciosActuales.remove(posicion);            
            
            recalcularTotal();
        }
    }

    private void recalcularTotal() {
        double total = 0.0;        
        
        for (double precio : preciosActuales) {
            total += precio;
        }       
        
        vista.actualizarTotal(String.format("%.2f", total));
    }

    private void reiniciarMesa() {
        
        preciosActuales.clear();       
        
        vista.limpiarPedidosMesero();        
        
        vista.actualizarTotal("0.00");
    }

    
    private double obtenerPrecioDelPlato(String nombrePlato) {    
    for (modelo.entidades.Plato plato : listaPlatosDisponibles) {
        if (plato.getNombre().equalsIgnoreCase(nombrePlato)) {
            return plato.getPrecio(); 
        }
    }
    return 0.00; 
    }
}