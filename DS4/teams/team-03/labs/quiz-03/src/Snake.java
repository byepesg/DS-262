public class Snake {
    private Position[] cuerpo;
    private int size;
    private int capacity;
    private int tail;
    public boolean resizeFlag; // para saber si se hizo resize

    public Snake(Position posicion1, Position posicion2) {
        this.capacity = 2;
        this.size = 0;
        this.tail = 0;
        this.cuerpo = new Position[2];
        
        addHead(posicion1);
        addHead(posicion2);
    }

    public int size() { 
        return size; 
    }
    
    public int capacity() { 
        return capacity; 
    }

    public Position get(int indiceLogico) {
        if (indiceLogico < 0 || indiceLogico >= size) {
            throw new IndexOutOfBoundsException("Índice inválido");
        }
        
        return cuerpo[(tail+indiceLogico)%capacity];
    }

    public void addHead(Position p) {
        resizeFlag = false;
        if (size == capacity) { //se ejecuta en su primer movimiento cuando está llena
            resize(capacity * 2);
            resizeFlag = true; //para saber si se hizo resize!
        }
        cuerpo[(tail+size)%capacity] = p;
        size++;
    }

    public Position removeTail() {
        if (size == 0) {
            throw new IllegalStateException("Serpiente vacía");
        }
        Position oldTail = cuerpo[tail];
        cuerpo[tail] = null; //para que lo agarre el garbage collector
        tail = (tail+1) % capacity;
        size--;
        return oldTail;
    }

    private void resize(int newcapacity) {
        Position[] newCuerpo = new Position[newcapacity];
        
        for (int i =0; i < size; i++) { //copias
            newCuerpo[i] = cuerpo[(tail+i)%capacity];
        }
        
        this.cuerpo = newCuerpo;
        this.capacity = newcapacity;
        this.tail = 0; //pq lo reorganizamos
    }

    public void print(String movimiento) {  //para mostrar el estado despues de cada movimiento
        
        System.out.println("Movimiento: " + movimiento);
        
        for(int i = 0; i < capacity; i++){
            if(cuerpo[i]==null){
                System.out.print("[ , ]");
            }else{
                System.out.print("["+cuerpo[i].row()+","+cuerpo[i].column()+"]");
            }
            System.out.print(" ");
        }
        
        System.out.println();
        System.out.println("Size: "+size);
        System.out.println("Capacity: "+capacity);
        System.out.println("Tail: "+tail);
        if(resizeFlag){
            System.out.println("Sí hubo un resize!");
        }else{
            System.out.println("No hubo un resize :( ");
        }
        
        System.out.println("-------------------------------------------");
    }
}