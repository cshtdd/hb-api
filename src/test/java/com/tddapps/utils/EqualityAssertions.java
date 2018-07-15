package com.tddapps.utils;

import static org.junit.jupiter.api.Assertions.*;

public class EqualityAssertions {
    public static <T> void shouldBeEqual(T i1, T i2){
        assertEquals(i1, i2);
        assertEquals(i2, i1);

        if (i1 != null && i2 != null){
            assertEquals(i1.hashCode(), i2.hashCode());
            assertEquals(i2.hashCode(), i1.hashCode());
        }
    }

    public static <T> void shouldNotBeEqual(T i1, T i2){
        assertNotEquals(i1, i2);
        assertNotEquals(i2, i1);

        if (i1 != null && i2 != null){
            assertNotEquals(i1.hashCode(), i2.hashCode());
            assertNotEquals(i2.hashCode(), i1.hashCode());
        }
    }
}
