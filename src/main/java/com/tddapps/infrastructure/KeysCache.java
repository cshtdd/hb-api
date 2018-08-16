package com.tddapps.infrastructure;

public interface KeysCache {
    boolean Contains(String key);
    void Add(String key);
}
