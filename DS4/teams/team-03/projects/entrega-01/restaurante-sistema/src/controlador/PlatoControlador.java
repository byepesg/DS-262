package controlador;

import java.util.ArrayList;
import modelo.entidades.Plato; 

public class PlatoControlador {

    // pendiente  que listaPlatos.add se cree desde un json
    public ArrayList<Plato> cargarPlatosDesdeJSON() {
        ArrayList<Plato> listaPlatos = new ArrayList<>();

        // plato 1 
        Plato plato1 = new Plato();
        plato1.setNombre("Hamburguesa con Papas");
        plato1.setPrecio(45.00);
        listaPlatos.add(plato1); 

        // plato 2
        Plato plato2 = new Plato();
        plato2.setNombre("Pizza Personal");
        plato2.setPrecio(35.50);
        listaPlatos.add(plato2);

        //  plato 3
        Plato plato3 = new Plato();
        plato3.setNombre("Tacos al Pastor");
        plato3.setPrecio(30.00);
        listaPlatos.add(plato3);

        //  plato 4
        Plato plato4 = new Plato();
        plato4.setNombre("Refresco Grande");
        plato4.setPrecio(12.00);
        listaPlatos.add(plato4);

     
        return listaPlatos;
    }
}
