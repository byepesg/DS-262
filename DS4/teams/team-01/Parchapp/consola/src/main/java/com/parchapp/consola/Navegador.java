package com.parchapp.consola;

import java.awt.Desktop;
import java.net.URI;

// Abre un enlace en el navegador del computador. Solo existe en la versión de consola:
// java.awt no está disponible en Android, donde se usa un Intent.
final class Navegador {

    private Navegador() {
    }

    // Devuelve false si el sistema no deja abrir el navegador; en ese caso se muestra el enlace
    static boolean abrir(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
