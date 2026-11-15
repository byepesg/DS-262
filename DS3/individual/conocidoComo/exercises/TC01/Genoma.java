public class Genoma {
 //Implementación de un stack para el historial de generaciones 
    private class NodoStack {
        int[][] matriz;
        NodoStack siguiente;

        public NodoStack(int[][] matriz) {
            this.matriz = matriz;
            this.siguiente = null;
        }
    }

    private NodoStack top;
    private int size;

    public Genoma() {
        this.top = null;
        this.size = 0;
    }

    public void push(int[][] matrizActual) {
        int n = matrizActual.length;
        int[][] copia = new int[n][n];
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                copia[i][j] = matrizActual[i][j];
            }
        }
        
        NodoStack nuevo = new NodoStack(copia);
        nuevo.siguiente = top;
        top = nuevo;
        size++;
    }

    // Recuperar generación anterior
    public int[][] pop() {
        if (top == null) {
            return null;
        }
        int[][] matrizRetorno = top.matriz;
        top = top.siguiente;
        size--;
        return matrizRetorno;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}