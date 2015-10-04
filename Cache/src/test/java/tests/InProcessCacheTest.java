/**
 * 
 */
package tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import client.CacheEntry;
import client.InProcessCache;
import client.InProcessCacheStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ArunIyengar
 * 
 */
public class InProcessCacheTest {

    int numObjects = 2000;
    InProcessCache<String, Integer> spc = new InProcessCache<String, Integer>(
            numObjects);
    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";
    long lifetime = 3000;



    
    @Test
    public void testPutGetGetStatistics() {
        spc.put(key1, 42, lifetime);
        spc.printCacheEntry(key1);
        spc.printCacheEntry(key2);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        spc.print();
        InProcessCacheStats stats1 = spc.getStatistics();
        assertEquals("Cache size should be 3", 3, spc.size());
        assertEquals("Hit rate should be 1.0", 1.0,
                stats1.getStats().hitRate(), .0001);
        System.out.println("Cache size: " + spc.size());
        System.out.println("Hit rate: " + stats1.getStats().hitRate());
    }

    @Test
    public void testClear() {
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, spc.size());
        System.out.println("Cache size: " + spc.size());
        spc.print();
        spc.clear();
        assertEquals("Cache size should be 0", 0, spc.size());
        System.out.println("Cache size: " + spc.size());
        spc.print();
    }

    @Test
    public void testDelete() {
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, spc.size());
        spc.print();
        spc.delete(key2);
        assertEquals("Cache size should be 2", 2, spc.size());
        spc.print();
        spc.put(key2, 50, lifetime);
        spc.put("key4", 59, lifetime);
        spc.put("key5", 80, lifetime);

        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        spc.deleteAll(list);
        assertEquals("Cache size should be 3", 3, spc.size());
        spc.delete("adjkfjadfjdf");
        spc.delete("adfkasdklfjil");
        spc.print();
    }

    @Test
    public void testPutAll() {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(key1, 42);
        map.put(key2, 43);
        map.put(key3, 44);
        spc.putAll(map, lifetime);
        spc.print();
        assertEquals("Cache size should be 3", 3, spc.size());
    }

    static <K, V> void printMap(Map<K, V> map) {
        System.out.println("printMap: outputting map contents ");
        for (Map.Entry<K, V> entry : map.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " Value: "
                    + entry.getValue());
        }
    }

    @Test
    public void testGetAll() {
        spc.put(key1, 42, lifetime);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);
        Map<String, Integer> map = spc.getAll(list);
        printMap(map);
        assertEquals("Returned map size should be 3", 3, map.size());
    }

    @Test
    public void testUpdate() {
        spc.put(key1, 42, lifetime);
        spc.print();
        spc.put(key1, 43, lifetime);
        spc.print();
        spc.put(key1, 44, lifetime);
        spc.print();
        assertEquals("Cache size should be 1", 1, spc.size());
    }
    
}
