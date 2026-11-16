package datosObjetos;

import mundo.Ciudad;

/**
 * Fábrica de objetos para la creación de las 48 ciudades del tablero.
 * Configura las conexiones del mapa original de Pandemic (grafo no dirigido):
 * cada conexión se registra en ambas ciudades.
 */
public class Ciudades {

    // Conexiones del tablero original. Cada par se registra una sola vez
    // y el método conectar() lo agrega en ambos sentidos.
    private static final String[][] CONEXIONES = {
        // ---------- AZUL ----------
        {"San Francisco", "Chicago"},
        {"San Francisco", "Los Angeles"},
        {"San Francisco", "Tokio"},
        {"San Francisco", "Manila"},
        {"Chicago", "Montreal"},
        {"Chicago", "Atlanta"},
        {"Chicago", "Los Angeles"},
        {"Chicago", "Ciudad de Mexico"},
        {"Montreal", "Nueva York"},
        {"Montreal", "Washington"},
        {"Nueva York", "Washington"},
        {"Nueva York", "Londres"},
        {"Nueva York", "Madrid"},
        {"Washington", "Atlanta"},
        {"Washington", "Miami"},
        {"Atlanta", "Miami"},
        {"Londres", "Madrid"},
        {"Londres", "Paris"},
        {"Londres", "Essen"},
        {"Madrid", "Paris"},
        {"Madrid", "Argel"},
        {"Madrid", "Sao Paulo"},
        {"Paris", "Essen"},
        {"Paris", "Milan"},
        {"Paris", "Argel"},
        {"Essen", "Milan"},
        {"Essen", "San Petersburgo"},
        {"Milan", "Estambul"},
        {"San Petersburgo", "Estambul"},
        {"San Petersburgo", "Moscú"},

        // ---------- AMARILLO ----------
        {"Los Angeles", "Ciudad de Mexico"},
        {"Los Angeles", "Sídney"},
        {"Ciudad de Mexico", "Miami"},
        {"Ciudad de Mexico", "Bogota"},
        {"Ciudad de Mexico", "Lima"},
        {"Miami", "Bogota"},
        {"Bogota", "Lima"},
        {"Bogota", "Buenos Aires"},
        {"Bogota", "Sao Paulo"},
        {"Lima", "Santiago"},
        {"Buenos Aires", "Sao Paulo"},
        {"Sao Paulo", "Lagos"},
        {"Lagos", "Kinsasa"},
        {"Lagos", "Jartum"},
        {"Kinsasa", "Jartum"},
        {"Kinsasa", "Johannesburgo"},
        {"Jartum", "Johannesburgo"},
        {"Jartum", "El Cairo"},

        // ---------- NEGRO ----------
        {"Argel", "Estambul"},
        {"Argel", "El Cairo"},
        {"El Cairo", "Estambul"},
        {"El Cairo", "Bagdad"},
        {"El Cairo", "Riad"},
        {"Estambul", "Moscú"},
        {"Estambul", "Bagdad"},
        {"Moscú", "Teheran"},
        {"Bagdad", "Teheran"},
        {"Bagdad", "Karachi"},
        {"Bagdad", "Riad"},
        {"Riad", "Karachi"},
        {"Teheran", "Karachi"},
        {"Teheran", "Nueva Delhi"},
        {"Karachi", "Bombay"},
        {"Karachi", "Nueva Delhi"},
        {"Bombay", "Nueva Delhi"},
        {"Bombay", "Chennai"},
        {"Nueva Delhi", "Chennai"},
        {"Nueva Delhi", "Calcuta"},
        {"Calcuta", "Chennai"},
        {"Calcuta", "Bangkok"},
        {"Calcuta", "Hong Kong"},
        {"Chennai", "Bangkok"},
        {"Chennai", "Yakarta"},

        // ---------- ROJO ----------
        {"Pekin", "Seul"},
        {"Pekin", "Shangai"},
        {"Seul", "Shangai"},
        {"Seul", "Tokio"},
        {"Tokio", "Shangai"},
        {"Tokio", "Osaka"},
        {"Shangai", "Taipéi"},
        {"Shangai", "Hong Kong"},
        {"Hong Kong", "Taipéi"},
        {"Hong Kong", "Manila"},
        {"Hong Kong", "Ho Chi Minh"},
        {"Hong Kong", "Bangkok"},
        {"Taipéi", "Osaka"},
        {"Taipéi", "Manila"},
        {"Bangkok", "Ho Chi Minh"},
        {"Bangkok", "Yakarta"},
        {"Manila", "Ho Chi Minh"},
        {"Manila", "Sídney"},
        {"Ho Chi Minh", "Yakarta"},
        {"Yakarta", "Sídney"}
    };

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

        // Configuración de las conexiones del mapa original
        for (String[] conexion : CONEXIONES) {
            conectar(tablero, conexion[0], conexion[1]);
        }

        return tablero;
    }

    /**
     * Conecta dos ciudades en ambos sentidos (A es vecina de B y B es vecina de A).
     */
    private static void conectar(Ciudad[] tablero, String nombreA, String nombreB) {
        Ciudad a = buscar(tablero, nombreA);
        Ciudad b = buscar(tablero, nombreB);

        if (a == null || b == null) {
            throw new IllegalArgumentException("Conexión inválida: " + nombreA + " - " + nombreB
                    + " (revise que el nombre esté escrito igual que en el listado de ciudades).");
        }
        if (!a.esVecino(b)) {
            a.agregarVecino(b);
        }
        if (!b.esVecino(a)) {
            b.agregarVecino(a);
        }
    }

    private static Ciudad buscar(Ciudad[] tablero, String nombre) {
        for (Ciudad c : tablero) {
            if (c != null && c.obtenerNombre().equals(nombre)) {
                return c;
            }
        }
        return null;
    }
}
