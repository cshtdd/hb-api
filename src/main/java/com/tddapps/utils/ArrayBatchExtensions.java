package com.tddapps.utils;

import lombok.val;

import java.util.Arrays;
import java.util.stream.IntStream;

public abstract class ArrayBatchExtensions {
    public static Object[][] Split(Object[] input, int batchSize){
        if (input == null) throw new NullPointerException("input");
        if (batchSize < 1) throw new IllegalArgumentException("batchSize");

        if (input.length == 0) {
            return new Object[][]{};
        }

        return SplitInternal(input, Math.min(batchSize, input.length));
    }

    private static Object[][] SplitInternal(Object[] input, int batchSize){
        val batchesCount = (int)Math.ceil((double)input.length / (double) batchSize);

        return IntStream.range(0, batchesCount)
                .mapToObj(idx -> Arrays.copyOfRange(
                        input, rangeStart(batchSize, idx), Math.min(rangeEnd(batchSize, idx), input.length)))
                .toArray(Object[][]::new);
    }

    private static int rangeStart(int batchSize, int idx) {
        return idx * batchSize;
    }

    private static int rangeEnd(int batchSize, int idx) {
        return rangeStart(batchSize, idx + 1);
    }
}
