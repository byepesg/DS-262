package com.parchapp.android;

import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Parche;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class CrearParcheActivity extends AppCompatActivity {

    private Lugar lugar;
    private LocalDate fecha = LocalDate.now().plusDays(1);
    private LocalTime hora = LocalTime.of(16, 0);

    private TextInputLayout capaLugar;
    private TextInputLayout capaTitulo;
    private TextInputLayout capaFecha;
    private TextView campoLugar;
    private TextView campoTitulo;
    private TextView campoFecha;
    private TextView campoHora;
    private Slider capacidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_parche);
        Ui.respetarBarras(findViewById(R.id.raiz), true, true);
        Ui.barraConAtras(this);

        capaLugar = findViewById(R.id.capaLugar);
        capaTitulo = findViewById(R.id.capaTitulo);
        capaFecha = findViewById(R.id.capaFecha);
        campoLugar = findViewById(R.id.campoLugar);
        campoTitulo = findViewById(R.id.campoTitulo);
        campoFecha = findViewById(R.id.campoFecha);
        campoHora = findViewById(R.id.campoHora);
        capacidad = findViewById(R.id.capacidad);

        // Si se abre desde un lugar, ya viene escogido
        String idLugar = getIntent().getStringExtra(LugarActivity.EXTRA_LUGAR);
        if (idLugar != null) {
            lugar = Estado.get().catalogo.buscarPorId(idLugar);
        }

        campoLugar.setOnClickListener(v -> elegirLugar());
        campoFecha.setOnClickListener(v -> elegirFecha());
        campoHora.setOnClickListener(v -> elegirHora());
        capacidad.addOnChangeListener((s, valor, delUsuario) -> mostrarCapacidad());
        findViewById(R.id.crear).setOnClickListener(v -> crear());
        mostrarCampos();
        mostrarCapacidad();
    }

    private void mostrarCampos() {
        campoLugar.setText(lugar == null ? "" : lugar.getNombre());
        campoFecha.setText(Formato.dia(fecha));
        campoHora.setText(Formato.hora(fecha.atTime(hora)));
    }

    private void mostrarCapacidad() {
        ((TextView) findViewById(R.id.etiquetaCapacidad)).setText("Capacidad: " + (int) capacidad.getValue() + " personas");
    }

    private void elegirLugar() {
        ListaEnlazada<Lugar> lugares = Estado.get().catalogo.todos();
        CatalogoLugares.ordenarPorNombre(lugares);
        Lugar[] opciones = new Lugar[lugares.tamano()];
        CharSequence[] nombres = new CharSequence[lugares.tamano()];
        int i = 0;
        for (Lugar l : lugares) {
            opciones[i] = l;
            nombres[i] = l.getNombre();
            i++;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle("¿Dónde es el parche?")
                .setItems(nombres, (d, elegido) -> {
                    lugar = opciones[elegido];
                    capaLugar.setError(null);
                    mostrarCampos();
                })
                .show();
    }

    private void elegirFecha() {
        // El selector de Material trabaja con milisegundos en UTC
        long seleccion = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        MaterialDatePicker<Long> selector = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Día del parche")
                .setSelection(seleccion)
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now()).build())
                .build();
        selector.addOnPositiveButtonClickListener(milis -> {
            fecha = Instant.ofEpochMilli(milis).atZone(ZoneOffset.UTC).toLocalDate();
            capaFecha.setError(null);
            mostrarCampos();
        });
        selector.show(getSupportFragmentManager(), "fecha");
    }

    private void elegirHora() {
        MaterialTimePicker selector = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hora.getHour())
                .setMinute(hora.getMinute())
                .setTitleText("Hora del parche")
                .build();
        selector.addOnPositiveButtonClickListener(v -> {
            hora = LocalTime.of(selector.getHour(), selector.getMinute());
            capaFecha.setError(null);
            mostrarCampos();
        });
        selector.show(getSupportFragmentManager(), "hora");
    }

    // Validación básica para mostrar mensajes claros; las reglas de verdad están en core
    private void crear() {
        boolean listo = true;
        if (lugar == null) {
            capaLugar.setError("Escoge un lugar");
            listo = false;
        }
        String titulo = campoTitulo.getText().toString().trim();
        if (titulo.isEmpty()) {
            capaTitulo.setError("Escribe qué van a hacer");
            listo = false;
        } else {
            capaTitulo.setError(null);
        }
        if (!listo) {
            return;
        }
        Estado estado = Estado.get();
        try {
            Parche parche = estado.gestor.crear(estado.usuario, lugar, titulo, fecha.atTime(hora),
                    (int) capacidad.getValue(), Estado.ahora());
            Ui.abrirParche(this, parche.getId());
            finish();
        } catch (IllegalArgumentException e) {
            capaFecha.setError(e.getMessage());
        }
    }
}
