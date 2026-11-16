package co.edu.unal.asignacion;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public final class Main {
    // Instanciamos el motor genérico
    private final GestorAsignacion sistema = new GestorAsignacion();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new Main().ejecutar();
    }

    private void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            try {
                int opcion = leerEntero("Seleccione una opción: ");
                switch (opcion) {
                    case 1 -> registrar();
                    case 2 -> consultar();
                    case 3 -> listar();
                    case 4 -> modificar();
                    case 5 -> eliminar();
                    case 6 -> definirCupos();
                    case 7 -> asignar();
                    case 8 -> consultarEstado();
                    case 9 -> imprimirLista("LISTA DE ESPERA POR PRIORIDAD", sistema.listarEspera());
                    case 10 -> mostrarResumen();
                    case 11 -> mostrarHistorial();
                    case 12 -> guardar();
                    case 13 -> cargar();
                    case 14 -> exportar();
                    case 0 -> continuar = false;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (IllegalArgumentException excepcion) {
                System.out.println("Error: " + excepcion.getMessage());
            } catch (IOException excepcion) {
                System.out.println("No fue posible acceder al archivo: " + excepcion.getMessage());
            } catch (NoSuchElementException excepcion) {
                continuar = false;
            }
        }
        System.out.println("Programa finalizado.");
    }

    private void mostrarMenu() {
        // Interfaz idéntica a tu requerimiento de Residencias UNAL
        System.out.println("\n=== RESIDENCIAS UNAL ===");
        System.out.println("1. Registrar estudiante");
        System.out.println("2. Consultar por ID");
        System.out.println("3. Listar por puntaje");
        System.out.println("4. Modificar puntaje");
        System.out.println("5. Eliminar estudiante");
        System.out.println("6. Definir cupos");
        System.out.println("7. Asignar y mostrar resultados");
        System.out.println("8. Consultar estado y posición por ID");
        System.out.println("9. Mostrar lista de espera");
        System.out.println("10. Mostrar resumen del sistema");
        System.out.println("11. Mostrar historial de operaciones");
        System.out.println("12. Guardar estudiantes y cupos (.properties)");
        System.out.println("13. Cargar estudiantes y cupos (.properties)");
        System.out.println("14. Exportar reporte (.csv)");
        System.out.println("0. Salir");
    }

    private void registrar() {
        int id = leerEntero("ID: ");
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();
        int puntaje = leerEntero("Puntaje socioeconómico (0-100): ");
        // Usamos el método genérico pero para un estudiante
        sistema.registrarCandidato(id, nombre, puntaje);
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
        sistema.eliminarCandidato(id);
        System.out.println("Estudiante eliminado.");
    }

    private void definirCupos() {
        sistema.definirCupos(leerEntero("Cantidad de cupos: "));
        System.out.println("Cupos configurados.");
    }

    private void asignar() {
        ResultadoAsignacion resultado = sistema.asignarCupos();
        System.out.println("Cupos configurados: " + resultado.cuposConfigurados());
        // Traducimos los resultados genéricos a términos de Residencias
        imprimirLista("BENEFICIARIOS", resultado.asignados());
        imprimirLista("NO BENEFICIARIOS / EN ESPERA", resultado.enEspera());
    }

    private void consultarEstado() {
        EstadoSolicitud estado = sistema.consultarEstado(leerEntero("ID a consultar: "));
        System.out.println(estado.candidato());
        String estadoTexto = estado.asignado() ? "BENEFICIARIO" : "EN ESPERA";
        System.out.println("Posición: " + estado.posicion() + " | Estado: " + estadoTexto);
    }

    private void mostrarResumen() {
        ResumenSistema r = sistema.generarResumen();
        System.out.println("\n--- RESUMEN DEL SISTEMA ---");
        System.out.println("Solicitudes: " + r.totalSolicitudes());
        System.out.println("Cupos configurados: " + r.cuposConfigurados());
        System.out.println("Beneficiarios: " + r.asignados() + " | En espera: " + r.enEspera());
        System.out.println("Cupos libres: " + r.cuposLibres());
        System.out.printf("Puntaje promedio: %.2f%n", r.puntajePromedio());
    }

    private void mostrarHistorial() {
        System.out.println("\n--- ÚLTIMAS OPERACIONES DE ESTA SESIÓN (MÁX. 100) ---");
        List<Movimiento> movimientos = sistema.historialOperaciones();
        if (movimientos.isEmpty()) System.out.println("Sin operaciones.");
        else movimientos.forEach(System.out::println);
    }

    private void guardar() throws IOException {
        sistema.guardarDatos(leerRuta("Archivo de destino (.properties): "));
        System.out.println("Estudiantes y cupos guardados.");
    }

    private void cargar() throws IOException {
        Path ruta = leerRuta("Archivo que desea cargar (.properties): ");
        System.out.print("La carga reemplaza estudiantes y cupos actuales. Escriba CARGAR para continuar: ");
        if (!"CARGAR".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.println("Carga cancelada.");
            return;
        }
        sistema.cargarDatos(ruta);
        System.out.println("Archivo validado. Estudiantes y cupos cargados.");
    }

    private void exportar() throws IOException {
        Path ruta = leerRuta("Archivo de destino (.csv): ");
        ResultadoAsignacion resultado = sistema.asignarCupos();
        // Le pasamos al exportador genérico las etiquetas específicas de nuestro dominio
        ExportadorReporte.exportar(ruta, resultado, "BENEFICIARIO", "EN ESPERA");
        System.out.println("Reporte exportado.");
    }

    private Path leerRuta(String mensaje) {
        System.out.print(mensaje);
        String entrada = scanner.nextLine().trim();
        if (entrada.isEmpty()) throw new IllegalArgumentException("La ruta no puede estar vacía.");
        return Path.of(entrada);
    }

    private void imprimirLista(String titulo, List<Candidato> lista) {
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