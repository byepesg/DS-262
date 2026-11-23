package controlador;

import vista.Ventana;
import modelo.entidades.Factura;
import modelo.entidades.Pedido;
import modelo.entidades.Plato;
import modelo.estructuras.Cola;
import modelo.estructuras.Pila;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class PanelMeseroControlador {

    private Ventana vista;
    private ArrayList<Plato> listaPlatosDisponibles;
    private Map<Integer, ArrayList<Plato>> ordenesPorMesa;
    private Cola<Pedido> colaCocina;
    private Pila<Factura> historialFacturas;
    private int cantidadMesas;
    private boolean actualizandoVistaMesas;

    public PanelMeseroControlador(
            Ventana vista,
            ArrayList<Plato> listaPlatosDisponibles,
            Cola<Pedido> colaCocina,
            Pila<Factura> historialFacturas,
            int cantidadMesas) {
        this.vista = vista;
        this.listaPlatosDisponibles = listaPlatosDisponibles;
        this.ordenesPorMesa = new HashMap<>();
        this.colaCocina = colaCocina;
        this.historialFacturas = historialFacturas;
        this.cantidadMesas = cantidadMesas;
        this.actualizandoVistaMesas = false;

        for (int i = 1; i <= cantidadMesas; i++) {
            ordenesPorMesa.put(i, new ArrayList<>());
        }

        inicializarEventos();
        refrescarDesplegableMesas();
    }

    private void inicializarEventos() {
        // Evento para añadir plato (+)
        vista.alAgregarPlato(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarPlato();
            }
        });

        // Evento Quitar plato de la lista
        vista.alQuitarPedido(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int posicionEliminada = Integer.parseInt(e.getActionCommand());
                quitarPlato(posicionEliminada);
            }
        });

        // Evento al cambiar de mesa en el desplegable
        vista.alSeleccionarMesa(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!actualizandoVistaMesas) {
                    cargarOrdenDeMesaSeleccionada();
                }
            }
        });

        // Evento para reiniciar la mesa
        vista.alReiniciarMesa(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reiniciarMesa();
            }
        });

        // Evento para generar factura y enviar pedido a cocina
        vista.alGenerarFactura(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarFactura();
            }
        });

        // Evento para revertir la última factura generada (LIFO)
        vista.alRevertirFactura(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revertirFactura();
            }
        });
    }

    private int obtenerNumeroMesaActual() {
        int indice = vista.obtenerIndiceMesaSeleccionada();
        return (indice >= 0) ? indice + 1 : 1;
    }

    private ArrayList<Plato> obtenerOrdenMesaActual() {
        return ordenesPorMesa.get(obtenerNumeroMesaActual());
    }

    private void cargarOrdenDeMesaSeleccionada() {
        ArrayList<Plato> ordenActual = obtenerOrdenMesaActual();
        vista.limpiarPedidosMesero();

        if (ordenActual != null) {
            for (Plato plato : ordenActual) {
                vista.agregarPedidoMesero(plato.getNombre(), String.format("%.2f", plato.getPrecio()));
            }
        }
        recalcularTotal();
    }

    private void agregarPlato() {
        String nombreSeleccionado = vista.obtenerPlatoSeleccionado();

        if (nombreSeleccionado == null || nombreSeleccionado.equals("Escoja el plato")) {
            return;
        }

        Plato platoObj = buscarPlatoPorNombre(nombreSeleccionado);
        if (platoObj != null) {
            obtenerOrdenMesaActual().add(platoObj);
            vista.agregarPedidoMesero(platoObj.getNombre(), String.format("%.2f", platoObj.getPrecio()));
            recalcularTotal();
            refrescarDesplegableMesas();
        }
    }

    private void quitarPlato(int posicion) {
        ArrayList<Plato> ordenActual = obtenerOrdenMesaActual();
        if (posicion >= 0 && posicion < ordenActual.size()) {
            ordenActual.remove(posicion);
            recalcularTotal();
            refrescarDesplegableMesas();
        }
    }

    private void recalcularTotal() {
        double total = 0.0;
        for (Plato plato : obtenerOrdenMesaActual()) {
            total += plato.getPrecio();
        }
        vista.actualizarTotal(String.format("%.2f", total));
    }

    private void reiniciarMesa() {
        obtenerOrdenMesaActual().clear();
        vista.limpiarPedidosMesero();
        vista.actualizarTotal("0.00");
        refrescarDesplegableMesas();
    }

    private void generarFactura() {
        ArrayList<Plato> ordenActual = obtenerOrdenMesaActual();
        if (ordenActual.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe agregar al menos un plato para generar la factura.");
            return;
        }

        int numeroMesa = obtenerNumeroMesaActual();
        String mesaTexto = String.valueOf(numeroMesa);

        ArrayList<Pedido> ordenFactura = new ArrayList<>();
        double totalFactura = 0.0;

        for (Plato plato : ordenActual) {
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setNumeroMesa(numeroMesa);
            nuevoPedido.setPlato(plato);

            ordenFactura.add(nuevoPedido);
            totalFactura += plato.getPrecio();

            colaCocina.encolar(nuevoPedido);
            vista.agregarPedidoCocina(plato.getNombre(), mesaTexto);
        }

        Factura nuevaFactura = new Factura();
        nuevaFactura.setNumeroMesa(numeroMesa);
        nuevaFactura.setTotalPagado(totalFactura);
        nuevaFactura.setOrden(ordenFactura);
        historialFacturas.push(nuevaFactura);

        reiniciarMesa();
    }

    private void revertirFactura() {
        if (historialFacturas.estaVacia()) {
            JOptionPane.showMessageDialog(vista, "No hay facturas recientes para revertir.");
            return;
        }

        Factura ultimaFactura = historialFacturas.pop();
        ArrayList<Pedido> pedidosFactura = ultimaFactura.getOrden();

        Cola<Pedido> colaAuxiliar = new Cola<>();
        int pedidosCanceladosCocina = 0;

        while (!colaCocina.estaVacia()) {
            Pedido actual = colaCocina.desencolar();
            if (pedidosFactura != null && pedidosFactura.contains(actual)) {
                pedidosCanceladosCocina++;
            } else {
                colaAuxiliar.encolar(actual);
            }
        }

        vista.limpiarPedidosCocina();
        while (!colaAuxiliar.estaVacia()) {
            Pedido restante = colaAuxiliar.desencolar();
            colaCocina.encolar(restante);
            vista.agregarPedidoCocina(
                restante.getPlato().getNombre(),
                String.valueOf(restante.getNumeroMesa())
            );
        }

        int mesaRevertida = ultimaFactura.getNumeroMesa();
        ArrayList<Plato> ordenMesaRevertida = ordenesPorMesa.get(mesaRevertida);
        ordenMesaRevertida.clear();

        if (pedidosFactura != null) {
            for (Pedido pedido : pedidosFactura) {
                if (pedido.getPlato() != null) {
                    ordenMesaRevertida.add(pedido.getPlato());
                }
            }
        }

        actualizandoVistaMesas = true;
        try {
            vista.seleccionarIndiceMesa(mesaRevertida - 1);
        } finally {
            actualizandoVistaMesas = false;
        }

        cargarOrdenDeMesaSeleccionada();
        refrescarDesplegableMesas();

        JOptionPane.showMessageDialog(
            vista,
            "Factura de la Mesa " + mesaRevertida
                + " ($" + String.format("%.2f", ultimaFactura.getTotalPagado()) + ") revertida.\n"
                + "Pedidos retirados de cocina: " + pedidosCanceladosCocina
                + ". Puede modificar la orden o pulsar 'Reiniciar Mesa'."
        );
    }

    public void refrescarDesplegableMesas() {
        String[] opciones = new String[cantidadMesas];

        for (int i = 1; i <= cantidadMesas; i++) {
            int platosSinFacturar = ordenesPorMesa.get(i).size();
            int platosEnCocina = contarPedidosPendientesEnCocina(i);
            int totalPlatos = platosSinFacturar + platosEnCocina;

            if (totalPlatos == 0) {
                opciones[i - 1] = "Mesa " + i + " (Libre)";
            } else if (totalPlatos == 1) {
                opciones[i - 1] = "Mesa " + i + " (Ocupada - 1 plato)";
            } else {
                opciones[i - 1] = "Mesa " + i + " (Ocupada - " + totalPlatos + " platos)";
            }
        }

        actualizandoVistaMesas = true;
        try {
            vista.cargarMesas(opciones);
        } finally {
            actualizandoVistaMesas = false;
        }
    }

    private int contarPedidosPendientesEnCocina(int numeroMesa) {
        int contador = 0;
        Cola<Pedido> colaAuxiliar = new Cola<>();

        while (!colaCocina.estaVacia()) {
            Pedido actual = colaCocina.desencolar();
            if (actual.getNumeroMesa() == numeroMesa) {
                contador++;
            }
            colaAuxiliar.encolar(actual);
        }

        while (!colaAuxiliar.estaVacia()) {
            colaCocina.encolar(colaAuxiliar.desencolar());
        }

        return contador;
    }

    private Plato buscarPlatoPorNombre(String nombrePlato) {
        for (Plato plato : listaPlatosDisponibles) {
            if (plato.getNombre().equalsIgnoreCase(nombrePlato)) {
                return plato;
            }
        }
        return null;
    }
}