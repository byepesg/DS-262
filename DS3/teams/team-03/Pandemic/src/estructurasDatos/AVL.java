package estructurasDatos;

/**
 * Árbol AVL genérico (Clave-Valor) auto-balanceado.
 * Garantiza operaciones de búsqueda, inserción y eliminación en tiempo O(log n).
 *
 * @param <K> Tipo de la clave ordenable (debe implementar Comparable).
 * @param <V> Tipo del valor almacenado en cada nodo.
 */
public class AVL<K extends Comparable<K>, V> {

    private static class Nodo<K, V> {
        K clave;
        V valor;
        Nodo<K, V> izquierdo;
        Nodo<K, V> derecho;
        int altura;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
            this.izquierdo = null;
            this.derecho = null;
            this.altura = 0;
        }
    }

    private Nodo<K, V> raiz;
    private int tamano;

    public AVL() {
        this.raiz = null;
        this.tamano = 0;
    }

    // --- INSERCIÓN CON REBALANCEO ---

    public void insertar(K clave, V valor) {
        if (clave == null || valor == null) {
            throw new IllegalArgumentException("La clave y el valor no pueden ser nulos.");
        }
        this.raiz = insertarRecursivo(this.raiz, clave, valor);
    }

    private Nodo<K, V> insertarRecursivo(Nodo<K, V> actual, K clave, V valor) {
        if (actual == null) {
            tamano++;
            return new Nodo<>(clave, valor);
        }

        int comparacion = clave.compareTo(actual.clave);
        if (comparacion == 0) {
            throw new IllegalArgumentException("La clave '" + clave + "' ya existe en el árbol.");
        } else if (comparacion < 0) {
            actual.izquierdo = insertarRecursivo(actual.izquierdo, clave, valor);
        } else {
            actual.derecho = insertarRecursivo(actual.derecho, clave, valor);
        }

        return balancear(actual);
    }

    // --- ELIMINACIÓN CON REBALANCEO ---

    public void eliminar(K clave) {
        if (clave == null) {
            throw new IllegalArgumentException("La clave no puede ser nula.");
        }
        this.raiz = eliminarRecursivo(this.raiz, clave);
    }

    private Nodo<K, V> eliminarRecursivo(Nodo<K, V> actual, K clave) {
        if (actual == null) {
            throw new IllegalArgumentException("La clave '" + clave + "' no existe en el árbol.");
        }

        int comparacion = clave.compareTo(actual.clave);
        if (comparacion < 0) {
            actual.izquierdo = eliminarRecursivo(actual.izquierdo, clave);
        } else if (comparacion > 0) {
            actual.derecho = eliminarRecursivo(actual.derecho, clave);
        } else {
            // Caso 1: Hoja (0 hijos)
            if (actual.izquierdo == null && actual.derecho == null) {
                tamano--;
                return null;
            }

            // Caso 2: Solo subárbol derecho
            if (actual.izquierdo == null) {
                tamano--;
                return actual.derecho;
            }

            // Caso 3: Solo subárbol izquierdo
            if (actual.derecho == null) {
                tamano--;
                return actual.izquierdo;
            }

            // Caso 4: Ambos subárboles (2 hijos)
            Nodo<K, V> sucesor = obtenerMinimo(actual.derecho);
            actual.clave = sucesor.clave;
            actual.valor = sucesor.valor;
            actual.derecho = eliminarRecursivo(actual.derecho, sucesor.clave);
        }

        return balancear(actual);
    }

    // --- BÚSQUEDA ---

    public V buscar(K clave) {
        if (clave == null) {
            return null;
        }
        Nodo<K, V> actual = this.raiz;
        while (actual != null) {
            int comparacion = clave.compareTo(actual.clave);
            if (comparacion == 0) {
                return actual.valor;
            } else if (comparacion < 0) {
                actual = actual.izquierdo;
            } else {
                actual = actual.derecho;
            }
        }
        return null;
    }

    public boolean contiene(K clave) {
        return buscar(clave) != null;
    }

    // --- MOTOR DE BALANCEO Y ROTACIONES ---

    private Nodo<K, V> balancear(Nodo<K, V> actual) {
        if (actual == null) {
            return null;
        }

        actualizarAltura(actual);
        int fe = obtenerFactorBalance(actual);

        // Caso Left-Left (LL) o Left-Right (LR)
        if (fe > 1) {
            if (obtenerFactorBalance(actual.izquierdo) < 0) {
                actual.izquierdo = rotacionIzquierda(actual.izquierdo); // LR
            }
            return rotacionDerecha(actual); // LL
        }

        // Caso Right-Right (RR) o Right-Left (RL)
        if (fe < -1) {
            if (obtenerFactorBalance(actual.derecho) > 0) {
                actual.derecho = rotacionDerecha(actual.derecho); // RL
            }
            return rotacionIzquierda(actual); // RR
        }

        return actual;
    }

    private Nodo<K, V> rotacionDerecha(Nodo<K, V> y) {
        Nodo<K, V> x = y.izquierdo;
        Nodo<K, V> t2 = x.derecho;

        x.derecho = y;
        y.izquierdo = t2;

        actualizarAltura(y);
        actualizarAltura(x);

        return x;
    }

    private Nodo<K, V> rotacionIzquierda(Nodo<K, V> x) {
        Nodo<K, V> y = x.derecho;
        Nodo<K, V> t2 = y.izquierdo;

        y.izquierdo = x;
        x.derecho = t2;

        actualizarAltura(x);
        actualizarAltura(y);

        return y;
    }

    // --- MÉTODOS AUXILIARES Y RECORRIDO ---

    private void actualizarAltura(Nodo<K, V> actual) {
        if (actual != null) {
            actual.altura = 1 + Math.max(
                obtenerAlturaNodo(actual.izquierdo),
                obtenerAlturaNodo(actual.derecho)
            );
        }
    }

    private int obtenerAlturaNodo(Nodo<K, V> actual) {
        return (actual == null) ? -1 : actual.altura;
    }

    private int obtenerFactorBalance(Nodo<K, V> actual) {
        if (actual == null) {
            return 0;
        }
        return obtenerAlturaNodo(actual.izquierdo) - obtenerAlturaNodo(actual.derecho);
    }

    private Nodo<K, V> obtenerMinimo(Nodo<K, V> actual) {
        while (actual.izquierdo != null) {
            actual = actual.izquierdo;
        }
        return actual;
    }

    public int obtenerTamano() {
        return tamano;
    }

    public boolean isEmpty() {
        return tamano == 0;
    }

    public int obtenerAlturaArbol() {
        return obtenerAlturaNodo(this.raiz);
    }

    /**
     * Llena el arreglo 'destino' con los valores del árbol ordenados ascendentemente por clave.
     *
     * @param destino Arreglo con capacidad al menos igual a obtenerTamano().
     * @return Cantidad de elementos escritos en el arreglo.
     */
    public int inOrder(V[] destino) {
        if (destino == null || destino.length < tamano) {
            throw new IllegalArgumentException("El arreglo destino debe tener capacidad suficiente.");
        }
        return inOrderRecursivo(this.raiz, destino, 0);
    }

    private int inOrderRecursivo(Nodo<K, V> actual, V[] destino, int indice) {
        if (actual == null) {
            return indice;
        }
        indice = inOrderRecursivo(actual.izquierdo, destino, indice);
        destino[indice++] = actual.valor;
        indice = inOrderRecursivo(actual.derecho, destino, indice);
        return indice;
    }

    /**
     * Imprime en consola la estructura jerárquica del árbol (útil para depuración).
     */
    public void mostrarArbol() {
        if (raiz == null) {
            System.out.println("ÁRBOL VACÍO");
            return;
        }
        System.out.println(raiz.clave);
        imprimirRamas(raiz, "");
    }

    private void imprimirRamas(Nodo<K, V> actual, String prefijo) {
        Nodo<K, V> izq = actual.izquierdo;
        Nodo<K, V> der = actual.derecho;
        if (izq != null) {
            boolean ultimo = (der == null);
            System.out.println(prefijo + (ultimo ? "└── " : "├── ") + "I: " + izq.clave);
            imprimirRamas(izq, prefijo + (ultimo ? "    " : "│   "));
        }
        if (der != null) {
            System.out.println(prefijo + "└── D: " + der.clave);
            imprimirRamas(der, prefijo + "    ");
        }
    }
}