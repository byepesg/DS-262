package com.parchapp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.GestorParches;

public class LugarActivity extends AppCompatActivity {

    static final String EXTRA_LUGAR = "lugar";

    private Lugar lugar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Estado estado = Estado.get();
        lugar = estado.catalogo.buscarPorId(getIntent().getStringExtra(EXTRA_LUGAR));
        if (lugar == null) {
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lugar);

        // la cabecera va hasta arriba, detrás de la barra de estado
        View atras = findViewById(R.id.atras);
        View cabecera = findViewById(R.id.cabecera);
        View desplazable = findViewById(R.id.desplazable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.raiz), (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            atras.setTranslationY(barras.top);
            cabecera.setPadding(0, barras.top, 0, 0);
            desplazable.setPadding(0, 0, 0, barras.bottom);
            return insets;
        });
        atras.setOnClickListener(v -> finish());

        // Solo cuenta como vista cuando se abre la pantalla, no cada vez que se vuelve a ella
        if (savedInstanceState == null) {
            estado.historial.registrarVista(lugar);
        }

        Estilo.pintarCategoria(cabecera, (ImageView) findViewById(R.id.icono), lugar.getCategoria());
        Estilo.pastillaCategoria(findViewById(R.id.categoria), lugar.getCategoria());
        ((TextView) findViewById(R.id.nombre)).setText(lugar.getNombre());
        TextView descripcion = findViewById(R.id.descripcion);
        descripcion.setText(lugar.getDescripcion());
        descripcion.setVisibility(lugar.getDescripcion() == null ? View.GONE : View.VISIBLE);
        TextView direccion = findViewById(R.id.direccion);
        direccion.setText(lugar.getDireccion() != null ? lugar.getDireccion()
                : lugar.getZona() != null ? lugar.getZona() + ", Bogotá" : "Bogotá");

        ChipGroup etiquetas = findViewById(R.id.etiquetas);
        for (Etiqueta etiqueta : Etiqueta.values()) {
            if (lugar.tiene(etiqueta) && etiqueta != Etiqueta.IDEAL_SOLO) {
                Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_chip, etiquetas, false);
                chip.setText(etiqueta.getDescripcion());
                chip.setClickable(false);
                etiquetas.addView(chip);
            }
        }

        findViewById(R.id.comoLlegar).setOnClickListener(v -> Ui.abrirComoLlegar(this, lugar));
        findViewById(R.id.crearAqui).setOnClickListener(v -> startActivity(
                new Intent(this, CrearParcheActivity.class).putExtra(EXTRA_LUGAR, lugar.getId())));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lugar != null) {
            refrescar();
        }
    }

    private void refrescar() {
        Estado estado = Estado.get();
        ((TextView) findViewById(R.id.meta)).setText(Formato.metaLugar(lugar, estado.ubicacion.distanciaKm(lugar))
                + " de " + estado.ubicacion.getNombre());

        ListaEnlazada<Parche> aqui = estado.gestor.activosEn(lugar, Estado.ahora());
        GestorParches.ordenarPorFecha(aqui);
        LinearLayout contenedor = findViewById(R.id.parches);
        contenedor.removeAllViews();
        for (Parche parche : aqui) {
            View tarjeta = getLayoutInflater().inflate(R.layout.item_parche, contenedor, false);
            ParcheAdapter.Fila fila = new ParcheAdapter.Fila(tarjeta);
            fila.llenar(parche);
            fila.conFecha();
            contenedor.addView(tarjeta);
        }
        findViewById(R.id.sinParches).setVisibility(aqui.estaVacia() ? View.VISIBLE : View.GONE);
    }
}
