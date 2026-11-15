public class Position {
    
    private final int fila;
    private final int columna;

    public Position(int fila, int columna) {
        
        this.fila = fila;
        this.columna = columna;
        
    }

    public int getFila() {
        
        return this.fila;
        
    }

    public int getColumna() {
        
        return this.columna;
        
    }

    @Override
    public boolean equals(Object obj) {
        
        if (this == obj){
            
            return true;
            
        } 
        
        if (obj == null || getClass() != obj.getClass()){
        
            return false;
            
        } 
        
        Position other = (Position) obj;
        
        return this.fila == other.fila && this.columna == other.columna;
    }


    @Override
    public int hashCode() {
        
        return 31 * fila + columna;
        
    }


    @Override
    public String toString() {
        
        return "(" + fila + ", " + columna + ")";
        
    }
}