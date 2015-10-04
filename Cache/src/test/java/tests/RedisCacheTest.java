/**
 * 
 */
package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SealedObject;

import org.junit.Test;

import client.Encryption;
import client.RedisCache;
import client.Serializer;
import client.Util;
import client.Encryption.Key;

/**
 * @author ArunIyengar
 *
 */
public class RedisCacheTest {
    
    int numObjects = 2000;
    RedisCache<String, Integer> opc = new RedisCache<String, Integer>
        ("localhost", 6379, 60);
    String key1 = "key1";
    String key2 = "key2";
    String key3 = "key3";
    long lifetime = 3000;

    
    @Test
    public void testPutGet() {
        opc.clear();
        opc.put(key1, 42, lifetime);
        opc.printCacheEntry(key1);
        opc.printCacheEntry(key2);
        opc.put(key2, 43, lifetime);
        opc.put(key3, 44, lifetime);
        opc.print();
        assertEquals("Cache size should be 3", 3, opc.size());
        opc.clear();
    }

    @Test
    public void testClear() {
        opc.clear();
        opc.put(key1, 42, lifetime);
        opc.put(key2, 43, lifetime);
        opc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, opc.size());
        System.out.println("Cache size: " + opc.size());
        opc.print();
        opc.clear();
        assertEquals("Cache size should be 0", 0, opc.size());
        System.out.println("Cache size: " + opc.size());
        opc.print();
    }

    @Test
    public void testDelete() {
        opc.clear();
        opc.put(key1, 42, lifetime);
        opc.put(key2, 43, lifetime);
        opc.put(key3, 44, lifetime);
        assertEquals("Cache size should be 3", 3, opc.size());
        opc.print();
        opc.delete(key2);
        assertEquals("Cache size should be 2", 2, opc.size());
        opc.print();
        opc.put(key2, 50, lifetime);
        opc.put("key4", 59, lifetime);
        opc.put("key5", 80, lifetime);

        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        opc.deleteAll(list);
        assertEquals("Cache size should be 3", 3, opc.size());
        opc.delete("adjkfjadfjdf");
        opc.delete("adfkasdklfjil");
        opc.print();
        opc.clear();
    }

    @Test
    public void testPutAll() {
        opc.clear();
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        map.put(key1, 42);
        map.put(key2, 43);
        map.put(key3, 44);
        opc.putAll(map, lifetime);
        opc.print();
        assertEquals("Cache size should be 3", 3, opc.size());
        opc.clear();
    }

    @Test
    public void testGetAll() {
        opc.clear();
        opc.put(key1, 42, lifetime);
        opc.put(key2, 43, lifetime);
        opc.put(key3, 44, lifetime);
        ArrayList<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);
        Map<String, Integer> map = opc.getAll(list);
        InProcessCacheTest.printMap(map);
        assertEquals("Returned map size should be 3", 3, map.size());
        opc.clear();
    }

    @Test
    public void testUpdate() {
        opc.clear();
        opc.put(key1, 42, lifetime);
        opc.print();
        opc.put(key1, 43, lifetime);
        opc.print();
        opc.put(key1, 44, lifetime);
        opc.print();
        assertEquals("Cache size should be 1", 1, opc.size());
        opc.clear();
    }

    @Test
    public void testHashMap() {
        RedisCache<String, HashMap<String, Integer>> opc2 = new RedisCache<String, HashMap<String, Integer>>(
                "localhost", 6379, 60);

        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        try {

            HashMap<String, Integer> hm = new HashMap<String, Integer>();
            hm.put(key1, 22);
            hm.put(key2, 23);
            hm.put(key3, 24);
            System.out.println("original hash table: " + hm);
            opc2.put("bar", hm, 5000);
            HashMap<String, Integer> hm2 = opc2.get("bar");
            System.out.println("fetched hash table: " + hm2);
        } finally {
            opc2.clear();
            opc2.close();
        }
    }
    
    @Test
    public void testDeleteAll() {
        RedisCache<String, Integer> opc2 = new RedisCache<String, Integer>(
                "localhost", 6379, 60);
        String key1 = "foo";
        String key2 = "bar";
        String key3 = "baz";
        List<String> list = new ArrayList<String>();
        list.add(key1);
        list.add(key2);
        list.add(key3);

        try {
            opc2.clear();
            opc2.put(key1, 57, 5000);
            int val = opc2.get(key1);
            System.out.println("Stored value of " + key1 + " is: " + val);
            opc2.put(key2, 30, 5000);
            val = opc2.get(key2);
            System.out.println("Stored value of " + key2 + " is: " + val);
            opc2.put(key3, 100, 5000);
            val = opc2.get(key3);
            System.out.println("Stored value of " + key3 + " is: " + val);
            System.out.println("# of objects in cache: " + opc2.size());
            opc2.print();
            opc2.deleteAll(list);
            System.out.println("# of objects in cache after deleteAll: "
                    + opc2.size());
            System.out.println(opc2.getStatistics().getStats());

        } finally {
            opc2.clear();
            opc2.close();
        }
    }

    @Test
    public void testSerialize() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        hm.put(key1, 22);
        hm.put(key2, 23);
        hm.put(key3, 24);
        System.out.println("original hash table: " + hm);
        byte[] compressed = Util.compress(hm);
        HashMap<String, Integer> hm2 = Util.decompress(compressed);
        System.out.println("Hash table after compression and decompression: " + hm2);
        byte[] serialized = Serializer.serializeToByteArray(hm);
        System.out.println("Compressed object size: " + compressed.length);
        System.out.println("Serialized object size: " + serialized.length);
    }

    @Test
    public void testEncryption() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        HashMap<String, Integer> hm = new HashMap<String, Integer>();
        hm.put(key1, 22);
        hm.put(key2, 23);
        hm.put(key3, 24);
        System.out.println("original hash table: " + hm);
        Encryption.Key secretKey = Encryption.generateKey();
        SealedObject so = Encryption.encrypt(hm, secretKey);
        HashMap<String, Integer> hm2 = Encryption.decrypt(so, secretKey);
        System.out.println("Decrypted hash table: " + hm2);
  }

}
