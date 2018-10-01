package com.tddapps.model;

import lombok.val;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class HeartBeatListTestHelper {
    public static void ShouldMatch(HeartBeat[] listA, HeartBeat[] listB) {
        assertEquals(listA.length, listB.length);

        Predicate<HeartBeat> listBContainsHeartBeat = hb -> Arrays.asList(listB).contains(hb);

        val allSeededHeartBeatsHaveBeenRetrieved = Arrays.stream(listA)
                .allMatch(listBContainsHeartBeat);

        assertTrue(allSeededHeartBeatsHaveBeenRetrieved);
    }
}
