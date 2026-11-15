package modelo.estructuras;

public class Cola<T>{
	private T[] datos;
    private int frente;
    private int cola;
    private int tamagno;
    
    public Cola(){
        this.datos=(T[])new Object[2];
        this.frente=0;
        this.cola=0;
        this.tamagno=0;
    }
    
    public boolean estaVacia(){
        return tamagno==0;
    }
    
    public boolean estaLlena(){
        return tamagno==datos.length;
    }
    
    public void encolar(T dato){
        if(estaLlena()){
            redimensionar();
        }
        datos[cola]=dato;
        cola=(cola+1)%datos.length;
        tamagno++;
    }
    
    public T desencolar(){
        if(estaVacia()){
            throw new IllegalStateException("No ha ingresado datos");
        }
        T salida=datos[frente];
        datos[frente]=null;
        frente=(frente+1)%datos.length;
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
        this.frente=0;
        this.cola=tamagno;
    }
    
    public T tope(){
        T salida=datos[frente];
        System.out.println(salida);
        return salida;
    }
    
    public T getDato(int indice){
        T salida=datos[indice];
        return salida;
    }
}
