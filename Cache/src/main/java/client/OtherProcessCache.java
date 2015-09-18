/**
 * 
 */
package client;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
//java.util.Date.getTime() method returns how many milliseconds have passed since 
//January 1, 1970, 00:00:00 GMT

import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

/**
 * @author ArunIyengar
 * 
 */
public class OtherProcessCache<K, V> {

    private BinaryJedis cache;

    /**
     * Constructor
     * 
     * @param host
     *            post where Redis is running
     * 
     * */
    public OtherProcessCache(String host) {
        cache = new BinaryJedis(host);
    }

    /**
     * Constructor
     * 
     * @param host
     *            post where Redis is running
     * @param port
     *            port number
     * 
     * */
    public OtherProcessCache(String host, int port) {
        cache = new BinaryJedis(host, port);
    }

    /**
     * Constructor
     * 
     * @param host
     *            post where Redis is running
     * @param port
     *            port number
     * @param timeout
     *            number of seconds before Jedis closes an idle connection
     * 
     * */
    public OtherProcessCache(String host, int port, int timeout) {
        cache = new BinaryJedis(host, port, timeout);
    }

    /**
     * delete all key-value pairs from the current database
     * 
     * */
    public void clear() {
        cache.flushDB();
    }

    /**
     * Close a Redis connection
     * 
     * */
    public void close() {
        cache.close();
    }

    /**
     * delete a key-value pair from the cache
     * 
     * @param key
     *            key corresponding to value
     * 
     * */
    public void delete(K key) {
        cache.del(Serializer.serializeToByteArray(key));
    }

    /**
     * delete all key-value pairs from all databases
     * 
     * */
    public String flushAll() {
        return cache.flushAll();
    }

    /**
     * look up a value in the cache
     * 
     * @param key
     *            key corresponding to value
     * @return value corresponding to key, null if key is not in cache or if
     *         value is expired
     * 
     * */
    public V get(K key) {
        byte[] rawValue = cache.get(Serializer.serializeToByteArray(key));
        if (rawValue == null)
            return null;
        CacheEntry<V> cacheEntry = Serializer
                .deserializeFromByteArray(rawValue);
        ;
        if (cacheEntry == null)
            return null;
        if (cacheEntry.getExpirationTime() >= Util.getTime())
            return cacheEntry.getValue();
        return null;
    }

    /**
     * Return BinaryJedis data structure so users can make direct API calls to
     * it
     * 
     * @return BinaryJedis data structure corresponding to underlying cache
     * 
     * */
    public BinaryJedis getBinaryJedis() {
        return cache;
    }

    /**
     * cache a key-value pair
     * 
     * @param key
     *            key associated with value
     * @param value
     *            value associated with key
     * @param lifetime
     *            lifetime in milliseconds associated with data
     * 
     * */
    public void put(K key, V value, long lifetime) {
        CacheEntry<V> cacheEntry = new CacheEntry<V>(value, lifetime
                + Util.getTime());
        byte[] array1 = Serializer.serializeToByteArray(key);
        byte[] array2 = Serializer.serializeToByteArray(cacheEntry);
        cache.set(array1, array2);
    }

    /**
     * Select the DB having the specified zero-based numeric index.
     * 
     * */
    public String select(int index) {
        return cache.select(index);
    }

    public static void test3() {
        OtherProcessCache<String, HashMap<String, Integer>> opc = new OtherProcessCache<String, HashMap<String, Integer>>(
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
            opc.put("bar", hm, 5000);
            HashMap<String, Integer> hm2 = opc.get("bar");
            System.out.println("fetched hash table: " + hm2);
        } finally {
            opc.close();
        }
        System.out.println("test3 has finished executing");
    }

    public static void test2() {
        OtherProcessCache<String, String> opc = new OtherProcessCache<String, String>(
                "localhost", 6379, 60);
        String key1 = "foo";
        String key2 = "bar";

        try {
            opc.put(key1, key2, 5000);
            String key3 = opc.get(key1);
            System.out.println("Value of lookup is: " + key3);
        } finally {
            opc.close();
        }
        System.out.println("test2 has finished executing");
    }

    public static void test1() {
        BinaryJedis jedis;
        String key1 = "foo";
        String key2 = "bar";
        byte[] val1 = Serializer.serializeToByteArray(key1);
        byte[] val2 = Serializer.serializeToByteArray(key2);
        jedis = new Jedis("localhost", 6379, 60);
        jedis.set(val1, val2);
        byte[] val3 = jedis.get(val1);
        String key3 = Serializer.deserializeFromByteArray(val3);
        System.out.println("lookup value is: " + key3);
        jedis.close();
        System.out.println("test1 has finished executing");

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // test1();
        // test2();
        test3();
    }

}
