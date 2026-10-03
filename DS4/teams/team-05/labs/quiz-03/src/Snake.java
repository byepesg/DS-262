package QUIZ_3;
import java.util.NoSuchElementException;

// El "record" es una estructura simple de Java para guardar datos inmutables.
record Position(int row, int column) {}

public class Snake {
    // 3a. Variables de estado adicionales:
    private Position[] data; // El arreglo físico nativo en memoria
    private int size;        // Número real de celdas ocupadas por la serpiente
    private int capacity;    // Tamaño total de casilleros disponibles
    private int tailFisico;  // Índice físico real donde está sentada la cola
    private int headFisico;  // Índice físico real donde está sentada la cabeza

    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad inicial debe ser al menos 1");
        }
        this.capacity = initialCapacity;
        this.size = 0;
        this.data = new Position[this.capacity];
        this.tailFisico = 0;
        this.headFisico = -1; // -1 significa que aún no hay cabeza
    }

    // 3c. Implementación de get(logicalIndex)
    public Position get(int logicalIndex) {
        // Validamos siempre contra "size", nunca contra "capacity"
        if (logicalIndex < 0 || logicalIndex >= this.size) {
            throw new IndexOutOfBoundsException("Índice fuera del rango lógico");
        }
        // 3b. Transformación de índice lógico a físico
        int indexFisico = (this.tailFisico + logicalIndex) % this.capacity;
        return this.data[indexFisico];
    }

    // 3c. Implementación de addHead
    public void addHead(Position p) {
        // Si el tamaño lógico alcanza la capacidad, la serpiente no cabe y hay que duplicar
        if (this.size == this.capacity) {
            resize(); // Esto garantiza el costo amortizado O(1)
        }
        
        // Movemos la flecha de la cabeza hacia adelante circularmente
        this.headFisico = (this.headFisico + 1) % this.capacity;
        
        // Escribimos la nueva cabeza en el arreglo físico
        this.data[this.headFisico] = p;
        this.size++;
    }

    // 3c. Implementación de removeTail
    public Position removeTail() {
        if (this.size == 0) {
            // Validamos que no intenten borrar si está vacía
            throw new NoSuchElementException("No se puede eliminar la cola de una serpiente vacía");
        }
        
        Position oldTail = this.data[this.tailFisico]; // Guardamos la cola para devolverla
        this.data[this.tailFisico] = null; // Borramos el rastro físico (buena práctica)
        
        // Movemos la flecha de la cola hacia adelante circularmente
        this.tailFisico = (this.tailFisico + 1) % this.capacity;
        this.size--; // Reducimos el tamaño lógico
        
        return oldTail; // Costo O(1) worst-case garantizado
    }

    // 3d. Implementación de resize()
    private void resize() {
        int newCapacity = this.capacity * 2; // Duplicamos capacidad
        Position[] newData = new Position[newCapacity];
        
        // Copiamos los elementos preservando el orden lógico estricto
        for (int i = 0; i < this.size; i++) {
            // Usamos nuestro propio get() que ya sabe cómo traducir de lógico a físico
            newData[i] = this.get(i); 
        }
        
        // Reasignamos los punteros al nuevo arreglo desenrollado
        this.data = newData;
        this.capacity = newCapacity;
        this.tailFisico = 0; // Al desenrollar, la cola siempre queda en el índice 0 físico
        this.headFisico = this.size - 1; // Y la cabeza al final lógico
    }
    
    // Método auxiliar 
    public void printState(String mov, boolean resized) {
        System.out.println("--- " + mov + " ---");
        System.out.print("Físico: [");
        for (int i = 0; i < this.capacity; i++) {
            if (this.data[i] == null) {
                System.out.print("null");
            } else {
                // Usamos el parámetro "row" del record como si fuera su código ASCII
                System.out.print((char) this.data[i].row()); 
            }
            if (i < this.capacity - 1) System.out.print(", ");
        }
        System.out.println("]");
        System.out.println("tail: " + this.tailFisico + ", head: " + this.headFisico + ", size: " + this.size + ", cap: " + this.capacity);
        System.out.println("Resize: " + (resized ? "SÍ" : "NO") + "\n");
    }
}