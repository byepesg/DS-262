package com.parchapp.android;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.google.android.material.color.MaterialColors;
import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.EstadoParche;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.Usuario;

// Colores e íconos de cada categoría, estados de parche y avatares
final class Estilo {

    // Pares {fondo, tinta} que se reparten entre los avatares
    private static final int[][] COLORES_AVATAR = {
        {R.color.cat_comida_fondo, R.color.cat_comida}, {R.color.cat_cultura_fondo, R.color.cat_cultura},
        {R.color.cat_parque_fondo, R.color.cat_parque}, {R.color.cat_mirador_fondo, R.color.cat_mirador},
        {R.color.cat_rumba_fondo, R.color.cat_rumba}, {R.color.cat_diversion_fondo, R.color.cat_diversion},
        {R.color.cat_aventura_fondo, R.color.cat_aventura}, {R.color.cat_deporte_fondo, R.color.cat_deporte}
    };

    private Estilo() {
    }

    static int icono(Categoria categoria) {
        switch (categoria) {
            case COMIDA: return R.drawable.ic_comida;
            case CAFE: return R.drawable.ic_cafe;
            case RUMBA: return R.drawable.ic_rumba;
            case CULTURA: return R.drawable.ic_cultura;
            case MIRADOR: return R.drawable.ic_mirador;
            case PARQUE: return R.drawable.ic_parque;
            case DEPORTE: return R.drawable.ic_deporte;
            case DIVERSION: return R.drawable.ic_diversion;
            case AVENTURA: return R.drawable.ic_aventura;
            default: return R.drawable.ic_estudio;
        }
    }

    static int fondo(Categoria categoria) {
        switch (categoria) {
            case COMIDA: return R.color.cat_comida_fondo;
            case CAFE: return R.color.cat_cafe_fondo;
            case RUMBA: return R.color.cat_rumba_fondo;
            case CULTURA: return R.color.cat_cultura_fondo;
            case MIRADOR: return R.color.cat_mirador_fondo;
            case PARQUE: return R.color.cat_parque_fondo;
            case DEPORTE: return R.color.cat_deporte_fondo;
            case DIVERSION: return R.color.cat_diversion_fondo;
            case AVENTURA: return R.color.cat_aventura_fondo;
            default: return R.color.cat_estudio_fondo;
        }
    }

    static int tinta(Categoria categoria) {
        switch (categoria) {
            case COMIDA: return R.color.cat_comida;
            case CAFE: return R.color.cat_cafe;
            case RUMBA: return R.color.cat_rumba;
            case CULTURA: return R.color.cat_cultura;
            case MIRADOR: return R.color.cat_mirador;
            case PARQUE: return R.color.cat_parque;
            case DEPORTE: return R.color.cat_deporte;
            case DIVERSION: return R.color.cat_diversion;
            case AVENTURA: return R.color.cat_aventura;
            default: return R.color.cat_estudio;
        }
    }

    static int color(Context contexto, int recurso) {
        return ContextCompat.getColor(contexto, recurso);
    }

    static ColorStateList lista(Context contexto, int recurso) {
        return ColorStateList.valueOf(color(contexto, recurso));
    }

    // fondo e ícono de la categoría
    static void pintarCategoria(View fondo, ImageView icono, Categoria categoria) {
        Context c = fondo.getContext();
        if (fondo.getBackground() != null) {
            fondo.setBackgroundTintList(lista(c, fondo(categoria)));
        } else {
            fondo.setBackgroundColor(color(c, fondo(categoria)));
        }
        icono.setImageResource(icono(categoria));
        icono.setImageTintList(lista(c, tinta(categoria)));
    }

    static void pastillaCategoria(TextView pastilla, Categoria categoria) {
        Context c = pastilla.getContext();
        pastilla.setText(categoria.getNombre());
        pastilla.setBackgroundTintList(lista(c, R.color.sup_mas_baja));
        pastilla.setTextColor(color(c, tinta(categoria)));
    }

    static void pastillaEstado(TextView pastilla, EstadoParche estado) {
        Context c = pastilla.getContext();
        pastilla.setText(estado.getNombre());
        switch (estado) {
            case ABIERTO:
                pastilla.setBackgroundTintList(lista(c, R.color.cat_parque_fondo));
                pastilla.setTextColor(color(c, R.color.cat_parque));
                break;
            case LLENO:
                pastilla.setBackgroundTintList(lista(c, R.color.estado_lleno_fondo));
                pastilla.setTextColor(color(c, R.color.estado_lleno));
                break;
            default:
                pastilla.setBackgroundTintList(ColorStateList.valueOf(MaterialColors.getColor(pastilla,
                        com.google.android.material.R.attr.colorSurfaceContainerHighest)));
                pastilla.setTextColor(MaterialColors.getColor(pastilla,
                        com.google.android.material.R.attr.colorOnSurfaceVariant));
        }
    }

    // Iniciales en un círculo. El color sale del id, así cada quien tiene siempre el suyo
    static void avatar(TextView vista, Usuario usuario) {
        Context c = vista.getContext();
        int[] par = COLORES_AVATAR[Math.floorMod(usuario.getId().hashCode(), COLORES_AVATAR.length)];
        vista.setText(Formato.iniciales(usuario.getNombre()));
        vista.setBackgroundTintList(lista(c, par[0]));
        vista.setTextColor(color(c, par[1]));
    }

    // avatares encimados, hasta maximo y después "+n"
    static void pilaDeAvatares(LinearLayout contenedor, Iterable<Usuario> personas, int total, int maximo) {
        contenedor.removeAllViews();
        Context c = contenedor.getContext();
        float densidad = c.getResources().getDisplayMetrics().density;
        int tamano = (int) (28 * densidad);
        int borde = (int) (2 * densidad);
        int fondoTarjeta = MaterialColors.getColor(contenedor, com.google.android.material.R.attr.colorSurfaceContainerLow);
        int puestos = 0;
        for (Usuario usuario : personas) {
            if (puestos == maximo) {
                break;
            }
            TextView circulo = circulo(c, tamano, borde, fondoTarjeta, puestos);
            int[] par = COLORES_AVATAR[Math.floorMod(usuario.getId().hashCode(), COLORES_AVATAR.length)];
            ((GradientDrawable) circulo.getBackground()).setColor(color(c, par[0]));
            circulo.setTextColor(color(c, par[1]));
            circulo.setText(Formato.iniciales(usuario.getNombre()).substring(0, 1));
            contenedor.addView(circulo);
            puestos++;
        }
        if (total > puestos) {
            TextView mas = circulo(c, tamano, borde, fondoTarjeta, puestos);
            ((GradientDrawable) mas.getBackground()).setColor(MaterialColors.getColor(contenedor,
                    com.google.android.material.R.attr.colorSurfaceContainerHighest));
            mas.setTextColor(MaterialColors.getColor(contenedor, com.google.android.material.R.attr.colorOnSurfaceVariant));
            mas.setText("+" + (total - puestos));
            contenedor.addView(mas);
        }
    }

    private static TextView circulo(Context c, int tamano, int borde, int colorBorde, int posicion) {
        TextView circulo = new TextView(c);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamano, tamano);
        params.setMarginStart(posicion == 0 ? 0 : -tamano / 3);
        circulo.setLayoutParams(params);
        circulo.setGravity(android.view.Gravity.CENTER);
        circulo.setTextSize(11);
        circulo.setTypeface(circulo.getTypeface(), android.graphics.Typeface.BOLD);
        GradientDrawable forma = new GradientDrawable();
        forma.setShape(GradientDrawable.OVAL);
        forma.setStroke(borde, colorBorde);
        circulo.setBackground(forma);
        return circulo;
    }

    // Qué es el usuario actual en ese parche, o null si no tiene nada que ver
    static String rol(Parche parche, Usuario usuario) {
        if (parche.esCreador(usuario)) {
            return "Organizas";
        }
        if (parche.esParticipante(usuario)) {
            return "Vas";
        }
        int puesto = parche.posicionEnEspera(usuario);
        if (puesto > 0) {
            return "En fila · " + puesto + "º";
        }
        return null;
    }

    static View inflar(ViewGroup padre, int layout) {
        return LayoutInflater.from(padre.getContext()).inflate(layout, padre, false);
    }
}
