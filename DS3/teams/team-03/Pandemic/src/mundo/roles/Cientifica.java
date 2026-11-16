package mundo.roles;

import juego.LogicaJuego;
import mundo.Personaje;

public class Cientifica extends Rol {

    public Cientifica() {
        super(
            "Científica",
            "Solo necesita 4 cartas de Ciudad del mismo color (en lugar de 5) para descubrir la cura de esa enfermedad."
        );
    }

    @Override
    public boolean descubrirCura(Personaje jugador, String color, LogicaJuego juego) {
        if (!jugador.obtenerCiudadActual().tenerCentroDeInvestigacion()) {
            System.out.println("Debes estar en una estación de investigación.");
            return false;
        }

        int indice = juego.indiceColor(color);
        if (indice == -1) {
            System.out.println("Color inválido. Elige Azul, Amarillo, Negro o Rojo.");
            return false;
        }

        if (juego.estaCurada(indice)) {
            System.out.println("Ya se descubrió la cura de ese color.");
            return false;
        }

        if (!jugador.descartarCartasDeCiudad(LogicaJuego.COLORES[indice], 4)) {
            System.out.println("Como Científica, necesitas cuatro cartas de ciudad del mismo color.");
            return false;
        }

        juego.registrarCura(indice);
        System.out.println("¡Cura descubierta por la Científica! Color: " + LogicaJuego.COLORES[indice] + ".");
        return true;
    }
}