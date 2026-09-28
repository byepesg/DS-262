// Implementación eficiente del juego Snake 
public class Snake {
    private Position[] data; // Arreglo nativo para almacenar las posiciones
    private int head;        // Índice físico donde reside la cabeza (último elemento insertado)
    private int tail;        // Índice físico donde reside la cola (primer elemento insertado)
    private int size;        // Longitud actual de la serpiente
    private int capacity;    // Capacidad del arreglo físico

    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser >= 1");
        }
        this.capacity = initialCapacity;
        this.data = new Position[initialCapacity];
        this.size = 0;
        this.head = -1; // Estado especial: estructura vacía
        this.tail = 0;
    }

    public int size() { 
        return size; }

    public int capacity() { 
        return capacity; }

    public int getHeadIndex() { 
        return head; }

    public int getTailIndex() { 
        return tail; }

    public Position[] getData() { 
        return data; }
        
    // Inserta una nueva cabeza en O(1) amortizado
    public void addHead(Position p) {
        // Si el arreglo circular está lleno, duplica su capacidad
        if (size == capacity) {
            resize(capacity * 2);
        }
        
        // Si la serpiente está vacía, la cabeza y cola inician en el índice 0
        if (size == 0) {
            head = 0;
            tail = 0;
        } else {
            // Aritmética modular: avanza la cabeza en forma de anillo/círculo
            head = (head + 1) % capacity;
        }
        
        data[head] = p; // Coloca la nueva cabeza en su casilla física
        size++;          // Incrementa el tamaño
    }

    // Elimina la cola en O(1) estricto (Worst-Case)
    public Position removeTail() {
        if (size == 0) {
            throw new IllegalStateException("No se puede eliminar la cola de una serpiente vacia");
        }
        
        Position removed = data[tail]; // Guarda la posición de la cola a retornar
        data[tail] = null;            // Limpieza de memoria (Garbage Collection)
        
        if (size == 1) {
            // Si solo había un elemento, se reinicia al estado vacío
            head = -1;
            tail = 0;
        } else {
            // Aritmética modular: avanza el índice de la cola en forma de anillo/círculo
            // ¡ESTE ES EL TRUCO!: No se mueven los elementos, solo se desplaza el puntero tail.
            tail = (tail + 1) % capacity;
        }
        
        size--; // Decrementa el tamaño
        return removed;
    }

    // Mapeo formal: convierte un índice lógico i (0 = cola, size-1 = cabeza) a su posición física
    public Position get(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("Índice lógico fuera de rango: " + logicalIndex);
        }
        // Fórmula de transformación lógica a física usando aritmética modular
        int physicalIndex = (tail + logicalIndex) % capacity;
        return data[physicalIndex];
    }

    // Redimensionamiento O(n): Desenrolla el buffer circular
    public void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        
        // Copia ordenando los elementos desde la cola (0) hasta la cabeza (size-1)
        for (int i = 0; i < size; i++) {
            int physicalIndex = (tail + i) % capacity; // Lee desde el desorden circular
            newData[i] = data[physicalIndex];           // Escribe de forma lineal alineada en el nuevo
        }
        
        this.data = newData;
        this.tail = 0;        // La cola vuelve a quedar en el índice 0
        this.head = size - 1; // La cabeza queda alineada al final del bloque ocupado
        this.capacity = newCapacity;
    }
}