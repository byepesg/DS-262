package co.edu.unal.residencias;

import java.util.List;
import java.util.Scanner;

public final class Main {
    private final SistemaResidencias sistema = new SistemaResidencias();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().ejecutar();
    }

    private void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = leerEntero("Seleccione una opción: ");
            try {
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> listar();
                    case 4 -> modificar();
                    case 5 -> eliminar();
                    case 6 -> definirCupos();
                    case 7 -> asignar();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException excepcion) {
                System.out.println("Error: " + excepcion.getMessage());
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void mostrarMenu() {
        System.out.println("\n=== RESIDENCIAS UNAL ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Consultar por ID");
        System.out.println("3. Listar por puntaje");
        System.out.println("4. Modificar puntaje");
        System.out.println("5. Eliminar estudiante");
        System.out.println("6. Definir cupos");
        System.out.println("7. Asignar y mostrar resultados");
        System.out.println("0. Salir");
    }

    private void registrar() {
        int id = leerEntero("ID: ");
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        int puntaje = leerEntero("Puntaje socioeconómico (0-100): ");
        sistema.registrarEstudiante(id, nombre, puntaje);
        System.out.println("Estudiante registrado.");
    }

    private void consultar() {
        int id = leerEntero("ID a consultar: ");
        System.out.println(sistema.buscarPorId(id).map(Object::toString).orElse("Estudiante no encontrado."));
    }

    private void listar() {
        imprimirLista("SOLICITUDES ORDENADAS", sistema.listarPorPuntaje());
    }

    private void modificar() {
        int id = leerEntero("ID: ");
        int puntaje = leerEntero("Nuevo puntaje (0-100): ");
        sistema.modificarPuntaje(id, puntaje);
        System.out.println("Puntaje actualizado.");
    }

    private void eliminar() {
        int id = leerEntero("ID a eliminar: ");
        sistema.eliminarEstudiante(id);
        System.out.println("Estudiante eliminado.");
    }

    private void definirCupos() {
        sistema.definirCupos(leerEntero("Cantidad de cupos: "));
        System.out.println("Cupos configurados.");
    }

    private void asignar() {
        ResultadoAsignacion resultado = sistema.asignarCupos();
        System.out.println("Cupos configurados: " + resultado.cuposConfigurados());
        imprimirLista("BENEFICIARIOS", resultado.beneficiarios());
        imprimirLista("NO BENEFICIARIOS", resultado.noBeneficiarios());
    }

    private void imprimirLista(String titulo, List<Estudiante> lista) {
        System.out.println("\n--- " + titulo + " ---");
        if (lista.isEmpty()) System.out.println("Sin registros.");
        else lista.forEach(System.out::println);
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            try { return Integer.parseInt(entrada); }
            catch (NumberFormatException e) { System.out.println("Ingrese un número entero válido."); }
        }
    }
}
