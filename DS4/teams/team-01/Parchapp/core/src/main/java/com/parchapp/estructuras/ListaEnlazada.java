package com.parchapp.estructuras;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

// Lista simple con referencia al primer y al último nodo.
// Invariantes: primero == null <=> ultimo == null <=> tamano == 0, y ultimo.siguiente == null.
public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamano;

    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.siguiente = primero;
        primero = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        tamano++;
    }

    // O(1) gracias a la referencia al último
    public void agregarAlFinal(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            primero = nuevo;
        } else {
            ultimo.siguiente = nuevo;
        }
        ultimo = nuevo;
        tamano++;
    }

    public T obtener(int posicion) {
        if (posicion < 0 || posicion >= tamano) {
            throw new IndexOutOfBoundsException("Posicion " + posicion + " fuera de una lista de tamano " + tamano);
        }
        Nodo<T> actual = primero;
        for (int i = 0; i < posicion; i++) {
            actual = actual.siguiente;
        }
        return actual.dato;
    }

    public T obtenerPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        return primero.dato;
    }

    public T obtenerUltimo() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        return ultimo.dato;
    }

    public boolean contiene(T dato) {
        for (T elemento : this) {
            if (Objects.equals(elemento, dato)) {
                return true;
            }
        }
        return false;
    }

    public T eliminarPrimero() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        T dato = primero.dato;
        primero = primero.siguiente;
        if (primero == null) {
            ultimo = null;
        }
        tamano--;
        return dato;
    }

    // O(n): toca llegar al penúltimo para actualizar ultimo
    public T eliminarUltimo() {
        if (estaVacia()) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        if (primero == ultimo) {
            return eliminarPrimero();
        }
        Nodo<T> penultimo = primero;
        while (penultimo.siguiente != ultimo) {
            penultimo = penultimo.siguiente;
        }
        T dato = ultimo.dato;
        penultimo.siguiente = null;
        ultimo = penultimo;
        tamano--;
        return dato;
    }

    // Quita la primera aparición; false si no estaba
    public boolean eliminar(T dato) {
        if (estaVacia()) {
            return false;
        }
        if (Objects.equals(primero.dato, dato)) {
            eliminarPrimero();
            return true;
        }
        Nodo<T> anterior = primero;
        while (anterior.siguiente != null) {
            Nodo<T> candidato = anterior.siguiente;
            if (Objects.equals(candidato.dato, dato)) {
                anterior.siguiente = candidato.siguiente;
                if (candidato == ultimo) {
                    ultimo = anterior;
                }
                tamano--;
                return true;
            }
            anterior = candidato;
        }
        return false;
    }

    public void vaciar() {
        primero = null;
        ultimo = null;
        tamano = 0;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    // Merge sort, O(n log n). Solo reenlaza nodos; quicksort necesitaría acceso por posición
    public void ordenar(Comparator<T> comparador) {
        primero = ordenarDesde(primero, comparador);
        ultimo = primero;
        while (ultimo != null && ultimo.siguiente != null) {
            ultimo = ultimo.siguiente;
        }
    }

    private Nodo<T> ordenarDesde(Nodo<T> inicio, Comparator<T> comparador) {
        if (inicio == null || inicio.siguiente == null) {
            return inicio;
        }
        // Para encontrar la mitad, "rapido" avanza dos nodos por cada uno que avanza "lento"
        Nodo<T> lento = inicio;
        Nodo<T> rapido = inicio.siguiente;
        while (rapido != null && rapido.siguiente != null) {
            lento = lento.siguiente;
            rapido = rapido.siguiente.siguiente;
        }
        Nodo<T> segundaMitad = lento.siguiente;
        lento.siguiente = null;

        Nodo<T> izquierda = ordenarDesde(inicio, comparador);
        Nodo<T> derecha = ordenarDesde(segundaMitad, comparador);
        return mezclar(izquierda, derecha, comparador);
    }

    private Nodo<T> mezclar(Nodo<T> a, Nodo<T> b, Comparator<T> comparador) {
        Nodo<T> auxiliar = new Nodo<>(null); // evita tratar aparte el primer nodo del resultado
        Nodo<T> fin = auxiliar;
        while (a != null && b != null) {
            // <= y no <, para que sea estable
            if (comparador.compare(a.dato, b.dato) <= 0) {
                fin.siguiente = a;
                a = a.siguiente;
            } else {
                fin.siguiente = b;
                b = b.siguiente;
            }
            fin = fin.siguiente;
        }
        if (a != null) {
            fin.siguiente = a;
        } else {
            fin.siguiente = b;
        }
        return auxiliar.siguiente;
    }

    // Para el for-each. Recorrer con obtener(i) sería O(n²)
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = primero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.dato;
                actual = actual.siguiente;
                return dato;
            }
        };
    }

    public boolean invariantesValidos() {
        if (tamano == 0) {
            return primero == null && ultimo == null;
        }
        if (primero == null || ultimo == null || ultimo.siguiente != null) {
            return false;
        }
        int contados = 0;
        Nodo<T> actual = primero;
        // por si los enlaces quedaron en ciclo, no quedarse dando vueltas
        while (actual != null && contados <= tamano) {
            contados++;
            if (actual.siguiente == null && actual != ultimo) {
                return false;
            }
            actual = actual.siguiente;
        }
        return contados == tamano;
    }

    public String dibujarEnlaces(Function<T, String> comoTexto) {
        String dibujo = "primero -> ";
        for (T elemento : this) {
            dibujo += "[" + comoTexto.apply(elemento) + "] -> ";
        }
        return dibujo + "null";
    }

    @Override
    public String toString() {
        String texto = "[";
        for (Nodo<T> actual = primero; actual != null; actual = actual.siguiente) {
            texto += actual.dato;
            if (actual.siguiente != null) {
                texto += ", ";
            }
        }
        return texto + "]";
    }
}
