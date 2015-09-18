/**
 * 
 */
package client;

import com.google.common.cache.CacheStats;

/**
 * @author ArunIyengar
 * 
 */
public class CacheStats1 implements Stats {
    private CacheStats cacheStats;

    CacheStats1(CacheStats stats) {
        cacheStats = stats;
    }

    public CacheStats getStats() {
        return cacheStats;
    }

}
