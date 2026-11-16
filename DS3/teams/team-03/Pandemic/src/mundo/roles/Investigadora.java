package mundo.roles;

public class Investigadora extends Rol {

    public Investigadora() {
        super(
            "Investigadora",
            "Al compartir conocimiento, puede dar cualquier carta de Ciudad de su mano a otro jugador en su misma ciudad, sin necesidad de que coincida con la ciudad actual."
        );
    }

    @Override
    public boolean puedeEntregarCualquierCartaCiudad() {
        return true;
    }
}