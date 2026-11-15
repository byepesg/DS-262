//Función: Esta clase almacena los metadatos extraídos del archivo .hea. Otros lectores utilizarán estos datos para interpretar correctamente la señal.

//LIBRERIAS
//Implementación de la interfaz List basada en un arreglo dinámico.
import java.util.ArrayList;
//Interfaz para listas ordenadas (colecciones dinámicas de elementos). 
import java.util.List;

public class CabeceraECG {
    public String nombreRegistro;
    public int numeroCanales;
    public double frecuenciaMuestreo;
    public long numeroMuestras;

    //Nombre del archivo que contiene la señal.
    public String archivosen;

    //Información específica de cada canal.
    public List<CanalECG> canales = new ArrayList<>();

    //Representa la información de un único canal ECG tal y como aparece en una línea de canal del fichero .hea.
    public static class CanalECG {
        public String archivo;
        public int formato;
        public double ganancia;
        public String unidades;
        public int resolucionADC;
        public int ceroADC;
        public int muestraInicial;
        public String descripcion;

        // Construye la información de un canal a partir de los campos extraídos de una línea de canal del .hea.
        public CanalECG(String archivo,int formato,double ganancia,String unidades,int resolucionADC,int ceroADC,int muestraInicial,String descripcion) {
            this.archivo = archivo;
            this.formato = formato;
            this.ganancia = ganancia;
            this.unidades = unidades;
            this.resolucionADC = resolucionADC;
            this.ceroADC = ceroADC;
            this.muestraInicial = muestraInicial;
            this.descripcion = descripcion;
        }
    }
}