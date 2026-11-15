import java.util.NoSuchElementException;

public class Snake {

    // Arreglo nativo que almacena físicamente las posiciones de la serpiente
    private Position[] data;
    // Índice físico que apunta a la cabeza de la serpiente (el último elemento agregado)
    private int head;
    // Índice físico que apunta a la cola de la serpiente (el elemento más antiguo)
    private int tail;
    // Cantidad actual de elementos en la serpiente (longitud lógica)
    private int size;
    // Tamaño máximo actual del arreglo físico
    private int capacity;

    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial no es correcta");
        }

        this.capacity = initialCapacity;
        this.size = 0;
        this.tail = 0;
        this.head = 0;
        this.data = new Position[initialCapacity];
    }

    /**
     * Convierte un índice lógico (donde 0 es la cola y size-1 es la cabeza)
     * en un índice físico dentro del arreglo circular.
     */
    private int getIndexFisico(int index) {
        // El módulo (%) asegura que si el índice supera la capacidad, vuelva a empezar desde 0 (circularidad)
        return (this.tail + index) % this.capacity;
    }

    public Position get(int index) {
        // Valida que el índice lógico solicitado exista dentro de la serpiente
        if(index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        return this.data[getIndexFisico(index)];
    }

    /**
     * Agrega una nueva posición a la cabeza de la serpiente.
     * Complejidad: O(1) amortizado.
     */
    public void addHead(Position p) {
        if (p == null) {
            throw new IllegalArgumentException("La posición no puede ser null");
        }

        // Si el arreglo está lleno, duplicamos su capacidad manteniendo el orden lógico
        if (this.size == this.capacity) {
            resize();
        }

        if (this.size == 0) {
            // Caso base: la serpiente está vacía
            this.tail = 0;
            this.head = 0;
            this.data[this.head] = p;
        } else {
            // Se calcula la posición física adyacente al final de la serpiente usando aritmética modular
            int newHeadPosition = (this.tail + this.size) % this.capacity;
            this.data[newHeadPosition] = p;
            this.head = newHeadPosition;
        }
        this.size++;
    }

    /**
     * Elimina la cola de la serpiente (el elemento más antiguo).
     * Complejidad: O(1) worst-case.
     */
    public Position removeTail() {
        if (this.size == 0) {
            throw new NoSuchElementException("Está vacía");
        }

        Position removed = this.data[this.tail];
        this.data[this.tail] = null; // Liberamos la referencia para el recolector de basura

        // Movemos el apuntador de la cola una posición hacia adelante de forma circular
        this.tail = (this.tail + 1) % this.capacity;
        this.size--;

        // Si la serpiente quedó vacía, reiniciamos los apuntadores
        if (this.size == 0) {
            this.tail = 0;
            this.head = 0;
        }
        return removed;
    }

    /**
     * Duplica la capacidad del arreglo y reorganiza los elementos para que queden
     * alineados desde el índice físico 0, preservando el orden lógico.
     */
    private void resize() {
        int newCapacity = this.capacity * 2;
        Position[] newData = new Position[newCapacity];

        // Copia los elementos secuencialmente; get(i) ya resuelve la posición lógica a física
        for (int i = 0; i < this.size; i++) {
            newData[i] = this.get(i);
        }

        this.data = newData;
        this.tail = 0; // La nueva cola queda alineada al inicio del nuevo arreglo
        this.head = this.size - 1; // La cabeza queda al final de los elementos copiados
        this.capacity = newCapacity;
    }

    public int size() {
        return this.size;
    }

    public int capacity() {
        return this.capacity;
    }


    public void imprimirEstado() { //Metodo para corroborar el estado de Snake
        System.out.println("  Size actual: " + this.size);
        System.out.println("  Capacity actual: " + this.capacity);
        System.out.println("  Head (índice físico): " + this.head);
        System.out.println("  Tail (índice físico): " + this.tail);

        System.out.print("  Arreglo físico completo: [");
        for (int i = 0; i < this.capacity; i++) {
            if (this.data[i] == null) {
                System.out.print("null");
            } else {
                System.out.print("(" + this.data[i].row() + "," + this.data[i].column() + ")");
            }
            if (i < this.capacity - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }
}