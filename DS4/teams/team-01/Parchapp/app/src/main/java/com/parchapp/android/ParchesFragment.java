package com.parchapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.GestorParches;

public class ParchesFragment extends Pestana {

    private final ParcheAdapter adaptador = new ParcheAdapter(false, true);
    private boolean soloMios = false;
    private TextView vacio;
    private RecyclerView lista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor, @Nullable Bundle estado) {
        View vista = inflador.inflate(R.layout.fragment_parches, contenedor, false);
        vacio = vista.findViewById(R.id.vacio);
        lista = vista.findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista.setAdapter(adaptador);

        // El botón flotante se encoge a solo el ícono mientras se baja por la lista
        ExtendedFloatingActionButton crear = vista.findViewById(R.id.crear);
        lista.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                if (dy > 8 && crear.isExtended()) {
                    crear.shrink();
                } else if (dy < -8 && !crear.isExtended()) {
                    crear.extend();
                }
            }
        });

        MaterialButtonToggleGroup filtro = vista.findViewById(R.id.filtro);
        filtro.check(R.id.botonProximos);
        filtro.addOnButtonCheckedListener((grupo, id, marcado) -> {
            if (marcado) {
                soloMios = id == R.id.botonMios;
                refrescar();
                lista.scheduleLayoutAnimation();
            }
        });
        crear.setOnClickListener(v -> startActivity(new Intent(requireContext(), CrearParcheActivity.class)));
        return vista;
    }

    @Override
    void refrescar() {
        Estado estado = Estado.get();
        ListaEnlazada<Parche> parches = soloMios
                ? estado.gestor.delUsuario(estado.usuario, Estado.ahora())
                : estado.gestor.activos(Estado.ahora());
        GestorParches.ordenarPorFecha(parches);
        adaptador.mostrar(parches);
        vacio.setText(soloMios
                ? "Todavía no estás en ningún parche.\nÚnete a uno o arma el tuyo."
                : "No hay parches por ahora.\n¡Arma el primero!");
        vacio.setVisibility(parches.estaVacia() ? View.VISIBLE : View.GONE);
    }
}
