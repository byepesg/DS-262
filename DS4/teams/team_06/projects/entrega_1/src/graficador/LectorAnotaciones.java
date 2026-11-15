//Función: Decodificar las anotaciones de referencia de .atr.
//El archivo .atr contiene anotaciones binarias.
//Este lector acumula las diferencias temporales entre anotaciones para obtener la posición absoluta de cada una en la señal.

//LIBRERIAS
//Excepción que se lanza cuando ocurre un error de entrada/salida. Se usa para manejar problemas al leer o escribir archivos.
import java.io.IOException;
//Clase abstracta para leer datos binarios desde una fuente (archivos, sockets, etc.). Permite trabajar con flujos de entrada de manera general.
import java.io.InputStream;
//Métodos utilitarios para trabajar con archivos y directorios. Permite leer, escribir, copiar, mover y borrar archivos de manera sencilla.
import java.nio.file.Files;
//Representa rutas de archivos o directorios en el sistema. Es una forma moderna y flexible de manejar ubicaciones de archivos.
import java.nio.file.Path;
//Implementación de la interfaz List basada en un arreglo dinámico.
import java.util.ArrayList;
//Interfaz para listas ordenadas (colecciones dinámicas de elementos). 
import java.util.List;


public class LectorAnotaciones {
    public static List<RegistroECG.Anotacion> leer(Path archivo) throws IOException {
        List<RegistroECG.Anotacion> resultado = new ArrayList<>();
        try (InputStream entrada = Files.newInputStream(archivo)) {
            long muestraActual = 0;

            //Hasta agotar el archivo
            while (true) {
                //Leer 2 bytes en orden little-endian (bajo primero)
                int bajo = entrada.read();
                if (bajo == -1) {break;}
                int alto = entrada.read();
                if (alto == -1) {throw new IOException("Archivo .atr truncado.");
                }
                //Reconstruir la palabra de 16 bits
                int palabra = (alto << 8) | bajo;
                //Extraer el código (6 bits altos) y la diferencia (10 bits bajos)
                int codigo = (palabra >> 10) & 0x3F;
                int diferencia = palabra & 0x03FF;

                //Código 0: fin de las anotaciones.
                if (codigo == 0) {break;}
                //Código 59: incremento temporal de 32 bits.
                if (codigo == 59) {
                    int b0 = entrada.read();
                    int b1 = entrada.read();
                    int b2 = entrada.read();
                    int b3 = entrada.read();

                    if (b0 == -1 || b1 == -1|| b2 == -1 || b3 == -1) {
                        throw new IOException("Anotación SKIP incompleta.");
                    }

                    //Componer el entero de 32 bits en little-endian
                    int incremento = b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
                    muestraActual += incremento;
                    continue;
                }

                //Códigos especiales que llevan datos adicionales (60 (NUM), 61 (SUB) y 62 (CHN)) que no necesitamos así que se omiten.
                if (codigo == 60 || codigo == 61 || codigo == 62) {
                    continue;
                }

                //Código 63: anotación auxiliar con texto.
                if (codigo == 63) {
                    //diferencia: contiene la longitud del texto.
                    int longitud = diferencia;
                    //El texto se rellena a un número par de bytes (padding)
                    int bytesRelleno = longitud + (longitud % 2);
                    for (int i = 0; i < bytesRelleno; i++) {
                        if (entrada.read() == -1) {
                            throw new IOException("Anotación AUX incompleta.");
                        }
                    }
                    continue;
                }
                //La posición absoluta se obtiene sumando la diferencia (relativa) a la posición actual.
                muestraActual += diferencia;
                resultado.add(new RegistroECG.Anotacion(muestraActual,codigo,obtenerSimbolo(codigo)));
            }
        }
        return resultado;
    }

    //Traduce un código numérico de anotación MIT-BIH a su símbolo estándar
    private static String obtenerSimbolo(int codigo) {
        switch (codigo) {
            case 1:  return "N";   // latido normal
            case 2:  return "L";   // bloqueo de rama izquierda
            case 3:  return "R";   // bloqueo de rama derecha
            case 4:  return "a";   // latido aberrante
            case 5:  return "V";   // latido ventricular prematuro
            case 6:  return "F";   // latido de fusión
            case 7:  return "J";   // latido nodal (junctional)
            case 8:  return "A";   // latido auricular prematuro
            case 9:  return "S";   // latido supraventricular prematuro
            case 10: return "E";   // latido escape
            case 11: return "j";   // latido nodal de escape
            case 12: return "/";   // latido marcado (paced)
            case 13: return "Q";   // latido no clasificado
            case 14: return "~";   // cambio de ritmo
            case 16: return "|";   // anotación de comentario
            case 18: return "s";   // cambio de segmento ST
            case 19: return "T";   // cambio de onda T
            case 20: return "*";   // sístole
            case 21: return "D";   // diástole
            case 22: return "\"";  // comienzo de ritmo
            case 23: return "=";   // fin de ritmo
            case 24: return "p";   // pico de presión
            case 25: return "B";   // comienzo de un latido
            case 26: return "^";   // estimulación no capturada
            case 27: return "t";   // cambio de onda T (tipo 2)
            case 28: return "+";   // ritmo nodal
            case 29: return "u";   // ritmo desconocido
            case 30: return "?";   // ritmo no especificado
            default: return "?";
        }
    }
}