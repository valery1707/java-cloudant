package client;

import com.google.common.cache.LoadingCache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

public class SampleCache2 {

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

    LoadingCache<String, Object> shapes = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, Object>() {
                public Rectangle load(String key) throws Exception {
                    return createRectangle(key);
                }
            });

    LoadingCache<String, Object> shape2 = CacheBuilder.newBuilder()
            .maximumSize(1000).build(new CacheLoader<String, Object>() {
                public Rectangle load(String key) throws Exception {
                    return null;
                }
            });

    static Rectangle createRectangle(String key) {
        return new Rectangle(key.length(), key.length() * 2);
    }

    public static <T> T find(Class<T> classType, String id,
            LoadingCache<String, Object> cache1) throws Exception {
        return classType.cast(cache1.get(id));
    }

    public static void store(Object obj, String id,
            LoadingCache<String, Object> cache1) throws Exception {
        cache1.put(id, obj);
    }

    public static void main(String[] args) throws Exception {

        Rectangle rect1 = new Rectangle(5, 6);
        SampleCache2 sampleCache = new SampleCache2();
        LoadingCache<String, Object> cache1 = sampleCache.shapes;
        Rectangle rect2;
        String key1 = "key1";
        String key2 = "key2";

        store(rect1, key1, cache1);
        // cache1.put(key1, rect1);

        Class<Rectangle> classType = Rectangle.class;

        rect2 = find(Rectangle.class, key2, cache1);
        // rect2 = classType.cast(cache1.get(key2));
        System.out.println("Cached value for " + key2);
        rect2.printRectangle();

        rect2 = find(classType, key1, cache1);
        // rect2 = classType.cast(cache1.get(key1));
        System.out.println("Cached value for " + key1);
        rect2.printRectangle();

        cache1 = sampleCache.shape2;

        rect2 = classType.cast(cache1.getIfPresent(key2));
        if (rect2 == null)
            System.out.println("Rect2 is null");
        else
            rect2.printRectangle();

        store(rect1, key1, cache1);
        // cache1.put(key1, rect1);

        rect2 = find(classType, key1, cache1);
        // rect2 = classType.cast(cache1.getIfPresent(key1));
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
