package modelo.estructuras;

public class Pila<T>{
	private T[] datos;
    private int frente;
    private int cola;
    private int tamagno;
    
    public Pila(){
        this.datos=(T[])new Object[2];
        this.frente=0;
        this.cola=-1;
        this.tamagno=0;
    }
    
    public boolean estaVacia(){
        return tamagno==0;
    }
    
    public boolean estaLlena(){
        return tamagno==datos.length;
    }
    
    public void push(T dato){
        if(estaLlena()){
            redimensionar();
        }
        cola++;
        datos[cola]=dato;
        tamagno++;
    }
    
    public T pop(){
        if(estaVacia()){
            throw new IllegalStateException("No ha ingresado datos");
        }
        T salida=datos[cola];
        datos[cola]=null;
        cola--;
        tamagno--;
        System.out.println(salida);
        return salida;
    }
    
    public void redimensionar(){
        int capacidadNueva=datos.length*2;
        T[] nuevosDatos=(T[]) new Object[capacidadNueva];
        for(int i=0;i<tamagno;i++){
            nuevosDatos[i]=datos[(frente+i)%datos.length];
        }
        this.datos=nuevosDatos;
        this.cola=tamagno-1;
    }
    
    public T tope(){
        T salida=datos[cola];
        System.out.println(salida);
        return salida;
    }
    
    public T getDato(int indice){
        T salida=datos[indice];
        return salida;
    }
}
