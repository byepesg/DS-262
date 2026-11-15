// Heap mínimo en arreglo: el hijo izquierdo de i está en 2i+1 y el derecho en 2i+2.
public class BinaryHeapPriorityQueue<T extends Comparable<T>> implements PriorityQueueADT<T>
{
    private T[] heap;
    private int size;

    @SuppressWarnings("unchecked")
    public BinaryHeapPriorityQueue() {
        this.heap = (T[]) new Comparable[8];
        this.size = 0;
    }

    @Override
    public void insert(T item) {
        if (size == heap.length) resize(2 * heap.length);
        heap[size] = item;
        siftUp(size);
        size++;
    }

    @Override
    public T removeHighest() {
        if (isEmpty()) throw new IllegalStateException("La cola de prioridad está vacía");
        T top = heap[0];
        removeAt(0);
        return top;
    }

    @Override
    public T peekHighest() {
        if (isEmpty()) throw new IllegalStateException("La cola de prioridad está vacía");
        return heap[0];
    }

    @Override
    public boolean remove(T item) {
        int index = indexOf(item);
        if (index < 0) return false;
        removeAt(index);
        return true;
    }

    @Override
    public boolean contains(T item) {
        return indexOf(item) >= 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    private void removeAt(int index) {
        size--;
        if (index == size) {
            heap[size] = null;
            return;
        }
        heap[index] = heap[size];
        heap[size] = null;
        siftDown(index);
        siftUp(index);
    }

    private int indexOf(T item) {
        for (int i = 0; i < size; i++)
            if (heap[i].equals(item)) return i;
        return -1;
    }

    private void siftUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap[index].compareTo(heap[parent]) >= 0) break;
            swap(index, parent);
            index = parent;
        }
    }

    private void siftDown(int index) {
        while (true) {
            int left = 2 * index + 1;
            int right = left + 1;
            int best = index;
            if (left < size && heap[left].compareTo(heap[best]) < 0) best = left;
            if (right < size && heap[right].compareTo(heap[best]) < 0) best = right;
            if (best == index) return;
            swap(index, best);
            index = best;
        }
    }

    private void swap(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @SuppressWarnings("unchecked")
    private void resize(int capacity) {
        T[] bigger = (T[]) new Comparable[capacity];
        for (int i = 0; i < size; i++) bigger[i] = heap[i];
        heap = bigger;
    }

    @Override
    public String toString() {
        BinaryHeapPriorityQueue<T> copy = new BinaryHeapPriorityQueue<>();
        for (int i = 0; i < size; i++) copy.insert(heap[i]);
        StringBuilder sb = new StringBuilder("[");
        while (!copy.isEmpty()) {
            sb.append(copy.removeHighest());
            if (!copy.isEmpty()) sb.append(", ");
        }
        return sb.append("]").toString();
    }
}
