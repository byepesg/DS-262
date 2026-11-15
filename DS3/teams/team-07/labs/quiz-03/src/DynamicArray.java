public class DynamicArray<T> {
    private T[] data;
    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 1)
            throw new IllegalArgumentException("Initial capacity must be at least 1.");

        this.size = 0;
        this.capacity = initialCapacity;
        this.data = (T[]) new Object[initialCapacity];
    }

    public int size() { return this.size; }

    public int capacity() { return this.capacity;}

    public T get(int index) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Invalid index value, must be 0 <= index < size");

        return this.data[index];
    }

    public void set(int index, T value) {
        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Invalid index value, must be 0 <= index < size");

        this.data[index] = value;
    }

    public void append(T value) {
        if (this.size >= this.capacity)
            this.resize(this.capacity * 2);

        this.data[size] = value;
        this.size++;
    }

    public T removeLast() {
        if (this.size == 0)
            throw new IllegalStateException("Cannot remove last element of an empty array.");

        this.size--;
        T value = this.data[this.size];
        this.data[this.size] = null;

        return value;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        if (newCapacity <= this.capacity)
            throw new IllegalArgumentException("New capacity must be higher than the actual array capacity.");

        this.capacity = newCapacity;
        T[] newArray = (T[]) new Object[newCapacity];

        for (int i = 0; i < this.size; i++) {
            newArray[i] = this.data[i];
        }

        this.data = newArray;
    }
}