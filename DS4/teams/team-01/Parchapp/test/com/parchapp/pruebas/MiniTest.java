package com.parchapp.pruebas;

import java.util.Objects;

// Pruebas para no depender de JUnit (así corre en cualquier IDE solo con el JDK).
// Cuando pasemos a Gradle se puede migrar: cada caso
final class MiniTest {

    private static int aprobadas;
    private static int fallidas;

    static void grupo(String nombre) {
        System.out.println();
        System.out.println("== " + nombre);
    }

    static void caso(String nombre, Runnable prueba) {
        try {
            prueba.run();
            aprobadas++;
            System.out.println("   [OK]    " + nombre);
        } catch (Throwable error) {
            fallidas++;
            System.out.println("   [FALLA] " + nombre + " -> " + error);
        }
    }

    static void verificar(boolean condicion, String mensaje) {
        if (!condicion) {
            throw new AssertionError(mensaje);
        }
    }

    static void iguales(Object esperado, Object obtenido) {
        if (!Objects.equals(esperado, obtenido)) {
            throw new AssertionError("se esperaba <" + esperado + "> pero fue <" + obtenido + ">");
        }
    }

    static void lanza(Class<? extends Throwable> tipo, Runnable accion) {
        try {
            accion.run();
        } catch (Throwable error) {
            if (tipo.isInstance(error)) {
                return;
            }
            throw new AssertionError("se esperaba " + tipo.getSimpleName() + " pero se lanzo " + error);
        }
        throw new AssertionError("se esperaba " + tipo.getSimpleName() + " y no se lanzo nada");
    }

    static int resumen() {
        System.out.println();
        System.out.println("Resultado: " + aprobadas + " aprobadas, " + fallidas + " fallidas.");
        return fallidas == 0 ? 0 : 1;
    }
}
