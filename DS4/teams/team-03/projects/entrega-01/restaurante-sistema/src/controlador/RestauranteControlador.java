package controlador;

import vista.Ventana;
import modelo.entidades.Plato;
import java.util.ArrayList;

public class RestauranteControlador {

    private Ventana ventanaPrincipal;
    private PanelMeseroControlador meseroControlador;
    private PlatoControlador platoControlador;

    public RestauranteControlador(Ventana ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.platoControlador = new PlatoControlador();
        
        inicializarDatosBase();
    }

    private void inicializarDatosBase() {
        // Cargar los meseros y mesas estáticos ( cambiar a un json PENDIENTE)
        String[] meseros = {"Nicolás", "María", "Juan"};
        ventanaPrincipal.cargarMeseros(meseros);
        ventanaPrincipal.cargarMesas(5);
        
        
        ArrayList<Plato> platosBD = platoControlador.cargarPlatosDesdeJSON();
        
        String[] nombresPlatos = new String[platosBD.size()];
        for (int i = 0; i < platosBD.size(); i++) {
            nombresPlatos[i] = platosBD.get(i).getNombre();
        }
        
        // POBLAR LISTA DE PLATOS
        ventanaPrincipal.cargarPlatos(nombresPlatos);        
        
        this.meseroControlador = new PanelMeseroControlador(this.ventanaPrincipal, platosBD);
    }
}
