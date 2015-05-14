package com.azsuth.test;

import com.azsuth.AppCache;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by andrewsutherland on 5/12/15.
 */
public class AppCacheTest {
    @Test
    public void testHas() {
        AppCache.INSTANCE.put("test", "some_value");

        assertTrue(AppCache.INSTANCE.has("test"));
    }

    @Test
    public void testRemove() {
        AppCache.INSTANCE.put("test", "test");
        boolean removed = AppCache.INSTANCE.remove("test");

        assertTrue(removed);
    }

    @Test
    public void testRemoveAll() {
        AppCache.INSTANCE.put("test", "test");
        AppCache.INSTANCE.removeAll();

        boolean test = AppCache.INSTANCE.has("test");

        assertFalse(test);
    }

    @Test
    public void testGetNoArgumentConstructor() {
        AppCache.INSTANCE.put("test", new TestNoArgumentConstructor());
        TestNoArgumentConstructor value = AppCache.INSTANCE.get("test", new AppCache.TypeReference<TestNoArgumentConstructor>() {
        });

        assertEquals(value.testString, "test");
        assertEquals(value.testInt, 42);
    }

    @Test
    public void testGetArgumentConstructor() {
        TestArgumentConstructor test = new TestArgumentConstructor("test", 42);
        AppCache.INSTANCE.put("test", test);

        TestArgumentConstructor value = AppCache.INSTANCE.get("test", new AppCache.TypeReference<TestArgumentConstructor>(new Class[]{String.class, int.class}, "", -1) {
        });

        assertEquals(test, value);
    }

    @Test
    public void testBuiltInIntegerType() {
        AppCache.INSTANCE.put("test", 5);

        Integer test = AppCache.INSTANCE.get("test", AppCache.INTEGER_TYPE_REFERENCE);

        if (test != null) {
            assertEquals(test.intValue(), 5);
        } else {
            fail();
        }
    }

    @Test
    public void testBuiltInBooleanType() {
        AppCache.INSTANCE.put("test", false);

        Boolean test = AppCache.INSTANCE.get("test", AppCache.BOOLEAN_TYPE_REFERENCE);

        if (test != null) {
            assertEquals(test, false);
        } else {
            fail();
        }
    }

    @Test
    public void testBuiltInFloatType() {
        AppCache.INSTANCE.put("test", 5.67F);

        Float test = AppCache.INSTANCE.get("test", AppCache.FLOAT_TYPE_REFERENCE);

        if (test != null) {
            assertEquals(test, 5.67F, 0.0F);
        } else {
            fail();
        }
    }

    @Test
    public void testBuiltInLongType() {
        AppCache.INSTANCE.put("test", 123456L);

        Long test = AppCache.INSTANCE.get("test", AppCache.LONG_TYPE_REFERENCE);

        if (test != null) {
            assertEquals(test.longValue(), 123456L);
        } else {
            fail();
        }
    }

    @Test
    public void testBuiltInStringType() {
        AppCache.INSTANCE.put("test", "this is a test");

        String test = AppCache.INSTANCE.get("test", AppCache.STRING_TYPE_REFERENCE);

        if (test != null) {
            assertEquals(test, "this is a test");
        } else {
            fail();
        }
    }

    @Test(expected = RuntimeException.class)
    public void testRuntimeException() {
        AppCache.INSTANCE.put("test", 5);
        Integer test = AppCache.INSTANCE.get("test", new AppCache.TypeReference<Integer>() {
        });

        fail();
    }

    @Test
    public void benchmarkAppcache100() {
        benchmarkAppcache(100);
    }

    @Test
    public void benchmarkAppcache1000() {
        benchmarkAppcache(1000);
    }

    @Test
    public void benchmarkAppcache10000() {
        benchmarkAppcache(10000);
    }

    @Test
    public void benchmarkAppcache100000() {
        benchmarkAppcache(100000);
    }

    @After
    public void tearDown() {
        AppCache.INSTANCE.removeAll();
    }

    private void benchmarkAppcache(int count) {
        System.out.println(String.format("benchmarking with count: %d", count));

        long startTime;
        long endTime;
        String value;

        // test putting strings
        startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            AppCache.INSTANCE.put(String.valueOf(i), "test");
        }

        endTime = System.currentTimeMillis();
        System.out.println(String.format("put %d strings took %dms", count, endTime - startTime));

        // test getting casted strings
        startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            value = (String) AppCache.INSTANCE.get(String.valueOf(i));
        }

        endTime = System.currentTimeMillis();
        System.out.println(String.format("get %d casted strings took %dms", count, endTime - startTime));

        // test getting type referenced strings
        startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            value = AppCache.INSTANCE.get(String.valueOf(i), AppCache.STRING_TYPE_REFERENCE);
        }

        endTime = System.currentTimeMillis();

        System.out.println(String.format("get %d type referenced strings took %dms\n", count, endTime - startTime));
    }
}
