import java.util.Random;

public class PriorityQueueTest
{
    public static void run() {
        emptyQueue();
        sameOrderAsSortedList();
        removeFromMiddle();
    }

    private static void emptyQueue() {
        PriorityQueueADT<Integer> heap = new BinaryHeapPriorityQueue<>();
        TestRunner.check(heap.isEmpty(), "heap vacío al crearlo");
        TestRunner.checkThrows(heap::removeHighest, "removeHighest en heap vacío lanza excepción");
        TestRunner.checkThrows(heap::peekHighest, "peekHighest en heap vacío lanza excepción");
        TestRunner.check(!heap.remove(5), "remove en heap vacío devuelve false");
    }

    private static void sameOrderAsSortedList() {
        Random random = new Random(7);
        PriorityQueueADT<Integer> heap = new BinaryHeapPriorityQueue<>();
        PriorityQueueADT<Integer> list = new SortedLinkedPriorityQueue<>();
        for (int i = 0; i < 1000; i++) {
            int value = random.nextInt(500);
            heap.insert(value);
            list.insert(value);
        }
        boolean same = heap.size() == list.size();
        while (same && !list.isEmpty())
            same = heap.removeHighest().equals(list.removeHighest());
        TestRunner.check(same && heap.isEmpty(), "heap y lista ordenada sacan 1000 elementos en el mismo orden");
    }

    private static void removeFromMiddle() {
        PriorityQueueADT<Integer> heap = new BinaryHeapPriorityQueue<>();
        for (int value : new int[] {5, 3, 8, 1, 9, 2, 7}) heap.insert(value);
        TestRunner.check(heap.remove(8), "remove de un elemento intermedio");
        TestRunner.check(!heap.contains(8), "el elemento eliminado ya no está");
        StringBuilder order = new StringBuilder();
        while (!heap.isEmpty()) order.append(heap.removeHighest());
        TestRunner.check(order.toString().equals("123579"), "tras remove el orden sigue siendo correcto");
    }
}
