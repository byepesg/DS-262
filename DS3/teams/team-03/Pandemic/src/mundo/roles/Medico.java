package mundo.roles;

import juego.LogicaJuego;
import mundo.Ciudad;
import mundo.Personaje;

public class Medico extends Rol {

    public Medico() {
        super(
            "Médico",
            "Retira todos los cubos de una enfermedad al tratarla. Si una enfermedad está curada, retira sus cubos automáticamente al entrar o estar en la ciudad y previene nuevos cubos."
        );
    }

    @Override
    public boolean tratarEnfermedad(Personaje personaje) {
        Ciudad ciudadActual = personaje.obtenerCiudadActual();
        int cubos = ciudadActual.obtenerCubosDeInfeccion();

        if (cubos > 0) {
            while (ciudadActual.obtenerCubosDeInfeccion() > 0) {
                ciudadActual.quitarCuboDeInfeccion();
            }
            System.out.println("¡Habilidad de Médico! Se removieron TODOS los cubos (" + cubos
                    + ") en " + ciudadActual.obtenerNombre() + ".");
            return true;
        }

        System.out.println("No hay cubos de infección en " + ciudadActual.obtenerNombre() + ".");
        return false;
    }

    @Override
    public void alEntrarACiudad(Personaje personaje, Ciudad ciudad, LogicaJuego juego) {
        limpiarSiEnfermedadCurada(ciudad, juego);
    }

    @Override
    public void alDescubrirCura(Personaje personaje, LogicaJuego juego) {
        limpiarSiEnfermedadCurada(personaje.obtenerCiudadActual(), juego);
    }

    @Override
    public boolean protegeCiudad(Personaje personaje, Ciudad ciudadObjetivo, LogicaJuego juego) {
        if (personaje.obtenerCiudadActual() == ciudadObjetivo) {
            int idxColor = juego.indiceColor(ciudadObjetivo.obtenerColor());
            return juego.estaCurada(idxColor);
        }
        return false;
    }

    private void limpiarSiEnfermedadCurada(Ciudad ciudad, LogicaJuego juego) {
        if (ciudad == null) return;
        int idxColor = juego.indiceColor(ciudad.obtenerColor());
        if (juego.estaCurada(idxColor) && ciudad.obtenerCubosDeInfeccion() > 0) {
            int retirados = ciudad.obtenerCubosDeInfeccion();
            while (ciudad.obtenerCubosDeInfeccion() > 0) {
                ciudad.quitarCuboDeInfeccion();
            }
            System.out.println("[Habilidad Pasiva - Médico] La cura " + ciudad.obtenerColor()
                    + " ya fue descubierta: se eliminaron automáticamente " + retirados
                    + " cubo(s) en " + ciudad.obtenerNombre() + ".");
        }
    }
}