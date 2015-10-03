/**
 * 
 */
package client;

import java.io.Serializable;

/**
 * @author ArunIyengar This class represents what is actually stored in the
 *         cache.
 * 
 */
public class CacheEntry<V> implements Serializable {

    private static final long serialVersionUID = 1L;
    private V value;
    private long expirationTime;

    public CacheEntry(V val, long expires) {
        value = val;
        expirationTime = expires;
    }

    public V getValue() {
        return value;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void print() {
        System.out.println("CacheEntry value: " + value);
        System.out.println("CacheEntry expiration time: " + expirationTime);
        System.out.println("Milliseconds until expiration: "
                + (expirationTime - Util.getTime()));
    }
}
