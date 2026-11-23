package com.parchapp.android;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parchapp.dominio.Categoria;
import com.parchapp.dominio.Lugar;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;

public class ExplorarFragment extends Pestana {

    private static final int POR_CERCANIA = 0;
    private static final int POR_NOMBRE = 1;
    private static final int POR_CATEGORIA = 2;
    private static final String[] NOMBRES_ORDEN = {"Más cerca", "Nombre", "Categoría"};

    private final LugarAdapter adaptador = new LugarAdapter(false);
    private ChipGroup grupoCategorias;
    private Chip[] chips;
    private String texto = "";
    private Categoria categoria = null;
    private int orden = POR_CERCANIA;

    private Chip ubicacion;
    private TextView resultados;
    private TextView vacio;
    private MaterialButton botonOrden;
    private RecyclerView lista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor, @Nullable Bundle estado) {
        View vista = inflador.inflate(R.layout.fragment_explorar, contenedor, false);
        ubicacion = vista.findViewById(R.id.ubicacion);
        resultados = vista.findViewById(R.id.resultados);
        vacio = vista.findViewById(R.id.vacio);
        botonOrden = vista.findViewById(R.id.orden);

        lista = vista.findViewById(R.id.lista);
        lista.setLayoutManager(new LinearLayoutManager(requireContext()));
        lista.setAdapter(adaptador);

        ubicacion.setOnClickListener(v -> Ui.elegirUbicacion(requireContext(), this::refrescar));
        ((TextView) vista.findViewById(R.id.busqueda)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int inicio, int cuenta, int despues) {
            }

            @Override
            public void onTextChanged(CharSequence s, int inicio, int antes, int cuenta) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                texto = s.toString();
                refrescar();
            }
        });
        botonOrden.setOnClickListener(this::elegirOrden);
        crearFiltros(inflador, vista.findViewById(R.id.categorias));
        return vista;
    }

    private void crearFiltros(LayoutInflater inflador, ChipGroup grupo) {
        grupoCategorias = grupo;
        Categoria[] categorias = Categoria.values();
        chips = new Chip[categorias.length + 1];
        chips[0] = agregarFiltro(inflador, grupo, "Todo", null);
        chips[0].setChecked(categoria == null);
        for (int i = 0; i < categorias.length; i++) {
            chips[i + 1] = agregarFiltro(inflador, grupo, categorias[i].getNombre(), categorias[i]);
            chips[i + 1].setChecked(categoria == categorias[i]);
        }
    }

    // Desde Inicio: abrir Explorar ya filtrado por una categoría
    void filtrarPor(Categoria filtro) {
        categoria = filtro;
        if (chips != null) {
            Chip chip = chips[filtro == null ? 0 : filtro.ordinal() + 1];
            chip.setChecked(true);
            ((android.widget.HorizontalScrollView) grupoCategorias.getParent()).post(() ->
                    ((android.widget.HorizontalScrollView) grupoCategorias.getParent()).smoothScrollTo(
                            Math.max(0, chip.getLeft() - 40), 0));
        }
        if (getView() != null) {
            refrescar();
        }
    }

    private Chip agregarFiltro(LayoutInflater inflador, ChipGroup grupo, String texto, Categoria filtro) {
        Chip chip = (Chip) inflador.inflate(R.layout.item_chip_filtro, grupo, false);
        chip.setId(View.generateViewId());
        chip.setText(texto);
        if (filtro != null) {
            chip.setChipIconResource(Estilo.icono(filtro));
            chip.setChipIconVisible(true);
        }
        chip.setOnClickListener(v -> {
            categoria = filtro;
            refrescar();
            lista.scheduleLayoutAnimation();
        });
        grupo.addView(chip);
        return chip;
    }

    private void elegirOrden(View ancla) {
        PopupMenu menu = new PopupMenu(requireContext(), ancla);
        for (int i = 0; i < NOMBRES_ORDEN.length; i++) {
            menu.getMenu().add(0, i, i, NOMBRES_ORDEN[i]);
        }
        menu.setOnMenuItemClickListener(item -> {
            orden = item.getItemId();
            refrescar();
            lista.scheduleLayoutAnimation();
            return true;
        });
        menu.show();
    }

    @Override
    void refrescar() {
        Estado estado = Estado.get();
        ubicacion.setText("Cerca de " + estado.ubicacion.getNombre());
        botonOrden.setText(NOMBRES_ORDEN[orden]);

        ListaEnlazada<Lugar> encontrados = estado.catalogo.buscar(texto, categoria);
        if (orden == POR_CERCANIA) {
            CatalogoLugares.ordenarPorCercania(encontrados, estado.ubicacion);
        } else if (orden == POR_NOMBRE) {
            CatalogoLugares.ordenarPorNombre(encontrados);
        } else {
            CatalogoLugares.ordenarPorCategoria(encontrados);
        }
        adaptador.mostrar(encontrados);
        int n = encontrados.tamano();
        resultados.setText(n == 1 ? "1 lugar" : n + " lugares");

        vacio.setVisibility(n == 0 ? View.VISIBLE : View.GONE);
    }
}
