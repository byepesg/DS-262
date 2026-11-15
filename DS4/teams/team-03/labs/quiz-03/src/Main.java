/******************************************************************************

Welcome to GDB Online.
GDB online is an online compiler and debugger tool for C, C++, Python, Java, PHP, Ruby, Perl,
C#, OCaml, VB, Swift, Pascal, Fortran, Haskell, Objective-C, Assembly, HTML, CSS, JS, SQLite, Prolog.
Code, Compile, Run and Debug online from anywhere in world.

*******************************************************************************/
import java.util.Random;
import java.util.Scanner;

public class Main {
    static int filasTablero=10;
    static int columnasTablero=10;
    
    static Position food = new Position(2,1);
    static Snake snake = new Snake(new Position(0,0),new Position(0,1));
    
    static Scanner scanner = new Scanner(System.in);
    static boolean ejecutando = true;
        
    
    public static void main(String[] args) {
        
        
        
        //---------------------------Prueba de la pregunta 3---------------------------------------
        System.out.println("-----------------Traza de la preguta 3-----------------------");
        System.out.println();
        generateFood();
        snake.print("Inicialización");
        
        //N1 no come
        snake.addHead(new Position(0,2));
        snake.removeTail();
        snake.print("Después de N1, no come");
        
        //N2 come
        snake.addHead(new Position(0,3));
        snake.print("Después de N2, come");
        
        //N3 no come
        snake.addHead(new Position(0,4));
        snake.removeTail();
        snake.print("Después de N3, no come");
        
        //N4 no come
        snake.addHead(new Position(0,5));
        snake.removeTail();
        snake.print("Después de N4, no come");
        
        //N5 come
        snake.addHead(new Position(0,6));
        snake.print("Después de N5, come");
        
        //N6 no come
        snake.addHead(new Position(0,7));
        snake.removeTail();
        snake.print("Después de N6, no come");
        
        //---------------------------------Juego interactivo----------------------------------------
        
        System.out.println();
        System.out.println("-----------------Juego interactivo-----------------------");
        System.out.println("Te puedes mover con WASD y salir con q, despues de cada movimiento se imprime el estado del snake");
        
        snake = new Snake(new Position(0,0),new Position(0,1));
        while(ejecutando){
            
            imprimirTablero();
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("q")) { //para dejar de jugar
                ejecutando = false;
            }else if(input.equals("w")){
                move("up");
            }else if(input.equals("a")){
                move("left");
            }else if(input.equals("s")){
                move("down");
            }else if(input.equals("d")){
                move("right");
            }else{
                System.out.println("Ingresaste un input inválido!");
            }
        }
        
        

    }
    
    public static void move(String direccion){
        
        Position newHead = computeNewHead(direccion);
        
        if(collision(newHead)){
            throw new IllegalStateException("Perdiste :(");
        }
        
        snake.addHead(newHead);
        
        if(foodAt(newHead)){
            generateFood();
        }else{
            snake.removeTail();
        }
        
        snake.print(direccion);
    }
    
    public static void imprimirTablero(){
        System.out.println();
        
        for(int i=0; i<filasTablero;i++){ //iteración de filas tablero
            for(int j=0; j<columnasTablero;j++){ //iteración de columnas tablero
                boolean posicionEncontrada=false; //para saber si se dibujo el snake o no
                for(int k=0;k<snake.size();k++){ //iteracion sobre el snake
                    if(snake.get(k).row()==i && snake.get(k).column()==j){
                        System.out.print("1 ");
                        posicionEncontrada=true;
                        break;
                    }
                }
                if(!posicionEncontrada){
                    if(food.row()==i && food.column()==j){
                        System.out.print("2 ");
                    }
                    else {
                        System.out.print("0 ");
                    }
                }
            }
            System.out.println();
        }
        
        System.out.println();
    }
    
    public static boolean collision(Position posicion){
        for(int i=0;i<snake.size();i++){ //iteracion sobre el snake
            if(snake.get(i).equals(posicion)){
                return true;
            }
        }
        if(posicion.row()==-1 || posicion.row()==filasTablero || posicion.column()==-1 || posicion.column()==columnasTablero){
            return true;
        }
        return false;
    }
    
    public static Position computeNewHead(String direccion){
        Position cabeza = snake.get(snake.size()-1);
        if(direccion=="up"){
            return new Position(cabeza.row()-1,cabeza.column());
        }
        else if(direccion=="down"){
            return new Position(cabeza.row()+1,cabeza.column());
        }
        else if(direccion=="left"){
            return new Position(cabeza.row(),cabeza.column()-1);
        }
        else if(direccion=="right"){
            return new Position(cabeza.row(),cabeza.column()+1);
        }else{
            throw new IllegalArgumentException("Direccion invalida");
        }
        
    }
    
    public static boolean foodAt(Position posicion){
        if(posicion.equals(food)){
            return true;
        }else{
            return false;
        }
    }
    
    public static void generateFood(){
        Random aleatorio = new Random();
        boolean buscandoPosicion = true;
        Position newFood = new Position(0,0);
        
        while(buscandoPosicion){
            
            int aleatorioFilas = aleatorio.nextInt(filasTablero); //saca de 0 a x-1
            int aleatorioColumnas = aleatorio.nextInt(columnasTablero);
            newFood= new Position(aleatorioFilas,aleatorioColumnas);
            
            for(int i=0;i<snake.size();i++){ //iteracion sobre el snake para nocrearla ahí
                if(snake.get(i).equals(newFood)){
                    buscandoPosicion=true;
                    break;
                }else{
                    buscandoPosicion=false;
                }
            }
        }
        food = newFood;
        
    }
}
