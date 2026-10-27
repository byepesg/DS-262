import controlador.RestauranteControlador;
import vista.Ventana;

public class Main {
    public static void main(String[] args) {
        
        // Inicializar la interfaz gráfica 
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                Ventana miVentana = new Ventana();          
               
                RestauranteControlador controladorPrincipal = new RestauranteControlador(miVentana);
                
                
                miVentana.setVisible(true);
            }
        });
    }
}
