package com.parchapp.consola;

import com.parchapp.datos.DatosDemo;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import com.parchapp.servicio.HistorialVistas;
import com.parchapp.servicio.RutaDelDia;
import java.time.LocalDateTime;

// Estado de la aplicación mientras está abierta. Todas las vistas comparten esta misma sesión.
class Sesion {

    final CatalogoLugares catalogo = new CatalogoLugares(DatosDemo.lugares());
    final GestorParches gestor = new GestorParches();
    final HistorialVistas historial = new HistorialVistas(10);
    final RutaDelDia ruta = new RutaDelDia();
    final ListaEnlazada<Usuario> usuarios = DatosDemo.usuarios();
    final ListaEnlazada<Ubicacion> ubicaciones = DatosDemo.ubicaciones();

    Usuario usuario;
    Ubicacion ubicacion = ubicaciones.obtenerPrimero();

    Sesion() {
        DatosDemo.crearParches(gestor, catalogo, usuarios, LocalDateTime.now());
    }

    // Lo que se muestra a la derecha del encabezado
    String resumen() {
        return usuario.getNombre() + " · " + ubicacion.getNombre();
    }
}
