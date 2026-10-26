//Función: Leer el contenido textual de .xws, si existe.

//LIBRERIAS
//Excepción que se lanza cuando ocurre un error de entrada/salida. Se usa para manejar problemas al leer o escribir archivos.
import java.io.IOException;
//Métodos utilitarios para trabajar con archivos y directorios. Permite leer, escribir, copiar, mover y borrar archivos de manera sencilla.
import java.nio.file.Files;
//Representa rutas de archivos o directorios en el sistema. Es una forma moderna y flexible de manejar ubicaciones de archivos.
import java.nio.file.Path;
//Interfaz para listas ordenadas (colecciones dinámicas de elementos). 
import java.util.List;


public class LectorAuxiliar {
    public static List<String> leer(Path archivo)
            throws IOException {
        return Files.readAllLines(archivo);
    }
}