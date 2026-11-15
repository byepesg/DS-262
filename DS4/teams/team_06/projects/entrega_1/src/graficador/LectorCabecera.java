//Función: Interpretar los metadatos de .hea

//LIBRERIAS
//Excepción que se lanza cuando ocurre un error de entrada/salida. Se usa para manejar problemas al leer o escribir archivos.
import java.io.IOException;
//Métodos utilitarios para trabajar con archivos y directorios. Permite leer, escribir, copiar, mover y borrar archivos de manera sencilla.
import java.nio.file.Files;
//Representa rutas de archivos o directorios en el sistema. Es una forma moderna y flexible de manejar ubicaciones de archivos.
import java.nio.file.Path;
//Interfaz para listas ordenadas (colecciones dinámicas de elementos). 
import java.util.List;


public class LectorCabecera {
    public static CabeceraECG leer(Path archivo) throws IOException {
        List<String> lineas = Files.readAllLines(archivo);
        CabeceraECG datos = new CabeceraECG();
        String primeraLinea = null;

        //Se ignoran líneas vacías y comentarios (que empiezan por '#').
        //La primera línea no comentada es la línea de registro.
        for (String linea : lineas) {
            linea = linea.trim();
            if (!linea.isEmpty() && !linea.startsWith("#")) {
                primeraLinea = linea;
                break;
            }
        }

        if (primeraLinea == null) {
            throw new IOException("La cabecera está vacía.");
        }

        //Los campos de la línea de registro están separados por espacios o tabuladores; split("\\s+") colapsa cualquier secuencia de blancos.
        String[] campos = primeraLinea.split("\\s+");

        if (campos.length < 3) {
            throw new IOException("Cabecera (.hea) inválida.");
        }

        datos.nombreRegistro = campos[0];
        datos.numeroCanales = Integer.parseInt(campos[1]);

        //La frecuencia puede aparecer como 360 o como "360/mV" así que nos quedamos solo con la parte numérica
        datos.frecuenciaMuestreo = Double.parseDouble(campos[2].split("/")[0]);
        datos.numeroMuestras = campos.length >= 4? Long.parseLong(campos[3]): 0;

         //Validación temprana de rangos (evita errores de dimensionamiento o divición por cero)
        if (datos.numeroCanales <= 0|| datos.frecuenciaMuestreo <= 0 || datos.numeroMuestras < 0) {
            throw new IOException("Metadatos numéricos fuera de rango.");
        }

        int lineasCanales = 0;

        //Extraer las línes de canal
        for (String linea : lineas) {
            linea = linea.trim();
            //Ignorar lineas vacías
            if (linea.isEmpty() || linea.startsWith("#")) {continue;}
            //Ignorar lineas ya procesadas
            if (linea.equals(primeraLinea)) {continue;}
            
            String[] c = linea.split("\\s+");
            if (c.length < 2) {continue;}
            
            //Si no es un entero, la línea no es una línea de canal válida
            int formato;
            try {formato = Integer.parseInt(c[1]);
            } catch (NumberFormatException e) {continue;}

            //Se exige que cada linea tenga todos los campos obligatorios
            if (c.length < 7) {
                throw new IOException("Línea de canal incompleta: " + linea);
            }

            String archivoSenal = c[0];
            //Formato de ganancia: 200, 200/mV o 200(adu)/mV.
            String campoGanancia = c[2];
            String unidades = "mV";
            double ganancia;
            //Extraer las unidades entre paréntesis (adu) si existen
            int parentesis = campoGanancia.indexOf('(');
            if (parentesis >= 0) {
                int cierre = campoGanancia.indexOf(')', parentesis);
                if (cierre > parentesis) {
                    unidades = campoGanancia.substring(parentesis + 1, cierre);
                }
                campoGanancia = campoGanancia.substring(0, parentesis) + campoGanancia.substring(cierre + 1);
            }

            //Separar la ganancia numérica de las unidades tras la barra
            String[] partesGanancia = campoGanancia.split("/");
            ganancia = Double.parseDouble(partesGanancia[0]);

            if (partesGanancia.length > 1) {unidades = partesGanancia[1];}

            int resolucion = Integer.parseInt(c[3]);
            int ceroADC = Integer.parseInt(c[4]);
            int muestraInicial = Integer.parseInt(c[5]);

            //La descripción comienza después de los campos numéricos.
            String descripcion = c.length >= 9? String.join(" ",java.util.Arrays.copyOfRange(c, 8, c.length)) : "Canal " + (lineasCanales + 1);
            
            //Crea el objeto CanalECG y lo anñade a la cabecera
            CabeceraECG.CanalECG canal =new CabeceraECG.CanalECG(archivoSenal,formato,ganancia,unidades,resolucion,ceroADC,muestraInicial,descripcion);
            datos.canales.add(canal);
            lineasCanales++;
        }

        if (datos.canales.size() != datos.numeroCanales) {
            throw new IOException("Se esperaban " + datos.numeroCanales+ " canales, pero se encontraron "+ datos.canales.size() + ".");
        }

        datos.archivosen = datos.canales.get(0).archivo;
        for (CabeceraECG.CanalECG canal : datos.canales) {
            if (!canal.archivo.equals(datos.archivosen)) {
                throw new IOException("Este lector requiere que todos los canales estén en un mismo archivo de señal.");
            }
        }
        return datos;
    }
}