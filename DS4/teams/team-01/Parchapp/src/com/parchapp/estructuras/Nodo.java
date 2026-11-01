package com.parchapp.estructuras;

// Solo es visible dentro del paquete: nadie por fuera puede dañar los enlaces de la lista o la cola.
class Nodo<T> {

    T dato;
    Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }
}
