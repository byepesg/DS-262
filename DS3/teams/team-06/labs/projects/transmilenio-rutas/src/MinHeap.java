import java.util.Arrays;
import java.util.NoSuchElementException;

public class MinHeap<T extends Comparable<T>> {
    private static final int CAPACIDAD_INICIAL = 16;

    private Object[] datos;
    private int tamano;

    public MinHeap() {
        this.datos = new Object[CAPACIDAD_INICIAL];
        this.tamano = 0;
    }

    // agrega y sube el elemento hasta su lugar
    public void insertar(T elemento) {
        if (elemento == null) {
            throw new IllegalArgumentException("No se permiten elementos nulos");
        }
        if (tamano == datos.length) {
            datos = Arrays.copyOf(datos, datos.length * 2); // duplica la capacidad
        }
        datos[tamano] = elemento;
        subir(tamano);
        tamano++;
    }

    // saca el menor y reacomoda el montículo
    public T extraerMin() {
        if (tamano == 0) {
            throw new NoSuchElementException("El montículo está vacío");
        }
        T minimo = elemento(0);
        tamano--;
        datos[0] = datos[tamano];
        datos[tamano] = null;
        if (tamano > 0) {
            bajar(0);
        }
        return minimo;
    }

    public T verMin() {
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

    private void subir(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (elemento(i).compareTo(elemento(padre)) >= 0) {
                break;
            }
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajar(int i) {
        while (true) {
            int izq = 2 * i + 1;
            int der = izq + 1;
            int menor = i;
            if (izq < tamano && elemento(izq).compareTo(elemento(menor)) < 0) {
                menor = izq;
            }
            if (der < tamano && elemento(der).compareTo(elemento(menor)) < 0) {
                menor = der;
            }
            if (menor == i) {
                return;
            }
            intercambiar(i, menor);
            i = menor;
        }
    }

    @SuppressWarnings("unchecked")
    private T elemento(int i) {
        return (T) datos[i];
    }

    private void intercambiar(int i, int j) {
        Object tmp = datos[i];
        datos[i] = datos[j];
        datos[j] = tmp;
    }
}
