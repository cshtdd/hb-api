package com.tddapps.utils;

import org.junit.jupiter.api.Test;

import static com.tddapps.utils.StringExtensions.*;
import static org.junit.Assert.*;

class StringExtensionsTest {
    @Test
    void EmptyWhenNullWorksAsExpected(){
        assertEquals("", EmptyWhenNull(null));
        assertEquals("", EmptyWhenNull(""));
        assertEquals("a", EmptyWhenNull("a"));
    }
}
