/**
 * 
 */
package client;

import com.google.common.cache.CacheStats;

/**
 * @author ArunIyengar
 * 
 */
public class InProcessCacheStats {
    private CacheStats cacheStats;

    InProcessCacheStats(CacheStats stats) {
        cacheStats = stats;
    }

    public CacheStats getStats() {
        return cacheStats;
    }

}
