package com.parchapp.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;

public class PerfilFragment extends Pestana {

    private View vista;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflador, @Nullable ViewGroup contenedor, @Nullable Bundle estado) {
        vista = inflador.inflate(R.layout.fragment_perfil, contenedor, false);
        vista.findViewById(R.id.cambiarUsuario).setOnClickListener(v ->
                Ui.elegirUsuario(requireContext(), this::refrescar));
        vista.findViewById(R.id.tarjetaUbicacion).setOnClickListener(v ->
                Ui.elegirUbicacion(requireContext(), this::refrescar));
        ((TextView) vista.findViewById(R.id.acercaDe)).setText("Parchapp " + BuildConfig.VERSION_NAME);
        return vista;
    }

    @Override
    void refrescar() {
        Estado estado = Estado.get();
        Estilo.avatar(vista.findViewById(R.id.avatar), estado.usuario);
        ((TextView) vista.findViewById(R.id.nombre)).setText(estado.usuario.getNombre());
        ((TextView) vista.findViewById(R.id.desde)).setText("Bogotá · " + estado.ubicacion.getNombre());
        ((TextView) vista.findViewById(R.id.ubicacion)).setText(estado.ubicacion.getNombre());

        int organizas = 0;
        int vas = 0;
        int enEspera = 0;
        for (Parche parche : estado.gestor.delUsuario(estado.usuario, Estado.ahora())) {
            if (!parche.estaActivo()) {
                continue;
            }
            if (parche.esCreador(estado.usuario)) {
                organizas++;
            } else if (parche.esParticipante(estado.usuario)) {
                vas++;
            } else {
                enEspera++;
            }
        }
        estadistica(R.id.statOrganizas, organizas, "Organizas");
        estadistica(R.id.statVas, vas, "Vas a ir");
        estadistica(R.id.statEspera, enEspera, "En fila");

        ChipGroup vistos = vista.findViewById(R.id.vistos);
        vistos.removeAllViews();
        for (Lugar lugar : estado.historial.recientes()) {
            Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_chip, vistos, false);
            chip.setText(lugar.getNombre());
            chip.setChipIconResource(Estilo.icono(lugar.getCategoria()));
            chip.setChipIconTint(Estilo.lista(requireContext(), Estilo.tinta(lugar.getCategoria())));
            chip.setChipIconVisible(true);
            chip.setOnClickListener(v -> Ui.abrirLugar(requireContext(), lugar));
            vistos.addView(chip);
        }
        vista.findViewById(R.id.sinVistos).setVisibility(estado.historial.tamano() == 0 ? View.VISIBLE : View.GONE);
    }

    private void estadistica(int id, int valor, String etiqueta) {
        View tarjeta = vista.findViewById(id);
        ((TextView) tarjeta.findViewById(R.id.valor)).setText(String.valueOf(valor));
        ((TextView) tarjeta.findViewById(R.id.etiqueta)).setText(etiqueta);
    }
}
