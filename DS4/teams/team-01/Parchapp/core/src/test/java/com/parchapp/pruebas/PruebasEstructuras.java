package com.parchapp.pruebas;

import static com.parchapp.pruebas.MiniTest.caso;
import static com.parchapp.pruebas.MiniTest.grupo;
import static com.parchapp.pruebas.MiniTest.iguales;
import static com.parchapp.pruebas.MiniTest.lanza;
import static com.parchapp.pruebas.MiniTest.verificar;

import com.parchapp.estructuras.Cola;
import com.parchapp.estructuras.ListaEnlazada;
import java.util.NoSuchElementException;

// Casos de la sección 11.3 del documento maestro: vacía, un elemento, varios, dato inexistente y límites
final class PruebasEstructuras {

    private static ListaEnlazada<Integer> listaCon(int... valores) {
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        for (int valor : valores) {
            lista.agregarAlFinal(valor);
        }
        return lista;
    }

    static void ejecutar() {
        grupo("ListaEnlazada");

        caso("lista vacia: consultar y recorrer sin errores", () -> {
            ListaEnlazada<Integer> lista = new ListaEnlazada<>();
            iguales(0, lista.tamano());
            verificar(lista.estaVacia(), "deberia estar vacia");
            verificar(!lista.iterator().hasNext(), "el iterador no deberia tener elementos");
            verificar(!lista.contiene(5), "no deberia contener nada");
            iguales("[]", lista.toString());
            verificar(lista.invariantesValidos(), "invariantes");
        });

        caso("lista vacia: eliminar es una operacion controlada (T-06)", () -> {
            ListaEnlazada<Integer> lista = new ListaEnlazada<>();
            verificar(!lista.eliminar(1), "eliminar deberia devolver false");
            lanza(NoSuchElementException.class, lista::eliminarPrimero);
            lanza(NoSuchElementException.class, lista::eliminarUltimo);
            lanza(IndexOutOfBoundsException.class, () -> lista.obtener(0));
            verificar(lista.invariantesValidos(), "la lista no deberia quedar dañada");
        });

        caso("un elemento: agregar, buscar y eliminar", () -> {
            ListaEnlazada<Integer> lista = listaCon(7);
            iguales(7, lista.obtenerPrimero());
            iguales(7, lista.obtenerUltimo());
            verificar(lista.contiene(7), "deberia contener 7");
            verificar(lista.eliminar(7), "deberia eliminar 7");
            verificar(lista.estaVacia(), "deberia quedar vacia");
            verificar(lista.invariantesValidos(), "primero y ultimo deberian quedar en null");
        });

        caso("varios elementos: orden de agregarAlInicio y agregarAlFinal (T-05)", () -> {
            ListaEnlazada<Integer> lista = listaCon(2, 3);
            lista.agregarAlInicio(1);
            lista.agregarAlFinal(4);
            iguales("[1, 2, 3, 4]", lista.toString());
            iguales(3, lista.obtener(2));
            iguales("primero -> [1] -> [2] -> [3] -> [4] -> null", lista.dibujarEnlaces(String::valueOf));
            verificar(lista.invariantesValidos(), "invariantes");
        });

        caso("dato inexistente: la lista no cambia", () -> {
            ListaEnlazada<Integer> lista = listaCon(1, 2, 3);
            verificar(!lista.eliminar(99), "eliminar deberia devolver false");
            iguales("[1, 2, 3]", lista.toString());
            iguales(3, lista.tamano());
        });

        caso("eliminar el primero, el ultimo y uno del medio mantiene bien 'ultimo'", () -> {
            ListaEnlazada<Integer> lista = listaCon(1, 2, 3, 4, 5);
            iguales(1, lista.eliminarPrimero());
            iguales(5, lista.eliminarUltimo());
            verificar(lista.eliminar(4), "eliminar el ultimo por valor");
            iguales(3, lista.obtenerUltimo());
            verificar(lista.eliminar(2), "eliminar uno del medio");
            // Si 'ultimo' hubiera quedado mal, este elemento se perderia
            lista.agregarAlFinal(9);
            iguales("[3, 9]", lista.toString());
            verificar(lista.invariantesValidos(), "invariantes");
        });

        caso("posiciones fuera de rango lanzan IndexOutOfBoundsException", () -> {
            ListaEnlazada<Integer> lista = listaCon(1, 2);
            lanza(IndexOutOfBoundsException.class, () -> lista.obtener(-1));
            lanza(IndexOutOfBoundsException.class, () -> lista.obtener(2));
        });

        caso("duplicados: eliminar quita solo la primera aparicion", () -> {
            ListaEnlazada<Integer> lista = listaCon(1, 2, 1);
            verificar(lista.eliminar(1), "eliminar 1");
            iguales("[2, 1]", lista.toString());
        });

        caso("ordenar (merge sort): casos borde y actualiza 'ultimo'", () -> {
            ListaEnlazada<Integer> vacia = new ListaEnlazada<>();
            vacia.ordenar(Integer::compare);
            verificar(vacia.invariantesValidos(), "lista vacia");

            ListaEnlazada<Integer> lista = listaCon(5, 3, 9, 1, 3, 8, 2);
            lista.ordenar(Integer::compare);
            iguales("[1, 2, 3, 3, 5, 8, 9]", lista.toString());
            iguales(9, lista.obtenerUltimo());
            lista.agregarAlFinal(10);
            iguales(10, lista.obtenerUltimo());
            verificar(lista.invariantesValidos(), "invariantes");
        });

        caso("ordenar es estable: los iguales conservan su orden", () -> {
            ListaEnlazada<String> lista = new ListaEnlazada<>();
            lista.agregarAlFinal("b1");
            lista.agregarAlFinal("a1");
            lista.agregarAlFinal("b2");
            lista.agregarAlFinal("a2");
            lista.ordenar((x, y) -> Character.compare(x.charAt(0), y.charAt(0)));
            iguales("[a1, a2, b1, b2]", lista.toString());
        });

        caso("1000 operaciones mezcladas mantienen los invariantes", () -> {
            ListaEnlazada<Integer> lista = new ListaEnlazada<>();
            for (int i = 0; i < 1000; i++) {
                if (i % 5 == 0 || i % 5 == 1) {
                    lista.agregarAlFinal(i);
                } else if (i % 5 == 2) {
                    lista.agregarAlInicio(i);
                } else if (i % 5 == 3) {
                    lista.eliminarPrimero();
                } else {
                    lista.eliminar(i - 4);
                }
                verificar(lista.invariantesValidos(), "invariantes en la iteracion " + i);
            }
        });

        grupo("Cola");

        caso("cola vacia: desencolar y verFrente son controlados (T-06)", () -> {
            Cola<String> cola = new Cola<>();
            verificar(cola.estaVacia(), "deberia estar vacia");
            lanza(NoSuchElementException.class, cola::desencolar);
            lanza(NoSuchElementException.class, cola::verFrente);
            verificar(cola.invariantesValidos(), "no deberia quedar dañada");
        });

        caso("FIFO: sale primero el que entro primero", () -> {
            Cola<String> cola = new Cola<>();
            cola.encolar("A");
            cola.encolar("B");
            cola.encolar("C");
            iguales("A", cola.verFrente());
            iguales("A", cola.desencolar());
            iguales("B", cola.desencolar());
            iguales(1, cola.tamano());
            iguales("frente -> [C] <- fin", cola.dibujarEnlaces(s -> s));
        });

        caso("vaciar y volver a encolar reinicia bien 'fin'", () -> {
            Cola<String> cola = new Cola<>();
            cola.encolar("A");
            cola.desencolar();
            verificar(cola.invariantesValidos(), "fin deberia quedar en null");
            cola.encolar("B");
            cola.encolar("C");
            iguales("[B, C]", cola.toString());
            verificar(cola.invariantesValidos(), "invariantes");
        });

        caso("posicionDe cuenta desde el frente y devuelve 0 si no esta", () -> {
            Cola<String> cola = new Cola<>();
            cola.encolar("A");
            cola.encolar("B");
            iguales(2, cola.posicionDe("B"));
            iguales(0, cola.posicionDe("Z"));
            verificar(cola.contiene("A"), "contiene A");
        });
    }
}
