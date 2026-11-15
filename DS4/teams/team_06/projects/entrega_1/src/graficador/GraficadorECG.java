//Función: Dibujar la señal, la cuadrícula, las anotaciones y la descripción; después, guardar la imagen en PNG.

//LIBRERIAS PARA PERSONALIZAR LOS GRAFICOS
//Define el grosor y el estilo de las lineas
import java.awt.BasicStroke;
//Permite establecer los colores de los elementos
import java.awt.Color;
//Permite definir el tipo, tamaño y estilo de la fuente
import java.awt.Font;
//Para dibujar lineas, texto y figuras en 2D
import java.awt.Graphics2D;
//Mejora la calidad visual
import java.awt.RenderingHints;
//Permite construir trazados mediante segmentos de líneas y curvas
import java.awt.geom.Path2D;

//LIBRERIAS PARA CREAR Y ALMACENAR LA IMAGEN DEL ECG
//
import java.awt.image.BufferedImage;
//Permite guardar la imagen generada
import javax.imageio.ImageIO;

//LIBRERIAS PARA GESTIONAR ARCHIVOS
//Permite manejar errores relacionados con operaciones de entrada y salida
import java.io.IOException;
//Para comprobar la existencia de archivos y gestionarlos
import java.nio.file.Files;
//Para las rutas y directorios de los archivos
import java.nio.file.Path;

//Interfaz para listas ordenadas (colecciones dinámicas de elementos).
import java.util.List;

//LIBRARIAS PARA MOSTRAR LA IMAGEN EN UNA VENTANA
//Permite mostrar una imágen dentro de un componenete SWING

import javax.swing.ImageIcon;
//Representa la ventana principal de la aplicación
import javax.swing.JFrame;
//Permite mostrar texto o imagenes en una ventana
import javax.swing.JLabel;
//Añade barras de deslizamiento
import javax.swing.JScrollPane;
//Permite ejecutar operaciones de interfaz gráfica en el hijo de eventos de SWING
import javax.swing.SwingUtilities;


public class GraficadorECG {
        //Dimensiones de la imagen en píxeles.
        private static final int ANCHO = 1662;
        private static final int ALTO = 990;

        //Escala de representación: 6 píxeles por milímetro.
        private static final int PIXELES_POR_MM = 6;

        //Escalas convencionales de presentación del ECG.
        private static final double VELOCIDAD_MM_S = 25.0;
        private static final double GANANCIA_MM_MV = 10.0;

        //Ventana temporal mostrada.
        private static final double DURACION_S = 10.0;

        private static final int MARGEN_IZQUIERDO = 65;
        private static final int MARGEN_DERECHO = 25;
        private static final int MARGEN_SUPERIOR = 120;
        private static final int MARGEN_INFERIOR = 45;

        private static final int ESPACIO_CANALES = 35;
        private static final Color COLOR_MENOR = new Color(255, 205, 210);
        private static final Color COLOR_MAYOR = new Color(245, 125, 140);
        private static final Color COLOR_SENAL = new Color(25, 30, 35);
        private static final Color COLOR_ANOTACION = new Color(35, 65, 220);
        private final RegistroECG registro;

        public GraficadorECG(
                CabeceraECG cabecera,
                Path archivoSenal,
                Path archivoAnotaciones,
                List<String> auxiliar) throws IOException {
                double[][] senales = LectorSen.leer(archivoSenal, cabecera);
                List<RegistroECG.Anotacion> anotaciones = LectorAnotaciones.leer(archivoAnotaciones);
                registro = new RegistroECG(cabecera, senales, anotaciones, auxiliar);
        }

        public void mostrar(Path destino) throws IOException {
                BufferedImage imagen = generarImagen();
                Path carpeta = destino.getParent();
                if (carpeta != null) {
                        Files.createDirectories(carpeta);
                }

                if (!ImageIO.write(imagen, "png", destino.toFile())) {
                throw new IOException("No se pudo guardar el PNG.");
                }

                System.out.println("Imagen guardada en: " + destino.toAbsolutePath());

                if (!java.awt.GraphicsEnvironment.isHeadless()) {
                SwingUtilities.invokeLater(() -> {
                        JFrame ventana = new JFrame("Electrocardiograma - "+ registro.cabecera.nombreRegistro);
                        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        ventana.setContentPane(new JScrollPane(new JLabel(new ImageIcon(imagen))));
                        ventana.pack();
                        ventana.setLocationRelativeTo(null);
                        ventana.setVisible(true);
                });
                }
        }

        private BufferedImage generarImagen() {
                BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = imagen.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, ANCHO, ALTO);
                dibujarEncabezado(g);

                int cantidadCanales = Math.min(2, registro.senales.length);
                int anchoGrafica = ANCHO- MARGEN_IZQUIERDO- MARGEN_DERECHO;

                // A 25 mm/s, 10 s requieren 250 mm.
                // Con 6 px/mm, la gráfica ocupa 1500 px.
                int anchoEscalado = (int) Math.round(DURACION_S * VELOCIDAD_MM_S* PIXELES_POR_MM);
                anchoGrafica = Math.min(anchoGrafica, anchoEscalado);
                int altoDisponible = ALTO- MARGEN_SUPERIOR- MARGEN_INFERIOR- ESPACIO_CANALES * (cantidadCanales - 1);
                int altoPanel = altoDisponible / cantidadCanales;

                for (int canal = 0; canal < cantidadCanales; canal++) {
                        int yPanel = MARGEN_SUPERIOR + canal * (altoPanel + ESPACIO_CANALES);
                        dibujarCuadricula(g, yPanel, altoPanel, anchoGrafica);
                        dibujarSenal(g, canal, yPanel, altoPanel, anchoGrafica);
                        dibujarAnotaciones(g, canal, yPanel, altoPanel, anchoGrafica);
                        dibujarEtiquetaCanal(g, canal, yPanel, altoPanel);
                        dibujarEjeTemporal(g, yPanel, altoPanel, anchoGrafica);
                }
                g.dispose();
                return imagen;
        }

        private void dibujarEncabezado(Graphics2D g) {
                CabeceraECG c = registro.cabecera;
                g.setColor(new Color(30, 40, 55));
                g.setFont(new Font("SansSerif", Font.BOLD, 23));
                g.drawString("Electrocardiograma - Registro "+ c.nombreRegistro,MARGEN_IZQUIERDO, 32);
                g.setFont(new Font("SansSerif", Font.PLAIN, 14));
                double duracionTotal =c.numeroMuestras / c.frecuenciaMuestreo;
                g.drawString(String.format("Frecuencia: %.1f Hz | Canales: %d"+ " | Muestras por canal: %d",c.frecuenciaMuestreo,c.numeroCanales,c.numeroMuestras),MARGEN_IZQUIERDO, 58);
                g.drawString(String.format("Duración total: %.2f s"+ " | Ventana mostrada: %.2f s",duracionTotal, DURACION_S),MARGEN_IZQUIERDO, 81);
                g.drawString(String.format("Escala horizontal: %.1f mm/s"+ " | Escala vertical: %.1f mm/mV"+ " | Resolución gráfica: %d px/mm",VELOCIDAD_MM_S,GANANCIA_MM_MV,PIXELES_POR_MM),MARGEN_IZQUIERDO, 104);
                g.setColor(COLOR_ANOTACION);
                g.drawString("Líneas azules: anotaciones .atr",ANCHO - 300, 32);

        }

        private void dibujarCuadricula(Graphics2D g,int y,int alto,int ancho) {
                int x = MARGEN_IZQUIERDO;
                g.setColor(Color.WHITE);
                g.fillRect(x, y, ancho, alto);
                //Cada cuadro pequeño equivale a 1 mm.
                int paso = PIXELES_POR_MM;
                g.setStroke(new BasicStroke(1f));

                for (int px = 0; px <= ancho; px += paso) {
                        g.setColor(px % (5 * paso) == 0? COLOR_MAYOR : COLOR_MENOR);
                        g.drawLine(x + px, y, x + px, y + alto);
                }

                for (int py = 0; py <= alto; py += paso) {
                        g.setColor(py % (5 * paso) == 0? COLOR_MAYOR : COLOR_MENOR);
                        g.drawLine(x, y + py, x + ancho, y + py);
                }
                g.setColor(Color.GRAY);
                g.drawRect(x, y, ancho, alto);
        }
        
        private void dibujarSenal(Graphics2D g,int canal,int yPanel,int alto,int ancho) {
                double[] datos = registro.senales[canal];
                int cantidad = Math.min(datos.length,(int) Math.ceil(DURACION_S* registro.cabecera.frecuenciaMuestreo));

                if (cantidad < 2) {return;}

                double frecuencia = registro.cabecera.frecuenciaMuestreo;
                int xInicio = MARGEN_IZQUIERDO;
                int yCentro = yPanel + alto / 2;
                Path2D trayectoria = new Path2D.Double();

                for (int i = 0; i < cantidad; i++) {
                        double tiempo = i / frecuencia;
                        int x = xInicio + (int) Math.round(tiempo * VELOCIDAD_MM_S * PIXELES_POR_MM);
                        double amplitud = datos[i];
                        int y = yCentro - (int) Math.round(amplitud * GANANCIA_MM_MV * PIXELES_POR_MM);

                        if (i == 0) {
                                trayectoria.moveTo(x, y);
                        } else {
                                trayectoria.lineTo(x, y);
                        }
                }

                Graphics2D gCanal = (Graphics2D) g.create();
                //Recortar la señal estrictamente al área de su canal.
                gCanal.clipRect(xInicio,yPanel + 1,ancho,alto - 1);
                gCanal.setColor(COLOR_SENAL);
                gCanal.setStroke(new BasicStroke(1.2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                gCanal.draw(trayectoria);
                gCanal.dispose();
        }

        private void dibujarAnotaciones(Graphics2D g,int canal,int yPanel,int alto,int ancho) {
                int cantidad = Math.min(registro.senales[canal].length,(int) Math.ceil(DURACION_S* registro.cabecera.frecuenciaMuestreo));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                for (RegistroECG.Anotacion a : registro.anotaciones) {
                        if (a.muestra < 0 || a.muestra >= cantidad) {continue;}

                        double tiempo = a.muestra / registro.cabecera.frecuenciaMuestreo;
                        int x = MARGEN_IZQUIERDO + (int) Math.round(tiempo * VELOCIDAD_MM_S * PIXELES_POR_MM);

                        if (x > MARGEN_IZQUIERDO + ancho) {continue;}

                        g.setColor(new Color(35, 65, 220, 170));
                        g.setStroke(new BasicStroke(1f));
                        g.drawLine(x, yPanel, x, yPanel + alto);

                        //El símbolo se muestra solo en el panel superior para evitar duplicarlo visualmente.
                        if (canal == 0) {
                                g.setColor(COLOR_ANOTACION);
                                g.drawString(a.simbolo, x + 2, yPanel + 14);
                        }
                }
        }

        private void dibujarEtiquetaCanal(Graphics2D g,int canal,int y,int alto) {
                String nombre = registro.cabecera.canales.get(canal).descripcion;
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                g.setColor(Color.BLACK);
                g.drawString(nombre,8,y + alto / 2);
        }

        private void dibujarEjeTemporal(Graphics2D g,int y,int alto,int ancho) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 12));
                g.setColor(Color.DARK_GRAY);

                //Etiquetas cada segundo.
                for (int segundo = 0;segundo <= (int) DURACION_S;segundo++) {
                        int x = MARGEN_IZQUIERDO + (int) Math.round(segundo * VELOCIDAD_MM_S* PIXELES_POR_MM);

                        if (x > MARGEN_IZQUIERDO + ancho) {continue;}

                        g.drawString(Integer.toString(segundo),x - 3,y + alto + 15);
                }

                g.drawString("Tiempo (s)",MARGEN_IZQUIERDO + ancho / 2 - 30,y + alto + 30);
        }

}

