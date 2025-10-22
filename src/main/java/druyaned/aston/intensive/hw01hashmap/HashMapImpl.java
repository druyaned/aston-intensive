package druyaned.aston.intensive.hw01hashmap;

/**
 * Implementation of the HashMap as specified in the
 * {@link HashMapTask task}.
 * 
 * <p>
 * I decided to use a basic approach:
 * <code>(hash & 0x7fffffff) % capacity</code> is for seeking
 * a table index for a key in the table and <code>linked list</code> (Node)
 * is for dealing with collisions. Shifting bits and applying XOR can be used
 * instead of a simple modulo operation. TreeNode can be defined (which
 * represents self-balanced binary tree) instead of a simple Node
 * (linked list). But these enhancements are useless in this task
 * that should be concise and clear.
 * 
 * <p>
 * The map permits null key and null values. Rehashing occurs when
 * <code>size &lt; LOAD_FACTOR * capacity</code>. Capacity schema is
 * <code>2^k - 1</code> which can be up to <code>Integer.MAX_VALUE</code>.
 * 
 * <p>
 * Amortized complexity of get/put/remove is <code>O(1)</code>,
 * complexity of rehashing is - O(n).
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author druyaned
 */
public class HashMapImpl<K, V> implements HashMapTask<K, V> {
    private int capacity = DEFAULT_CAPACITY;
    private Node<K, V>[] table = newTable(DEFAULT_CAPACITY);
    private int size = 0;
    
    /**
     * {@inheritDoc}
     * 
     * <p>
     * Execution can be concisely described as follows.
     * 
     * <p>
     * If a table index for the key is free, null value is returned,
     * otherwise an iteration is started through the linked list
     * of a node which occupies the index:
     * <ol>
     *   <li>If hashes and keys are equal of the given key and the node,
     *     the value of the node is returned</li>
     *   <li>Else if the end of the linked list is reached,
     *     null is returned</li>
     *   <li>Else the iteration is continued for the next node</li>
     * </ol>
     */
    @Override public V get(K key) {
        Node<K, V> node = table[getTableIndex(key, capacity)];
        if (node == null) {
            return null;
        }
        int hash = getHash(key);
        while (true) {
            if (hash == node.hash && areEqual(key, node.key)) {
                return node.value;
            }
            if (node.next == null) {
                return null;
            }
            node = node.next;
        }
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>
     * Execution corresponds to {@link HashMapImpl#get},
     * but if the size is not less than a product of
     * LOAD_FACTOR and capacity, the table is rehashed.
     * 
     * @see HashMapImpl
     */
    @Override public V put(K key, V value) {
        int index = getTableIndex(key, capacity);
        Node<K, V> node = table[index];
        if (node == null) {
            table[index] = new Node<>(key, value);
            size++;
            rehashOnDemand();
            return null;
        }
        int hash = getHash(key);
        while (true) {
            if (hash == node.hash && areEqual(key, node.key)) {
                V prevValue = node.value;
                node.value = value;
                return prevValue;
            }
            if (node.next == null) {
                node.next = new Node<>(key, value);
                size++;
                rehashOnDemand();
                return null;
            }
            node = node.next;
        }
    }
    
    private void rehashOnDemand() {
        if (size < (int)(LOAD_FACTOR * capacity)) {
            return;
        }
        Node<K, V>[] prevTable = table;
        capacity += 1 + capacity;
        table = newTable(capacity);
        for (int i = 0; i < prevTable.length; i++) {
            Node<K, V> appended = prevTable[i];
            while (appended != null) {
                reappend(appended);
                Node<K, V> prevAppended = appended;
                appended = appended.next;
                prevAppended.next = null;
            }
            prevTable[i] = null;
        }
    }
    
    private void reappend(Node<K, V> appended) {
        int index = getTableIndex(appended.key, capacity);
        Node<K, V> node = table[index];
        if (node == null) {
            table[index] = appended;
            return;
        }
        while (node.next != null) {
            node = node.next;
        }
        node.next = appended;
    }
    
    /**
     * {@inheritDoc}
     * 
     * <p>
     * Execution corresponds to {@link HashMapImpl#get},
     * but with a special case for the first node in a bucket.
     */
    @Override public V remove(K key) {
        int index = getTableIndex(key, capacity);
        Node<K, V> node = table[index];
        if (node == null) {
            return null;
        }
        int hash = getHash(key);
        if (hash == node.hash && areEqual(key, node.key)) {
            table[index] = node.next;
            node.next = null;
            size--;
            return node.value;
        }
        if (node.next == null) {
            return null;
        }
        Node<K, V> prev = node;
        node = node.next;
        while (true) {
            if (hash == node.hash && areEqual(key, node.key)) {
                prev.next = node.next;
                node.next = null;
                size--;
                return node.value;
            }
            if (node.next == null) {
                return null;
            }
            prev = node;
            node = node.next;
        }
    }
    
    @Override public int size() {
        return size;
    }
    
    /**
     * Removes all key-value pairs from the map also refreshes
     * the capacity.
     */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            Node<K, V> node = table[i];
            while (node != null) {
                Node<K, V> prev = node;
                node = node.next;
                prev.next = null;
            }
            table[i] = null;
        }
        size = 0;
        capacity = DEFAULT_CAPACITY;
    }
    
    /* ---------------------------- Utility Part ---------------------------- */
    
    public static final int DEFAULT_CAPACITY = 16 - 1;
    public static final double LOAD_FACTOR = 0.75;
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <K, V> Node<K, V>[] newTable(int capacity) {
        return (Node<K, V>[])new Node[capacity];
    }
    
    private static <K> int getHash(K key) {
        return key == null ? 0 : key.hashCode();
    }
    
    private static <K> int getTableIndex(K key, int capacity) {
        return (getHash(key) & 0x7fffffff) % capacity;
    }
    
    private static <K> boolean areEqual(K key1, K key2) {
        if (key1 == null) {
            return key2 == null;
        } else {
            return key1.equals(key2);
        }
    }
    
    static <K, V> Node<K, V>[] copyTable(HashMapImpl<K, V> map) {
        Node<K, V>[] copy = newTable(map.table.length);
        System.arraycopy(map.table, 0, copy, 0, map.table.length);
        return copy;
    }
    
    static <K, V> int capacity(HashMapImpl<K, V> map) {
        return map.capacity;
    }
    
    static class Node<K, V> {
        final K key;
        private V value;
        final int hash;
        private Node<K, V> next = null;
        
        private Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.hash = getHash(key);
        }
        
        V getValue() {
            return value;
        }
        
        Node<K, V> getNext() {
            return next;
        }
    }
}
