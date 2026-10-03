import java.util.NoSuchElementException;

/**
 * Pregunta 3 - Serpiente sobre un BUFFER CIRCULAR gestionado directamente.
 *
 * Estado (un arreglo nativo + un numero constante de variables):
 *   data     : arreglo nativo Position[] (no delega en DynamicArray).
 *   tail     : indice FISICO donde esta el elemento logico 0 (la cola, p0).
 *   size     : numero de posiciones logicas almacenadas (k).
 *   capacity : longitud de data.
 *   La cabeza NO se guarda: esta en phys(size - 1).
 *
 * Funcion logico -> fisico:   phys(i) = (tail + i) mod capacity,   0 <= i < size.
 *
 * Invariantes:
 *   J1. 1 <= capacity == data.length  y  0 <= size <= capacity
 *   J2. 0 <= tail < capacity
 *   J3. Para 0 <= i < size:  data[phys(i)] == p_i   (orden logico cola -> cabeza)
 *   J4. Las capacity - size celdas restantes, phys(size), ..., phys(capacity-1), son null.
 *   J5. capacity nunca disminuye.
 *
 * Complejidad: addHead O(1) amortizado (O(k) worst-case solo al redimensionar),
 *              removeTail O(1) worst-case, get O(1) worst-case.
 */
public final class Snake {
    private Position[] data;
    private int tail;
    private int size;
    private int capacity;

    // Instrumentacion (NO forma parte de la representacion): sirve para verificar la Pregunta 4.
    private int resizeCount = 0;
    private long copyCount = 0;

    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("initialCapacity debe ser >= 1");
        }
        this.capacity = initialCapacity;
        this.data = new Position[initialCapacity];
        this.tail = 0;
        this.size = 0;
    }

    /** Estado inicial del quiz: size = capacity = 2, cola = tailPos, cabeza = headPos. */
    public Snake(Position tailPos, Position headPos) {
        this(2);
        addHead(tailPos);
        addHead(headPos);
    }

    // ------------------------------------------------------------------ mapeo
    /** Transforma un indice logico en su indice fisico. Siempre en [0, capacity). */
    private int phys(int logicalIndex) {
        return (tail + logicalIndex) % capacity;   // tail + i < 2*capacity: no hay overflow
    }

    // ------------------------------------------------------ operaciones P3 c)
    /** O(1) amortizado. Escribe en la primera celda libre despues de la cabeza. */
    public void addHead(Position p) {
        if (size == capacity) {
            resize(nextCapacity(capacity));
        }
        data[phys(size)] = p;       // phys(size) esta libre por J4
        size++;
    }

    /** O(1) worst-case: no desplaza nada, solo avanza el indice tail. */
    public Position removeTail() {
        if (size == 0) {
            throw new NoSuchElementException("removeTail sobre una serpiente vacia");
        }
        Position old = data[tail];
        data[tail] = null;                  // mantiene J4
        tail = (tail + 1) % capacity;       // el nuevo p0 era el antiguo p1
        size--;
        return old;
    }

    /** O(1) worst-case. */
    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("indice " + logicalIndex + " fuera de [0, " + size + ")");
        }
        return data[phys(logicalIndex)];
    }

    // --------------------------------------------------------------- P3 d)
    /** Politica de crecimiento centralizada (facil de cambiar en la sustentacion). */
    private int nextCapacity(int c) {
        if (c > Integer.MAX_VALUE / 2) {
            throw new IllegalStateException("capacidad maxima alcanzada");
        }
        return 2 * c;
        // Politica A: return c + 1;
        // Politica C: return Math.max(c + 1, (int) Math.ceil(1.5 * c));
    }

    /**
     * "Desenrolla" el circulo: el elemento logico i pasa a newData[i].
     * Despues del resize tail = 0 y el orden logico se conserva exactamente.
     */
    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[phys(i)];     // se usa la capacidad VIEJA en phys
            copyCount++;
        }
        data = newData;
        capacity = newCapacity;
        tail = 0;
        resizeCount++;
    }

    // ------------------------------------------------------------- juego
    /** O(k): recorre la serpiente. Considera ocupada la cola actual (segun el enunciado). */
    public boolean occupies(Position p) {
        for (int i = 0; i < size; i++) {
            if (data[phys(i)].equals(p)) return true;
        }
        return false;
    }

    /**
     * MOVE del enunciado. Devuelve false si hay GAME_OVER.
     * Orden obligatorio: addHead -> (removeTail si no come).
     */
    public boolean move(Position newHead, boolean eats) {
        if (occupies(newHead)) {
            return false;               // GAME_OVER
        }
        addHead(newHead);
        if (!eats) {
            removeTail();
        }
        return true;
    }

    // --------------------------------------------------- consultas / traza
    public int size()          { return size; }
    public int capacity()      { return capacity; }
    public int tailIndex()     { return tail; }
    public int headIndex()     { return size == 0 ? -1 : phys(size - 1); }
    public Position head()     { return get(size - 1); }
    public Position tailPos()  { return get(0); }
    public int resizeCount()   { return resizeCount; }
    public long copyCount()    { return copyCount; }

    /** Solo para imprimir la traza: contenido de una celda fisica (puede ser null). */
    public Position rawCell(int physicalIndex) {
        return data[physicalIndex];
    }
}
