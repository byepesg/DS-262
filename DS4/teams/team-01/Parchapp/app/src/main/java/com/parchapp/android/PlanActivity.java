package com.parchapp.android;

import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.slider.Slider;
import com.parchapp.dominio.Compania;
import com.parchapp.dominio.Etiqueta;
import com.parchapp.servicio.Preferencias;
import com.parchapp.servicio.Recomendacion;
import com.parchapp.servicio.Recomendador;
import com.parchapp.estructuras.ListaEnlazada;

// "¿Qué hacemos hoy?": unas preguntas y al final los lugares que mejor encajan.
// Las preguntas dependen de con quién vas (pareja -> tipo de cita, amigos -> cuántos son, etc.)
public class PlanActivity extends AppCompatActivity {

    static final String EXTRA_COMPANIA = "compania";

    private enum Paso { COMPANIA, PERSONAS, PLAN, TRAGOS, COMIDA, PRESUPUESTO, RESULTADO }

    // Una respuesta posible a una pregunta
    private static final class Opcion {
        final String titulo;
        final String subtitulo;
        final int icono;
        final int fondo;
        final int tinta;
        final Etiqueta[] buscar;

        Opcion(String titulo, String subtitulo, int icono, int fondo, int tinta, Etiqueta... buscar) {
            this.titulo = titulo;
            this.subtitulo = subtitulo;
            this.icono = icono;
            this.fondo = fondo;
            this.tinta = tinta;
            this.buscar = buscar;
        }
    }

    // Respuestas
    private Compania compania;
    private int personas = 4;
    private Opcion plan;
    private int tragos = -1;
    private Opcion comida;
    private int presupuesto = 2;
    private final int[] elegida = new int[Paso.values().length];

    private Paso[] secuencia;
    private int actual;

    private LinearLayout contenido;
    private TextView textoPaso;
    private TextView pregunta;
    private TextView ayuda;
    private LinearLayout opciones;
    private LinearProgressIndicator progreso;
    private NestedScrollView desplazable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plan);
        Ui.respetarBarras(findViewById(R.id.raiz), true, true);

        contenido = findViewById(R.id.contenido);
        textoPaso = findViewById(R.id.paso);
        pregunta = findViewById(R.id.pregunta);
        ayuda = findViewById(R.id.ayuda);
        opciones = findViewById(R.id.opciones);
        progreso = findViewById(R.id.progreso);
        desplazable = findViewById(R.id.desplazable);
        for (int i = 0; i < elegida.length; i++) {
            elegida[i] = -1;
        }

        findViewById(R.id.atras).setOnClickListener(v -> retroceder());
        findViewById(R.id.cerrar).setOnClickListener(v -> finish());
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                retroceder();
            }
        });

        // Desde un atajo de Inicio ("Cita", "Con amigos"...) la primera pregunta ya está respondida
        String preelegida = getIntent().getStringExtra(EXTRA_COMPANIA);
        if (preelegida != null) {
            compania = Compania.valueOf(preelegida);
            elegida[Paso.COMPANIA.ordinal()] = compania.ordinal();
            armarSecuencia();
            actual = 1;
        } else {
            secuencia = new Paso[] {Paso.COMPANIA};
            actual = 0;
        }
        mostrar();
    }

    private void armarSecuencia() {
        switch (compania) {
            case SOLO:
                secuencia = new Paso[] {Paso.COMPANIA, Paso.PLAN, Paso.COMIDA, Paso.PRESUPUESTO, Paso.RESULTADO};
                break;
            case PAREJA:
                secuencia = new Paso[] {Paso.COMPANIA, Paso.PLAN, Paso.TRAGOS, Paso.COMIDA, Paso.PRESUPUESTO, Paso.RESULTADO};
                break;
            case AMIGOS:
                secuencia = new Paso[] {Paso.COMPANIA, Paso.PERSONAS, Paso.PLAN, Paso.TRAGOS, Paso.COMIDA,
                    Paso.PRESUPUESTO, Paso.RESULTADO};
                break;
            default:
                secuencia = new Paso[] {Paso.COMPANIA, Paso.PERSONAS, Paso.PLAN, Paso.COMIDA, Paso.PRESUPUESTO, Paso.RESULTADO};
        }
    }

    private void avanzar() {
        if (actual < secuencia.length - 1) {
            actual++;
            transicion(true);
        }
    }

    private void retroceder() {
        if (actual == 0) {
            finish();
            return;
        }
        actual--;
        transicion(false);
    }

    // La pregunta actual se va hacia un lado y la nueva entra desde el otro
    private void transicion(boolean adelante) {
        float distancia = 48 * getResources().getDisplayMetrics().density;
        contenido.animate().cancel();
        contenido.animate()
                .alpha(0f)
                .translationX(adelante ? -distancia : distancia)
                .setDuration(130)
                .withEndAction(() -> {
                    mostrar();
                    desplazable.scrollTo(0, 0);
                    contenido.setTranslationX(adelante ? distancia : -distancia);
                    contenido.animate().alpha(1f).translationX(0f).setDuration(260)
                            .setInterpolator(new DecelerateInterpolator(2f)).start();
                })
                .start();
    }

    private void mostrar() {
        Paso paso = secuencia[actual];
        opciones.removeAllViews();
        int preguntas = secuencia.length > 1 ? secuencia.length - 1 : 5;
        progreso.setProgressCompat(paso == Paso.RESULTADO ? 100 : (int) (100f * (actual + 0.5f) / preguntas), true);
        textoPaso.setVisibility(paso == Paso.RESULTADO ? View.GONE : View.VISIBLE);
        textoPaso.setText("Pregunta " + (actual + 1) + (secuencia.length > 1 ? " de " + preguntas : ""));

        switch (paso) {
            case COMPANIA:
                preguntar("¿Con quién vas a salir?", "Así sabemos qué tipo de plan buscar.");
                opcion(Paso.COMPANIA, 0, new Opcion("Solo", "Un rato para mí", R.drawable.ic_solo,
                        R.color.cat_mirador_fondo, R.color.cat_mirador), () -> elegirCompania(Compania.SOLO));
                opcion(Paso.COMPANIA, 1, new Opcion("En pareja", "Una cita o un plan de dos", R.drawable.ic_pareja,
                        R.color.cat_deporte_fondo, R.color.cat_deporte), () -> elegirCompania(Compania.PAREJA));
                opcion(Paso.COMPANIA, 2, new Opcion("Con amigos", "Armar parche con el combo", R.drawable.ic_amigos,
                        R.color.cat_comida_fondo, R.color.cat_comida), () -> elegirCompania(Compania.AMIGOS));
                opcion(Paso.COMPANIA, 3, new Opcion("En familia", "Algo para todas las edades", R.drawable.ic_familia,
                        R.color.cat_parque_fondo, R.color.cat_parque), () -> elegirCompania(Compania.FAMILIA));
                break;
            case PERSONAS:
                preguntar("¿Cuántos van a ser?", "Contándote a ti.");
                mostrarPersonas();
                break;
            case PLAN:
                mostrarPlanes();
                break;
            case TRAGOS:
                preguntar(compania == Compania.PAREJA ? "¿Con unos tragos?" : "¿Va a haber trago?",
                        "Para saber si incluimos bares y rumba.");
                opcion(Paso.TRAGOS, 0, new Opcion("Sí, unos tragos", "Cerveza, cócteles o lo que salga",
                        R.drawable.ic_alcohol, R.color.cat_rumba_fondo, R.color.cat_rumba), () -> tragos = 0);
                opcion(Paso.TRAGOS, 1, new Opcion("Sin alcohol", "Plan sano", R.drawable.ic_sin_alcohol,
                        R.color.cat_aventura_fondo, R.color.cat_aventura), () -> tragos = 1);
                opcion(Paso.TRAGOS, 2, new Opcion("Da igual", "Lo que se dé", R.drawable.ic_da_igual,
                        R.color.cat_estudio_fondo, R.color.cat_estudio), () -> tragos = 2);
                break;
            case COMIDA:
                preguntar(compania == Compania.SOLO ? "¿Vas a comer algo?" : "¿Van a comer algo?", null);
                opcion(Paso.COMIDA, 0, new Opcion("Sí, con hambre", "Que el plan incluya comida",
                        R.drawable.ic_comida, R.color.cat_comida_fondo, R.color.cat_comida, Etiqueta.COMIDA),
                        null);
                opcion(Paso.COMIDA, 1, new Opcion("Algo ligero", "Un café, un postre", R.drawable.ic_ligero,
                        R.color.cat_cafe_fondo, R.color.cat_cafe, Etiqueta.TRANQUILO), null);
                opcion(Paso.COMIDA, 2, new Opcion("No, ya comimos", "Solo el plan", R.drawable.ic_check,
                        R.color.cat_estudio_fondo, R.color.cat_estudio), null);
                break;
            case PRESUPUESTO:
                preguntar("¿Cuánto quieren gastar?", "Por persona, más o menos.");
                opcion(Paso.PRESUPUESTO, 0, new Opcion("Poco o nada", "Gratis o barato · $", R.drawable.ic_economico,
                        R.color.cat_parque_fondo, R.color.cat_parque), () -> presupuesto = 1);
                opcion(Paso.PRESUPUESTO, 1, new Opcion("Normal", "Lo de siempre · $$", R.drawable.ic_normal,
                        R.color.cat_diversion_fondo, R.color.cat_diversion), () -> presupuesto = 2);
                opcion(Paso.PRESUPUESTO, 2, new Opcion("Darnos gusto", "Hoy no se mira el precio · $$$",
                        R.drawable.ic_lujo, R.color.cat_cultura_fondo, R.color.cat_cultura), () -> presupuesto = 3);
                break;
            default:
                mostrarResultado();
        }
        animarOpciones();
    }

    private void preguntar(String texto, String detalle) {
        pregunta.setText(texto);
        ayuda.setText(detalle);
        ayuda.setVisibility(detalle == null ? View.GONE : View.VISIBLE);
    }

    private void elegirCompania(Compania nueva) {
        compania = nueva;
        if (nueva != Compania.SOLO && nueva != Compania.PAREJA && personas < 2) {
            personas = 4;
        }
        // Las preguntas siguientes dependen de esta respuesta: se reinician
        plan = null;
        elegida[Paso.PLAN.ordinal()] = -1;
        elegida[Paso.TRAGOS.ordinal()] = -1;
        armarSecuencia();
    }

    private void mostrarPlanes() {
        switch (compania) {
            case PAREJA:
                preguntar("¿Qué tipo de cita?", "Escoge la que más se parezca a lo que tienes en mente.");
                planes(new Opcion("Romántica", "Buena comida y conversación sin afán", R.drawable.ic_romantico,
                                R.color.cat_deporte_fondo, R.color.cat_deporte, Etiqueta.ROMANTICO, Etiqueta.TRANQUILO),
                        new Opcion("Emocionante", "Adrenalina, risas y algo distinto", R.drawable.ic_emocionante,
                                R.color.cat_comida_fondo, R.color.cat_comida, Etiqueta.EMOCIONANTE, Etiqueta.ACTIVIDAD),
                        new Opcion("Con una buena vista", "Un mirador, un atardecer", R.drawable.ic_mirador,
                                R.color.cat_mirador_fondo, R.color.cat_mirador, Etiqueta.PAISAJE, Etiqueta.ROMANTICO),
                        new Opcion("Cultural", "Museo, teatro o cine", R.drawable.ic_arte,
                                R.color.cat_cultura_fondo, R.color.cat_cultura, Etiqueta.CULTURAL, Etiqueta.ROMANTICO),
                        new Opcion("Salir a bailar", "La noche es joven", R.drawable.ic_noche,
                                R.color.cat_rumba_fondo, R.color.cat_rumba, Etiqueta.NOCTURNO, Etiqueta.SOCIAL));
                break;
            case AMIGOS:
                preguntar("¿Qué les provoca?", "Para " + personas + " personas.");
                planes(new Opcion("Socializar", "Charla, música y conocer gente", R.drawable.ic_noche,
                                R.color.cat_rumba_fondo, R.color.cat_rumba, Etiqueta.SOCIAL, Etiqueta.NOCTURNO),
                        new Opcion("Hacer algo", "Deporte, juegos, moverse", R.drawable.ic_actividad,
                                R.color.cat_deporte_fondo, R.color.cat_deporte, Etiqueta.ACTIVIDAD, Etiqueta.GRUPOS),
                        new Opcion("Adrenalina", "Gritar en una montaña rusa", R.drawable.ic_rayo,
                                R.color.cat_diversion_fondo, R.color.cat_diversion, Etiqueta.EMOCIONANTE),
                        new Opcion("Aire libre", "Parque, caminata, picnic", R.drawable.ic_bosque,
                                R.color.cat_parque_fondo, R.color.cat_parque, Etiqueta.AIRE_LIBRE, Etiqueta.PAISAJE),
                        new Opcion("Cultura", "Museos, conciertos, el centro", R.drawable.ic_arte,
                                R.color.cat_cultura_fondo, R.color.cat_cultura, Etiqueta.CULTURAL));
                break;
            case FAMILIA:
                preguntar("¿Qué plan en familia?", "Para " + personas + " personas.");
                planes(new Opcion("Aire libre", "Parques y naturaleza", R.drawable.ic_bosque,
                                R.color.cat_parque_fondo, R.color.cat_parque, Etiqueta.AIRE_LIBRE, Etiqueta.FAMILIAR),
                        new Opcion("Aprender algo", "Museos y ciencia", R.drawable.ic_aprender,
                                R.color.cat_cultura_fondo, R.color.cat_cultura, Etiqueta.CULTURAL, Etiqueta.FAMILIAR),
                        new Opcion("Diversión", "Atracciones y juegos", R.drawable.ic_diversion,
                                R.color.cat_diversion_fondo, R.color.cat_diversion, Etiqueta.EMOCIONANTE, Etiqueta.ACTIVIDAD),
                        new Opcion("Paseo con vista", "Salir de la rutina", R.drawable.ic_mirador,
                                R.color.cat_mirador_fondo, R.color.cat_mirador, Etiqueta.PAISAJE, Etiqueta.FAMILIAR));
                break;
            default:
                preguntar("¿Qué te provoca?", "Un plan para ti.");
                planes(new Opcion("Desconectarme", "Silencio, verde, una buena vista", R.drawable.ic_desconectar,
                                R.color.cat_aventura_fondo, R.color.cat_aventura, Etiqueta.TRANQUILO, Etiqueta.PAISAJE),
                        new Opcion("Aprender algo", "Un museo, una exposición", R.drawable.ic_aprender,
                                R.color.cat_cultura_fondo, R.color.cat_cultura, Etiqueta.CULTURAL),
                        new Opcion("Moverme", "Caminar, trotar, rodar", R.drawable.ic_actividad,
                                R.color.cat_deporte_fondo, R.color.cat_deporte, Etiqueta.ACTIVIDAD, Etiqueta.AIRE_LIBRE),
                        new Opcion("Trabajar o estudiar", "Un sitio tranquilo con mesa", R.drawable.ic_trabajar,
                                R.color.cat_estudio_fondo, R.color.cat_estudio, Etiqueta.TRANQUILO, Etiqueta.IDEAL_SOLO));
        }
    }

    private void planes(Opcion... posibles) {
        for (int i = 0; i < posibles.length; i++) {
            Opcion o = posibles[i];
            opcion(Paso.PLAN, i, o, () -> plan = o);
        }
    }

    private void opcion(Paso paso, int indice, Opcion o, Runnable alElegir) {
        MaterialCardView tarjeta = (MaterialCardView) Estilo.inflar(opciones, R.layout.item_opcion);
        ((TextView) tarjeta.findViewById(R.id.titulo)).setText(o.titulo);
        ((TextView) tarjeta.findViewById(R.id.subtitulo)).setText(o.subtitulo);
        View fondo = tarjeta.findViewById(R.id.fondoIcono);
        fondo.setBackgroundTintList(Estilo.lista(this, o.fondo));
        ImageView icono = tarjeta.findViewById(R.id.icono);
        icono.setImageResource(o.icono);
        icono.setImageTintList(Estilo.lista(this, o.tinta));
        boolean marcada = elegida[paso.ordinal()] == indice;
        marcar(tarjeta, marcada, false);
        tarjeta.setOnClickListener(v -> {
            elegida[paso.ordinal()] = indice;
            if (paso == Paso.COMIDA) {
                comida = o;
            }
            if (alElegir != null) {
                alElegir.run();
            }
            for (int i = 0; i < opciones.getChildCount(); i++) {
                marcar((MaterialCardView) opciones.getChildAt(i), opciones.getChildAt(i) == tarjeta, true);
            }
            // pausa corta para que se alcance a ver lo que se marcó
            tarjeta.postDelayed(this::avanzar, 220);
        });
        opciones.addView(tarjeta);
    }

    private void marcar(MaterialCardView tarjeta, boolean marcada, boolean animar) {
        tarjeta.setStrokeColor(com.google.android.material.color.MaterialColors.getColor(tarjeta, marcada
                ? com.google.android.material.R.attr.colorPrimary
                : com.google.android.material.R.attr.colorOutlineVariant));
        View marca = tarjeta.findViewById(R.id.marca);
        if (animar) {
            marca.animate().alpha(marcada ? 1f : 0f).scaleX(marcada ? 1f : 0.6f).scaleY(marcada ? 1f : 0.6f)
                    .setDuration(160).start();
        } else {
            marca.setAlpha(marcada ? 1f : 0f);
        }
    }

    private void mostrarPersonas() {
        View vista = Estilo.inflar(opciones, R.layout.item_personas);
        TextView numero = vista.findViewById(R.id.numero);
        TextView texto = vista.findViewById(R.id.texto);
        Slider deslizador = vista.findViewById(R.id.deslizador);
        deslizador.setValue(Math.max(2, Math.min(20, personas)));
        Runnable actualizar = () -> {
            numero.setText(String.valueOf(personas));
            texto.setText(personas >= 8 ? "Un parche grande" : personas >= 5 ? "Un buen combo" : "Un grupo pequeño");
        };
        personas = (int) deslizador.getValue();
        actualizar.run();
        deslizador.addOnChangeListener((s, valor, delUsuario) -> {
            personas = (int) valor;
            actualizar.run();
        });
        vista.findViewById(R.id.siguiente).setOnClickListener(v -> avanzar());
        opciones.addView(vista);
    }

    private Preferencias preferencias() {
        Preferencias p = new Preferencias()
                .con(compania, compania == Compania.SOLO ? 1 : compania == Compania.PAREJA ? 2 : personas)
                .presupuesto(presupuesto);
        if (plan != null) {
            p.buscar(plan.buscar);
        }
        if (comida != null) {
            p.buscar(comida.buscar);
        }
        if (tragos == 0) {
            p.buscar(Etiqueta.ALCOHOL);
        } else if (tragos == 1 || compania == Compania.FAMILIA) {
            p.evitar(Etiqueta.ALCOHOL);
        }
        if (compania == Compania.FAMILIA) {
            p.buscar(Etiqueta.FAMILIAR);
        }
        return p;
    }

    private void mostrarResultado() {
        Estado estado = Estado.get();
        ListaEnlazada<Recomendacion> mejores = Recomendador.recomendar(estado.catalogo.todos(), preferencias(),
                estado.ubicacion, 5);
        String resumen = (compania == Compania.SOLO ? "Solo" : compania == Compania.PAREJA ? "En pareja"
                : compania == Compania.AMIGOS ? personas + " amigos" : "En familia")
                + (plan != null ? " · " + plan.titulo.toLowerCase(Formato.ES) : "")
                + (tragos == 0 ? " · con tragos" : tragos == 1 ? " · sin alcohol" : "")
                + " · hasta " + Formato.precio(presupuesto);
        if (mejores.estaVacia()) {
            preguntar("No encontramos nada así", "Prueba con otro presupuesto o cambia alguna respuesta.");
        } else {
            preguntar("Esto es para ustedes", resumen);
            if (compania == Compania.SOLO) {
                preguntar("Esto es para ti", resumen);
            }
        }

        int puesto = 1;
        for (Recomendacion r : mejores) {
            View tarjeta = Estilo.inflar(opciones, R.layout.item_recomendacion);
            Estilo.pintarCategoria(tarjeta.findViewById(R.id.franja), tarjeta.findViewById(R.id.icono),
                    r.getLugar().getCategoria());
            TextView numero = tarjeta.findViewById(R.id.puesto);
            numero.setText(String.valueOf(puesto));
            numero.setTextColor(Estilo.color(this, Estilo.tinta(r.getLugar().getCategoria())));
            ((TextView) tarjeta.findViewById(R.id.nombre)).setText(r.getLugar().getNombre());
            ((TextView) tarjeta.findViewById(R.id.meta)).setText(Formato.metaLugar(r.getLugar(), r.getDistanciaKm()));
            ((TextView) tarjeta.findViewById(R.id.porque)).setText("Porque buscas algo "
                    + Etiqueta.describir(r.getCoincidencias() & ~Etiqueta.mascara(Etiqueta.IDEAL_SOLO)).replace(" · ", ", "));
            int abiertos = estado.gestor.activosEn(r.getLugar(), Estado.ahora()).tamano();
            TextView parches = tarjeta.findViewById(R.id.parches);
            if (abiertos > 0) {
                parches.setVisibility(View.VISIBLE);
                parches.setText(abiertos == 1 ? "Hay 1 parche armándose aquí" : "Hay " + abiertos + " parches armándose aquí");
            }
            tarjeta.setOnClickListener(v -> Ui.abrirLugar(this, r.getLugar()));
            opciones.addView(tarjeta);
            puesto++;
        }

        MaterialButton otraVez = (MaterialButton) getLayoutInflater().inflate(R.layout.item_atajo, opciones, false);
        otraVez.setText("Responder de nuevo");
        otraVez.setIconResource(R.drawable.ic_atras);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
        params.topMargin = (int) (8 * getResources().getDisplayMetrics().density);
        otraVez.setLayoutParams(params);
        otraVez.setOnClickListener(v -> {
            actual = 0;
            transicion(false);
        });
        opciones.addView(otraVez);
    }

    // las opciones van apareciendo una por una
    private void animarOpciones() {
        float desplazamiento = 16 * getResources().getDisplayMetrics().density;
        for (int i = 0; i < opciones.getChildCount(); i++) {
            View hija = opciones.getChildAt(i);
            hija.setAlpha(0f);
            hija.setTranslationY(desplazamiento);
            hija.animate().alpha(1f).translationY(0f).setStartDelay(60L + 45L * i).setDuration(260)
                    .setInterpolator(new DecelerateInterpolator(2f)).start();
        }
    }
}
