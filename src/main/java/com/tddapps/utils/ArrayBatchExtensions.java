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
                .map(idx -> rangeStart(idx, batchSize))
                .mapToObj(startIdx -> Arrays.copyOfRange(input, startIdx, rangeEnd(startIdx, batchSize, input.length)))
                .toArray(Object[][]::new);
    }

    private static int rangeStart(int idx, int batchSize) {
        return idx * batchSize;
    }

    private static int rangeEnd(int startIdx, int batchSize, int maxLength) {
        return Math.min(startIdx + batchSize, maxLength);
    }
}
