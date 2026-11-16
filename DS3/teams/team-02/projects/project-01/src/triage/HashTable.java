package triage;

/**
 * Tabla hash generica con encadenamiento separado (separate chaining).
 *
 * <p>Cada posicion del arreglo de cubetas guarda una lista simplemente
 * enlazada con las entradas cuyas claves caen en esa cubeta. Cuando el
 * factor de carga {@code size / capacidad} supera {@value #MAX_LOAD_FACTOR},
 * la capacidad se duplica y todas las entradas se redistribuyen
 * ({@code rehash}). Con una funcion hash que reparta bien las claves, las
 * operaciones cuestan {@code O(1)} esperado; el redimensionamiento cuesta
 * {@code O(n)} pero ocurre cada vez que el tamano se duplica, por lo que su
 * costo amortizado por insercion es {@code O(1)}.</p>
 *
 * <p>Las claves no pueden ser {@code null}.</p>
 */
public class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private static final class Entry<K, V> {

        private final K key;
        private V value;
        private Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * @param initialCapacity capacidad inicial; se redondea a la siguiente
     *        potencia de dos para poder calcular el indice con una mascara
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public HashTable(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        int capacity = 1;
        while (capacity < initialCapacity) {
            capacity <<= 1;
        }
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
    }

    /**
     * Inserta o reemplaza el valor asociado a la clave.
     *
     * @return el valor anterior, o null si la clave no existia
     */
    public V put(K key, V value) {
        requireKey(key);
        int index = indexFor(key, buckets.length);
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                V old = e.value;
                e.value = value;
                return old;
            }
        }
        buckets[index] = new Entry<>(key, value, buckets[index]);
        size++;
        if (size > MAX_LOAD_FACTOR * buckets.length) {
            resize(buckets.length * 2);
        }
        return null;
    }

    /**
     * @return el valor asociado a la clave, o null si no existe
     */
    public V get(K key) {
        requireKey(key);
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                return e.value;
            }
        }
        return null;
    }

    public boolean containsKey(K key) {
        requireKey(key);
        for (Entry<K, V> e = buckets[indexFor(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina la clave.
     *
     * @return el valor que tenia asociado, o null si no existia
     */
    public V remove(K key) {
        requireKey(key);
        int index = indexFor(key, buckets.length);
        Entry<K, V> previous = null;
        for (Entry<K, V> e = buckets[index]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = e.next;
                } else {
                    previous.next = e.next;
                }
                size--;
                return e.value;
            }
            previous = e;
        }
        return null;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Numero de cubetas; se expone para las pruebas del redimensionamiento. */
    int capacity() {
        return buckets.length;
    }

    /** Longitud de la cadena mas larga; se usa en las pruebas y el benchmark. */
    int longestChain() {
        int longest = 0;
        for (Entry<K, V> head : buckets) {
            int length = 0;
            for (Entry<K, V> e = head; e != null; e = e.next) {
                length++;
            }
            longest = Math.max(longest, length);
        }
        return longest;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void resize(int newCapacity) {
        Entry<K, V>[] old = buckets;
        buckets = (Entry<K, V>[]) new Entry[newCapacity];
        for (Entry<K, V> head : old) {
            Entry<K, V> e = head;
            while (e != null) {
                Entry<K, V> next = e.next;
                int index = indexFor(e.key, newCapacity);
                e.next = buckets[index];
                buckets[index] = e;
                e = next;
            }
        }
    }

    /**
     * Indice de cubeta. Se mezclan los bits altos del hashCode con los bajos
     * antes de aplicar la mascara, porque con capacidad potencia de dos la
     * mascara solo conserva los bits bajos.
     */
    private static int indexFor(Object key, int capacity) {
        int h = key.hashCode();
        h ^= (h >>> 16);
        return h & (capacity - 1);
    }

    private static void requireKey(Object key) {
        if (key == null) {
            throw new IllegalArgumentException("La clave no puede ser null");
        }
    }
}
