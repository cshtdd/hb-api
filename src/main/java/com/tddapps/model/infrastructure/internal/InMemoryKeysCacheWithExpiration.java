package com.tddapps.model.infrastructure.internal;

import com.tddapps.model.infrastructure.KeysCache;
import lombok.val;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;

public class InMemoryKeysCacheWithExpiration implements KeysCache, Closeable {
    public static final int TTL_SECONDS = 30;
    public static final int MAX_CAPACITY = 100;

    private final CacheManager cacheManager;
    private final Cache<String, String> cache;

    public InMemoryKeysCacheWithExpiration(){
        val expiryPolicy = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(TTL_SECONDS));

        val configurationBuilder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(    MAX_CAPACITY))
                .withExpiry(expiryPolicy);

        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder()
                .withCache(getCacheName(), configurationBuilder)
                .build(true);

        cache = cacheManager.getCache(getCacheName(), String.class, String.class);
    }

    private String getCacheName() {
        return getClass().getName();
    }

    @Override
    public boolean Contains(String key) {
        return cache.containsKey(key);
    }

    @Override
    public void Add(String key) {
        cache.put(key, "1");
    }

    @Override
    public void close() throws IOException {
        cacheManager.removeCache(getCacheName());
        cacheManager.close();
    }
}
