package datosObjetos;

import mundo.Ciudad;

/**
 * Fábrica de objetos para la creación de las 48 ciudades del tablero.
 * Configura relaciones mutuas de exactamente 2 vecinos por ciudad y asigna poblaciones de ejemplo.
 */
public class Ciudades {

    public static Ciudad[] crearCiudades() {
        Ciudad[] tablero = new Ciudad[48];

        // Listado de las 48 ciudades clasificadas por su color (12 por color)
        String[] nombresAzul = {
            "San Francisco", "Chicago", "Montreal", "Nueva York", "Washington", "Atlanta",
            "Londres", "San Petersburgo", "Essen", "Paris", "Milan", "Madrid"
        };
        String[] nombresAmarillo = {
            "Los Angeles", "Ciudad de Mexico", "Miami", "Bogota", "Lima", "Santiago",
            "Buenos Aires", "Sao Paulo", "Lagos", "Kinsasa", "Jartum", "Johannesburgo"
        };
        String[] nombresNegro = {
            "Argel", "El Cairo", "Estambul", "Moscú", "Bagdad", "Riad",
            "Teheran", "Karachi", "Bombay", "Nueva Delhi", "Calcuta", "Chennai"
        };
        String[] nombresRojo = {
            "Pekin", "Seul", "Tokio", "Shangai", "Hong Kong", "Taipéi", "Bangkok",
            "Manila", "Ho Chi Minh", "Yakarta", "Sídney", "Osaka"
        };

        int idx = 0;
        for (String nombre : nombresAzul) tablero[idx++] = new Ciudad(nombre, "Azul");
        for (String nombre : nombresAmarillo) tablero[idx++] = new Ciudad(nombre, "Amarillo");
        for (String nombre : nombresNegro) tablero[idx++] = new Ciudad(nombre, "Negro");
        for (String nombre : nombresRojo) tablero[idx++] = new Ciudad(nombre, "Rojo");

        // Configuración de conexiones mutuas (Anillo cerrado: cada ciudad i se conecta con i-1 e i+1)
        int total = tablero.length;
        for (int i = 0; i < total; i++) {
            int vecinoAnterior = (i - 1 + total) % total;
            int vecinoSiguiente = (i + 1) % total;

            tablero[i].agregarVecino(tablero[vecinoAnterior]);
            tablero[i].agregarVecino(tablero[vecinoSiguiente]);
        }

        return tablero;
    }
}
