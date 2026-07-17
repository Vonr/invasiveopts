package dev.qther.invasiveopts.helpers;

import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;

import java.util.BitSet;

public class SFMFilterCachingHelper {
    public static ObjectIntPair<BitSet> listToBitSet(IntList a) {
        if (a.isEmpty()) {
            return ObjectIntPair.of(new BitSet(), 0);
        }

        var bitset = new BitSet(a.size());

        var start = a.getInt(0);
        for (var n : a) {
            start = Math.min(start, n);
        }

        for (var n : a) {
            bitset.set(n - start);
        }

        return ObjectIntPair.of(bitset, start);
    }
}
