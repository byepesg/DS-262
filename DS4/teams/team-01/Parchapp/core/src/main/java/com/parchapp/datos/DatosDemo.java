package com.parchapp.datos;

import static com.parchapp.dominio.Etiqueta.ACTIVIDAD;
import static com.parchapp.dominio.Etiqueta.AIRE_LIBRE;
import static com.parchapp.dominio.Etiqueta.ALCOHOL;
import static com.parchapp.dominio.Etiqueta.COMIDA;
import static com.parchapp.dominio.Etiqueta.CULTURAL;
import static com.parchapp.dominio.Etiqueta.EMOCIONANTE;
import static com.parchapp.dominio.Etiqueta.FAMILIAR;
import static com.parchapp.dominio.Etiqueta.GRUPOS;
import static com.parchapp.dominio.Etiqueta.IDEAL_SOLO;
import static com.parchapp.dominio.Etiqueta.NOCTURNO;
import static com.parchapp.dominio.Etiqueta.PAISAJE;
import static com.parchapp.dominio.Etiqueta.ROMANTICO;
import static com.parchapp.dominio.Etiqueta.SOCIAL;
import static com.parchapp.dominio.Etiqueta.TRANQUILO;

import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

// Datos de prueba mientras no tengamos Google Places ni base de datos.
// Los lugares son reales, las coordenadas aproximadas.
public class DatosDemo {

    private static final String CAMPUS = "Ciudad Universitaria UNAL, Cra 45 #26-85";
    private static final String UNAL = "Universidad Nacional";

    // Semilla fija: los parches generados son los mismos en cada ejecución
    private static final long SEMILLA = 2026;

    public static ListaEnlazada<Lugar> lugares() {
        ListaEnlazada<Lugar> l = new ListaEnlazada<>();
        // Campus y alrededores (los primeros doce son los de la Entrega 1)
        l.agregarAlFinal(lugar("P01", "Edificio de Ciencia y Tecnología", Categoria.ESTUDIO, UNAL, 4.63806, -74.08475, 0,
                "Salas de estudio y mesas para trabajar en grupo, con buena señal y enchufes.", TRANQUILO, IDEAL_SOLO, GRUPOS));
        l.agregarAlFinal(lugar("P02", "Auditorio León de Greiff", Categoria.CULTURA, UNAL, 4.6356777, -74.0823307, 1,
                "Sede de la Filarmónica de Bogotá. Los conciertos de fin de semana suelen ser gratis o muy baratos.",
                CULTURAL, ROMANTICO, SOCIAL, NOCTURNO));
        l.agregarAlFinal(lugar("P03", "Estadio Alfonso López", Categoria.DEPORTE, UNAL, 4.640356, -74.086458, 0,
                "Pista atlética y cancha. Al final de la tarde se llena de gente trotando.", ACTIVIDAD, AIRE_LIBRE, GRUPOS));
        l.agregarAlFinal(lugar("P04", "Biblioteca Central Gabriel García Márquez", Categoria.ESTUDIO, UNAL, 4.6354, -74.0834, 0,
                "Silencio de verdad. Ideal para sacar adelante una entrega larga.", TRANQUILO, IDEAL_SOLO, CULTURAL));
        l.agregarAlFinal(lugar("P05", "Museo de Arte UNAL", Categoria.CULTURA, UNAL, 4.6362, -74.0818, 0,
                "Exposiciones temporales de arte contemporáneo. Entrada libre.", CULTURAL, TRANQUILO, IDEAL_SOLO, ROMANTICO));
        l.agregarAlFinal(lugar("P06", "Biblioteca Virgilio Barco", Categoria.ESTUDIO, "Salitre", 4.6566, -74.0898, 0,
                "Edificio de Rogelio Salmona rodeado de agua y jardines. Se puede estudiar y luego caminar.",
                TRANQUILO, IDEAL_SOLO, CULTURAL, PAISAJE, AIRE_LIBRE));
        l.agregarAlFinal(lugar("P07", "Parque Simón Bolívar", Categoria.PARQUE, "Salitre", 4.6585, -74.0939, 0,
                "El parque grande de la ciudad: lago, bicis, picnic y conciertos al aire libre.",
                AIRE_LIBRE, ACTIVIDAD, FAMILIAR, GRUPOS, TRANQUILO, ROMANTICO));
        l.agregarAlFinal(lugar("P08", "Jardín Botánico de Bogotá", Categoria.PARQUE, "Engativá", 4.6687, -74.0994, 1,
                "Senderos con plantas de todo el país y un tropicario. Plan tranquilo para cualquier día.",
                AIRE_LIBRE, PAISAJE, TRANQUILO, ROMANTICO, FAMILIAR, CULTURAL));
        l.agregarAlFinal(lugar("P09", "Museo Nacional de Colombia", Categoria.CULTURA, "La Macarena", 4.6155, -74.0687, 1,
                "Una antigua cárcel convertida en museo de historia y arte del país.", CULTURAL, TRANQUILO, IDEAL_SOLO, FAMILIAR));
        l.agregarAlFinal(lugar("P10", "Biblioteca Luis Ángel Arango", Categoria.ESTUDIO, "La Candelaria", 4.5968, -74.0733, 0,
                "Una de las bibliotecas más visitadas de Latinoamérica, con salas de lectura enormes.",
                TRANQUILO, IDEAL_SOLO, CULTURAL));
        l.agregarAlFinal(lugar("P11", "Parque Nacional", Categoria.PARQUE, "Teusaquillo", 4.6236, -74.0647, 0,
                "Canchas, senderos y pasto al pie de los cerros. Clásico para un domingo.",
                AIRE_LIBRE, ACTIVIDAD, GRUPOS, FAMILIAR));
        l.agregarAlFinal(lugar("P12", "Estadio El Campín", Categoria.DEPORTE, "Teusaquillo", 4.6460, -74.0776, 2,
                "Partidos de Millonarios y Santa Fe. Llegar temprano y con la camiseta puesta.",
                EMOCIONANTE, SOCIAL, GRUPOS, NOCTURNO));

        // Miradores y naturaleza
        l.agregarAlFinal(lugar("P13", "Cerro de Monserrate", Categoria.MIRADOR, "Centro", 4.6058, -74.0556, 1,
                "Se sube a pie por el sendero o en teleférico. Arriba está la mejor vista de Bogotá.",
                PAISAJE, ROMANTICO, AIRE_LIBRE, ACTIVIDAD, FAMILIAR, EMOCIONANTE));
        l.agregarAlFinal(lugar("P14", "Miradores de la vía a La Calera", Categoria.MIRADOR, "La Calera", 4.6815, -74.0250, 2,
                "Restaurantes colgados de la montaña. De noche se ve toda la ciudad iluminada.",
                PAISAJE, ROMANTICO, NOCTURNO, COMIDA, SOCIAL));
        l.agregarAlFinal(lugar("P15", "Sendero Quebrada La Vieja", Categoria.AVENTURA, "Chapinero Alto", 4.6487, -74.0498, 0,
                "Caminata corta por bosque andino a pocas cuadras de la Séptima. Mejor ir temprano.",
                AIRE_LIBRE, ACTIVIDAD, PAISAJE, TRANQUILO, IDEAL_SOLO));
        l.agregarAlFinal(lugar("P16", "Cerro de Guadalupe", Categoria.MIRADOR, "Centro", 4.5920, -74.0530, 0,
                "Más alto que Monserrate y mucho menos lleno. Subida exigente, vista enorme.",
                PAISAJE, AIRE_LIBRE, ACTIVIDAD, EMOCIONANTE));
        l.agregarAlFinal(lugar("P17", "Mirador Torre Colpatria", Categoria.MIRADOR, "Centro Internacional", 4.6110, -74.0705, 1,
                "Piso 48, terraza abierta. Al atardecer se ven los cerros y la sabana.",
                PAISAJE, ROMANTICO, CULTURAL));
        l.agregarAlFinal(lugar("P18", "Parque El Virrey", Categoria.PARQUE, "Chicó", 4.6735, -74.0540, 0,
                "Un parque largo junto a la quebrada, con árboles, perros y gente trotando.",
                AIRE_LIBRE, TRANQUILO, ACTIVIDAD, ROMANTICO, IDEAL_SOLO));
        l.agregarAlFinal(lugar("P19", "Parque de la 93", Categoria.PARQUE, "Chicó", 4.6766, -74.0483, 0,
                "Pasto en el centro y restaurantes alrededor. En diciembre lo llenan de luces.",
                SOCIAL, AIRE_LIBRE, FAMILIAR, NOCTURNO, COMIDA));
        l.agregarAlFinal(lugar("P20", "Parque de los Novios", Categoria.PARQUE, "Salitre", 4.6575, -74.0800, 0,
                "Lago con botes de pedal y ciclas para alquilar. El nombre lo dice todo.",
                ROMANTICO, AIRE_LIBRE, ACTIVIDAD, FAMILIAR));
        l.agregarAlFinal(lugar("P21", "Humedal Santa María del Lago", Categoria.PARQUE, "Engativá", 4.6900, -74.0960, 0,
                "Aves, juncos y silencio en plena ciudad. Ideal para caminar sin afán.",
                PAISAJE, TRANQUILO, AIRE_LIBRE, IDEAL_SOLO));

        // Cultura
        l.agregarAlFinal(lugar("P22", "Usaquén y su mercado de pulgas", Categoria.CULTURA, "Usaquén", 4.6950, -74.0310, 1,
                "Los domingos hay mercado de pulgas, artesanías y música en la calle. Restaurantes para todos los gustos.",
                SOCIAL, COMIDA, ROMANTICO, CULTURAL, FAMILIAR, AIRE_LIBRE));
        l.agregarAlFinal(lugar("P23", "Chorro de Quevedo", Categoria.CULTURA, "La Candelaria", 4.5975, -74.0690, 0,
                "Plazoleta donde dicen que se fundó Bogotá. Calles con grafiti, cuenteros y chicha.",
                CULTURAL, SOCIAL, ALCOHOL, NOCTURNO));
        l.agregarAlFinal(lugar("P24", "Museo del Oro", Categoria.CULTURA, "Centro", 4.6019, -74.0720, 1,
                "La colección de orfebrería prehispánica más grande del mundo. Los domingos es gratis.",
                CULTURAL, TRANQUILO, IDEAL_SOLO, FAMILIAR, ROMANTICO));
        l.agregarAlFinal(lugar("P25", "Museo Botero", Categoria.CULTURA, "La Candelaria", 4.5965, -74.0730, 0,
                "Obras de Botero y de su colección personal en una casa colonial. Entrada libre.",
                CULTURAL, TRANQUILO, IDEAL_SOLO, ROMANTICO));
        l.agregarAlFinal(lugar("P26", "Planetario de Bogotá", Categoria.CULTURA, "Centro Internacional", 4.6125, -74.0685, 1,
                "Funciones en el domo y exposiciones del espacio. Funciona igual de bien en cita que con niños.",
                CULTURAL, FAMILIAR, ROMANTICO, TRANQUILO));
        l.agregarAlFinal(lugar("P27", "Teatro Colón", Categoria.CULTURA, "La Candelaria", 4.5967, -74.0748, 2,
                "Ópera, danza y conciertos en un teatro de 1892. Vale la pena aunque sea por el edificio.",
                CULTURAL, ROMANTICO, NOCTURNO));
        l.agregarAlFinal(lugar("P28", "Teatro Mayor Julio Mario Santo Domingo", Categoria.CULTURA, "Suba", 4.7290, -74.0710, 2,
                "La programación más grande de la ciudad: conciertos, teatro y festivales internacionales.",
                CULTURAL, ROMANTICO, NOCTURNO));
        l.agregarAlFinal(lugar("P29", "Cinemateca de Bogotá", Categoria.CULTURA, "Centro", 4.6080, -74.0700, 1,
                "Cine independiente, clásicos y ciclos temáticos a buen precio.",
                CULTURAL, ROMANTICO, IDEAL_SOLO, TRANQUILO));
        l.agregarAlFinal(lugar("P30", "Museo de Arte Moderno (MAMBO)", Categoria.CULTURA, "Centro Internacional", 4.6105, -74.0690, 1,
                "Arte moderno y contemporáneo colombiano en un edificio de Salmona.",
                CULTURAL, TRANQUILO, ROMANTICO, IDEAL_SOLO));
        l.agregarAlFinal(lugar("P31", "Quinta de Bolívar", Categoria.CULTURA, "Las Aguas", 4.6015, -74.0640, 1,
                "Casa y jardines donde vivió Bolívar. Corta, bonita y tranquila.",
                CULTURAL, TRANQUILO, FAMILIAR, ROMANTICO));
        l.agregarAlFinal(lugar("P32", "Recorrido de grafiti", Categoria.CULTURA, "La Candelaria", 4.5985, -74.0710, 0,
                "Murales de artistas locales explicados por quienes los pintaron. Se paga con propina.",
                CULTURAL, SOCIAL, IDEAL_SOLO, AIRE_LIBRE));
        l.agregarAlFinal(lugar("P33", "Plaza de Bolívar", Categoria.CULTURA, "Centro", 4.5981, -74.0760, 0,
                "Catedral, Capitolio y palomas. El punto de partida para recorrer el centro.",
                CULTURAL, FAMILIAR, AIRE_LIBRE));

        // Diversión y aventura
        l.agregarAlFinal(lugar("P34", "Maloka", Categoria.DIVERSION, "Ciudad Salitre", 4.6553, -74.1090, 2,
                "Museo interactivo de ciencia: aquí se puede tocar todo. Tiene domo de cine.",
                ACTIVIDAD, FAMILIAR, CULTURAL, GRUPOS));
        l.agregarAlFinal(lugar("P35", "Movistar Arena", Categoria.DIVERSION, "Teusaquillo", 4.6485, -74.0770, 3,
                "Los conciertos grandes de la ciudad. Revisa la agenda antes de armar el plan.",
                EMOCIONANTE, SOCIAL, NOCTURNO, GRUPOS, ALCOHOL));
        l.agregarAlFinal(lugar("P36", "Salitre Mágico", Categoria.DIVERSION, "Salitre", 4.6680, -74.0905, 2,
                "Montaña rusa, rueda de Chicago y carros chocones. Plan de gritar con amigos.",
                EMOCIONANTE, ACTIVIDAD, GRUPOS, FAMILIAR));
        l.agregarAlFinal(lugar("P37", "Mundo Aventura", Categoria.DIVERSION, "Kennedy", 4.6200, -74.1360, 2,
                "Parque de atracciones con zona extrema y granja para los más pequeños.",
                EMOCIONANTE, ACTIVIDAD, FAMILIAR, GRUPOS));
        l.agregarAlFinal(lugar("P38", "Club de Tejo La 76", Categoria.DIVERSION, "Chapinero", 4.6620, -74.0620, 1,
                "Tejo, mecha y cerveza: el deporte nacional en su versión más bogotana.",
                SOCIAL, ALCOHOL, EMOCIONANTE, GRUPOS, NOCTURNO, ACTIVIDAD));
        l.agregarAlFinal(lugar("P39", "Cine Colombia Andino", Categoria.DIVERSION, "Zona Rosa", 4.6670, -74.0530, 2,
                "Estrenos, salas cómodas y crispetas. El plan seguro cuando llueve.",
                ROMANTICO, TRANQUILO, GRUPOS, FAMILIAR));
        l.agregarAlFinal(lugar("P40", "Parapente en Sopó", Categoria.AVENTURA, "Sopó", 4.9000, -73.9400, 3,
                "Vuelo en tándem sobre la sabana. A una hora de Bogotá; se reserva con anticipación.",
                EMOCIONANTE, PAISAJE, AIRE_LIBRE, ROMANTICO));
        l.agregarAlFinal(lugar("P41", "Laguna de Guatavita", Categoria.AVENTURA, "Sesquilé", 4.9786, -73.7760, 2,
                "La laguna de la leyenda de El Dorado. Caminata guiada con vista a los cerros.",
                PAISAJE, AIRE_LIBRE, ACTIVIDAD, CULTURAL, FAMILIAR));
        l.agregarAlFinal(lugar("P42", "Catedral de Sal de Zipaquirá", Categoria.AVENTURA, "Zipaquirá", 5.0190, -74.0060, 2,
                "Una catedral tallada dentro de una mina de sal, a 180 metros bajo tierra.",
                CULTURAL, FAMILIAR, ROMANTICO));
        l.agregarAlFinal(lugar("P43", "Cascada La Chorrera", Categoria.AVENTURA, "Choachí", 4.5500, -73.9800, 2,
                "La cascada más alta de Colombia. Caminata de medio día con barro incluido.",
                PAISAJE, ACTIVIDAD, AIRE_LIBRE, EMOCIONANTE));

        // Rumba
        l.agregarAlFinal(lugar("P44", "Andrés D.C.", Categoria.RUMBA, "Zona Rosa", 4.6670, -74.0530, 3,
                "Cuatro pisos de comida, decoración loca y baile hasta tarde.",
                ALCOHOL, SOCIAL, NOCTURNO, COMIDA, GRUPOS, EMOCIONANTE));
        l.agregarAlFinal(lugar("P45", "Andrés Carne de Res", Categoria.RUMBA, "Chía", 4.8619, -74.0322, 3,
                "La original, en Chía. Se va a comer y se termina bailando. Hay que planear el regreso.",
                ALCOHOL, SOCIAL, NOCTURNO, COMIDA, GRUPOS, EMOCIONANTE, ROMANTICO));
        l.agregarAlFinal(lugar("P46", "Zona T", Categoria.RUMBA, "Zona Rosa", 4.6667, -74.0533, 2,
                "Calles peatonales llenas de bares y discotecas. Siempre hay algo abierto.",
                ALCOHOL, SOCIAL, NOCTURNO, GRUPOS));
        l.agregarAlFinal(lugar("P47", "Theatron", Categoria.RUMBA, "Chapinero", 4.6440, -74.0645, 2,
                "Una de las discotecas más grandes de Latinoamérica, con muchos ambientes.",
                ALCOHOL, SOCIAL, NOCTURNO, GRUPOS, EMOCIONANTE));
        l.agregarAlFinal(lugar("P48", "Bogotá Beer Company Parque 93", Categoria.RUMBA, "Chicó", 4.6762, -74.0490, 2,
                "Cerveza artesanal bogotana, alitas y partidos en pantalla grande.",
                ALCOHOL, SOCIAL, GRUPOS, COMIDA));
        l.agregarAlFinal(lugar("P49", "Galería Café Libro", Categoria.RUMBA, "Zona Rosa", 4.6665, -74.0545, 2,
                "Salsa en vivo. Si no sabes bailar, aquí alguien te enseña.",
                ALCOHOL, SOCIAL, NOCTURNO, ROMANTICO, EMOCIONANTE));
        l.agregarAlFinal(lugar("P50", "Armando Records", Categoria.RUMBA, "Zona Rosa", 4.6668, -74.0540, 2,
                "Terraza en un cuarto piso con música indie y electrónica.",
                ALCOHOL, SOCIAL, NOCTURNO, GRUPOS));

        // Comida
        l.agregarAlFinal(lugar("P51", "La Puerta Falsa", Categoria.COMIDA, "La Candelaria", 4.5980, -74.0752, 1,
                "Abierta desde 1816. Tamal, chocolate con queso y almojábanas.",
                COMIDA, CULTURAL, TRANQUILO, IDEAL_SOLO));
        l.agregarAlFinal(lugar("P52", "Plaza de Paloquemao", Categoria.COMIDA, "Paloquemao", 4.6160, -74.0855, 1,
                "Frutas que no conocías, flores y almuerzo corrientazo. Mejor por la mañana.",
                COMIDA, CULTURAL, SOCIAL, FAMILIAR, IDEAL_SOLO));
        l.agregarAlFinal(lugar("P53", "Crepes & Waffles Parque 93", Categoria.COMIDA, "Chicó", 4.6768, -74.0478, 2,
                "Crepes, ensaladas y los helados de siempre. No falla.",
                COMIDA, ROMANTICO, FAMILIAR, TRANQUILO));
        l.agregarAlFinal(lugar("P54", "Leo Cocina y Cava", Categoria.COMIDA, "Chapinero Alto", 4.6530, -74.0560, 3,
                "Menú de degustación con ingredientes de todas las regiones de Colombia.",
                COMIDA, ROMANTICO, CULTURAL));
        l.agregarAlFinal(lugar("P55", "El Chato", Categoria.COMIDA, "Chapinero", 4.6480, -74.0620, 3,
                "Cocina de autor con producto colombiano. Para una ocasión especial.",
                COMIDA, ROMANTICO));
        l.agregarAlFinal(lugar("P56", "Zona G", Categoria.COMIDA, "Chapinero", 4.6560, -74.0575, 3,
                "Varias cuadras de restaurantes: italiano, peruano, tailandés, de todo.",
                COMIDA, ROMANTICO, SOCIAL));
        l.agregarAlFinal(lugar("P57", "Mini-mal", Categoria.COMIDA, "Chapinero", 4.6450, -74.0610, 2,
                "Cocina colombiana contemporánea en una casa llena de diseño local.",
                COMIDA, CULTURAL, ROMANTICO));
        l.agregarAlFinal(lugar("P58", "Club Colombia", Categoria.COMIDA, "Zona Rosa", 4.6668, -74.0525, 3,
                "Ajiaco, sobrebarriga y postres de la abuela en una casona inglesa.",
                COMIDA, ROMANTICO, CULTURAL, FAMILIAR));
        l.agregarAlFinal(lugar("P59", "Abasto", Categoria.COMIDA, "Usaquén", 4.6955, -74.0300, 2,
                "Desayunos y almuerzos con productos de pequeños productores.",
                COMIDA, ROMANTICO, TRANQUILO));
        l.agregarAlFinal(lugar("P60", "Wok Parque 93", Categoria.COMIDA, "Chicó", 4.6770, -74.0485, 2,
                "Comida asiática para compartir. Funciona bien con grupos grandes.",
                COMIDA, FAMILIAR, GRUPOS, SOCIAL));

        // Cafés
        l.agregarAlFinal(lugar("P61", "Azahar Café", Categoria.CAFE, "Chicó", 4.6780, -74.0530, 1,
                "Café de origen preparado de mil formas. Buenas mesas para quedarse un rato.",
                TRANQUILO, IDEAL_SOLO, ROMANTICO, COMIDA));
        l.agregarAlFinal(lugar("P62", "Amor Perfecto", Categoria.CAFE, "Chapinero", 4.6545, -74.0565, 1,
                "Tostadores colombianos con catas guiadas. Para aprender qué tomas.",
                TRANQUILO, IDEAL_SOLO, ROMANTICO, CULTURAL));
        l.agregarAlFinal(lugar("P63", "Varietale", Categoria.CAFE, "Chapinero", 4.6470, -74.0605, 1,
                "Café de especialidad y pastelería. Wifi estable para trabajar.",
                TRANQUILO, IDEAL_SOLO));

        // Más deporte y estudio
        l.agregarAlFinal(lugar("P64", "Ciclovía de la Séptima", Categoria.DEPORTE, "Chapinero", 4.6400, -74.0640, 0,
                "Domingos y festivos de 7 a. m. a 2 p. m. las calles son para bicis y patines.",
                ACTIVIDAD, AIRE_LIBRE, FAMILIAR, IDEAL_SOLO, GRUPOS));
        l.agregarAlFinal(lugar("P65", "Unidad Deportiva El Salitre", Categoria.DEPORTE, "Salitre", 4.6620, -74.0880, 0,
                "Canchas de fútbol, baloncesto y voleibol. Se pueden reservar.",
                ACTIVIDAD, GRUPOS, AIRE_LIBRE));
        l.agregarAlFinal(lugar("P66", "Parque El Tunal", Categoria.PARQUE, "Tunjuelito", 4.5770, -74.1300, 0,
                "Parque metropolitano con biblioteca, canchas y espacio para correr.",
                AIRE_LIBRE, FAMILIAR, ACTIVIDAD, GRUPOS));
        l.agregarAlFinal(lugar("P67", "Biblioteca El Tintal", Categoria.ESTUDIO, "Kennedy", 4.6425, -74.1530, 0,
                "Una planta de basuras convertida en biblioteca. Amplia y luminosa.",
                TRANQUILO, IDEAL_SOLO, CULTURAL));
        l.agregarAlFinal(lugar("P68", "Biblioteca Julio Mario Santo Domingo", Categoria.ESTUDIO, "Suba", 4.7295, -74.0705, 0,
                "Salas modernas junto al Teatro Mayor. Buen lugar para estudiar en el norte.",
                TRANQUILO, IDEAL_SOLO, CULTURAL));
        return l;
    }

    private static Lugar lugar(String id, String nombre, Categoria categoria, String zona, double latitud,
                               double longitud, int precio, String descripcion, Etiqueta... etiquetas) {
        String direccion = UNAL.equals(zona) ? CAMPUS : null;
        return new Lugar(id, nombre, categoria, direccion, zona, latitud, longitud, precio,
                Etiqueta.mascara(etiquetas), descripcion);
    }

    private static final String[] NOMBRES = {
        "Juan Camilo", "Santiago", "Pablo", "Valentina", "Mateo", "Sofía", "Daniela", "Andrés", "Laura",
        "Sebastián", "Mariana", "Camila", "Nicolás", "Isabella", "Felipe", "Natalia", "Alejandro", "Paula",
        "Tomás", "Gabriela", "Juliana", "David", "Manuela", "Samuel", "Sara", "Esteban", "Catalina", "Diego",
        "Luisa", "Miguel", "Ana María", "Julián", "Carolina", "Simón", "Antonia", "Martín", "Lina", "Daniel",
        "Melissa", "Óscar"
    };

    public static ListaEnlazada<Usuario> usuarios() {
        ListaEnlazada<Usuario> usuarios = new ListaEnlazada<>();
        for (int i = 0; i < NOMBRES.length; i++) {
            usuarios.agregarAlFinal(new Usuario("U" + (i + 1), NOMBRES[i]));
        }
        return usuarios;
    }

    public static ListaEnlazada<Ubicacion> ubicaciones() {
        ListaEnlazada<Ubicacion> ubicaciones = new ListaEnlazada<>();
        ubicaciones.agregarAlFinal(new Ubicacion("Universidad Nacional", 4.6357, -74.0829));
        ubicaciones.agregarAlFinal(new Ubicacion("Chapinero", 4.6486, -74.0628));
        ubicaciones.agregarAlFinal(new Ubicacion("Parque de la 93", 4.6766, -74.0483));
        ubicaciones.agregarAlFinal(new Ubicacion("Usaquén", 4.6950, -74.0310));
        ubicaciones.agregarAlFinal(new Ubicacion("Centro (Plaza de Bolívar)", 4.5981, -74.0760));
        ubicaciones.agregarAlFinal(new Ubicacion("Ciudad Salitre", 4.6530, -74.1060));
        ubicaciones.agregarAlFinal(new Ubicacion("Suba", 4.7400, -74.0830));
        ubicaciones.agregarAlFinal(new Ubicacion("Kennedy", 4.6280, -74.1530));
        return ubicaciones;
    }

    // Títulos por categoría para los parches generados
    private static String[] titulos(Categoria categoria) {
        switch (categoria) {
            case COMIDA: return new String[] {"Almuerzo largo de domingo", "Cena de cumpleaños de Lau",
                "Probar algo nuevo", "Brunch después del parcial"};
            case CAFE: return new String[] {"Café y charla sin afán", "Trabajar juntos con café",
                "Cata de café para principiantes"};
            case RUMBA: return new String[] {"Viernes de salsa", "Cervezas después del parcial",
                "Cumpleaños de Mateo", "Rumba hasta que cierre"};
            case CULTURA: return new String[] {"Museo y almuerzo", "Concierto y luego comer algo",
                "Recorrido por el centro", "Plan de domingo cultural"};
            case MIRADOR: return new String[] {"Atardecer con vista", "Subir temprano y desayunar arriba",
                "Ver la ciudad de noche"};
            case PARQUE: return new String[] {"Picnic y frisbee", "Caminata y helado", "Leer al aire libre",
                "Botes y onces"};
            case DEPORTE: return new String[] {"Fútbol 5 después de clase", "Trote suave de 5 km",
                "Partido en el estadio", "Bici dominical"};
            case DIVERSION: return new String[] {"Montaña rusa y gritos", "Tejo y cerveza", "Cine y crispetas",
                "Concierto con el parche"};
            case AVENTURA: return new String[] {"Paseo de un día", "Caminata al amanecer", "Salir de la ciudad"};
            default: return new String[] {"Estudio para el parcial de Cálculo", "Repaso de Estructuras de Datos",
                "Sesión de lectura en silencio", "Grupo de inglés conversacional"};
        }
    }

    // Hora habitual de cada tipo de plan
    private static int hora(Categoria categoria, Random azar) {
        switch (categoria) {
            case RUMBA: return 20 + azar.nextInt(3);
            case COMIDA: return azar.nextBoolean() ? 12 + azar.nextInt(2) : 19 + azar.nextInt(2);
            case MIRADOR: return azar.nextBoolean() ? 6 : 17;
            case AVENTURA: return 7 + azar.nextInt(2);
            case DIVERSION: return 15 + azar.nextInt(5);
            default: return 9 + azar.nextInt(9);
        }
    }

    private static final int[] CAPACIDADES = {2, 4, 4, 5, 6, 6, 8, 10, 12, 15};

    // Parches de ejemplo, con fechas relativas a hoy
    public static void crearParches(GestorParches gestor, CatalogoLugares catalogo,
                                    ListaEnlazada<Usuario> usuarios, LocalDateTime ahora) {
        Usuario juan = usuarios.obtener(0);
        Usuario santiago = usuarios.obtener(1);
        Usuario pablo = usuarios.obtener(2);
        Usuario valentina = usuarios.obtener(3);
        Usuario mateo = usuarios.obtener(4);
        LocalDate hoy = ahora.toLocalDate();

        // Los cuatro de la Entrega 1
        Parche estudio = gestor.crear(juan, catalogo.buscarPorId("P01"), "Estudio de Álgebra Lineal",
                hoy.plusDays(1).atTime(14, 0), 6, ahora);
        estudio.unirse(santiago);

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

        // El resto al azar. Se pasan a arreglos para escoger en O(1)
        Usuario[] gente = new Usuario[usuarios.tamano()];
        int i = 0;
        for (Usuario usuario : usuarios) {
            gente[i++] = usuario;
        }
        ListaEnlazada<Lugar> todos = catalogo.todos();
        Lugar[] sitios = new Lugar[todos.tamano()];
        i = 0;
        for (Lugar lugar : todos) {
            sitios[i++] = lugar;
        }

        Random azar = new Random(SEMILLA);
        for (int n = 0; n < 44; n++) {
            Lugar lugar = sitios[azar.nextInt(sitios.length)];
            Usuario creador = gente[azar.nextInt(gente.length)];
            String[] opciones = titulos(lugar.getCategoria());
            LocalDateTime cuando = hoy.plusDays(1 + azar.nextInt(12)).atTime(hora(lugar.getCategoria(), azar),
                    azar.nextBoolean() ? 0 : 30);
            int capacidad = CAPACIDADES[azar.nextInt(CAPACIDADES.length)];
            Parche parche = gestor.crear(creador, lugar, opciones[azar.nextInt(opciones.length)], cuando, capacidad, ahora);
            // Algunos quedan llenos y con fila de espera
            int interesados = azar.nextInt(capacidad + 3);
            for (int k = 0; k < interesados; k++) {
                parche.unirse(gente[azar.nextInt(gente.length)]);
            }
        }

        // Pablo ya tiene vida social: organiza uno, va a otros y espera cupo en otro
        Parche cafe = gestor.crear(pablo, catalogo.buscarPorId("P62"), "Cata de café para principiantes",
                hoy.plusDays(4).atTime(10, 0), 5, ahora);
        cafe.unirse(gente[9]);
        cafe.unirse(gente[11]);
        gestor.buscarPorId("PCH-006").unirse(pablo);
        gestor.buscarPorId("PCH-012").unirse(pablo);
        concierto.unirse(pablo);
    }
}
