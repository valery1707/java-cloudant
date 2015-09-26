/**
 * 
 */
package client;

/**
 * @author ArunIyengar
 * 
 */
public class CacheStats2 implements Stats {
    private String cacheStats;

    CacheStats2(String stats) {
        cacheStats = stats;
    }

    public String getStats() {
        return cacheStats;
    }

}
