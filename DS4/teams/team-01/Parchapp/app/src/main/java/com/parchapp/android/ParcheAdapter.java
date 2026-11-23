package com.parchapp.android;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import java.time.LocalDate;

// Parches agrupados por día ("Mañana", "Sábado 29"...). Igual que LugarAdapter, usa un arreglo.
// La lista tiene que venir ordenada por fecha.
final class ParcheAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TITULO = 0;
    private static final int PARCHE = 1;
    private static final int MINI = 2;

    private final boolean compacto;
    private final boolean agruparPorDia;
    // Cada posición es un Parche o un String con el título del día
    private Object[] filas = new Object[0];

    ParcheAdapter(boolean compacto, boolean agruparPorDia) {
        this.compacto = compacto;
        this.agruparPorDia = agruparPorDia;
    }

    void mostrar(ListaEnlazada<Parche> lista) {
        // Primero se cuentan los días distintos para saber el tamaño exacto del arreglo
        int dias = 0;
        LocalDate anterior = null;
        for (Parche parche : lista) {
            LocalDate dia = parche.getFechaHora().toLocalDate();
            if (!dia.equals(anterior)) {
                dias++;
                anterior = dia;
            }
        }
        filas = new Object[lista.tamano() + (agruparPorDia ? dias : 0)];
        int i = 0;
        anterior = null;
        for (Parche parche : lista) {
            LocalDate dia = parche.getFechaHora().toLocalDate();
            if (agruparPorDia && !dia.equals(anterior)) {
                filas[i++] = Formato.dia(dia);
                anterior = dia;
            }
            filas[i++] = parche;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int posicion) {
        if (filas[posicion] instanceof String) {
            return TITULO;
        }
        return compacto ? MINI : PARCHE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        if (tipo == TITULO) {
            return new RecyclerView.ViewHolder(Estilo.inflar(padre, R.layout.item_dia)) { };
        }
        return new Fila(Estilo.inflar(padre, tipo == MINI ? R.layout.item_parche_mini : R.layout.item_parche));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder fila, int posicion) {
        if (fila instanceof Fila) {
            ((Fila) fila).llenar((Parche) filas[posicion]);
        } else {
            ((TextView) fila.itemView).setText((String) filas[posicion]);
        }
    }

    @Override
    public int getItemCount() {
        return filas.length;
    }

    // También la usa la pantalla de un lugar para su lista corta de parches
    static final class Fila extends RecyclerView.ViewHolder {
        private final View fondo;
        private final ImageView icono;
        private final TextView hora;
        private final TextView cuando;
        private final TextView titulo;
        private final TextView estado;
        private final TextView lugar;
        private final LinearLayout avatares;
        private final TextView personas;
        private final TextView rol;

        Fila(View vista) {
            super(vista);
            View fondoHora = vista.findViewById(R.id.fondoHora);
            fondo = fondoHora != null ? fondoHora : vista.findViewById(R.id.franja);
            icono = vista.findViewById(R.id.icono);
            hora = vista.findViewById(R.id.hora);
            cuando = vista.findViewById(R.id.cuando);
            titulo = vista.findViewById(R.id.titulo);
            estado = vista.findViewById(R.id.estado);
            lugar = vista.findViewById(R.id.lugar);
            avatares = vista.findViewById(R.id.avatares);
            personas = vista.findViewById(R.id.personas);
            rol = vista.findViewById(R.id.rol);
        }

        // En listas que no están agrupadas por día, la fecha se muestra dentro de la tarjeta
        void conFecha() {
            cuando.setVisibility(View.VISIBLE);
        }

        void llenar(Parche parche) {
            Estilo.pintarCategoria(fondo, icono, parche.getLugar().getCategoria());
            titulo.setText(parche.getTitulo());
            lugar.setText(parche.getLugar().getNombre());
            if (hora != null) {
                hora.setText(Formato.hora(parche.getFechaHora()));
                hora.setTextColor(Estilo.color(hora.getContext(), Estilo.tinta(parche.getLugar().getCategoria())));
            }
            if (cuando != null) {
                cuando.setText(Formato.cuando(parche.getFechaHora()));
                cuando.setTextColor(Estilo.color(cuando.getContext(), Estilo.tinta(parche.getLugar().getCategoria())));
            }
            if (estado != null) {
                estado.setVisibility(parche.getEstado() == com.parchapp.dominio.EstadoParche.ABIERTO ? View.GONE : View.VISIBLE);
                Estilo.pastillaEstado(estado, parche.getEstado());
            }
            Estilo.pilaDeAvatares(avatares, parche.getParticipantes(), parche.cantidadParticipantes(), 4);
            String texto = parche.cuposLibres() > 0
                    ? (parche.cuposLibres() == 1 ? "Queda 1 cupo" : "Quedan " + parche.cuposLibres() + " cupos")
                    : parche.cantidadEnEspera() > 0 ? parche.cantidadEnEspera() + " en fila" : "Lleno";
            if (personas != null) {
                personas.setText(texto);
                String miRol = Estilo.rol(parche, Estado.get().usuario);
                rol.setVisibility(miRol == null ? View.GONE : View.VISIBLE);
                rol.setText(miRol);
            } else {
                ((TextView) itemView.findViewById(R.id.cupos)).setText(texto);
            }
            itemView.setAlpha(parche.estaActivo() ? 1f : 0.55f);
            itemView.setOnClickListener(v -> Ui.abrirParche(v.getContext(), parche.getId()));
        }
    }
}
