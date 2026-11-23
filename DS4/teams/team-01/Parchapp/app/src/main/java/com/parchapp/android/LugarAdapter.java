package com.parchapp.android;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;

// Lugares en un RecyclerView. Pide las filas por posición y obtener(i) es O(n),
// así que la lista se copia una vez a un arreglo y cada fila sale en O(1).
final class LugarAdapter extends RecyclerView.Adapter<LugarAdapter.Fila> {

    private final boolean compacto;
    private Lugar[] lugares = new Lugar[0];
    private int[] parchesActivos = new int[0];

    // compacto = tarjeta pequeña para los carruseles de Inicio
    LugarAdapter(boolean compacto) {
        this.compacto = compacto;
    }

    void mostrar(ListaEnlazada<Lugar> lista) {
        ListaEnlazada<Parche> activos = Estado.get().gestor.activos(Estado.ahora());
        lugares = new Lugar[lista.tamano()];
        parchesActivos = new int[lista.tamano()];
        int i = 0;
        for (Lugar lugar : lista) {
            lugares[i] = lugar;
            for (Parche parche : activos) {
                if (parche.getLugar().equals(lugar)) {
                    parchesActivos[i]++;
                }
            }
            i++;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new Fila(Estilo.inflar(padre, compacto ? R.layout.item_lugar_mini : R.layout.item_lugar));
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Lugar lugar = lugares[posicion];
        double km = Estado.get().ubicacion.distanciaKm(lugar);
        fila.nombre.setText(lugar.getNombre());
        fila.meta.setText(Formato.metaLugar(lugar, km));
        Estilo.pintarCategoria(fila.fondo, fila.icono, lugar.getCategoria());
        if (fila.etiquetas != null) {
            fila.etiquetas.setText(Etiqueta.describir(lugar.getEtiquetas() & ~Etiqueta.mascara(Etiqueta.IDEAL_SOLO)));
        }
        if (fila.parches != null) {
            int cantidad = parchesActivos[posicion];
            fila.parches.setVisibility(cantidad > 0 ? View.VISIBLE : View.GONE);
            fila.parches.setText(cantidad == 1 ? "1 parche" : cantidad + " parches");
        }
        fila.itemView.setOnClickListener(v -> Ui.abrirLugar(v.getContext(), lugar));
    }

    @Override
    public int getItemCount() {
        return lugares.length;
    }

    static final class Fila extends RecyclerView.ViewHolder {
        final View fondo;
        final ImageView icono;
        final TextView nombre;
        final TextView meta;
        final TextView etiquetas;
        final TextView parches;

        Fila(View vista) {
            super(vista);
            View fondoIcono = vista.findViewById(R.id.fondoIcono);
            fondo = fondoIcono != null ? fondoIcono : vista.findViewById(R.id.franja);
            icono = vista.findViewById(R.id.icono);
            nombre = vista.findViewById(R.id.nombre);
            meta = vista.findViewById(R.id.meta);
            etiquetas = vista.findViewById(R.id.etiquetas);
            parches = vista.findViewById(R.id.parches);
        }
    }
}
