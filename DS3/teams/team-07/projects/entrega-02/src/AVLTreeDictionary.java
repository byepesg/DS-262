public class AVLTreeDictionary<K extends Comparable<K>, V> implements Dictionary<K, V>
{
    private class AVLNode
    {
        K key;
        V value;
        int height;
        AVLNode left;
        AVLNode right;

        AVLNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private AVLNode root;
    private int size;

    @Override
    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("La llave no puede ser nula");
        root = put(root, key, value);
    }

    @Override
    public V get(K key) {
        AVLNode current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = cmp < 0 ? current.left : current.right;
        }
        return null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    @Override
    public boolean remove(K key) {
        int before = size;
        root = remove(root, key);
        return size < before;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    public int height() {
        return height(root);
    }

    private AVLNode put(AVLNode node, K key, V value) {
        if (node == null) {
            size++;
            return new AVLNode(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = put(node.left, key, value);
        else if (cmp > 0) node.right = put(node.right, key, value);
        else node.value = value;
        return balance(node);
    }

    private AVLNode remove(AVLNode node, K key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) node.left = remove(node.left, key);
        else if (cmp > 0) node.right = remove(node.right, key);
        else {
            if (node.left == null || node.right == null) {
                size--;
                return node.left != null ? node.left : node.right;
            }
            AVLNode successor = node.right;
            while (successor.left != null) successor = successor.left;
            node.key = successor.key;
            node.value = successor.value;
            node.right = remove(node.right, successor.key);
        }
        return balance(node);
    }

    // Si un lado es más alto por más de 1, se rota; el caso zigzag necesita dos rotaciones.
    private AVLNode balance(AVLNode node) {
        update(node);
        int factor = height(node.left) - height(node.right);
        if (factor > 1) {
            if (height(node.left.left) < height(node.left.right)) node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (factor < -1) {
            if (height(node.right.right) < height(node.right.left)) node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private AVLNode rotateRight(AVLNode node) {
        AVLNode child = node.left;
        node.left = child.right;
        child.right = node;
        update(node);
        update(child);
        return child;
    }

    private AVLNode rotateLeft(AVLNode node) {
        AVLNode child = node.right;
        node.right = child.left;
        child.left = node;
        update(node);
        update(child);
        return child;
    }

    private void update(AVLNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }
}
