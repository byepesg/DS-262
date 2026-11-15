public interface Dictionary<K extends Comparable<K>, V>
{
    void put(K key, V value);

    V get(K key);

    boolean containsKey(K key);

    boolean remove(K key);

    int size();

    boolean isEmpty();
}
