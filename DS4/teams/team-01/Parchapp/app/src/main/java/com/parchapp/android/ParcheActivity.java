package com.parchapp.android;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.parchapp.dominio.Parche;
import com.parchapp.dominio.ResultadoUnion;
import com.parchapp.dominio.Usuario;

public class ParcheActivity extends AppCompatActivity {

    static final String EXTRA_PARCHE = "parche";

    private Parche parche;
    private MaterialButton accion;
    private ColorStateList fondoNormal;
    private ColorStateList textoNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parche = Estado.get().gestor.buscarPorId(getIntent().getStringExtra(EXTRA_PARCHE));
        if (parche == null) {
            finish();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parche);
        Ui.respetarBarras(findViewById(R.id.raiz), true, true);
        Ui.barraConAtras(this);
        accion = findViewById(R.id.accion);
        fondoNormal = accion.getBackgroundTintList();
        textoNormal = accion.getTextColors();
        findViewById(R.id.tarjetaLugar).setOnClickListener(v -> Ui.abrirLugar(this, parche.getLugar()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (parche != null) {
            refrescar();
        }
    }

    private void refrescar() {
        Estado estado = Estado.get();
        parche.finalizarSiYaPaso(Estado.ahora());

        Estilo.pastillaEstado(findViewById(R.id.estado), parche.getEstado());
        ((TextView) findViewById(R.id.titulo)).setText(parche.getTitulo());
        ((TextView) findViewById(R.id.cuando)).setText(Formato.fechaLarga(parche.getFechaHora()));
        ((TextView) findViewById(R.id.organiza)).setText("Organiza " + parche.getCreador().getNombre());

        Estilo.pintarCategoria(findViewById(R.id.fondoIcono), findViewById(R.id.icono), parche.getLugar().getCategoria());
        ((TextView) findViewById(R.id.lugar)).setText(parche.getLugar().getNombre());
        ((TextView) findViewById(R.id.distancia)).setText(Formato.metaLugar(parche.getLugar(),
                estado.ubicacion.distanciaKm(parche.getLugar())));

        ((TextView) findViewById(R.id.ocupacion)).setText(parche.cantidadParticipantes() + " de "
                + parche.getCapacidad() + " personas");
        LinearProgressIndicator cupos = findViewById(R.id.cupos);
        cupos.setMax(parche.getCapacidad());
        cupos.setProgressCompat(parche.cantidadParticipantes(), true);
        int libres = parche.cuposLibres();
        ((TextView) findViewById(R.id.libres)).setText(libres == 0 ? "Está lleno: si te unes, quedas en la fila"
                : libres == 1 ? "Queda 1 cupo" : "Quedan " + libres + " cupos");

        LinearLayout participantes = findViewById(R.id.participantes);
        participantes.removeAllViews();
        for (Usuario usuario : parche.getParticipantes()) {
            View persona = getLayoutInflater().inflate(R.layout.item_persona, participantes, false);
            Estilo.avatar(persona.findViewById(R.id.avatar), usuario);
            ((TextView) persona.findViewById(R.id.nombre)).setText(usuario.equals(estado.usuario)
                    ? "Tú" : usuario.getNombre().split(" ")[0]);
            ((TextView) persona.findViewById(R.id.nota)).setText(parche.esCreador(usuario) ? "Organiza" : "");
            participantes.addView(persona);
        }

        LinearLayout espera = findViewById(R.id.espera);
        espera.removeAllViews();
        int puesto = 1;
        for (Usuario usuario : parche.getListaEspera()) {
            View fila = getLayoutInflater().inflate(R.layout.item_espera, espera, false);
            ((TextView) fila.findViewById(R.id.numero)).setText(String.valueOf(puesto));
            ((TextView) fila.findViewById(R.id.nombre)).setText(usuario.getNombre()
                    + (usuario.equals(estado.usuario) ? " (tú)" : ""));
            espera.addView(fila);
            puesto++;
        }
        findViewById(R.id.sinEspera).setVisibility(parche.cantidadEnEspera() == 0 ? View.VISIBLE : View.GONE);

        configurarAccion(estado.usuario);
    }

    // El botón de abajo depende de qué puede hacer el usuario. Las reglas están en Parche.
    private void configurarAccion(Usuario yo) {
        TextView miEstado = findViewById(R.id.miEstado);
        miEstado.setVisibility(View.GONE);
        accion.setEnabled(true);
        estiloAccion(false);

        if (!parche.estaActivo()) {
            accion.setText("Este parche está " + parche.getEstado().getNombre().toLowerCase(Formato.ES));
            accion.setEnabled(false);
            accion.setOnClickListener(null);
        } else if (parche.esCreador(yo)) {
            miEstado.setText("Tú organizas este parche");
            miEstado.setVisibility(View.VISIBLE);
            accion.setText("Cancelar parche");
            estiloAccion(true);
            accion.setOnClickListener(v -> confirmar("¿Cancelar el parche?",
                    "Nadie más podrá unirse y quedará como cancelado.", "Cancelar parche", () -> {
                        parche.cancelar(yo);
                        avisar("Parche cancelado");
                    }));
        } else if (parche.esParticipante(yo)) {
            miEstado.setText("Estás dentro de este parche");
            miEstado.setVisibility(View.VISIBLE);
            accion.setText("Salir del parche");
            estiloAccion(true);
            accion.setOnClickListener(v -> confirmar("¿Salir del parche?",
                    "Si hay alguien en la fila, tomará tu cupo.", "Salir", () -> {
                        Usuario entro = parche.salir(yo);
                        avisar(entro == null ? "Saliste del parche"
                                : "Saliste. " + entro.getNombre() + " tomó tu cupo");
                    }));
        } else if (parche.posicionEnEspera(yo) > 0) {
            accion.setText("Estás en la fila · " + parche.posicionEnEspera(yo) + "º");
            accion.setEnabled(false);
            accion.setOnClickListener(null);
        } else {
            accion.setText(parche.cuposLibres() > 0 ? "Me uno" : "Hacer fila");
            accion.setOnClickListener(v -> {
                ResultadoUnion resultado = parche.unirse(yo);
                avisar(resultado == ResultadoUnion.UNIDO ? "¡Listo! Ya estás en el parche"
                        : resultado == ResultadoUnion.EN_LISTA_DE_ESPERA
                        ? "Está lleno: quedaste de " + parche.posicionEnEspera(yo) + "º en la fila"
                        : "No fue posible unirse");
            });
        }
    }

    // salir y cancelar van en rojo
    private void estiloAccion(boolean peligrosa) {
        if (peligrosa) {
            accion.setBackgroundTintList(ColorStateList.valueOf(MaterialColors.getColor(accion,
                    com.google.android.material.R.attr.colorErrorContainer)));
            accion.setTextColor(MaterialColors.getColor(accion,
                    com.google.android.material.R.attr.colorOnErrorContainer));
        } else {
            accion.setBackgroundTintList(fondoNormal);
            accion.setTextColor(textoNormal);
        }
    }

    private void confirmar(String titulo, String mensaje, String si, Runnable alConfirmar) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(si, (d, w) -> alConfirmar.run())
                .setNegativeButton("Volver", null)
                .show();
    }

    private void avisar(String mensaje) {
        refrescar();
        Snackbar.make(findViewById(R.id.raiz), mensaje, Snackbar.LENGTH_LONG).setAnchorView(R.id.barraAccion).show();
    }
}
