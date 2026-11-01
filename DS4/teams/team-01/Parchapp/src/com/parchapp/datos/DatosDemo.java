package com.parchapp.datos;

import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import java.time.LocalDate;
import java.time.LocalDateTime;

// Datos de demostración generados con IA mientras no hay conexión con Google Places ni base de datos.
// Las coordenadas son aproximadas. Las direcciones que no verificamos quedan en null.
public class DatosDemo {

    private static final String CAMPUS = "Ciudad Universitaria UNAL, Cra 45 #26-85";

    public static ListaEnlazada<Lugar> lugares() {
        ListaEnlazada<Lugar> lugares = new ListaEnlazada<>();
        lugares.agregarAlFinal(new Lugar("P01", "Edificio de Ciencia y Tecnología", Categoria.ESTUDIO,
                CAMPUS + " (Edificio 454)", 4.63806, -74.08475,
                "Salas de estudio y espacios para trabajar en grupo."));
        lugares.agregarAlFinal(new Lugar("P02", "Auditorio León de Greiff", Categoria.CULTURA,
                CAMPUS + " (Edificio 104)", 4.6356777, -74.0823307,
                "Auditorio de conciertos y eventos culturales del campus."));
        lugares.agregarAlFinal(new Lugar("P03", "Estadio Alfonso López", Categoria.DEPORTE,
                CAMPUS, 4.640356, -74.086458,
                "Estadio y pista atlética de la universidad."));
        lugares.agregarAlFinal(new Lugar("P04", "Biblioteca Central Gabriel García Márquez", Categoria.ESTUDIO,
                CAMPUS, 4.6354, -74.0834, null));
        lugares.agregarAlFinal(new Lugar("P05", "Museo de Arte UNAL", Categoria.CULTURA,
                CAMPUS, 4.6362, -74.0818, null));
        lugares.agregarAlFinal(new Lugar("P06", "Biblioteca Virgilio Barco", Categoria.ESTUDIO,
                "Av. Cra 60 #57-60", 4.6566, -74.0898,
                "Biblioteca pública rodeada de zonas verdes."));
        lugares.agregarAlFinal(new Lugar("P07", "Parque Simón Bolívar", Categoria.PARQUE,
                null, 4.6585, -74.0939,
                "Parque grande con lago, bueno para planes al aire libre."));
        lugares.agregarAlFinal(new Lugar("P08", "Jardín Botánico de Bogotá", Categoria.PARQUE,
                "Av. Calle 63 #68-95", 4.6687, -74.0994, null));
        lugares.agregarAlFinal(new Lugar("P09", "Museo Nacional de Colombia", Categoria.CULTURA,
                "Cra 7 #28-66", 4.6155, -74.0687, null));
        lugares.agregarAlFinal(new Lugar("P10", "Biblioteca Luis Ángel Arango", Categoria.ESTUDIO,
                "Calle 11 #4-14", 4.5968, -74.0733, null));
        lugares.agregarAlFinal(new Lugar("P11", "Parque Nacional", Categoria.PARQUE,
                null, 4.6236, -74.0647, null));
        lugares.agregarAlFinal(new Lugar("P12", "Estadio El Campín", Categoria.DEPORTE,
                null, 4.6460, -74.0776, null));
        return lugares;
    }

    public static ListaEnlazada<Usuario> usuarios() {
        ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();
        usuarios.agregarAlFinal(new Usuario("U1", "Juan Camilo"));
        usuarios.agregarAlFinal(new Usuario("U2", "Santiago"));
        usuarios.agregarAlFinal(new Usuario("U3", "Pablo"));
        usuarios.agregarAlFinal(new Usuario("U4", "Valentina"));
        usuarios.agregarAlFinal(new Usuario("U5", "Mateo"));
        return usuarios;
    }

    public static ListaEnlazada<Ubicacion> ubicaciones() {
        ListaEnlazada<Ubicacion> ubicaciones = new ListaEnlazada<>();
        ubicaciones.agregarAlFinal(new Ubicacion("Plaza Che (UNAL)", 4.6357, -74.0829));
        ubicaciones.agregarAlFinal(new Ubicacion("Plaza de Bolívar", 4.5981, -74.0760));
        ubicaciones.agregarAlFinal(new Ubicacion("Parque de la 93", 4.6766, -74.0483));
        return ubicaciones;
    }

    // Unos parches de ejemplo para que la app no arranque vacía. Las fechas son relativas a hoy.
    public static void crearParches(GestorParches gestor, CatalogoLugares catalogo,
                                    ListaEnlazada<Usuario> usuarios, LocalDateTime ahora) {
        Usuario juan = usuarios.obtener(0);
        Usuario santiago = usuarios.obtener(1);
        Usuario valentina = usuarios.obtener(3);
        Usuario mateo = usuarios.obtener(4);
        LocalDate hoy = ahora.toLocalDate();

        Parche estudio = gestor.crear(juan, catalogo.buscarPorId("P01"), "Estudio de Álgebra Lineal",
                hoy.plusDays(1).atTime(14, 0), 6, ahora);
        estudio.unirse(santiago);

        // Este queda lleno y con alguien en la lista de espera
        Parche concierto = gestor.crear(valentina, catalogo.buscarPorId("P02"), "Concierto en el León de Greiff",
                hoy.plusDays(2).atTime(18, 30), 3, ahora);
        concierto.unirse(mateo);
        concierto.unirse(santiago);
        concierto.unirse(juan);

        Parche futbol = gestor.crear(mateo, catalogo.buscarPorId("P03"), "Fútbol 5 después de clase",
                hoy.plusDays(1).atTime(16, 0), 10, ahora);
        futbol.unirse(juan);

        gestor.crear(santiago, catalogo.buscarPorId("P07"), "Picnic en el Simón Bolívar",
                hoy.plusDays(3).atTime(11, 0), 8, ahora);
    }
}
