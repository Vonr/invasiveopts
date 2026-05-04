package dev.qther.invasiveopts.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

public class CachedSort {
    private static final long LOWER_HALF = 0xFFFF_FFFFL;
    private static final long UPPER_HALF = ~LOWER_HALF;

    public static <T> void sortByCachedIntKey(List<T> list, ToIntFunction<T> mappingFunction) {
        long[] indices = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            indices[i] = ((long) mappingFunction.applyAsInt(list.get(i)) << 32L) | i;
        }

        Arrays.sort(indices);
        for (int i = 0; i < list.size(); i++) {
            int index = (int) (indices[i] & LOWER_HALF);
            while (index < i) {
                index = (int) (indices[index] & LOWER_HALF);
            }
            indices[i] = (indices[i] & UPPER_HALF) | index;
            Collections.swap(list, i, index);
        }
    }
}
