import java.util.Random;

public class Benchmark
{
    private static final int[] SIZES = {1000, 5000, 10000, 20000};
    private static final int REPETITIONS = 5;

    public static void main(String[] args) {
        System.out.println("Lista de espera: insertar n estudiantes y sacarlos todos (ms, mediana de " + REPETITIONS + ")");
        System.out.printf("%8s %16s %10s%n", "n", "lista ordenada", "heap");
        for (int n : SIZES) {
            Student[] students = randomStudents(n, n);
            double list = median(() -> fillAndEmpty(new SortedLinkedPriorityQueue<>(), students));
            double heap = median(() -> fillAndEmpty(new BinaryHeapPriorityQueue<>(), students));
            System.out.printf("%8d %16.2f %10.2f%n", n, list, heap);
        }

        System.out.println();
        System.out.println("Búsqueda: n búsquedas por código entre n estudiantes (ms, mediana de " + REPETITIONS + ")");
        System.out.printf("%8s %16s %10s%n", "n", "lista enlazada", "AVL");
        for (int n : SIZES) {
            Student[] students = randomStudents(n, n + 1);
            LinkedList<Student> list = new LinkedList<>();
            AVLTreeDictionary<String, Student> tree = new AVLTreeDictionary<>();
            for (Student s : students) {
                list.addLast(s);
                tree.put(s.getCode(), s);
            }
            double linear = median(() -> { for (Student s : students) list.contains(s); });
            double avl = median(() -> { for (Student s : students) tree.get(s.getCode()); });
            System.out.printf("%8d %16.2f %10.2f%n", n, linear, avl);
        }
    }

    private static void fillAndEmpty(PriorityQueueADT<Student> queue, Student[] students) {
        for (Student s : students) queue.insert(s);
        while (!queue.isEmpty()) queue.removeHighest();
    }

    private static Student[] randomStudents(int n, long seed) {
        Random random = new Random(seed);
        Student[] students = new Student[n];
        for (int i = 0; i < n; i++) {
            String code = String.valueOf(10000000 + random.nextInt(90000000));
            students[i] = new Student(code + "-" + i, "E" + i, random.nextInt(100) < 2,
                    2.5 + 2.5 * random.nextDouble(), random.nextInt(100) < 10,
                    100 * random.nextDouble(), 10 * random.nextDouble());
        }
        return students;
    }

    private static double median(Runnable task) {
        task.run();
        double[] times = new double[REPETITIONS];
        for (int i = 0; i < REPETITIONS; i++) {
            long start = System.nanoTime();
            task.run();
            times[i] = (System.nanoTime() - start) / 1e6;
        }
        java.util.Arrays.sort(times);
        return times[REPETITIONS / 2];
    }
}
