public class Snake { // hacer un array circular, elimino el invariante de data[0] = Tail
    public Position[] data; // quiero guardar en data la pos de los segmentos de la serpiente
    public int size;
    public int capacity;
    public int posTail;
    public String[][] cuadricula;
    public int cuadriculaSize;
    public Position[] frutas;

    public Snake(int cuadriculaSize, Position[] frutas) {
        this.size = 2;
        this.posTail = 0;
        this.capacity = 2;
        this.data = new Position[2];
        data[0] =  new Position(cuadriculaSize/2, cuadriculaSize/2);
        data[1] =  new Position(cuadriculaSize/2, cuadriculaSize/2 + 1);
        this.cuadricula = new String[cuadriculaSize][cuadriculaSize];
        this.cuadriculaSize = cuadriculaSize;
        this.frutas = frutas;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return capacity;
    }

    public Position get(int index) { // complejidad O(1), aca como convencion es position desde el tail, dado que el tail es como la pos inicial de la serpiente
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("indice fuera del array");
        }
        return data[(posTail + index) % capacity];
    }

    public void set(int index, Position value) { // complejidad O(1)
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("indice fuera del array");
        }
        data[(posTail + index) % capacity] = value;
    }

    public void addHead(Position value) { // complejidad O(1) amortizada
        if (size == capacity) {
            resize(capacity * 2);
        }

        if (size == 0) {
            data[posTail] = value;
        } 
        else {
            data[(posTail + size) % capacity] = value;
        }

        size++;
    }

    public void removeTail() {
        if (size == 0) {
            throw new IllegalArgumentException("La capacidad no puede ser menor o igual a cero");
        }

        data[posTail] = null; // esto es para limpiar memoria
        posTail = (posTail + 1) % capacity;
        size--;

        if (size == 0) { // manejar el caso vacio
            posTail = 0;
        }
    }

    public void resize(int newCapacity) { // al hacer el resize debo copiar el orden de la snake como estaba
        Position[] temporalArray = new Position[newCapacity];
        for (int i = 0; i < size; i++) {
            temporalArray[i] = data[(posTail + i) % capacity];
        }

        data = temporalArray;
        posTail = 0;
        capacity = newCapacity;
    }

    public void actualizarCuadricula() {
        for (int i = 0; i < cuadriculaSize; i++) {
            for (int j = 0; j < cuadriculaSize; j++) {
                cuadricula[i][j] = null;
            }
        }

        for (Position fruit : frutas) {
            if (fruit != null) {
                cuadricula[fruit.x][fruit.y] = "f";
            }
        }

        for (int i = 0; i < size; i++) {
            Position segmento = data[(posTail + i) % capacity];
            if (cuadricula[segmento.x][segmento.y] == null || "f".equals(cuadricula[segmento.x][segmento.y])) {
                cuadricula[segmento.x][segmento.y] = "#";
            }
            else {
                throw new IllegalArgumentException("colision");
            }
        }
    }

    public void actualizarPosiciones(Position input) {
        Position newHead = new Position(data[(posTail + (size - 1)) % capacity].x + input.x, data[(posTail + (size - 1)) % capacity].y + input.y);
        if (newHead.x < 0 || newHead.x >= cuadriculaSize || newHead.y < 0 || newHead.y >= cuadriculaSize) {
            throw new IllegalArgumentException("La serpiente salio de la cuadricula");
        }
        if (occupiedBySnake(newHead)) {
            throw new IllegalArgumentException("La serpiente colisiono consigo misma");
        }

        boolean ateFruit = removeFruit(newHead);
        addHead(newHead);
        if (!ateFruit) {
            removeTail();
        }
    }


    public boolean occupiedBySnake(Position position) {
        for (int i = 0; i < size; i++) {
            Position segmento = data[(posTail + i) % capacity];
            if (segmento.x == position.x && segmento.y == position.y) {
                return true;
            }
        }
        return false;
    }

    public boolean removeFruit(Position position) {
        for (int i = 0; i < frutas.length; i++) {
            Position currentFruit = frutas[i];
            if (currentFruit != null && currentFruit.x == position.x && currentFruit.y == position.y) {
                frutas[i] = null;
                return true;
            }
        }
        return false;
    }
}