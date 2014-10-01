package root.util;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * Created by Semyon on 05.09.2014.
 *
 * Class for cacheing, it's self clearing
 * ttl is time to live for a record
 * ttc is time to clean for all the cache object
 *
 * @param <K> key capture
 * @param <V> value capture
 */
public class Cache<K, V> implements Map<K, V> {

    public static final long DEFAULT_TTL = 600000; //10 минут, нормально
    public static final long DEFAULT_TTC = 60000;

    private long lastCleanTime = 0;

    private long ttl;
    private long ttc;

    private final Object mMonitor = new Object();

    private Map<K, CacheValue<K, V>> mMap;

    public Cache() {
        this(DEFAULT_TTL, DEFAULT_TTC);
    }

    public Cache(final long ttl, final long ttc) {
        mMap = new ConcurrentHashMap<>();
        this.ttl = ttl;
        this.ttc = ttc;
    }

    public V put(final K key, final V data) {
        if (needToClean()) {
            clean();
        }
        CacheValue<K, V> cacheValue = new CacheValue<K, V>(key, data, ttl);
        mMap.put(key, cacheValue);
        return data;
    }

    @Override
    public V get(final Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (needToClean()) {
            clean();
        }
        CacheValue<K, V> cacheValue = mMap.get(key);
        if (cacheValue == null) {
            return null;
        }
        cacheValue.hit();
        return cacheValue.getData();
    }

    private class CacheValue<K, V> {

        private long lastHitTime = 0;
        private long ttl;

        private K key;
        private V data;

        public CacheValue(final K key, final V data, final long ttl) {
            this.key = key;
            this.data = data;
            this.ttl = ttl;
            lastHitTime = new Date().getTime();
        }

        public V getData() {
            return data;
        }

        public K getKey() {
            return key;
        }

        public void setData(final V value) {
            this.data = value;
        }

        public boolean isExpired() {
            return (new Date()).getTime() > lastHitTime + ttl;
        }

        public void hit() {
            lastHitTime = new Date().getTime();
        }

    }

    private boolean needToClean() {
        return (new Date()).getTime() > lastCleanTime + ttc;
    }

    private void clean() {
        synchronized (mMonitor) {
            Set<K> keySet = mMap.keySet();
            for (K key : keySet) {
                CacheValue<K, V> value = mMap.get(key);
                if (value.isExpired()) {
                    mMap.remove(key);
                }
            }
        }
        long curTime = (new Date()).getTime();
        lastCleanTime = curTime;
    }

    @Override
    public void clear() {
        mMap.clear();
    }

    @Override
    public boolean containsKey(final Object key) {
        return mMap.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        Set<Entry<K, CacheValue<K, V>>> entrySet = mMap.entrySet();
        if (entrySet == null) {
            return false;
        }
        for (Entry<K, CacheValue<K, V>> cur : entrySet) {
            if (cur.getValue().equals(value) || cur.getValue() == value) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        Set<Entry<K, CacheValue<K, V>>> entrySet = mMap.entrySet();
        if (entrySet == null) {
            return null;
        }
        Set<java.util.Map.Entry<K, V>> cacheEntrySet = new HashSet<>();
        for (Entry<K, CacheValue<K, V>> en : entrySet) {
            CacheEntry<K, V> cacheEntry = new CacheEntry<>(en.getValue());
            cacheEntrySet.add(cacheEntry);
        }
        return cacheEntrySet;
    }

    @Override
    public boolean isEmpty() {
        return mMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return mMap.keySet();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        // TODO Auto-generated method stub

    }

    @Override
    public V remove(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (needToClean()) {
            clean();
        }
        CacheValue<K, V> cacheValue = mMap.remove(key);
        if (cacheValue == null) {
            return null;
        }
        return cacheValue.getData();
    }

    @Override
    public int size() {
        return mMap.size();
    }

    @Override
    public Collection<V> values() {
        // TODO Auto-generated method stub
        return null;
    }


    public class CacheEntry<K, V> implements Map.Entry<K, V> {

        private final CacheValue<K, V> cacheValue;

        public CacheEntry(final CacheValue<K, V> cacheValue) {
            this.cacheValue = cacheValue;
        }

        @Override
        public K getKey() {
            return cacheValue.getKey();
        }

        @Override
        public V getValue() {
            return cacheValue.getData();
        }

        @Override
        public V setValue(final V arg0) {
            this.cacheValue.setData(arg0);
            return arg0;
        }

    }

}