package vista;

import java.awt.*; //para importar todas las clases de los paquetes
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class Ventana extends JFrame{

    private JTabbedPane pestagnas;
    private JPanel panelMesero;
    private JPanel panelCocina;

    //Atributos panel mesero
    private JPanel superiorMesero;
    private JPanel centralMesero;
    private JPanel inferiorMesero;

    private JComboBox<String> meseroBtn;
    private JComboBox<String> mesaBtn;

    private JPanel listaPedidos;
    private JLabel totalLabel;
    private JComboBox<String> selectorPlato;
    private JButton agregarPlato;
    private ActionListener quitarPedidoListener;

    private JButton generarFactura;
    private JButton revertirFactura;
    private JButton reiniciarMesa;

    //Atributos panel cocina
    private JPanel listaPedidosCocina;
    private ActionListener pedidoCompletadoListener;


    public Ventana(){

        //Configs básicas
        this.setSize(1000, 1000);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE); //Determina que pasa al cerrar la ventana
        this.setTitle("restaurante-sistema");
        this.setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.black);

        iniciarPanelMesero();
        iniciarPanelCocina();;

        pestagnas = new JTabbedPane();
        pestagnas.addTab("Panel Mesero", panelMesero);
        pestagnas.addTab("Panel Cocina", panelCocina);
        this.add(pestagnas);
        
        this.setVisible(true);

    }

    private void iniciarPanelMesero(){
        panelMesero= new JPanel(new BorderLayout());

        superiorMesero = new JPanel(new GridLayout(1, 2, 5, 5));
        centralMesero = new JPanel(new BorderLayout());
        inferiorMesero = new JPanel(new GridLayout(1, 3, 5, 5));
        

        ////para diferenciar por color
        //superiorMesero.setBackground(Color.BLUE);
        //centralMesero.setBackground(Color.yellow);
        //inferiorMesero.setBackground(Color.green);

        //Panel superior

        meseroBtn = new JComboBox<>(new String[]{"Mesero x"});
        mesaBtn = new JComboBox<>(new String[]{"1"});

        JPanel mesa = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JPanel mesero = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        mesa.add(new JLabel("Mesa: "));
        mesa.add(mesaBtn);

        mesero.add(new JLabel("Mesero: "));
        mesero.add(meseroBtn);
        
        superiorMesero.add(mesa);
        superiorMesero.add(mesero);


        //Panel central
        JPanel encabezadoPedidos = new JPanel(new GridLayout(1, 3, 5, 5));
        encabezadoPedidos.add(new JLabel("Plato"));
        encabezadoPedidos.add(new JLabel("Precio"));
        encabezadoPedidos.add(new JLabel(" "));
        encabezadoPedidos.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        listaPedidos = new JPanel();
        listaPedidos.setLayout(new BoxLayout(listaPedidos, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listaPedidos);

        selectorPlato = new JComboBox<>(new String[]{"Escoja el plato"});
        agregarPlato = new JButton("+");
        totalLabel = new JLabel("Total: ");
        JPanel resumen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        resumen.add(selectorPlato);
        resumen.add(agregarPlato);
        resumen.add(totalLabel);

        centralMesero.add(encabezadoPedidos, BorderLayout.NORTH);
        centralMesero.add(scroll, BorderLayout.CENTER);
        centralMesero.add(resumen, BorderLayout.SOUTH);
        
        //Panel inferior
        generarFactura = new JButton("Generar Factura");
        revertirFactura = new JButton("Revertir Factura");
        reiniciarMesa = new JButton("Reiniciar Mesa");

        inferiorMesero.add(generarFactura);
        inferiorMesero.add(revertirFactura);
        inferiorMesero.add(reiniciarMesa);

        panelMesero.add(superiorMesero, BorderLayout.NORTH);
        panelMesero.add(centralMesero, BorderLayout.CENTER);
        panelMesero.add(inferiorMesero, BorderLayout.SOUTH);
        panelMesero.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //Margenes
    }

    private void iniciarPanelCocina(){
        panelCocina= new JPanel(new BorderLayout());

        JPanel encabezadoPedidos = new JPanel(new GridLayout(1, 3, 5, 5));
        encabezadoPedidos.add(new JLabel("Plato"));
        encabezadoPedidos.add(new JLabel("Mesa"));
        encabezadoPedidos.add(new JLabel(" "));
        encabezadoPedidos.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        listaPedidosCocina = new JPanel();
        listaPedidosCocina.setLayout(new BoxLayout(listaPedidosCocina, BoxLayout.Y_AXIS));

        panelCocina.add(encabezadoPedidos, BorderLayout.NORTH);
        panelCocina.add(new JScrollPane(listaPedidosCocina), BorderLayout.CENTER);
        panelCocina.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); //Margenes
    }


    //---------------------------------PARA CONTROLADOR----------------------------------

    public void cargarMeseros(String[] nombres) { //Poner las opciones de meseros
        meseroBtn.removeAllItems();
        for (String nombre : nombres) {
            meseroBtn.addItem(nombre);
        }
    }

    public void cargarMesas(int cantidad) { //Poner las opciones de mesas
        mesaBtn.removeAllItems();
        for (int i=1; i<=cantidad; i++) {
            mesaBtn.addItem(String.valueOf(i));
        }
    }

    public void cargarPlatos(String[] nombres) { //Poner las opciones de platos
        selectorPlato.removeAllItems();
        selectorPlato.addItem("Escoja el plato");
        for (String nombre : nombres) {
            selectorPlato.addItem(nombre);
        }
    }
        // Saber cual selecciono
    public String obtenerMeseroSeleccionado() {
        return (String) meseroBtn.getSelectedItem();
    }

    public String obtenerMesaSeleccionada() {
        return (String) mesaBtn.getSelectedItem();
    }

    public String obtenerPlatoSeleccionado() {
        return (String) selectorPlato.getSelectedItem();
    }

    public void actualizarTotal(String valor) {
        totalLabel.setText("Total: " + valor);
    }

    public void agregarPedidoMesero(String plato, String precio) { 
        JPanel filaPedido = new JPanel(new GridLayout(1, 3, 5, 5));
        filaPedido.add(new JLabel(plato));
        filaPedido.add(new JLabel(precio));

        filaPedido.setMaximumSize(new Dimension(Integer.MAX_VALUE, filaPedido.getPreferredSize().height+20));

        JButton quitar = new JButton("Quitar");
        quitar.addActionListener((ActionEvent evento) -> {
            int posicion = listaPedidos.getComponentZOrder(filaPedido);
            quitarPedidoMesero(posicion);

            if (quitarPedidoListener != null) {
                quitarPedidoListener.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, String.valueOf(posicion)));
            }
        });
        JPanel quitarPanel =new JPanel();
        quitarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); //Margenes
        quitarPanel.add(quitar);
        filaPedido.add(quitarPanel);

        listaPedidos.add(filaPedido);
        listaPedidos.revalidate();
        listaPedidos.repaint();
    }

    private void quitarPedidoMesero(int posicion) { //Se usa únicamente en esta clase, ntp
        if (posicion >= 0 && posicion < listaPedidos.getComponentCount()) {
            listaPedidos.remove(posicion);
            listaPedidos.revalidate();
            listaPedidos.repaint();
        }
    }

    public void limpiarPedidosMesero() { //Borra todos los platos de la pantalla
        listaPedidos.removeAll();
        listaPedidos.revalidate();
        listaPedidos.repaint();
    }

    public void agregarPedidoCocina(String plato, String mesa) { 
        JPanel filaPedido = new JPanel(new GridLayout(1, 3, 5, 5));
        filaPedido.add(new JLabel(plato));
        filaPedido.add(new JLabel(mesa));

        JButton completado = new JButton("Completado");
        completado.addActionListener((ActionEvent evento) -> {
            listaPedidosCocina.remove(filaPedido);
            listaPedidosCocina.revalidate();
            listaPedidosCocina.repaint();

            if (pedidoCompletadoListener != null) {
                pedidoCompletadoListener.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, plato + ";" + mesa));
            }
        });
        JPanel completadoPanel =new JPanel();
        completadoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); //Margenes
        completadoPanel.add(completado);
        filaPedido.add(completadoPanel);
        filaPedido.setMaximumSize(new Dimension(Integer.MAX_VALUE, filaPedido.getPreferredSize().height + 20));
        listaPedidosCocina.add(filaPedido);
        listaPedidosCocina.revalidate();
        listaPedidosCocina.repaint();
    }

    public void limpiarPedidosCocina() {
        listaPedidosCocina.removeAll();
        listaPedidosCocina.revalidate();
        listaPedidosCocina.repaint();
    }

    //Para que se definan funciones al presionar el boton:
    //(O seleccionar de un desplegable)

    public void alGenerarFactura(ActionListener listener) {
        generarFactura.addActionListener(listener);
    }

    public void alRevertirFactura(ActionListener listener) {
        revertirFactura.addActionListener(listener);
    }

    public void alReiniciarMesa(ActionListener listener) {
        reiniciarMesa.addActionListener(listener);
    }
        //añade el plato a la cuenta
    public void alAgregarPlato(ActionListener listener) { //este es el boton "+"
        agregarPlato.addActionListener(listener);
    }

    public void alSeleccionarPlato(ActionListener listener) {
        selectorPlato.addActionListener(listener);
    }

    public void alSeleccionarMesero(ActionListener listener) {
        meseroBtn.addActionListener(listener);
    }

    public void alSeleccionarMesa(ActionListener listener) {
        mesaBtn.addActionListener(listener);
    }

    public void alQuitarPedido(ActionListener listener) {
        quitarPedidoListener = listener;
    }

    public void alCompletarPedido(ActionListener listener) {
        pedidoCompletadoListener = listener;
    }


}