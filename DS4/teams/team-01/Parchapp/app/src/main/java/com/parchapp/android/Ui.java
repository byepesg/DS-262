package com.parchapp.android;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Toast;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Ubicacion;
import com.parchapp.dominio.Usuario;
import com.parchapp.servicio.NavegacionMaps;

// Utilidades de interfaz que usan varias pantallas
final class Ui {

    private Ui() {
    }

    // Desde Android 15 se dibuja detrás de las barras: ese espacio se agrega como relleno
    static void respetarBarras(View vista, boolean arriba, boolean abajo) {
        int izquierda = vista.getPaddingLeft();
        int superior = vista.getPaddingTop();
        int derecha = vista.getPaddingRight();
        int inferior = vista.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vista, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(izquierda + barras.left, superior + (arriba ? barras.top : 0),
                    derecha + barras.right, inferior + (abajo ? barras.bottom : 0));
            return insets;
        });
    }

    static void abrirComoLlegar(Context contexto, Lugar lugar) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(NavegacionMaps.urlComoLlegar(lugar)));
        try {
            contexto.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(contexto, R.string.sin_app_mapas, Toast.LENGTH_LONG).show();
        }
    }

    static void abrirLugar(Context contexto, Lugar lugar) {
        contexto.startActivity(new Intent(contexto, LugarActivity.class).putExtra(LugarActivity.EXTRA_LUGAR, lugar.getId()));
    }

    static void abrirParche(Context contexto, String idParche) {
        contexto.startActivity(new Intent(contexto, ParcheActivity.class).putExtra(ParcheActivity.EXTRA_PARCHE, idParche));
    }

    // los diálogos piden un arreglo de textos
    static void elegirUbicacion(Context contexto, Runnable alCambiar) {
        Estado estado = Estado.get();
        CharSequence[] nombres = new CharSequence[estado.ubicaciones.tamano()];
        int actual = 0;
        int i = 0;
        for (Ubicacion ubicacion : estado.ubicaciones) {
            if (ubicacion == estado.ubicacion) {
                actual = i;
            }
            nombres[i++] = ubicacion.getNombre();
        }
        new MaterialAlertDialogBuilder(contexto)
                .setTitle("¿Desde dónde sales?")
                .setSingleChoiceItems(nombres, actual, (dialogo, elegido) -> {
                    estado.ubicacion = estado.ubicaciones.obtener(elegido);
                    dialogo.dismiss();
                    alCambiar.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    static void elegirUsuario(Context contexto, Runnable alCambiar) {
        Estado estado = Estado.get();
        CharSequence[] nombres = new CharSequence[estado.usuarios.tamano()];
        int actual = 0;
        int i = 0;
        for (Usuario usuario : estado.usuarios) {
            if (usuario.equals(estado.usuario)) {
                actual = i;
            }
            nombres[i++] = usuario.getNombre();
        }
        new MaterialAlertDialogBuilder(contexto)
                .setTitle("Usar la app como…")
                .setSingleChoiceItems(nombres, actual, (dialogo, elegido) -> {
                    estado.usuario = estado.usuarios.obtener(elegido);
                    dialogo.dismiss();
                    alCambiar.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Botón de atrás de la barra superior en las pantallas de detalle
    static void barraConAtras(Activity actividad) {
        com.google.android.material.appbar.MaterialToolbar barra = actividad.findViewById(R.id.barra);
        barra.setNavigationOnClickListener(v -> actividad.finish());
    }
}
