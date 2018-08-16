package com.tddapps.infrastructure;

import java.util.HashSet;

public class KeysCacheStub implements KeysCache {
    private final HashSet<String> keys = new HashSet<>();

    @Override
    public boolean Contains(String key) {
        return keys.contains(key);
    }

    @Override
    public void Add(String key) {
        keys.add(key);
    }

    public HashSet<String> getKeys() {
        return keys;
    }
}
