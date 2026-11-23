package com.parchapp.estructuras;

// Sin public: solo ListaEnlazada y Cola tocan los enlaces
class Nodo<T> {

    T dato;
    Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
    }
}
