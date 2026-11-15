/**
 * Una celda del tablero. Es inmutable (record): row y column no cambian nunca.
 * equals() y hashCode() se generan automaticamente comparando row y column.
 */
public record Position(int row, int column) {}
