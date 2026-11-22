package com.parchapp.android;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.parchapp.datos.DatosDemo;
import com.parchapp.dominio.Lugar;
import com.parchapp.dominio.Usuario;
import com.parchapp.estructuras.ListaEnlazada;
import com.parchapp.servicio.CatalogoLugares;
import com.parchapp.servicio.GestorParches;
import com.parchapp.servicio.NavegacionMaps;
import java.time.LocalDateTime;

// Prueba de concepto (etapa 1): comprueba que el módulo core corre dentro de Android
// y que el enlace "Cómo llegar" abre Google Maps. Las pantallas reales vienen después.
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        respetarBarrasDelSistema(findViewById(R.id.raiz));

        CatalogoLugares catalogo = new CatalogoLugares(DatosDemo.lugares());
        ListaEnlazada<Usuario> usuarios = DatosDemo.usuarios();
        GestorParches gestor = new GestorParches();
        DatosDemo.crearParches(gestor, catalogo, usuarios, LocalDateTime.now());

        boolean estructurasOk = catalogo.invariantesValidos() && gestor.invariantesValidos();
        String resumen = catalogo.cantidad() + " lugares y "
                + gestor.activos(LocalDateTime.now()).tamano() + " parches activos (datos de demostración).\n"
                + "Invariantes de las estructuras: " + (estructurasOk ? "OK" : "FALLA");
        ((TextView) findViewById(R.id.resumen)).setText(resumen);

        ListaEnlazada<Lugar> lugares = catalogo.todos();
        CatalogoLugares.ordenarPorNombre(lugares);
        StringBuilder texto = new StringBuilder();
        for (Lugar lugar : lugares) {
            texto.append("• ").append(lugar.getNombre())
                    .append(" (").append(lugar.getCategoria().getNombre()).append(")\n");
        }
        ((TextView) findViewById(R.id.lugares)).setText(texto);

        Lugar primero = lugares.obtenerPrimero();
        findViewById(R.id.botonComoLlegar).setOnClickListener(v -> abrirComoLlegar(primero));
    }

    private void abrirComoLlegar(Lugar lugar) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(NavegacionMaps.urlComoLlegar(lugar)));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.sin_app_mapas, Toast.LENGTH_LONG).show();
        }
    }

    // Desde Android 15 las apps dibujan detrás de las barras del sistema: se agrega ese espacio como margen
    private static void respetarBarrasDelSistema(View vista) {
        int izquierda = vista.getPaddingLeft();
        int arriba = vista.getPaddingTop();
        int derecha = vista.getPaddingRight();
        int abajo = vista.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(vista, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(izquierda + barras.left, arriba + barras.top, derecha + barras.right, abajo + barras.bottom);
            return insets;
        });
    }
}
