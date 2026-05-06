package dev.qther.invasiveopts.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

public class CachedSort {
    private static final long LONG_LOWER_HALF = 0xFFFF_FFFFL;
    private static final long LONG_UPPER_HALF = ~LONG_LOWER_HALF;
    private static final int INT_LOWER_HALF = 0xFFFF;
    private static final int INT_UPPER_HALF = ~INT_LOWER_HALF;

    /**
     * Sorts {@code list} in-place in ascending order according to the value obtaining by transforming each element into an {@link int} via {@code transform}.
     * Reverse the sort by using bitwise-not (~) on the result of {@code transform}.
     *
     * @param list The list to sort
     * @param transform An {@link int} transform function applied to each element in {@code list}
     * @param <T> The type of the elements in {@code list}
     */
    public static <T> void sortByCachedIntKey(List<T> list, ToIntFunction<T> transform) {
        var size = list.size();
        if (size <= 1) {
            return;
        }

        if (size == 2) {
            if (transform.applyAsInt(list.get(0)) > transform.applyAsInt(list.get(1))) {
                Collections.swap(list, 0, 1);
            }
            return;
        }

        long[] indices = new long[size];
        for (int i = 0; i < size; i++) {
            indices[i] = ((long) transform.applyAsInt(list.get(i)) << 32L) | i;
        }

        Arrays.sort(indices);
        for (int i = 0; i < size; i++) {
            int index = (int) (indices[i] & LONG_LOWER_HALF);
            while (index < i) {
                index = (int) (indices[index] & LONG_LOWER_HALF);
            }
            indices[i] = (indices[i] & LONG_UPPER_HALF) | index;
            Collections.swap(list, i, index);
        }
    }

    @FunctionalInterface
    public interface ToShortFunction<T> {
        /**
         * Applies this function to the given argument.
         *
         * @param value the function argument
         * @return the function result
         */
        short applyAsShort(T value);
    }

    /**
     * Sorts {@code list} in-place in ascending order according to the value obtaining by transforming each element into a {@link short} via {@code transform}.
     * Reverse the sort by using bitwise-not (~) on the result of {@code transform}.
     *
     * @param list The list to sort
     * @param transform An {@link short} transform function applied to each element in {@code list}
     * @param <T> The type of the elements in {@code list}
     */
    public static <T> void sortByCachedShortKey(List<T> list, ToShortFunction<T> transform) {
        var size = list.size();
        if (size <= 1) {
            return;
        }

        if (size == 2) {
            if (transform.applyAsShort(list.get(0)) > transform.applyAsShort(list.get(1))) {
                Collections.swap(list, 0, 1);
            }
            return;
        }

        if (size > 65536) {
            sortByCachedIntKey(list, transform::applyAsShort);
            return;
        }

        int[] indices = new int[size];
        for (int i = 0; i < size; i++) {
            indices[i] = ((int) transform.applyAsShort(list.get(i)) << 16) | i;
        }

        Arrays.sort(indices);
        for (int i = 0; i < size; i++) {
            int index = indices[i] & INT_LOWER_HALF;
            while (index < i) {
                index = indices[index] & INT_LOWER_HALF;
            }
            indices[i] = (indices[i] & INT_UPPER_HALF) | index;
            Collections.swap(list, i, index);
        }
    }
}
