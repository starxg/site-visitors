package com.starxg.site.visitors.counter;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * MemoryCounter
 * 
 * @author huangxingguang
 */
public class MemoryCounter implements Counter {
    protected final Cache<String, Long> cache;

    /**
     * based on Caffeine
     */
    public MemoryCounter() {
        cache = Caffeine.newBuilder().build();
    }

    @Override
    public long incr(String key) {
        ConcurrentMap<String, Long> map = cache.asMap();
        map.putIfAbsent(key, 0L);
        return map.compute(key, (s, c) -> Objects.isNull(c) ? 1L : c + 1);
    }

}
