package mundo.roles;

import juego.LogicaJuego;
import mundo.Ciudad;
import mundo.Personaje;

public class EspecialistaCuarentena extends Rol {

    public EspecialistaCuarentena() {
        super(
            "Especialista en Cuarentena",
            "Previene brotes y la colocación de cubos de enfermedad en la ciudad donde se encuentra y en todas las ciudades conectadas a ella."
        );
    }

    @Override
    public boolean protegeCiudad(Personaje personaje, Ciudad ciudadObjetivo, LogicaJuego juego) {
        Ciudad ubicacionActual = personaje.obtenerCiudadActual();
        if (ubicacionActual == null || ciudadObjetivo == null) {
            return false;
        }
        return ubicacionActual == ciudadObjetivo || ubicacionActual.esVecino(ciudadObjetivo);
    }
}