package com.parchapp.android;

import androidx.fragment.app.Fragment;

// Pestaña de la barra inferior. Se refresca cada vez que aparece porque otra pantalla
// pudo cambiar algo (p. ej. unirse a un parche).
public abstract class Pestana extends Fragment {

    abstract void refrescar();

    @Override
    public void onResume() {
        super.onResume();
        if (!isHidden()) {
            refrescar();
        }
    }

    @Override
    public void onHiddenChanged(boolean oculta) {
        super.onHiddenChanged(oculta);
        if (!oculta && getView() != null) {
            refrescar();
        }
    }
}
