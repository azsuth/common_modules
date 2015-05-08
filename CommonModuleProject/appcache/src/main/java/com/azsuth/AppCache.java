package com.azsuth;

import java.util.HashMap;

/**
 * Global singleton app cache.  Currently only works with objects that have
 * empty constructors.
 */
public enum AppCache {
    INSTANCE;

    private HashMap<String, Object> appCache;

    private AppCache() {
        appCache = new HashMap<>();
    }

    public void put(String key, Object object) {
        appCache.put(key, object);
    }

    public <T> T get(String key, AppCacheTypeReference<T> typeReference) {
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

    public boolean has(String key) {
        return appCache.containsKey(key);
    }

    public boolean remove(String key) {
        return appCache.remove(key) != null;
    }

    public void removeAll() {
        appCache.clear();
    }
}
