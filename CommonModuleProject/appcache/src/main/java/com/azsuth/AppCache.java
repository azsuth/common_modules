package com.azsuth;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Global singleton app cache.
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
     * Returns a cached value, or null if one doesn't exist for the given key.
     * If the TypeReference type doesn't match the value type for the given key, returns null.
     *
     * @param key String
     * @param typeReference representing expected value
     * @param <T>
     * @return object if one exists and matches TypeReference, null otherwise
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
                return null;
            }
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
         *
         *
         * @return
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
