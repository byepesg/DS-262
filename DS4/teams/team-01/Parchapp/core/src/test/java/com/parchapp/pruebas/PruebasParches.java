package com.parchapp.pruebas;

import static com.parchapp.pruebas.MiniTest.caso;
import static com.parchapp.pruebas.MiniTest.grupo;
import static com.parchapp.pruebas.MiniTest.iguales;
import static com.parchapp.pruebas.MiniTest.lanza;
import static com.parchapp.pruebas.MiniTest.verificar;

import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.EstadoParche;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.ResultadoUnion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.GestorParches;
import java.time.LocalDateTime;

// Reglas de la sección 10.3 del documento maestro y casos T-07 a T-09
final class PruebasParches {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 10, 16, 12, 0);
    private static final LocalDateTime MANANA = AHORA.plusDays(1);
    private static final Lugar CAFE = new Lugar("P1", "Cafe", Categoria.ESTUDIO, null, 4.6, -74.08, null);
    private static final Usuario ANA = new Usuario("U1", "Ana");
    private static final Usuario BETO = new Usuario("U2", "Beto");
    private static final Usuario CAMI = new Usuario("U3", "Cami");
    private static final Usuario DANI = new Usuario("U4", "Dani");

    private static Parche parcheDe(int capacidad) {
        return new Parche("PCH-001", CAFE, ANA, "Plan de prueba", MANANA, capacidad);
    }

    static void ejecutar() {
        grupo("Parche");

        caso("quien crea el parche cuenta como primer participante", () -> {
            Parche parche = parcheDe(3);
            iguales(1, parche.cantidadParticipantes());
            iguales(2, parche.cuposLibres());
            verificar(parche.esParticipante(ANA), "Ana deberia participar");
        });

        caso("rechaza capacidad cero o fuera de rango (T-07)", () -> {
            lanza(IllegalArgumentException.class, () -> parcheDe(0));
            lanza(IllegalArgumentException.class, () -> parcheDe(1));
            lanza(IllegalArgumentException.class, () -> parcheDe(Parche.CAPACIDAD_MAXIMA + 1));
        });

        caso("rechaza titulo vacio o demasiado largo", () -> {
            lanza(IllegalArgumentException.class, () -> new Parche("X", CAFE, ANA, "  ", MANANA, 3));
            lanza(IllegalArgumentException.class, () -> new Parche("X", CAFE, ANA, "a".repeat(41), MANANA, 3));
        });

        caso("no permite unirse dos veces (T-08)", () -> {
            Parche parche = parcheDe(4);
            iguales(ResultadoUnion.UNIDO, parche.unirse(BETO));
            iguales(ResultadoUnion.YA_PARTICIPA, parche.unirse(BETO));
            iguales(ResultadoUnion.YA_PARTICIPA, parche.unirse(ANA));
            iguales(2, parche.cantidadParticipantes());
        });

        caso("al llenarse cambia a LLENO y no supera la capacidad (T-09)", () -> {
            Parche parche = parcheDe(3);
            parche.unirse(BETO);
            parche.unirse(CAMI);
            iguales(EstadoParche.LLENO, parche.getEstado());
            iguales(ResultadoUnion.EN_LISTA_DE_ESPERA, parche.unirse(DANI));
            iguales(3, parche.cantidadParticipantes());
            iguales(1, parche.posicionEnEspera(DANI));
            iguales(ResultadoUnion.YA_EN_ESPERA, parche.unirse(DANI));
        });

        caso("si alguien sale, entra el primero de la lista de espera (FIFO)", () -> {
            Parche parche = parcheDe(2);
            parche.unirse(BETO);
            parche.unirse(CAMI);
            parche.unirse(DANI);
            Usuario quienEntra = parche.salir(BETO);
            iguales(CAMI, quienEntra);
            verificar(parche.esParticipante(CAMI), "Cami deberia haber entrado");
            iguales(1, parche.posicionEnEspera(DANI));
            iguales(EstadoParche.LLENO, parche.getEstado());
            verificar(parche.estructurasValidas(), "invariantes de la lista y la cola");
        });

        caso("si sale alguien y nadie espera, vuelve a ABIERTO", () -> {
            Parche parche = parcheDe(2);
            parche.unirse(BETO);
            iguales(null, parche.salir(BETO));
            iguales(EstadoParche.ABIERTO, parche.getEstado());
            iguales(1, parche.cuposLibres());
        });

        caso("quien crea no puede salirse y solo el creador puede cancelar", () -> {
            Parche parche = parcheDe(3);
            parche.unirse(BETO);
            lanza(IllegalArgumentException.class, () -> parche.salir(ANA));
            lanza(IllegalArgumentException.class, () -> parche.salir(CAMI));
            lanza(IllegalArgumentException.class, () -> parche.cancelar(BETO));
            parche.cancelar(ANA);
            iguales(EstadoParche.CANCELADO, parche.getEstado());
            iguales(ResultadoUnion.NO_DISPONIBLE, parche.unirse(CAMI));
        });

        caso("un parche cuya hora ya paso queda FINALIZADO", () -> {
            Parche parche = parcheDe(3);
            parche.finalizarSiYaPaso(AHORA);
            iguales(EstadoParche.ABIERTO, parche.getEstado());
            parche.finalizarSiYaPaso(MANANA.plusMinutes(1));
            iguales(EstadoParche.FINALIZADO, parche.getEstado());
            iguales(ResultadoUnion.NO_DISPONIBLE, parche.unirse(BETO));
        });

        grupo("GestorParches");

        caso("crea parches con ids consecutivos", () -> {
            GestorParches gestor = new GestorParches();
            iguales("PCH-001", gestor.crear(ANA, CAFE, "Uno", MANANA, 3, AHORA).getId());
            iguales("PCH-002", gestor.crear(BETO, CAFE, "Dos", MANANA, 3, AHORA).getId());
            iguales("Dos", gestor.buscarPorId("pch-002").getTitulo());
            iguales(null, gestor.buscarPorId("PCH-999"));
        });

        caso("rechaza fechas que ya pasaron", () -> {
            GestorParches gestor = new GestorParches();
            lanza(IllegalArgumentException.class, () -> gestor.crear(ANA, CAFE, "Tarde", AHORA.minusHours(1), 3, AHORA));
            lanza(IllegalArgumentException.class, () -> gestor.crear(ANA, CAFE, "Ahora", AHORA, 3, AHORA));
            iguales(0, gestor.cantidad());
        });

        caso("activos excluye cancelados y finalizados; delUsuario los incluye", () -> {
            GestorParches gestor = new GestorParches();
            Parche cancelado = gestor.crear(ANA, CAFE, "Cancelado", MANANA, 3, AHORA);
            cancelado.cancelar(ANA);
            gestor.crear(ANA, CAFE, "Pronto", AHORA.plusHours(1), 3, AHORA);
            gestor.crear(BETO, CAFE, "De Beto", MANANA, 3, AHORA);

            // "Pronto" ya paso
            LocalDateTime despues = AHORA.plusHours(2);
            iguales(1, gestor.activos(despues).tamano());
            iguales(2, gestor.delUsuario(ANA, despues).tamano());
        });

        caso("ordenarPorFecha deja primero el mas proximo", () -> {
            GestorParches gestor = new GestorParches();
            gestor.crear(ANA, CAFE, "Tercero", AHORA.plusDays(3), 3, AHORA);
            gestor.crear(ANA, CAFE, "Primero", AHORA.plusDays(1), 3, AHORA);
            gestor.crear(ANA, CAFE, "Segundo", AHORA.plusDays(2), 3, AHORA);
            ListaEnlazada<Parche> lista = gestor.activos(AHORA);
            GestorParches.ordenarPorFecha(lista);
            iguales("Primero", lista.obtener(0).getTitulo());
            iguales("Tercero", lista.obtenerUltimo().getTitulo());
        });
    }
}