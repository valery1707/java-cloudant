package client;

import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class SampleCache {

    static class Rectangle implements java.io.Serializable {
        int length;
        int width;
        private static final long serialVersionUID = 1;

        Rectangle(int x, int y) {
            length = x;
            width = y;
        }

        void printRectangle() {
            System.out.println("length: " + length);
            System.out.println("width: " + width);
        }
    }

    LoadingCache<String, Rectangle> shapes = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, Rectangle>() {
                public Rectangle load(String key) throws Exception {
                    return createRectangle(key);
                }
            });

    LoadingCache<String, Rectangle> shape2 = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, Rectangle>() {
                public Rectangle load(String key) throws Exception {
                    return null;
                }
            });

    static Rectangle createRectangle(String key) {
        return new Rectangle(key.length(), key.length() * 2);
    }

    public static void main(String[] args) throws Exception {

        Rectangle rect1 = new Rectangle(5, 6);
        SampleCache sampleCache = new SampleCache();
        LoadingCache<String, Rectangle> cache1 = sampleCache.shapes;
        Rectangle rect2;
        String key1 = "key1";
        String key2 = "key2";

        cache1.put(key1, rect1);

        rect2 = cache1.get(key2);
        System.out.println("Cached value for " + key2);
        rect2.printRectangle();

        rect2 = cache1.get(key1);
        System.out.println("Cached value for " + key1);
        rect2.printRectangle();

        cache1 = sampleCache.shape2;

        rect2 = cache1.getIfPresent(key2);
        if (rect2 == null)
            System.out.println("Rect2 is null");
        else
            rect2.printRectangle();

        cache1.put(key1, rect1);

        rect2 = cache1.getIfPresent(key1);
        if (rect2 == null)
            System.out.println("Rect2 is null");
        else
            rect2.printRectangle();

        System.out.println("Main finished executing");

        /*
         * System.out.println(x.getClass().getCanonicalName());
         * System.out.println(System.getProperty("java.runtime.version"));
         */
    }

}
