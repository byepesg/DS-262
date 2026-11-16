public class TimeSlotTest
{
    public static void run() {
        TimeSlot lunes7a9 = new TimeSlot("LUN", 7, 9);
        TestRunner.check(lunes7a9.overlaps(new TimeSlot("LUN", 8, 10)), "franjas que se solapan se cruzan");
        TestRunner.check(!lunes7a9.overlaps(new TimeSlot("LUN", 9, 11)), "una franja que empieza cuando la otra termina no se cruza");
        TestRunner.check(!lunes7a9.overlaps(new TimeSlot("MAR", 7, 9)), "misma hora en otro día no se cruza");
        TestRunner.check(lunes7a9.overlaps(new TimeSlot("LUN", 6, 12)), "una franja que contiene a otra se cruza");
        TestRunner.checkThrows(() -> new TimeSlot("LUN", 10, 8), "franja con inicio después del fin lanza excepción");
    }
}
