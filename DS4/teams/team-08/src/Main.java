import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Estudiante {
    private final String id;
    private final String nombre;
    private double puntaje;

    Estudiante(String id, String nombre, double puntaje) {
        this.id = id;
        this.nombre = nombre;
        this.puntaje = puntaje;
    }

    String getId() { return id; }
    String getNombre() { return nombre; }
    double getPuntaje() { return puntaje; }

    void setPuntaje(double puntaje) { this.puntaje = puntaje; }

    int comparar(Estudiante otro) {
        int c = Double.compare(this.puntaje, otro.puntaje);
        if (c != 0) {
            return c;
        }
        return this.id.compareTo(otro.id);
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (puntaje " + puntaje + ")";
    }
}

// ======================================================================= Nodo
class Nodo {
    Estudiante dato;
    Nodo izq;
    Nodo der;

    Nodo(Estudiante dato) {
        this.dato = dato;
    }
}

// ======================================================================= ArbolBinarioBusqueda
class ArbolBinarioBusqueda {
    private Nodo raiz;


    void insertar(Estudiante e) {
        raiz = insertar(raiz, e);
    }

    private Nodo insertar(Nodo n, Estudiante e) {
        if (n == null) {
            return new Nodo(e);
        }
        if (e.comparar(n.dato) < 0) {
            n.izq = insertar(n.izq, e);
        } else {
            n.der = insertar(n.der, e);
        }
        return n;
    }


    void eliminar(Estudiante e) {
        raiz = eliminar(raiz, e);
    }

    private Nodo eliminar(Nodo n, Estudiante e) {
        if (n == null) {
            return null;
        }
        int c = e.comparar(n.dato);
        if (c < 0) {
            n.izq = eliminar(n.izq, e);
        } else if (c > 0) {
            n.der = eliminar(n.der, e);
        } else {
            // Encontramos el nodo a borrar
            if (n.izq == null) {
                return n.der;
            }
            if (n.der == null) {
                return n.izq;
            }
            // Tiene dos hijos: lo reemplazamos por el menor del subarbol derecho
            Nodo menor = n.der;
            while (menor.izq != null) {
                menor = menor.izq;
            }
            n.dato = menor.dato;
            n.der = eliminar(n.der, menor.dato);
        }
        return n;
    }

    // Recorrido inorden (izquierda, nodo, derecha): de menor a mayor puntaje.
    ArrayList<Estudiante> enOrden() {
        ArrayList<Estudiante> lista = new ArrayList<>();
        enOrden(raiz, lista);
        return lista;
    }

    private void enOrden(Nodo n, ArrayList<Estudiante> lista) {
        if (n == null) {
            return;
        }
        enOrden(n.izq, lista);
        lista.add(n.dato);
        enOrden(n.der, lista);
    }

    // Altura del arbol (0 si esta vacio).
    int altura() {
        return altura(raiz);
    }

    private int altura(Nodo n) {
        if (n == null) {
            return 0;
        }
        return 1 + Math.max(altura(n.izq), altura(n.der));
    }
}

// ======================================================================= SistemaResidencias
class SistemaResidencias {
    private final HashMap<String, Estudiante> porId = new HashMap<>();
    private final ArbolBinarioBusqueda arbol = new ArbolBinarioBusqueda();
    private int cupos = 0;

    // Registra un estudiante nuevo.
    void registrar(String id, String nombre, double puntaje) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacio.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        validarPuntaje(puntaje);
        id = id.trim();
        if (porId.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe un estudiante con el ID " + id + ".");
        }
        Estudiante e = new Estudiante(id, nombre.trim(), puntaje);
        porId.put(id, e);   
        arbol.insertar(e);  
    }

    // Define cuantos cupos hay. 
    void definirCupos(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Los cupos no pueden ser negativos.");
        }
        cupos = n;
    }

    int getCupos() {
        return cupos;
    }

    // Acceso directo por ID. promedio. Devuelve null si no existe.
    Estudiante buscar(String id) {
        return porId.get(id.trim());
    }

    // Todos los estudiantes de menor a mayor puntaje.
    ArrayList<Estudiante> listarOrdenado() {
        return arbol.enOrden();
    }

    // Los primeros N estudiantes (menor puntaje) obtienen residencia.
    ArrayList<Estudiante> listaAsignados() {
        ArrayList<Estudiante> todos = arbol.enOrden();
        int corte = Math.min(cupos, todos.size());
        return new ArrayList<>(todos.subList(0, corte));
    }

    // El resto queda en lista de espera.
    ArrayList<Estudiante> listaEnEspera() {
        ArrayList<Estudiante> todos = arbol.enOrden();
        int corte = Math.min(cupos, todos.size());
        return new ArrayList<>(todos.subList(corte, todos.size()));
    }

    // Cambia el puntaje y reubica al estudiante.
    // Primero se saca del arbol (con el puntaje viejo), se cambia el puntaje
    // y se vuelve a insertar para que quede en su nueva posicion.
    void modificarPuntaje(String id, double nuevoPuntaje) {
        Estudiante e = buscar(id);
        if (e == null) {
            throw new IllegalArgumentException("No existe un estudiante con el ID " + id + ".");
        }
        validarPuntaje(nuevoPuntaje);
        arbol.eliminar(e);
        e.setPuntaje(nuevoPuntaje);
        arbol.insertar(e);
    }

    // Elimina de la tabla hash y del arbol.
    Estudiante eliminar(String id) {
        Estudiante e = buscar(id);
        if (e == null) {
            throw new IllegalArgumentException("No existe un estudiante con el ID " + id + ".");
        }
        arbol.eliminar(e);
        porId.remove(e.getId());
        return e;
    }

    int cantidad() {
        return porId.size();
    }

    // Altura del arbol (sirve para analizar el costo real de las operaciones).
    int alturaArbol() {
        return arbol.altura();
    }

    private void validarPuntaje(double p) {
        if (Double.isNaN(p) || Double.isInfinite(p) || p < 0) {
            throw new IllegalArgumentException("El puntaje debe ser un numero mayor o igual a 0.");
        }
    }
}

// ======================================================================= Main
public class Main {

    static SistemaResidencias sistema = new SistemaResidencias();
    static Scanner sc = new Scanner(System.in);

    // ---------------------------------------------------------- impresion
    static void imprimirLista(String titulo, ArrayList<Estudiante> lista) {
        System.out.println("-- " + titulo + " --");
        if (lista.isEmpty()) {
            System.out.println("   (vacio)");
            return;
        }
        for (int i = 0; i < lista.size(); i++) {
            System.out.println((i + 1) + ". " + lista.get(i));
        }
    }

    static void mostrarAsignacion(SistemaResidencias s) {
        System.out.println("Cupos disponibles: " + s.getCupos());
        imprimirLista("CON RESIDENCIA", s.listaAsignados());
        imprimirLista("SIN RESIDENCIA (lista de espera)", s.listaEnEspera());
    }

    // ---------------------------------------------------------- opciones del menu
    static void registrar() {
        System.out.print("ID: ");
        String id = sc.nextLine();
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        double puntaje = leerNumero("Puntaje socioeconomico: ");
        sistema.registrar(id, nombre, puntaje);
        System.out.println("Registrado: " + sistema.buscar(id));
    }

    static void definirCupos() {
        System.out.print("Cupos disponibles: ");
        try {
            sistema.definirCupos(Integer.parseInt(sc.nextLine().trim()));
            System.out.println("Cupos definidos: " + sistema.getCupos());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Debe escribir un numero entero.");
        }
    }

    static void buscar() {
        System.out.print("ID a buscar: ");
        Estudiante e = sistema.buscar(sc.nextLine());
        if (e == null) {
            System.out.println("No existe ese estudiante.");
        } else {
            System.out.println(e);
        }
    }

    static void modificarPuntaje() {
        System.out.print("ID del estudiante: ");
        String id = sc.nextLine();
        double nuevo = leerNumero("Nuevo puntaje: ");
        sistema.modificarPuntaje(id, nuevo);
        System.out.println("Actualizado: " + sistema.buscar(id));
    }

    static void eliminar() {
        System.out.print("ID del estudiante: ");
        System.out.println("Eliminado: " + sistema.eliminar(sc.nextLine()));
    }

    static double leerNumero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(sc.nextLine().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Debe escribir un numero (ej. 25.5).");
        }
    }

    // -----------Seccion de demostracion con datos quemados
    static void titulo(String t) {
        System.out.println();
        System.out.println("==== " + t + " ====");
    }

    // Usa su propio sistema, asi no mezcla los datos con los del menu.
    static void demostracion() {
        SistemaResidencias s = new SistemaResidencias();

        titulo("1. Registro de estudiantes");
        s.registrar("1001", "Ana Maria Torres", 35.0);
        s.registrar("1002", "Carlos Perez Gomez", 12.5);
        s.registrar("1003", "Laura Gomez Rojas", 48.0);
        s.registrar("1004", "Juan Sebastian Diaz", 12.5); // mismo puntaje que 1002
        s.registrar("1005", "Valentina Ruiz", 60.0);
        s.registrar("1006", "Andres Felipe Mora", 22.0);
        s.registrar("1007", "Daniela Castro", 5.0);
        s.registrar("1008", "Miguel Angel Rey", 41.5);
        System.out.println("Registrados: " + s.cantidad());
        try {
            s.registrar("1003", "Repetido", 10);
        } catch (IllegalArgumentException ex) {
            System.out.println("Intento de ID repetido -> " + ex.getMessage());
        }

        titulo("2. Definir cupos");
        s.definirCupos(3);
        System.out.println("Cupos: " + s.getCupos());

        titulo("3. Acceso directo por ID");
        System.out.println("Buscar 1006 -> " + s.buscar("1006"));
        Estudiante noExiste = s.buscar("9999");
        System.out.println("Buscar 9999 -> " + (noExiste == null ? "no existe" : noExiste));

        titulo("4. Listado ordenado por puntaje");
        imprimirLista("Estudiantes", s.listarOrdenado());

        titulo("5. Asignacion de 3 cupos");
        mostrarAsignacion(s);

        titulo("6. Modificar puntaje: 1005 baja de 60.0 a 8.0");
        s.modificarPuntaje("1005", 8.0);
        imprimirLista("Estudiantes (nuevo orden)", s.listarOrdenado());
        mostrarAsignacion(s);

        titulo("7. Eliminar al estudiante 1007 (tenia residencia)");
        System.out.println("Eliminado: " + s.eliminar("1007"));
        mostrarAsignacion(s);

        titulo("8. Mas cupos que estudiantes (20 cupos)");
        s.definirCupos(20);
        mostrarAsignacion(s);

        titulo("9. Limitacion del arbol sin balancear");
        System.out.println("Con los estudiantes de la demo (orden mezclado): altura = " + s.alturaArbol());
        SistemaResidencias ordenados = new SistemaResidencias();
        for (int i = 1; i <= 20; i++) {
            ordenados.registrar("E" + i, "Estudiante " + i, i); // puntajes ya ordenados
        }
        System.out.println("20 estudiantes con puntajes ya ordenados: altura = " + ordenados.alturaArbol()
                + " (lo ideal serian unos 5; el arbol se vuelve una lista y las operaciones cuestan O(n))");
    }

    // ---------------------------------------------------------- menu
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--demo")) {
            demostracion();
            return;
        }

        String opcion = "";
        while (!opcion.equals("0")) {
            System.out.println();
            System.out.println("=== RESIDENCIAS UNAL ===");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Definir cupos");
            System.out.println("3. Buscar por ID");
            System.out.println("4. Listar por puntaje");
            System.out.println("5. Asignar cupos");
            System.out.println("6. Modificar puntaje");
            System.out.println("7. Eliminar estudiante");
            System.out.println("8. Ver demostracion con datos de ejemplo");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            if (!sc.hasNextLine()) {
                break;
            }
            opcion = sc.nextLine().trim();

            try {
                switch (opcion) {
                    case "1": registrar(); break;
                    case "2": definirCupos(); break;
                    case "3": buscar(); break;
                    case "4": imprimirLista("Estudiantes de menor a mayor puntaje", sistema.listarOrdenado()); break;
                    case "5": mostrarAsignacion(sistema); break;
                    case "6": modificarPuntaje(); break;
                    case "7": eliminar(); break;
                    case "8": demostracion(); break;
                    case "0": System.out.println("Hasta luego."); break;
                    default: System.out.println("Opcion no valida.");
                }
            } catch (IllegalArgumentException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }
}
