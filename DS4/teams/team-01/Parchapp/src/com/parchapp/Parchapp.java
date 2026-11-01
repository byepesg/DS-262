package com.parchapp;

import com.parchapp.consola.App;

public class Parchapp {

    // Con --simple (o la variable de entorno NO_COLOR) no se usan colores ni caracteres especiales
    public static void main(String[] args) {
        boolean modoSimple = System.getenv("NO_COLOR") != null;
        for (String argumento : args) {
            if (argumento.equals("--simple")) {
                modoSimple = true;
            }
        }
        new App(modoSimple).iniciar();
    }
}
