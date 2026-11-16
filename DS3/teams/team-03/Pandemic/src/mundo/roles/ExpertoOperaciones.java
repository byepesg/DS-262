package mundo.roles;

import juego.InterfazJuego;
import juego.LogicaJuego;
import mundo.Ciudad;
import mundo.Personaje;
import mundo.cartas.Carta;
import mundo.cartas.CartaCiudad;
import java.util.Scanner;

public class ExpertoOperaciones extends Rol {

    public ExpertoOperaciones() {
        super("Experto en Operaciones",
                "Puede construir centros de investigación sin descartar carta y moverse desde un centro a cualquier ciudad descartando cualquier carta de ciudad.");
    }

    @Override
    protected void imprimirOpcionesBase() {
        super.imprimirOpcionesBase();
        InterfazJuego.opcion(10, "Vuelo especial desde centro");
    }

    @Override
    protected int procesarOpcionBase(int opcion, Personaje personaje, LogicaJuego juego) {
        if (opcion == 10) {
            return ejecutarVueloDesdeCentro(personaje, juego) ? 1 : 0;
        }
        return super.procesarOpcionBase(opcion, personaje, juego);
    }

    @Override
    public boolean construirCentro(Personaje personaje, LogicaJuego juego) {
        Ciudad ciudadActual = personaje.obtenerCiudadActual();

        if (ciudadActual.tenerCentroDeInvestigacion()) {
            System.out.println("Esta ciudad ya cuenta con un centro de investigación.");
            return false;
        }

        if (juego.puedeConstruirCentro()) {
            juego.registrarCentroDeInvestigacion(ciudadActual);
            System.out.println("¡Éxito! Se ha construido un centro de investigación en "
                    + ciudadActual.obtenerNombre() + " (" + juego.obtenerCentrosConstruidos() + "/6).");
            return true;
        } else {
            Ciudad origen = seleccionarCentroAReposicionar(personaje, ciudadActual, juego);
            if (origen == null) {
                return false;
            }
            juego.moverCentroDeInvestigacion(origen, ciudadActual);
            System.out.println("¡Éxito! Centro de investigación reposicionado de "
                    + origen.obtenerNombre() + " a " + ciudadActual.obtenerNombre() + " (6/6).");
            return true;
        }
    }

    private boolean ejecutarVueloDesdeCentro(Personaje personaje, LogicaJuego juego) {
        Ciudad ciudadActual = personaje.obtenerCiudadActual();
        if (!ciudadActual.tenerCentroDeInvestigacion()) {
            System.out.println("Debes estar en una ciudad con centro de investigación para usar esta habilidad.");
            return false;
        }

        if (personaje.obtenerNumCartas() == 0) {
            System.out.println("No tienes cartas en la mano para descartar.");
            return false;
        }

        Scanner scanner = personaje.obtenerScanner();
        InterfazJuego.opcionesCartasCiudad(personaje);
        InterfazJuego.pregunta("lista", "Ingrese el índice de la Carta Ciudad a descartar: ");
        int idxCarta = leerEntero(scanner);

        Carta[] mano = personaje.obtenerMano();
        if (idxCarta < 0 || idxCarta >= personaje.obtenerNumCartas()
                || !(mano[idxCarta] instanceof CartaCiudad)) {
            System.out.println("Debes seleccionar una Carta Ciudad válida.");
            return false;
        }

        InterfazJuego.pregunta("ciudad", "Ingrese el nombre exacto de la ciudad destino: ");
        String nombreDestino = scanner.nextLine().trim();
        Ciudad destino = juego.buscarCiudad(nombreDestino);

        if (destino == null) {
            System.out.println("Ciudad no encontrada en el tablero.");
            return false;
        }

        if (destino == ciudadActual) {
            System.out.println("Ya te encuentras en " + destino.obtenerNombre() + ".");
            return false;
        }

        personaje.removerCartaPorIndice(idxCarta);
        juego.cambiarUbicacion(personaje, destino);
        return true;
    }
}