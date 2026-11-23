package com.parchapp.android;

import com.parchapp.datos.DatosDemo;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import com.parchapp.servicio.HistorialVistas;
import java.time.LocalDateTime;

// Lo que la app tiene en memoria, compartido por todas las pantallas (como Sesion en la consola).
// Todavía no hay persistencia: al cerrar la app se pierde.
final class Estado {

    private static Estado instancia;

    final CatalogoLugares catalogo;
    final GestorParches gestor;
    final ListaEnlazada<Usuario> usuarios;
    final ListaEnlazada<Ubicacion> ubicaciones;
    final HistorialVistas historial = new HistorialVistas(10);
    Usuario usuario;
    Ubicacion ubicacion;

    private Estado() {
        catalogo = new CatalogoLugares(DatosDemo.lugares());
        usuarios = DatosDemo.usuarios();
        ubicaciones = DatosDemo.ubicaciones();
        gestor = new GestorParches();
        DatosDemo.crearParches(gestor, catalogo, usuarios, ahora());
        usuario = usuarios.obtener(2);
        ubicacion = ubicaciones.obtenerPrimero();
    }

    static synchronized Estado get() {
        if (instancia == null) {
            instancia = new Estado();
        }
        return instancia;
    }

    static LocalDateTime ahora() {
        return LocalDateTime.now();
    }
}
