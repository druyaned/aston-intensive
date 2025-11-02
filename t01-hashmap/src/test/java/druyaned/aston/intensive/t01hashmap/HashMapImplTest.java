package druyaned.aston.intensive.t01hashmap;

import druyaned.aston.intensive.t01hashmap.HashMapImpl.Node;
import java.util.HashMap;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class HashMapImplTest {
    
    @Test
    public void testPut() {
        HashMapImpl<Integer, String> map = new HashMapImpl<>();
        int n = 5;
        final int CAP = HashMapImpl.DEFAULT_CAPACITY;
        Integer[] keys = {null, 0, 10, CAP, 2 * CAP};
        String[] values = {"Null", "Zero", "Ten", "CAP", "2*CAP"};
        int[] sizes = {1, 2, 3, 4, 5};
        for (int i = 0; i < n; i++) {
            assertEquals(sizes[i] - 1, map.size());
            assertNull(map.put(keys[i], values[i]));
            assertEquals(sizes[i], map.size());
            assertEquals(values[i], map.put(keys[i], values[i] + "|COPY"));
        }
        // Internal assertions
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        HashMapImpl.Node<Integer, String> node = table[0];
        assertEquals(node.getValue(), "Null|COPY");
        node = node.getNext();
        assertEquals(node.getValue(), "Zero|COPY");
        node = node.getNext();
        assertEquals(node.getValue(), "CAP|COPY");
        node = node.getNext();
        assertEquals(node.getValue(), "2*CAP|COPY");
        assertNull(node.getNext());
        node = table[10];
        assertEquals(node.getValue(), "Ten|COPY");
        assertNull(node.getNext());
        for (int index = 0; index < CAP; index++) {
            if (index != 0 && index != 10) {
                assertNull(table[index]);
            }
        }
    }
    
    /**
     * Returns the map (default capacity must be 15):
     * <pre>
     *   0:  0 15 30 45
     *   5:  5 20 35
     *  10: 10 25 40
     * </pre>
     * Other indexes are occupied by nulls.
     * 
     * @return the map is described above
     */
    static HashMapImpl<Integer, String> getMap() {
        HashMapImpl<Integer, String> map = new HashMapImpl<>();
        int key = 0;
        while (map.size() < 10) {
            map.put(key, Integer.toString(key));
            key += 5;
        }
        return map;
    }
    
    @Test
    public void testPutWithRehashing() {
        HashMapImpl<Integer, String> map = getMap();
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        assertEquals(10, map.size());
        assertEquals(15, HashMapImpl.capacity(map));
        HashMapImpl.Node<Integer, String> node = table[0];
        assertNotNull(node);
        assertEquals(node.getValue(), "0");
        assertEquals((node = node.getNext()).getValue(), "15");
        assertEquals((node = node.getNext()).getValue(), "30");
        assertEquals((node = node.getNext()).getValue(), "45");
        assertNull(node.getNext());
        node = table[5];
        assertEquals(node.getValue(), "5");
        assertEquals((node = node.getNext()).getValue(), "20");
        assertEquals((node = node.getNext()).getValue(), "35");
        assertNull(node.getNext());
        node = table[10];
        assertEquals(node.getValue(), "10");
        assertEquals((node = node.getNext()).getValue(), "25");
        assertEquals((node = node.getNext()).getValue(), "40");
        assertNull(node.getNext());
        // Rehashing
        map.put(55, Integer.toString(55));
        table = HashMapImpl.copyTable(map);
        assertEquals(11, map.size());
        assertEquals(31, HashMapImpl.capacity(map));
        int[] indexes = { 0,  4,  5,  9, 10, 14, 15, 20, 24, 25, 30};
        int[] values = { 0, 35,  5, 40, 10, 45, 15, 20, 55, 25, 30};
        for (int index = 0, i = 0; index < 31; index++) {
            if (index == indexes[i]) {
                String value = Integer.toString(values[i]);
                assertEquals(value, table[index].getValue());
                assertNull(table[index].getNext());
                i++;
            } else {
                assertNull(table[index]);
            }
        }
    }
    
    @Test
    public void testRemove() {
        HashMapImpl<Integer, String> map = getMap();
        assertEquals(10, map.size());
        assertEquals("45", map.remove(45));
        assertNull(map.remove(45));
        assertNull(map.put(null, "Null"));
        assertEquals(10, map.size());
        assertEquals("30", map.remove(30));
        assertEquals("0", map.remove(0));
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        assertEquals("15", table[0].getValue());
        assertEquals("Null", table[0].getNext().getValue());
        assertEquals("15", map.remove(15));
        assertEquals("Null", map.remove(null));
        assertNull(map.remove(null));
        assertEquals(6, map.size());
        assertEquals("5", map.remove(5));
        assertEquals("20", map.remove(20));
        assertEquals("35", map.remove(35));
        assertEquals("10", map.remove(10));
        assertEquals("25", map.remove(25));
        assertEquals("40", map.remove(40));
        assertNull(map.remove(40));
        assertEquals(0, map.size());
    }
    
    @Test
    public void testGet() {
        HashMapImpl<Integer, String> map = getMap();
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        final int CAP = HashMapImpl.DEFAULT_CAPACITY;
        int[][] keyMap = {
            { 0,  0, 15, 30, 45},
            { 5,  5, 20, 35},
            {10, 10, 25, 40}
        };
        for (int index = 0, i = 0; index < CAP; index++) {
            if (i < keyMap.length && index == keyMap[i][0]) {
                Node<Integer, String> node = table[index];
                for (int j = 1; j < keyMap[i].length; j++) {
                    String value = Integer.toString(keyMap[i][j]);
                    assertEquals(keyMap[i][j], node.key);
                    assertEquals(value, map.get(keyMap[i][j]));
                    node = node.getNext();
                }
                i++;
            } else {
                assertNull(table[index]);
                assertNull(map.get(index));
            }
        }
    }
    
    @Test
    public void testWithNegativeKeys() {
        HashMapImpl<Integer, String> map = new HashMapImpl<>();
        int n = 6;
        Integer[] keys = {-6, 2, 0, null, -21, 32};
        for (int i = 0; i < n; i++) {
            if (keys[i] == null) {
                map.put(null, "null");
            } else {
                map.put(keys[i], keys[i].toString());
            }
        }
        assertEquals(6, map.size());
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        HashMapImpl.Node<Integer, String> node = table[2];
        assertEquals("-6", node.getValue());
        assertEquals("-6", map.get(-6));
        node = node.getNext();
        assertEquals("2", node.getValue());
        assertEquals("2", map.get(2));
        node = node.getNext();
        assertEquals("-21", node.getValue());
        assertEquals("-21", map.get(-21));
        node = node.getNext();
        assertEquals("32", node.getValue());
        assertEquals("32", map.get(32));
        assertNull(node.getNext());
        node = table[0];
        assertEquals("0", node.getValue());
        assertEquals("0", map.get(0));
        node = node.getNext();
        assertEquals("null", node.getValue());
        assertEquals("null", map.get(null));
    }
    
    @Test
    public void compareMyAndJavasImpls() {
        HashMapImpl<String, Integer> myMap = new HashMapImpl<>();
        HashMap<String, Integer> javaMap = new HashMap<>();
        final int N = 1_000_000;
        final int L = 20;
        Random rand = new Random();
        String[] data = new String[N];
        for (int i = 0; i < N; i++) {
            int len = L / 2 + rand.nextInt(L / 2 + 1);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                if (rand.nextBoolean()) {
                    sb.append((char)('a' + rand.nextInt(26)));
                } else {
                    sb.append((char)('A' + rand.nextInt(26)));
                }
            }
            data[i] = sb.toString();
        }
        long start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            myMap.put(data[i], i);
        }
        long myPutDur = System.currentTimeMillis() - start;
        start = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            javaMap.put(data[i], i);
        }
        long javaPutDur = System.currentTimeMillis() - start;
        assertEquals(javaMap.size(), myMap.size());
        double durDiff = (double)(myPutDur - javaPutDur) / javaPutDur;
        assertTrue(50d > (durDiff * 100d));
        myMap.clear();
        javaMap.clear();
        for (int i = 0; i < N; i++) {
            assertEquals(javaMap.put(data[i], i), myMap.put(data[i], i));
        }
        assertEquals(javaMap.size(), myMap.size());
        for (int i = 0; i < N; i++) {
            assertEquals(javaMap.get(data[i]), myMap.get(data[i]));
        }
        for (int i = 0; i < N; i++) {
            assertEquals(javaMap.remove(data[i]), myMap.remove(data[i]));
        }
        assertEquals(0, javaMap.size());
        assertEquals(0, myMap.size());
    }
    
    void print(HashMapImpl<Integer, String> map) {
        Node<Integer, String>[] table = HashMapImpl.copyTable(map);
        int capacity = HashMapImpl.capacity(map);
        System.out.println("table:");
        for (int i = 0; i < capacity; i++) {
            System.out.printf("  %2d:", i);
            HashMapImpl.Node<Integer, String> node = table[i];
            while (node != null) {
                System.out.printf(" %2s", node.getValue());
                node = node.getNext();
            }
            System.out.println();
        }
    }
}
