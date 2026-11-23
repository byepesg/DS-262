package controlador;

import vista.Ventana;
import modelo.entidades.Pedido;
import modelo.estructuras.Cola;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PanelCocinaControlador {

    private Ventana vista;
    private Cola<Pedido> colaCocina;
    private Runnable alActualizarEstadoMesas;

    public PanelCocinaControlador(Ventana vista, Cola<Pedido> colaCocina, Runnable alActualizarEstadoMesas) {
        this.vista = vista;
        this.colaCocina = colaCocina;
        this.alActualizarEstadoMesas = alActualizarEstadoMesas;

        inicializarEventos();
    }

    private void inicializarEventos() {
        vista.alCompletarPedido(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String comando = e.getActionCommand();
                int separador = comando.lastIndexOf(";");

                if (separador != -1) {
                    String nombrePlato = comando.substring(0, separador);
                    int numeroMesa = Integer.parseInt(comando.substring(separador + 1));
                    completarPedido(nombrePlato, numeroMesa);
                }
            }
        });
    }

    private void completarPedido(String nombrePlato, int numeroMesa) {
        if (colaCocina.estaVacia()) {
            return;
        }

        Cola<Pedido> colaAuxiliar = new Cola<>();
        boolean eliminado = false;

        while (!colaCocina.estaVacia()) {
            Pedido actual = colaCocina.desencolar();

            if (!eliminado
                    && actual.getNumeroMesa() == numeroMesa
                    && actual.getPlato().getNombre().equalsIgnoreCase(nombrePlato)) {
                eliminado = true;
                System.out.println("Pedido completado y eliminado de la cola -> Mesa "
                        + numeroMesa + ": " + nombrePlato);
            } else {
                colaAuxiliar.encolar(actual);
            }
        }

        while (!colaAuxiliar.estaVacia()) {
            colaCocina.encolar(colaAuxiliar.desencolar());
        }

        if (eliminado && alActualizarEstadoMesas != null) {
            alActualizarEstadoMesas.run();
        }
    }
}