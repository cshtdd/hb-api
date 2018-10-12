package com.tddapps.utils;

import java.util.Collection;
import java.util.stream.Collectors;

public abstract class CollectionExtensions {
    public static <T> Collection<T> Intersection(Collection<T> l1, Collection<T> l2){
        return l1
                .stream()
                .filter(l2::contains)
                .collect(Collectors.toList());
    }

    public static <T> Collection<T> Difference(Collection<T> all, Collection<T> subset){
        return all
                .stream()
                .filter(e -> !subset.contains(e))
                .collect(Collectors.toList());
    }
}
