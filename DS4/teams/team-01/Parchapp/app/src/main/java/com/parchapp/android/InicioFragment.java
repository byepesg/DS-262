package com.parchapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Compania;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;

public class InicioFragment extends Pestana {

    private final ParcheAdapter parches = new ParcheAdapter(true, false);
    private final LugarAdapter citas = new LugarAdapter(true);
    private View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor, @Nullable Bundle estado) {
        vista = inflador.inflate(R.layout.fragment_inicio, contenedor, false);

        vista.findViewById(R.id.heroe).setOnClickListener(v -> abrirPlan(null));
        vista.findViewById(R.id.empezar).setOnClickListener(v -> abrirPlan(null));
        vista.findViewById(R.id.avatar).setOnClickListener(v -> irA(R.id.nav_perfil));

        LinearLayout atajos = vista.findViewById(R.id.atajos);
        atajo(atajos, "Cita", R.drawable.ic_pareja, Compania.PAREJA);
        atajo(atajos, "Con amigos", R.drawable.ic_amigos, Compania.AMIGOS);
        atajo(atajos, "Solo", R.drawable.ic_solo, Compania.SOLO);
        atajo(atajos, "En familia", R.drawable.ic_familia, Compania.FAMILIA);

        encabezado(R.id.seccionParches, "Se están armando", "Únete antes de que se llenen", v -> irA(R.id.nav_parches));
        carrusel(R.id.carruselParches, parches);
        encabezado(R.id.seccionCitas, "Para una cita", "Lugares con buena vibra para dos", null);
        carrusel(R.id.carruselCitas, citas);
        encabezado(R.id.seccionCategorias, "¿Qué te provoca?", null, null);
        encabezado(R.id.seccionCerca, "Cerca de ti", null, v -> irA(R.id.nav_explorar));
        crearCategorias(inflador);
        return vista;
    }

    private void abrirPlan(Compania compania) {
        Intent intent = new Intent(requireContext(), PlanActivity.class);
        if (compania != null) {
            intent.putExtra(PlanActivity.EXTRA_COMPANIA, compania.name());
        }
        startActivity(intent);
    }

    private void irA(int pestana) {
        ((MainActivity) requireActivity()).irA(pestana);
    }

    private void atajo(LinearLayout fila, String texto, int icono, Compania compania) {
        MaterialButton boton = (MaterialButton) Estilo.inflar(fila, R.layout.item_atajo);
        boton.setText(texto);
        boton.setIconResource(icono);
        boton.setOnClickListener(v -> abrirPlan(compania));
        fila.addView(boton);
    }

    private void encabezado(int id, String titulo, String subtitulo, View.OnClickListener verTodos) {
        View seccion = vista.findViewById(id);
        ((TextView) seccion.findViewById(R.id.titulo)).setText(titulo);
        TextView sub = seccion.findViewById(R.id.subtitulo);
        sub.setText(subtitulo);
        sub.setVisibility(subtitulo == null ? View.GONE : View.VISIBLE);
        View boton = seccion.findViewById(R.id.verTodos);
        boton.setVisibility(verTodos == null ? View.GONE : View.VISIBLE);
        boton.setOnClickListener(verTodos);
    }

    private void carrusel(int id, RecyclerView.Adapter<?> adaptador) {
        RecyclerView lista = vista.findViewById(id);
        lista.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        lista.setAdapter(adaptador);
    }

    private void crearCategorias(LayoutInflater inflador) {
        GridLayout grilla = vista.findViewById(R.id.categorias);
        Estado estado = Estado.get();
        for (Categoria categoria : Categoria.values()) {
            View tarjeta = inflador.inflate(R.layout.item_categoria, grilla, false);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(tarjeta.getLayoutParams());
            params.width = 0;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            tarjeta.setLayoutParams(params);
            ((com.google.android.material.card.MaterialCardView) tarjeta).setCardBackgroundColor(
                    Estilo.color(requireContext(), Estilo.fondo(categoria)));
            int tinta = Estilo.color(requireContext(), Estilo.tinta(categoria));
            TextView nombre = tarjeta.findViewById(R.id.nombre);
            nombre.setText(categoria.getNombre());
            nombre.setTextColor(tinta);
            TextView cantidad = tarjeta.findViewById(R.id.cantidad);
            int n = estado.catalogo.contarPorCategoria(categoria);
            cantidad.setText(n == 1 ? "1 lugar" : n + " lugares");
            cantidad.setTextColor(tinta);
            ImageView icono = tarjeta.findViewById(R.id.icono);
            icono.setImageResource(Estilo.icono(categoria));
            icono.setImageTintList(Estilo.lista(requireContext(), Estilo.tinta(categoria)));
            tarjeta.setOnClickListener(v -> {
                MainActivity actividad = (MainActivity) requireActivity();
                actividad.explorarCategoria(categoria);
            });
            grilla.addView(tarjeta);
        }
    }

    @Override
    void refrescar() {
        Estado estado = Estado.get();
        ((TextView) vista.findViewById(R.id.saludo)).setText(Formato.saludo(estado.usuario.getNombre()));
        Estilo.avatar(vista.findViewById(R.id.avatar), estado.usuario);

        // Parches abiertos con cupo, los más próximos primero
        ListaEnlazada<Parche> abiertos = new ListaEnlazada<>();
        for (Parche parche : estado.gestor.activos(Estado.ahora())) {
            if (parche.cuposLibres() > 0 && !parche.esParticipante(estado.usuario)) {
                abiertos.agregarAlFinal(parche);
            }
        }
        GestorParches.ordenarPorFecha(abiertos);
        ListaEnlazada<Parche> primeros = new ListaEnlazada<>();
        for (Parche parche : abiertos) {
            if (primeros.tamano() == 8) {
                break;
            }
            primeros.agregarAlFinal(parche);
        }
        parches.mostrar(primeros);

        // Lugares románticos, los más cercanos primero
        ListaEnlazada<Lugar> romanticos = new ListaEnlazada<>();
        for (Lugar lugar : estado.catalogo.todos()) {
            if (lugar.tiene(Etiqueta.ROMANTICO)) {
                romanticos.agregarAlFinal(lugar);
            }
        }
        CatalogoLugares.ordenarPorCercania(romanticos, estado.ubicacion);
        citas.mostrar(romanticos);

        // Los cuatro más cercanos
        ListaEnlazada<Lugar> cercanos = estado.catalogo.todos();
        CatalogoLugares.ordenarPorCercania(cercanos, estado.ubicacion);
        LinearLayout cerca = vista.findViewById(R.id.cerca);
        cerca.removeAllViews();
        LugarAdapter fila = new LugarAdapter(false);
        int mostrados = 0;
        ListaEnlazada<Lugar> cuatro = new ListaEnlazada<>();
        for (Lugar lugar : cercanos) {
            if (mostrados++ == 4) {
                break;
            }
            cuatro.agregarAlFinal(lugar);
        }
        fila.mostrar(cuatro);
        for (int i = 0; i < fila.getItemCount(); i++) {
            LugarAdapter.Fila f = fila.onCreateViewHolder(cerca, 0);
            fila.onBindViewHolder(f, i);
            cerca.addView(f.itemView);
        }
        ((TextView) vista.findViewById(R.id.seccionCerca).findViewById(R.id.subtitulo)).setText(
                "Desde " + estado.ubicacion.getNombre());
        vista.findViewById(R.id.seccionCerca).findViewById(R.id.subtitulo).setVisibility(View.VISIBLE);
    }
}
