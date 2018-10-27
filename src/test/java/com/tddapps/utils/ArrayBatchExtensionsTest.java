package com.tddapps.utils;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayBatchExtensionsTest {
    @Test
    void CannotSplitNull(){
        try {
            ArrayBatchExtensions.Split(null, 10);
            fail("Should have thrown");
        } catch (NullPointerException e){
            assertNotNull(e);
        }
    }

    @Test
    void CannotSplitNegativeSizes(){
        try {
            ArrayBatchExtensions.Split(new Object[]{}, -1);
            fail("Should have thrown");
        } catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    @Test
    void CannotSplitSizesSmallerThanOne(){
        try {
            ArrayBatchExtensions.Split(new Object[]{}, 0);
            fail("Should have thrown");
        } catch (IllegalArgumentException e){
            assertNotNull(e);
        }
    }

    @Test
    void ReturnsEmptyWhenInputIsEmpty(){
        assertEquals(0, ArrayBatchExtensions.Split(new Integer[]{}, 1).length);
    }

    @Test
    void DoesNotSplitAnythingWhenBatchSizeEqualsInputSize(){
        val input = new Integer[]{
                1, 2, 3
        };

        val actual = ArrayBatchExtensions.Split(input, 3);

        assertEquals(1, actual.length);
        assertArrayEquals(new Integer[]{1, 2, 3}, actual[0]);
    }

    @Test
    void DoesNotSplitAnythingWhenBatchSizeIsTooLargerThanTheInputSize(){
        val input = new Integer[]{
                1, 2, 3
        };

        val actual = ArrayBatchExtensions.Split(input, 4);

        assertEquals(1, actual.length);
        assertArrayEquals(new Integer[]{1, 2, 3}, actual[0]);
    }

    @Test
    void SplitsArrayInBatchesOfOne(){
        val input = new Integer[]{
                1, 2, 3, 4, 5
        };

        val actual = ArrayBatchExtensions.Split(input, 1);

        assertEquals(5, actual.length);
        assertArrayEquals(new Integer[]{1}, actual[0]);
        assertArrayEquals(new Integer[]{2}, actual[1]);
        assertArrayEquals(new Integer[]{3}, actual[2]);
        assertArrayEquals(new Integer[]{4}, actual[3]);
        assertArrayEquals(new Integer[]{5}, actual[4]);
    }

    @Test
    void SplitsArrayInLargerBatches(){
        val input = new Integer[]{
                1, 2, 3, 4, 5
        };

        val actual = ArrayBatchExtensions.Split(input, 2);

        assertEquals(3, actual.length);
        assertArrayEquals(new Integer[]{1, 2}, actual[0]);
        assertArrayEquals(new Integer[]{3, 4}, actual[1]);
        assertArrayEquals(new Integer[]{5}, actual[2]);
    }

    @Test
    void CanProducePerfectBatchesWhenLengthsAreMultipleOfEachOther(){
        val input = new Integer[]{
                1, 2, 3, 4, 5, 6
        };

        val actual = ArrayBatchExtensions.Split(input, 3);

        assertEquals(2, actual.length);
        assertArrayEquals(new Integer[]{1, 2, 3}, actual[0]);
        assertArrayEquals(new Integer[]{4, 5, 6}, actual[1]);
    }
}
