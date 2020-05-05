import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CacheTest {

    @Test
    public void putGetTest() {
        var cache = new Cache<Integer, String>(10);

        cache.put(1234, "test");

        assertEquals("test", cache.get(1234));

    }

    @Test
    public void evictAtCapacityTest() {
        var cache = new Cache<Integer, String>(2);

        cache.put(1234, "test1");
        cache.put(1235, "test2");
        cache.put(1236, "test3");


        assertNull(cache.get(1234));
        assertEquals("test2", cache.get(1235));
        assertEquals("test3", cache.get(1236));
    }


    @Test
    public void removalTest() {
        var cache = new Cache<Integer, String>(3);

        cache.put(1234, "test1");
        cache.put(1235, "test2");
        cache.put(1236, "test3");

        cache.remove(1235);

        assertEquals("test1", cache.get(1234));
        assertNull(cache.get(1235));
        assertEquals("test3", cache.get(1236));
    }

    @Test
    public void removalAllTest() {
        var cache = new Cache<Integer, String>(3);

        cache.put(1234, "test1");
        cache.put(1235, "test2");
        cache.put(1236, "test3");

        cache.remove(1234);
        cache.remove(1235);
        cache.remove(1236);

        cache.put(1234, "test1");

        assertEquals("test1", cache.get(1234));
        assertNull(cache.get(1235));
        assertNull(cache.get(1236));
    }

    @Test
    public void overWriteTest() {
        var cache = new Cache<Integer, String>(3);

        cache.put(1234, "test1");
        cache.put(1234, "test2");

        assertEquals("test2", cache.get(1234));

    }

    @Test
    public void removeNonPresent() {
        var cache = new Cache<Integer, String>(3);
        cache.remove(1234);
    }


    @Test
    public void printCacheTest() {
        var cache = new Cache<Integer, String>(3);

        cache.put(1234, "test1");
        cache.put(1235, "test2");
        cache.put(1236, "test3");

        assertEquals("Cache{capacity=3, pairs new to old=[(1236 -> test3), (1235 -> test2), (1234 -> test1), Nil]}", cache.toString());
    }

}
