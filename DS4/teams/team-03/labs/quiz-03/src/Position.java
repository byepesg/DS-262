public record Position(int row, int column) {} //Una clase con atributos constantes

/* A lo que equivale como clase!!!

public final class Position {
    private final int row;
    private final int column;

    // Constructor automático
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    // Métodos de acceso (nota que no usan la palabra "get")
    public int row() { return row; }
    public int column() { return column; }

    // Métodos estándar autogenerados
    @Override public boolean equals(Object o) { // compara por valores  
        
    }
    @Override public int hashCode() { // genera hash basado en valores  
        
    }
    @Override public String toString() { // devuelve "Position[row=..., column=...]"  
        
    }
}
*/