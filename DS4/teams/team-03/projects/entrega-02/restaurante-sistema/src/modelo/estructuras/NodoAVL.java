package modelo.estructuras;

public class NodoAVL<T> {
    private T dato;
    private int altura;
    private NodoAVL<T> izquierdo;
    private NodoAVL<T> derecho;

    public NodoAVL(T dato) {
        this.dato = dato;
        this.altura = 1;
        this.izquierdo = null;
        this.derecho = null;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato){
        this.dato=dato;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura){
        this.altura=altura;
    }

    public NodoAVL<T> getIzquierdo() {
        return izquierdo;
    }

    public NodoAVL<T> getDerecho() {
        return derecho;
    }
    public void setIzquierdo(NodoAVL<T> izquierdo){
        this.izquierdo=izquierdo;
    }
    public void setDerecho(NodoAVL<T> derecho){
        this.derecho=derecho;
    }
}