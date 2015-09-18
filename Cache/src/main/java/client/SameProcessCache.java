/**
 * 
 */
package client;

import java.util.Date;
// java.util.Date.getTime() method returns how many milliseconds have passed since 
// January 1, 1970, 00:00:00 GMT
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @author ArunIyengar This cache implementation stores data in the same process
 *         as the executing program
 * 
 */
public class SameProcessCache<K, V> implements Cache<K, V> {

    private LoadingCache<K, CacheEntry<V>> cache;

    /**
     * Constructor
     * 
     * @param maxObjects
     *            maximum number of objects which can be stored before
     *            replacement starts
     * 
     * */
    public SameProcessCache(long maxObjects) {
        cache = CacheBuilder.newBuilder().maximumSize(maxObjects)
                .build(new CacheLoader<K, CacheEntry<V>>() {
                    public CacheEntry<V> load(K key) throws Exception {
                        return null;
                    }
                });

    }

    /**
     * delete all key-value pairs from the cache
     * 
     * */
    public void clear() {
        cache.invalidateAll();
    }

    /**
     * delete a key-value pair from the cache
     * 
     * @param key
     *            key corresponding to value
     * 
     * */
    public void delete(K key) {
        cache.invalidate(key);
    }

    /**
     * delete one or more key-value pairs from the cache
     * 
     * @param keys
     *            iterable data structure containing the keys to delete
     * 
     * */
    public void deleteAll(List<K> keys) {
        cache.invalidateAll(keys);
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
        CacheEntry<V> cacheEntry = cache.getIfPresent(key);
        if (cacheEntry == null)
            return null;
        if (cacheEntry.getExpirationTime() >= Util.getTime())
            return cacheEntry.getValue();
        return null;
    }

    /**
     * look up one or more values in the cache. Don't return expired values.
     * 
     * @param keys
     *            iterable data structure containing the keys to look up
     * @return map containing key-value pairs corresponding to unexpired data in
     *         the cache
     * 
     * */
    public Map<K, V> getAll(List<K> keys) {
        Map<K, CacheEntry<V>> cacheMap = cache.getAllPresent(keys);
        Map<K, V> hashMap = new HashMap<K, V>();
        Date date = new Date();

        for (Map.Entry<K, CacheEntry<V>> entry : cacheMap.entrySet()) {
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry.getExpirationTime() >= date.getTime())
                hashMap.put(entry.getKey(), cacheEntry.getValue());
        }
        return hashMap;
    }

    /**
     * look up a CacheEntry in the cache. The CacheEntry may correspond to
     * expired data. This method can be used to revalidate cached objects whose
     * expiration times have passed
     * 
     * @param key
     *            key corresponding to value
     * @return value corresponding to key (may be expired), null if key is not
     *         in cache
     * 
     * */
    public CacheEntry<V> getCacheEntry(K key) {
        return cache.getIfPresent(key);
    }

    /**
     * get cache statistics
     * 
     * @return data structure containing statistics
     * 
     * */
    public CacheStats1 getStatistics() {
        return new CacheStats1(cache.stats());
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
        cache.put(key, cacheEntry);
    }

    /**
     * cache one or more key-value pairs
     * 
     * @param map
     *            map containing key-value pairs to cache
     * @param value
     *            value associated with each key-value pair
     * @param lifetime
     *            lifetime in milliseconds associated with each key-value pair
     * 
     * */
    public void putAll(Map<K, V> map, long lifetime) {
        Date date = new Date();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            CacheEntry<V> cacheEntry = new CacheEntry<V>(entry.getValue(),
                    lifetime + date.getTime());
            cache.put(entry.getKey(), cacheEntry);
        }

    }

    /**
     * Return number of objects in cache
     * 
     * */
    public long size() {
        return cache.size();
    }

    void lookup(K key) {
        System.out.println("lookup: CacheEntry value for key: " + key);
        CacheEntry<V> cacheEntry = cache.getIfPresent(key);
        if (cacheEntry == null)
            System.out.println("Key " + key + " not in cache");
        else
            cacheEntry.print();
    }

    public void print() {
        Map<K, CacheEntry<V>> cacheMap = cache.asMap();
        System.out.println("\nContents of Entire Cache\n");
        for (Map.Entry<K, CacheEntry<V>> entry : cacheMap.entrySet()) {
            System.out.println("Key: " + entry.getKey());
            CacheEntry<V> cacheEntry = entry.getValue();
            if (cacheEntry == null)
                System.out.println("CacheEntry is null");
            else
                cacheEntry.print();
            System.out.println();
        }
        System.out.println("Cache size is: " + size());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        int numObjects = 2000;
        SameProcessCache<String, Integer> spc = new SameProcessCache<String, Integer>(
                numObjects);
        String key1, key2, key3;
        key1 = "key1";
        key2 = "key2";
        key3 = "key3";
        long lifetime = 3000;
        spc.put(key1, 42, lifetime);
        spc.lookup(key1);
        spc.lookup(key2);
        spc.put(key2, 43, lifetime);
        spc.put(key3, 44, lifetime);
        spc.print();
        CacheStats1 stats1 = spc.getStatistics();
        System.out.println("Cache size: " + spc.size());
        System.out.println("Hit rate: " + stats1.getStats().hitRate());
        System.out.println("Main finished executing");

    }

}
