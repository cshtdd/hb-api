package com.tddapps;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SampleTest {
    @Test
    public void CanSum(){
        assertEquals(2, 4 + (-2));
    }

    @Test
    @Disabled
    public void CannotMultiply(){
        assertEquals(25, 5 * 4);
    }
}
