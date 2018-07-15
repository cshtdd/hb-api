package com.tddapps.utils;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.StringExtensions.*;
import static org.junit.Assert.*;

public class StringExtensionsTest {
    @Test
    public void EmptyWhenNullWorksAsExpected(){
        assertEquals("", EmptyWhenNull(null));
        assertEquals("", EmptyWhenNull(""));
        assertEquals("a", EmptyWhenNull("a"));
    }
}
