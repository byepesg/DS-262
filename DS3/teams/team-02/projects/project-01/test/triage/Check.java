package triage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Mini marco de pruebas con aserciones (sin dependencias externas, para que
 * el proyecto compile solo con el JDK). Cada prueba es un metodo que lanza
 * {@link AssertionError} si falla; {@link #run} la ejecuta y lleva la cuenta.
 */
final class Check {

    /** Prueba sin argumentos que puede lanzar excepciones. */
    interface TestCase {
        void run() throws Exception;
    }

    private static int passed = 0;
    private static int assertions = 0;
    private static final List<String> failures = new ArrayList<>();

    private Check() {
    }

    static void run(String suite, String name, TestCase test) {
        String fullName = suite + " > " + name;
        try {
            test.run();
            passed++;
            System.out.println("  [OK]    " + fullName);
        } catch (Throwable e) {
            failures.add(fullName + ": " + e);
            System.out.println("  [FALLA] " + fullName + ": " + e);
        }
    }

    static void isTrue(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void equal(Object expected, Object actual, String message) {
        assertions++;
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " (esperado: " + expected + ", obtenido: " + actual + ")");
        }
    }

    static <E extends Throwable> E throwsException(Class<E> type, TestCase action, String message) {
        assertions++;
        try {
            action.run();
        } catch (Throwable e) {
            if (type.isInstance(e)) {
                return type.cast(e);
            }
            throw new AssertionError(message + " (se lanzo " + e + " en lugar de " + type.getSimpleName() + ")");
        }
        throw new AssertionError(message + " (no se lanzo " + type.getSimpleName() + ")");
    }

    static int passed() {
        return passed;
    }

    static int assertions() {
        return assertions;
    }

    static List<String> failures() {
        return failures;
    }
}
