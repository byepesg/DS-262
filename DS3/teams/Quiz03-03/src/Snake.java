import java.util.NoSuchElementException;

public class Snake {
    private Position[] data;
    private int size;
    private int capacity;
    private int front;

    
    public Snake(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Capacidad inicial debe ser >= 1");
        }
        this.capacity = initialCapacity;
        this.data = new Position[capacity];
        this.size = 0;
        this.front = 0;

        // Inicializamos las dos posiciones de la serpiente
        addHead(new Position(0, 0)); // Cola (size pasa a 1, matriz[0][0] = 1)
        addHead(new Position(0, 1)); // Cabeza (size pasa a 2, matriz[0][1] = 1)
    }

    public int size() { return size; }
    public int capacity() { return capacity; }

    // Convierte un índice lógico (0 = cola, size-1 = cabeza) a la posición física en el arreglo circular
    private int physicalIndex(int logicalIndex) {
        if (logicalIndex < 0 || logicalIndex >= size) {
            throw new IndexOutOfBoundsException("Índice lógico inválido: " + logicalIndex);
        }
        return (front + logicalIndex) % capacity;
    }

    // Obtiene un elemento según su orden lógico
    public Position get(int logicalIndex) {
        return data[physicalIndex(logicalIndex)];
    }

    public void addHead(Position p) {
        if (size == capacity) {
            resize(2 * capacity);
        }
        int newPhysical = (front + size) % capacity;
        data[newPhysical] = p;
        size++;

        // Integración: Marca la casilla de la nueva cabeza en el tablero
        Main.matriz[p.row()][p.column()] = 1;
    }

    public Position removeTail() {
        if (size == 0) {
            throw new NoSuchElementException("Snake vacía");
        }
        Position removed = data[front];
        data[front] = null;
        front = (front + 1) % capacity;
        size--;

        // Integración: Desocupa la casilla de la cola eliminada en el tablero
        Main.matriz[removed.row()][removed.column()] = 0;
        return removed;
    }

    private void resize(int newCapacity) {
        Position[] newData = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[(front + i) % capacity];
        }
        data = newData;
        capacity = newCapacity;
        front = 0; 
    }

    // Calcula la coordenada de la nueva cabeza
    private Position computeNewHead(int direction) {
        // La cabeza siempre es el último elemento en el orden lógico (size - 1)
        Position currentHead = get(size - 1);

        int row = currentHead.row();
        int col = currentHead.column();

        if (direction == 2) {        // UP
            row = row - 1;
        } else if (direction == 3) { // RIGHT
            col = col + 1;
        } else if (direction == 4) { // DOWN
            row = row + 1;
        } else if (direction == 1) { // LEFT
            col = col - 1;
        } else {
            throw new IllegalArgumentException("Dirección no válida: " + direction + ". Debe ser 1, 2, 3 o 4.");
        }

        return new Position(row, col);
    }

    // Evalúa si la nueva cabeza colisiona con el cuerpo de la serpiente
    private boolean collision(Position newHead) {
    // 1. Verificar colisión contra los bordes del tablero
        int r = newHead.row();
        int c = newHead.column();
        if (r < 0 || r >= Main.n || c < 0 || c >= Main.n) {
            return true;
        }
    //2. Verificar colisión contra serpiente
        for (int i = 0; i < size; i++) {
            Position p = get(i);
            if (p.equals(newHead)) {
                return true;
            }
        }
        return false;
    }


    // Método principal de movimiento
    public void move(int direction) {
        Position newHead = computeNewHead(direction);

        if (collision(newHead)) {
            throw new IllegalStateException("GAME_OVER");
        }

        boolean isFood = Main.foodAt(newHead);

        addHead(newHead);

        if (isFood) {
            Main.generateFood();
        } else {

            removeTail();
        }
    }

    public void imprimirEstadoInterno() {  //sirve para imprimir el estado interno luego de cada movimiento
    System.out.println("--- ESTADO INTERNO DE SNAKE");
    System.out.println("• size: " + size);
    System.out.println("• capacity: " + capacity);
    System.out.println("• front (índice físico de la cola): " + front);
    System.out.print("• Arreglo físico (data): [ ");
    for (int i = 0; i < capacity; i++) {
        if (data[i] == null) {
            System.out.print("null ");
        } else {
            System.out.print("(" + data[i].row() + "," + data[i].column() + ") ");
        }
    }
    System.out.println("]");
    System.out.println("------------------------------------------");
}
}