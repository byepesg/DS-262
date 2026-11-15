public class TestRunner
{
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        PriorityQueueTest.run();
        DictionaryTest.run();
        TimeSlotTest.run();
        RegistrationSystemTest.run();
        System.out.println();
        System.out.println(passed + " pruebas correctas, " + failed + " fallidas");
        if (failed > 0) System.exit(1);
    }

    public static void check(boolean condition, String description) {
        if (condition) {
            passed++;
            System.out.println("[OK]    " + description);
        } else {
            failed++;
            System.out.println("[FALLA] " + description);
        }
    }

    public static void checkThrows(Runnable action, String description) {
        boolean threw = false;
        try {
            action.run();
        } catch (RuntimeException e) {
            threw = true;
        }
        check(threw, description);
    }
}
