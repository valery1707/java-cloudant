/**
 * 
 */
package client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.Jedis;

/**
 * @author ArunIyengar
 * 
 */
public class RedisCache<K, V> implements Cache<K, V> {

    private Jedis cache;

    /**
     * Constructor
     * 
     * @param host
     *            post where Redis is running
     * 
     * */
    public RedisCache(String host) {
        cache = new Jedis(host);
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
    public RedisCache(String host, int port) {
        cache = new Jedis(host, port);
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
    public RedisCache(String host, int port, int timeout) {
        cache = new Jedis(host, port, timeout);
    }

    /**
     * delete all key-value pairs from the current database
     * 
     * */
    @Override
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
    @Override
    public void delete(K key) {
        cache.del(Serializer.serializeToByteArray(key));
    }

    /**
     * delete one or more key-value pairs from the cache
     * 
     * @param keys
     *            iterable data structure containing the keys to delete
     * 
     * */
    @Override
    public void deleteAll(List<K> keys) {
        for (K key : keys) {
            cache.del(Serializer.serializeToByteArray(key));
        }
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
    @Override
    public V get(K key) {
        CacheEntry<V> cacheEntry = getCacheEntry(key);
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
    @Override
    public Map<K, V> getAll(List<K> keys) {
        Map<K, V> hashMap = new HashMap<K, V>();
        for (K key : keys) {
            V value = get(key);
            if (value != null) {
                hashMap.put(key, value);
            }
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
    @Override
    public CacheEntry<V> getCacheEntry(K key) {
        byte[] rawValue = cache.get(Serializer.serializeToByteArray(key));
        if (rawValue == null)
            return null;
        return Serializer.deserializeFromByteArray(rawValue);
    }

    
    /**
     * Return Jedis data structure so users can make direct API calls to
     * it
     * 
     * @return Jedis data structure corresponding to underlying cache
     * 
     * */
    public Jedis getJedis() {
        return cache;
    }

    /**
     * get cache statistics.  For Redis, cache statistics are contained in a string.  The string is
     * returned by RedisCacheStats.getStats()
     * 
     * @return data structure containing statistics
     * 
     * */
    @Override
    public RedisCacheStats getStatistics() {
        return new RedisCacheStats(cache.info());
    }

    /**
     * Output contents of current database
     * */
    public void print() {
        System.out.println("\nContents of Entire Cache\n");
        StringRedisSerializer srs = new StringRedisSerializer();
        // If we know that keys are strings, we don't have to use
        // StringRedisSerializer
        Set<byte[]> keys = cache.keys(srs.serialize("*"));
        for (byte[] key : keys) {
            String keyString = Serializer.deserializeFromByteArray(key);
            System.out.println("Key: " + keyString);
            byte[] rawValue = cache.get(key);
            if (rawValue == null) {
                System.out.println("No value found in cache for keyString " + keyString + "\n");
                continue;
            }
            CacheEntry<V> cacheEntry = Serializer.deserializeFromByteArray(rawValue);
            if (cacheEntry == null) {
                System.out.println("CacheEntry is null for keyString " + keyString + "\n");
                continue;
            }
            cacheEntry.print();
            System.out.println();
        }
        System.out.println("Cache size is: " + size());
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
    @Override
    public void put(K key, V value, long lifetime) {
        CacheEntry<V> cacheEntry = new CacheEntry<V>(value, lifetime
                + Util.getTime());
        put(key, cacheEntry);
    }
    
    private void put(K key, CacheEntry<V> cacheEntry) {
        byte[] array1 = Serializer.serializeToByteArray(key);
        byte[] array2 = Serializer.serializeToByteArray(cacheEntry);
        cache.set(array1, array2);
        
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
    @Override
    public void putAll(Map<K, V> map, long lifetime) {
        Date date = new Date();

        for (Map.Entry<K, V> entry : map.entrySet()) {
            CacheEntry<V> cacheEntry = new CacheEntry<V>(entry.getValue(),
                    lifetime + date.getTime());
            put(entry.getKey(), cacheEntry);
        }

    }


    /**
     * Select the DB having the specified zero-based numeric index.
     * 
     * */
    public String select(int index) {
        return cache.select(index);
    }

    /**
     * Return number of objects in cache
     * 
     * */
    @Override
    public long size() {
        return cache.dbSize();
    }

    void lookup(K key) {
        System.out.println("lookup: CacheEntry value for key: " + key);
        CacheEntry<V> cacheEntry = getCacheEntry(key);
        if (cacheEntry == null)
            System.out.println("Key " + key + " not in cache");
        else
            cacheEntry.print();
    }

}
