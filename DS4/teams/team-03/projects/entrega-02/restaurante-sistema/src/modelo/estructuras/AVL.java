package modelo.estructuras;

public class AVL<T extends Comparable<T>> {
    private NodoAVL<T> raiz;
    private int tamagno;

    public AVL() {
        this.raiz = null;
        this.tamagno = 0;
    }

    public NodoAVL<T> balancear(NodoAVL<T> n){
        if(n==null){
            return null;
        }
        actualizarAltura(n);
        int balance=obtenerFactorBalance(n);
        if(balance>1){
            if(obtenerFactorBalance(n.getIzquierdo())<0){
                n.setIzquierdo(rotacionIzquierda(n.getIzquierdo()));
            }
            return rotacionDerecha(n);
        }
        if(balance<-1){
            if(obtenerFactorBalance(n.getDerecho())>0){
                n.setDerecho(rotacionDerecha(n.getDerecho()));
            }
            return rotacionIzquierda(n);
        }
        return n;
    }

    public void insertar(T dato) {
        raiz= insertar(raiz, dato);
    }
    private NodoAVL<T> insertar(NodoAVL<T> n, T dato){
        if(n==null){
            tamagno++;
            return new NodoAVL<>(dato);
        }
        int comparar = dato.compareTo(n.getDato());
        if(comparar<0){
            n.setIzquierdo(insertar(n.getIzquierdo(), dato));
        } else if(comparar>0){
            n.setDerecho(insertar(n.getDerecho(), dato));
        } else {
            return n;
        }
        return balancear(n);
    }

    public T buscar(T clave) {
        return buscar(raiz, clave);
    }

    private T buscar(NodoAVL<T> n, T clave){
        if(n==null) return null;
        int comparar=clave.compareTo(n.getDato());
        if(comparar<0){
            return buscar(n.getIzquierdo(), clave);
        }
        else if(comparar>0){
            return buscar(n.getDerecho(), clave);
        }
        else{
            return n.getDato();
        }
    }

    public boolean estaVacio() {
        return raiz == null;
    }

    public int getTamagno() {
        return tamagno;
    }

    private int obtenerAltura(NodoAVL<T> nodo) {
        return (nodo == null) ? 0 : nodo.getAltura();
    }

    private void actualizarAltura(NodoAVL<T> nodo){
        if(nodo!=null){
            int alturaIzquierda=obtenerAltura(nodo.getIzquierdo());
            int alturaDerecha=obtenerAltura(nodo.getDerecho());
            nodo.setAltura(1+Math.max(alturaIzquierda, alturaDerecha));
        }
    }

    private int obtenerFactorBalance(NodoAVL<T> nodo) {
        if(nodo==null){
            return 0;
        }
        return obtenerAltura(nodo.getIzquierdo())-obtenerAltura(nodo.getDerecho());
    }

    private NodoAVL<T> rotacionDerecha(NodoAVL<T> y) {
            NodoAVL<T> I= y.getIzquierdo();
            NodoAVL<T> I2=I.getDerecho();
            I.setDerecho(y);
            y.setIzquierdo(I2);
            actualizarAltura(y);
            actualizarAltura(I);
        return I;
    }

    private NodoAVL<T> rotacionIzquierda(NodoAVL<T> x) {
        NodoAVL<T> D= x.getDerecho();
        NodoAVL<T> D2=D.getIzquierdo();
        D.setIzquierdo(x);
        x.setDerecho(D2);
        actualizarAltura(x);
        actualizarAltura(D);
        return D;
    }

    public void eliminar(T dato){
        raiz=eliminar(raiz, dato);
    }

    private NodoAVL<T> eliminar(NodoAVL<T> n, T dato){
        if(n==null){
            return null;
        }
        int comparar=dato.compareTo(n.getDato());
        if(comparar<0){
            n.setIzquierdo(eliminar(n.getIzquierdo(), dato));
        }
        else if(comparar>0){
            n.setDerecho(eliminar(n.getDerecho(), dato));
        }
        else{
            tamagno--;
            if(n.getIzquierdo()==null) return n.getDerecho();
        else if(n.getDerecho()==null)return n.getIzquierdo();
        NodoAVL<T> sucesor=obtenerMinimo(n.getDerecho());
        n.setDato(sucesor.getDato());
        n.setDerecho(eliminar(n.getDerecho(), sucesor.getDato()));
        tamagno++;
        }
        return balancear(n);
    }

    private NodoAVL<T> obtenerMinimo(NodoAVL<T> n){
        NodoAVL<T> min=n;
        while(min.getIzquierdo()!=null){
            min=min.getIzquierdo();
        }
        return min;
    }
}