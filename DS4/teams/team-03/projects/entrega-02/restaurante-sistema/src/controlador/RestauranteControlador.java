package controlador;

import vista.Ventana;
import modelo.entidades.Factura;
import modelo.entidades.Pedido;
import modelo.entidades.Plato;
import modelo.estructuras.Cola;
import modelo.estructuras.Pila;
import java.util.ArrayList;

public class RestauranteControlador {

    private Ventana ventanaPrincipal;
    private PanelMeseroControlador meseroControlador;
    private PanelCocinaControlador cocinaControlador;
    private PlatoControlador platoControlador;

    private Cola<Pedido> colaCocina;
    private Pila<Factura> historialFacturas;

    public RestauranteControlador(Ventana ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        this.platoControlador = new PlatoControlador();
        this.colaCocina = new Cola<>();
        this.historialFacturas = new Pila<>();

        inicializarDatosBase();
    }

    private void inicializarDatosBase() {
        int cantidadMesas = 5;
        String[] meseros = {"Nicolás", "María", "Juan"};
        ventanaPrincipal.cargarMeseros(meseros);
        ventanaPrincipal.cargarMesas(cantidadMesas);

        ArrayList<Plato> platosBD = platoControlador.cargarPlatosDesdeJSON();

        String[] nombresPlatos = new String[platosBD.size()];
        for (int i = 0; i < platosBD.size(); i++) {
            nombresPlatos[i] = platosBD.get(i).getNombre();
        }

        ventanaPrincipal.cargarPlatos(nombresPlatos);

        this.meseroControlador = new PanelMeseroControlador(
            this.ventanaPrincipal,
            platosBD,
            colaCocina,
            historialFacturas,
            cantidadMesas
        );

        this.cocinaControlador = new PanelCocinaControlador(
            this.ventanaPrincipal,
            colaCocina,
            () -> meseroControlador.refrescarDesplegableMesas()
        );
    }
}