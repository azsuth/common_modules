package com.azsuth.appcache;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Global singleton app cache. Can be broken into any number of instances by defining
 * new enum values, i.e. AppCache.NETWORK_RESULTS, AppCache.USER_INPUT, AppCache.TEST_INSTANCE, etc...
 */
public enum AppCache {
    INSTANCE;

    /**
     * Common TypeReference representing an int
     */
    public static final TypeReference<Integer> INTEGER_TYPE_REFERENCE = new TypeReference<Integer>(new Class[]{int.class}, -1) {};
    /**
     * Common TypeReference representing a boolean
     */
    public static final TypeReference<Boolean> BOOLEAN_TYPE_REFERENCE = new TypeReference<Boolean>(new Class[]{boolean.class}, false) {};
    /**
     * Common TypeReference representing a float
     */
    public static final TypeReference<Float> FLOAT_TYPE_REFERENCE = new TypeReference<Float>(new Class[]{float.class}, -1F) {};
    /**
     * Common TypeReference representing a Long
     */
    public static final TypeReference<Long> LONG_TYPE_REFERENCE = new TypeReference<Long>(new Class[]{long.class}, -1L) {};
    /**
     * Common TypeReference representing a String
     */
    public static final TypeReference<String> STRING_TYPE_REFERENCE = new TypeReference<String>() {};

    private HashMap<String, Object> appCache;

    /**
     * Private constructor.
     */
    AppCache() {
        appCache = new HashMap<>();
    }

    /**
     * Adds a value to the cache.
     *
     * @param key String
     * @param object value
     */
    public void put(String key, Object object) {
        appCache.put(key, object);
    }

    /**
     * Type safe cache accessor.  Returns a cached value, or null if one doesn't exist for the given key.
     * If the TypeReference type doesn't match the value type for the given key, returns null.
     *
     * @param key String
     * @param typeReference representing expected value
     * @param <T>
     * @return casted Object if one exists and matches TypeReference, null otherwise
     */
    public <T> T get(String key, TypeReference<T> typeReference) {
        if (appCache.containsKey(key)) {
            try {
                Object obj = appCache.get(key);
                Object targetObj = typeReference.newInstance();
                if (obj.getClass().isInstance(targetObj)) {
                    return (T) obj;
                } else {
                    return null;
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("AppCache reflection error for key %s. Make sure expected Object constructor is reflected in TypeReference arguments.", key), e);
            }
        } else {
            return null;
        }
    }

    /**
     * Type unsafe cache accessor.  Returns a cached value, or null if one doesn't exist for the
     * given key.  Calling code is responsible for casting returned Object, which could throw
     * a ClassCastException and will result in many unchecked cast warnings.
     *
     * It is recommended that <code>get(String key, TypeReference typeReference></code> is used for
     * code cleanliness.
     *
     * @param key
     * @return Object if one exists, null otherwise
     */
    public Object get(String key) {
        if (appCache.containsKey(key)) {
            return appCache.get(key);
        } else {
            return null;
        }
    }

    /**
     * Checks if a value exists for the given key.
     * DOES NOT take into account the type of the object.
     *
     * @param key
     * @return true if a value exists for the given key, false otherwise
     */
    public boolean has(String key) {
        return appCache.containsKey(key);
    }

    /**
     * Removes a value for the given key.
     *
     * @param key
     * @return true if a value was removed, false otherwise
     */
    public boolean remove(String key) {
        return appCache.remove(key) != null;
    }

    /**
     * Clears out the entire cache.
     */
    public void removeAll() {
        appCache.clear();
    }

    /**
     * Class representing a type.  Ensures no runtime class cast exceptions will occur.
     *
     * @param <T> type expected from AppCache.INSTANCE.get(...)
     */
    public abstract static class TypeReference<T> {
        private final Type type;
        private Class<?>[] argTypes = {};
        private Object[] args = {};

        /**
         * Constructor for types that have a zero argument or default constructor.
         */
        protected TypeReference() {
            Type superclass = getClass().getGenericSuperclass();
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter");
            }
            type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        }

        /**
         * Constructor for types that do not have a zero argument or default constructor.
         * Indices in argTypes must correlate to indices in args.
         *
         * @param argTypes Class[] representing constructor argument types
         * @param args Object[] to be passed to non-zero argument constructor
         */
        protected TypeReference(Class[] argTypes, Object... args) {
            this();

            this.args = args;
            this.argTypes = argTypes;
        }

        /**
         * Constructs a new Object of the parameterized type.
         * Uses supplied argTypes to get proper constructor, and supplied args to call
         * that constructor.
         *
         * @return instance of the parameterized type
         * @throws NoSuchMethodException
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @throws InstantiationException
         */
        final public T newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
            Class<?> rawType = type instanceof Class<?>
                    ? (Class<?>) type
                    : (Class<?>) ((ParameterizedType) type).getRawType();
            Constructor<?> constructor = rawType.getConstructor(argTypes);
            return (T) constructor.newInstance(args);
        }
    }
}
