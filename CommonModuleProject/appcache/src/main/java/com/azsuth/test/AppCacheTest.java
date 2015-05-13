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
        AppCache.INSTANCE.put("test", "test");
        String value = AppCache.INSTANCE.get("test", new AppCache.TypeReference<String>() {
        });

        assertEquals(value, "test");
    }

    @Test
    public void testGetArgumentConstructor() {
        TestArgumentConstructor test = new TestArgumentConstructor("test", 56);
        AppCache.INSTANCE.put("test", test);

        TestArgumentConstructor test1 = AppCache.INSTANCE.get("test", new AppCache.TypeReference<TestArgumentConstructor>(new Class[]{String.class, int.class}, "", -1) {
        });

        assertEquals(test, test1);
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

    @After
    public void tearDown() {
        AppCache.INSTANCE.removeAll();
    }
}
