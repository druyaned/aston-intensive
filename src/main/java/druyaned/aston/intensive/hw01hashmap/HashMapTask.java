package druyaned.aston.intensive.hw01hashmap;

/**
 * Task#01: "You need to write your own HashMap implementation;
 * compulsory methods: get, put, remove".
 * 
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @author druyaned
 * @see java.util.HashMap
 */
public interface HashMapTask<K, V> {
    /**
     * Returns the value to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped,
     *   or null if this map contains no mapping for the key
     */
    V get(K key);
    
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null
     *   if there was no mapping for key
     */
    V put(K key, V value);
    
    /**
     * Removes the mapping for the specified key from this map if present.
     * 
     * @param key the key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null
     *   if there was no mapping for key
     */
    V remove(K key);
    
    /**
     * Returns the number of key-value mappings in this map.
     * 
     * @return the number of key-value mappings in this map
     */
    int size();
}
