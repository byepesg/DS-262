package com.parchapp.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

// Cola FIFO con nodos enlazados. Se encola por "fin" y se desencola por "frente",
// así encolar y desencolar son O(1) sin recorrer nada.
// Invariantes: frente == null <=> fin == null <=> tamano == 0, y fin.siguiente == null.
public class Cola<T> implements Iterable<T> {

    private Nodo<T> frente;
    private Nodo<T> fin;
    private int tamano;

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fin.siguiente = nuevo;
        }
        fin = nuevo;
        tamano++;
    }

    // Con la cola vacía se lanza una excepción y la cola no cambia; quien llama debe revisar estaVacia()
    public T desencolar() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        T dato = frente.dato;
        frente = frente.siguiente;
        if (frente == null) {
            fin = null;
        }
        tamano--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) {
            throw new NoSuchElementException("La cola esta vacia");
        }
        return frente.dato;
    }

    public boolean contiene(T dato) {
        return posicionDe(dato) > 0;
    }

    // Posición contando desde 1 (el frente), o 0 si no está
    public int posicionDe(T dato) {
        int posicion = 1;
        for (T elemento : this) {
            if (Objects.equals(elemento, dato)) {
                return posicion;
            }
            posicion++;
        }
        return 0;
    }

    public void vaciar() {
        frente = null;
        fin = null;
        tamano = 0;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = frente;

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
            return frente == null && fin == null;
        }
        if (frente == null || fin == null || fin.siguiente != null) {
            return false;
        }
        int contados = 0;
        Nodo<T> actual = frente;
        while (actual != null && contados <= tamano) {
            contados++;
            if (actual.siguiente == null && actual != fin) {
                return false;
            }
            actual = actual.siguiente;
        }
        return contados == tamano;
    }

    public String dibujarEnlaces(Function<T, String> comoTexto) {
        if (estaVacia()) {
            return "frente -> null <- fin";
        }
        String dibujo = "frente -> ";
        for (Nodo<T> actual = frente; actual != null; actual = actual.siguiente) {
            dibujo += "[" + comoTexto.apply(actual.dato) + "]";
            if (actual.siguiente != null) {
                dibujo += " -> ";
            }
        }
        return dibujo + " <- fin";
    }

    @Override
    public String toString() {
        String texto = "[";
        for (Nodo<T> actual = frente; actual != null; actual = actual.siguiente) {
            texto += actual.dato;
            if (actual.siguiente != null) {
                texto += ", ";
            }
        }
        return texto + "]";
    }
}
