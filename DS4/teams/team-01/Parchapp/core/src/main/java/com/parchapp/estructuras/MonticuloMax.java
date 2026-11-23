package com.parchapp.estructuras;

import java.util.Comparator;
import java.util.NoSuchElementException;

// Montículo de máximos sobre un arreglo que se duplica cuando se llena.
// Hijos de i: 2i+1 y 2i+2, padre: (i-1)/2. Ningún hijo es mayor que su padre (el máximo queda en 0).
// Lo usa Recomendador para sacar los k mejores sin ordenar todo.
public class MonticuloMax<T> {

    private static final int CAPACIDAD_INICIAL = 16;

    private final Comparator<T> comparador;
    private Object[] datos = new Object[CAPACIDAD_INICIAL];
    private int tamano;

    public MonticuloMax(Comparator<T> comparador) {
        this.comparador = comparador;
    }

    // O(log n). Si está lleno se duplica el arreglo (O(n) esa vez, O(1) amortizado)
    public void insertar(T dato) {
        if (tamano == datos.length) {
            Object[] mas = new Object[datos.length * 2];
            for (int i = 0; i < tamano; i++) {
                mas[i] = datos[i];
            }
            datos = mas;
        }
        datos[tamano] = dato;
        subir(tamano);
        tamano++;
    }

    // O(log n): el último pasa a la raíz y se hunde hasta su lugar
    public T extraerMax() {
        if (tamano == 0) {
            throw new NoSuchElementException("El montículo está vacío");
        }
        T maximo = elemento(0);
        tamano--;
        datos[0] = datos[tamano];
        datos[tamano] = null;
        if (tamano > 0) {
            bajar(0);
        }
        return maximo;
    }

    // O(1)
    public T verMax() {
        if (tamano == 0) {
            throw new NoSuchElementException("El montículo está vacío");
        }
        return elemento(0);
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacio() {
        return tamano == 0;
    }

    // Revisa que cada hijo sea menor o igual que su padre
    public boolean invariantesValidos() {
        if (tamano < 0 || tamano > datos.length) {
            return false;
        }
        for (int i = 1; i < tamano; i++) {
            if (comparador.compare(elemento(i), elemento((i - 1) / 2)) > 0) {
                return false;
            }
        }
        for (int i = tamano; i < datos.length; i++) {
            if (datos[i] != null) {
                return false;
            }
        }
        return true;
    }

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (comparador.compare(elemento(i), elemento(padre)) <= 0) {
                return;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int mayor = i;
            int izquierdo = 2 * i + 1;
            int derecho = 2 * i + 2;
            if (izquierdo < tamano && comparador.compare(elemento(izquierdo), elemento(mayor)) > 0) {
                mayor = izquierdo;
            }
            if (derecho < tamano && comparador.compare(elemento(derecho), elemento(mayor)) > 0) {
                mayor = derecho;
            }
            if (mayor == i) {
                return;
            }
            intercambiar(i, mayor);
            i = mayor;
        }
    }

    private void intercambiar(int a, int b) {
        Object temporal = datos[a];
        datos[a] = datos[b];
        datos[b] = temporal;
    }

    @SuppressWarnings("unchecked")
    private T elemento(int i) {
        return (T) datos[i];
    }
}
