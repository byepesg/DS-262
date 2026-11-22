package com.parchapp.pruebas;

public class EjecutarPruebas {

    public static void main(String[] args) {
        System.out.println("PRUEBAS DE PARCHAPP");
        PruebasEstructuras.ejecutar();
        PruebasParches.ejecutar();
        PruebasServicios.ejecutar();
        System.exit(MiniTest.resumen());
    }
}
