//Función: Reunir la señal, sus metadatos y las anotaciones en un solo objeto.

//LIBRERIAS
//Interfaz para listas ordenadas (colecciones dinámicas de elementos). 
import java.util.List;

public class RegistroECG {
    public final CabeceraECG cabecera;
    public final double[][] senales;
    public final List<Anotacion> anotaciones;
    public final List<String> auxiliar;

    //Construye un registro ECG completo a partir de sus componentes.
    public RegistroECG(CabeceraECG cabecera,double[][] senales,List<Anotacion> anotaciones,List<String> auxiliar) {
        this.cabecera = cabecera;
        this.senales = senales;
        this.anotaciones = anotaciones;
        this.auxiliar = auxiliar;
    }

    //Representa una anotación individual sobre la señal ECG: un instante (en número de muestra) junto con el tipo de evento que ocurre en él.
    public static class Anotacion {
        public final long muestra;
        public final int codigo;
        public final String simbolo;

        public Anotacion(long muestra, int codigo, String simbolo) {
            this.muestra = muestra;
            this.codigo = codigo;
            this.simbolo = simbolo;
        }

        //Convierte la posición de la anotación a tiempo en segundos.
        public double obtenerTiempo(double frecuenciaMuestreo) {
            return muestra / frecuenciaMuestreo;
        }
    }
}