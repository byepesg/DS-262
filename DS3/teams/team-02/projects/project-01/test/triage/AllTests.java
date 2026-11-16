package triage;

/**
 * Punto de entrada de la suite de pruebas automatizadas. Termina con codigo
 * de salida 1 si alguna prueba falla.
 */
public final class AllTests {

    private AllTests() {
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        System.out.println("== Estructuras de datos ==");
        DataStructureTests.runAll();
        System.out.println("== Sistema ==");
        TriageSystemTests.runAll();
        long millis = (System.nanoTime() - start) / 1_000_000;

        int failed = Check.failures().size();
        System.out.println();
        System.out.printf("Pruebas: %d correctas, %d fallidas; %d aserciones; %d ms%n",
                Check.passed(), failed, Check.assertions(), millis);
        if (failed > 0) {
            System.out.println("Fallas:");
            for (String f : Check.failures()) {
                System.out.println("  - " + f);
            }
            System.exit(1);
        }
    }
}
