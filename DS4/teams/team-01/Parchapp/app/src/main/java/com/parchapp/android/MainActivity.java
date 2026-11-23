package com.parchapp.android;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parchapp.dominio.Categoria;

// Las cuatro pestañas. Se crean una vez y se muestran/ocultan para no perder el scroll ni los filtros.
public class MainActivity extends AppCompatActivity {

    private static final int[] MENU = {R.id.nav_inicio, R.id.nav_explorar, R.id.nav_parches, R.id.nav_perfil};
    private static final String[] ETIQUETAS = {"inicio", "explorar", "parches", "perfil"};

    private BottomNavigationView navegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Ui.respetarBarras(findViewById(R.id.contenedor), true, false);

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            FragmentTransaction t = fm.beginTransaction();
            Fragment[] pestanas = {new InicioFragment(), new ExplorarFragment(), new ParchesFragment(), new PerfilFragment()};
            for (int i = 0; i < pestanas.length; i++) {
                t.add(R.id.contenedor, pestanas[i], ETIQUETAS[i]);
                if (i > 0) {
                    t.hide(pestanas[i]);
                }
            }
            t.commitNow();
        }

        navegacion = findViewById(R.id.navegacion);
        navegacion.setOnItemSelectedListener(item -> {
            mostrar(item.getItemId());
            return true;
        });
    }

    void irA(int idMenu) {
        navegacion.setSelectedItemId(idMenu);
    }

    void explorarCategoria(Categoria categoria) {
        ExplorarFragment explorar = (ExplorarFragment) getSupportFragmentManager().findFragmentByTag("explorar");
        if (explorar != null) {
            explorar.filtrarPor(categoria);
        }
        irA(R.id.nav_explorar);
    }

    private void mostrar(int idMenu) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction t = fm.beginTransaction().setCustomAnimations(R.anim.aparecer, R.anim.desaparecer);
        for (int i = 0; i < MENU.length; i++) {
            Fragment pestana = fm.findFragmentByTag(ETIQUETAS[i]);
            if (pestana == null) {
                continue;
            }
            if (MENU[i] == idMenu) {
                t.show(pestana);
            } else if (!pestana.isHidden()) {
                t.hide(pestana);
            }
        }
        t.commit();
    }
}
