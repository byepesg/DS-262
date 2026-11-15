//Función: Decodificar las muestras binarias de .dat, incluyendo el formato 212 de MIT-BIH.

//LIBRERIAS
//Excepción que se lanza cuando ocurre un error de entrada/salida. Se usa para manejar problemas al leer o escribir archivos.
import java.io.IOException;
//Métodos utilitarios para trabajar con archivos y directorios. Permite leer, escribir, copiar, mover y borrar archivos de manera sencilla.
import java.nio.file.Files;
//Representa rutas de archivos o directorios en el sistema. Es una forma moderna y flexible de manejar ubicaciones de archivos.
import java.nio.file.Path;


public class LectorSen {
    public static double[][] leer(Path archivo,CabeceraECG cabecera) throws IOException {
        //No se pueden indezar arrays con más de Integer.MAX_VALUE posiciones
        if (cabecera.numeroMuestras > Integer.MAX_VALUE) {
            throw new IOException("El registro es demasiado grande para este lector.");
        }

        //Sin el número de muestras no se sabe cúanto leer
        if (cabecera.numeroMuestras == 0) {
            throw new IOException("La cabecera no especifica el número de muestras.");
        }

        //Solo se implementa el formato 212 de MIT-BIH
        for (CabeceraECG.CanalECG canal : cabecera.canales) {
            if (canal.formato != 212) {
                throw new IOException("Formato no compatible: " + canal.formato+ ". Este lector implementa el formato 212.");
            }

            //Evita la divisón por cero
            if (canal.ganancia == 0) {
                throw new IOException("La ganancia del canal no puede ser cero.");
            }
        }

        int canales = cabecera.numeroCanales;
        int muestras = (int) cabecera.numeroMuestras;

        //Matriz de salida que tiene una fila por canal y una columna por muestra
        double[][] senales = new double[canales][muestras];

        //Formato 212: dos muestras empaquetadas en tres bytes.
        try (var entrada = Files.newInputStream(archivo)) {
            long indiceGlobal = 0;

            //b0 = bits 0-7 de muestra 1
            //b1 = bits 8-11 de muestra 1 + bits 0-3 de la muestra 2
            //b2 = bits 4-11 de muestra 2
            int b0, b1, b2;

            //Se lee de a 3 en 3 hasta agotar el archivo
            while ((b0 = entrada.read()) != -1) {
                b1 = entrada.read();
                b2 = entrada.read();

                if (b1 == -1 || b2 == -1) {
                    throw new IOException("Archivo .dat truncado: faltan bytes.");
                }

                int muestra1 = ((b1 & 0x0F) << 8) | b0;
                int muestra2 = ((b1 & 0xF0) << 4) | (b2 & 0xFF);

                //Convertir de 12 bits sin signo a 12 bits con signo
                muestra1 = convertir12BitsConSigno(muestra1);
                muestra2 = convertir12BitsConSigno(muestra2);
                //Colocar cada muestra en su canal/muestra correspondiente
                indiceGlobal = guardarMuestra(muestra1, indiceGlobal, senales, cabecera);
                indiceGlobal = guardarMuestra(muestra2, indiceGlobal, senales, cabecera);
            }
        }

        long esperadas = (long) canales * muestras;
        if (indiceMuestrasLeidas(senales) != esperadas) {
            //Cálculo del número de bytes que debería tener el .dat
            //Cada 2 muestras ocupan 3 bytes, redondeando hacia arriba si es impar
            long bytesEsperados = (esperadas + 1) / 2 * 3;

            if (Files.size(archivo) != bytesEsperados) {
                throw new IOException("La longitud de .dat no coincide con " + "el número de muestras de .hea.");
            }
        }

        return senales;
    }

    //Convierte un valor de 12 bits sin signo (0..4095) a su equivalente con signo en complemento a dos (-2048..2047)
    private static int convertir12BitsConSigno(int valor) {
        if ((valor & 0x800) != 0) {
            valor -= 0x1000;
        }
        return valor;
    }

    private static long guardarMuestra(int valorADC,long indiceGlobal,double[][] senales,CabeceraECG cabecera) {
        int canal = (int) (indiceGlobal % cabecera.numeroCanales);
        int muestra = (int) (indiceGlobal / cabecera.numeroCanales);

        if (muestra < senales[canal].length) {
            CabeceraECG.CanalECG datosCanal = cabecera.canales.get(canal);
            //Conversión a unidades físicas (p. ej. mV) usando cero y ganancia del canal
            senales[canal][muestra] = (valorADC - datosCanal.ceroADC) / datosCanal.ganancia;
        }
        return indiceGlobal + 1;
    }

    //Cuenta el número total de muestras almacenadas en la matriz (comprobación)
    private static long indiceMuestrasLeidas(double[][] senales) {
        long total = 0;

        for (double[] canal : senales) {
            total += canal.length;
        }
        return total;
    }
}