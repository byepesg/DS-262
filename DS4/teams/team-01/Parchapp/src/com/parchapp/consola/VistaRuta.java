package com.parchapp.consola;

import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;

class VistaRuta {

    private final Pantalla pantalla;
    private final Sesion sesion;

    VistaRuta(Pantalla pantalla, Sesion sesion) {
        this.pantalla = pantalla;
        this.sesion = sesion;
    }

    void ver() {
        while (true) {
            pantalla.nueva("Ruta del día", sesion.resumen());
            pantalla.linea("  " + pantalla.negrita("Tu ruta del día") + pantalla.gris("   " + Pantalla.contar(sesion.ruta.tamano(), "parada", "paradas")));
            pantalla.nota("Las paradas se visitan en el orden en que las agregaste: la primera que entra es la primera que sale.");
            pantalla.vacio();

            if (sesion.ruta.estaVacia()) {
                pantalla.linea("  " + pantalla.amarillo("Tu ruta está vacía."));
                pantalla.nota("Abre un lugar en Explorar lugares y elige \"Agregar a mi ruta del día\".");
                pantalla.titulo("Opciones");
                pantalla.opcion("0", "Volver");
                pantalla.preguntar("Opción:");
                return;
            }

            pantalla.linea("  " + pantalla.gris("Sales de " + sesion.ubicacion.getNombre()));
            Ubicacion anterior = sesion.ubicacion;
            int numero = 1;
            for (Lugar parada : sesion.ruta.paradas()) {
                String tramo = "+" + Pantalla.distancia(anterior.distanciaKm(parada));
                pantalla.linea("  " + pantalla.cian(Pantalla.ajustarDerecha(String.valueOf(numero), 3)) + "  "
                        + Pantalla.ajustar(parada.getNombre(), 54) + pantalla.gris(Pantalla.ajustarDerecha(tramo, 15)));
                anterior = new Ubicacion(parada);
                numero++;
            }
            pantalla.vacio();
            pantalla.dato("Recorrido", Pantalla.distancia(sesion.ruta.distanciaTotalKm(sesion.ubicacion)) + " en línea recta");

            pantalla.titulo("Opciones");
            pantalla.opcion("S", "Ir a la siguiente parada", sesion.ruta.proxima().getNombre());
            pantalla.opcion("V", "Vaciar la ruta");
            pantalla.opcion("0", "Volver");

            String respuesta = pantalla.preguntar("Opción:").toUpperCase();
            if (respuesta.equals("0")) {
                return;
            } else if (respuesta.equals("S")) {
                irASiguienteParada();
            } else if (respuesta.equals("V")) {
                if (pantalla.confirmar("¿Vaciar toda la ruta?")) {
                    sesion.ruta.vaciar();
                    pantalla.exito("Ruta vaciada.");
                }
            } else if (!respuesta.isEmpty()) {
                pantalla.error("\"" + respuesta + "\" no es una opción válida.");
            }
        }
    }

    private void irASiguienteParada() {
        Lugar parada = sesion.ruta.avanzar();
        VistaLugares.abrirComoLlegar(pantalla, parada);
        // Al llegar a la parada, esa pasa a ser la ubicación desde donde se miden las distancias
        sesion.ubicacion = new Ubicacion(parada);
        pantalla.info("Ahora tu ubicación es " + parada.getNombre() + ".");
        if (sesion.ruta.estaVacia()) {
            pantalla.exito("¡Completaste tu ruta del día!");
        }
    }
}
