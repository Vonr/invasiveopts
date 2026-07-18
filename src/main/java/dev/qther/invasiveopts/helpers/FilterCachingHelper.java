package dev.qther.invasiveopts.helpers;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import net.minecraft.resources.ResourceKey;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class FilterCachingHelper {
    public static <Ext> Predicate<Object> makePredicate(Predicate<Object> isStack, Predicate<Map.Entry<ResourceKey<Ext>, Ext>> entryPredicate, Set<Map.Entry<ResourceKey<Ext>, Ext>> registry) {
        var trueList = new IntArrayList();
        var falseList = new IntArrayList();

        for (var entry : registry) {
            var id = ((AtomicIdExtension) entry.getValue()).invasiveOpts$getId();
            if (entryPredicate.test(entry)) {
                trueList.push(id);
            } else {
                falseList.push(id);
            }
        }

        if (trueList.isEmpty()) {
            return o -> false;
        }
        if (falseList.isEmpty()) {
            return isStack;
        }

        trueList.trim();
        falseList.trim();

        var trues = trueList.elements();
        var falses = falseList.elements();
        IntArrays.radixSort(trues);
        IntArrays.radixSort(falses);

        var truesSpan = Integer.MAX_VALUE;
        if (trues.length != 0) {
            truesSpan = trues[trues.length - 1] - trues[0];
        }
        var falsesSpan = Integer.MAX_VALUE;
        if (falses.length != 0) {
            falsesSpan = falses[falses.length - 1] - falses[0];
        }

        if (truesSpan <= falsesSpan) {
            if (truesSpan > trues.length * Integer.SIZE) {
                // Sparse
                return o -> {
                    if (!isStack.test(o)) {
                        return false;
                    }

                    var id = ((AtomicIdExtension) o).invasiveOpts$getId();
                    return IntArrays.binarySearch(trues, id) >= 0;
                };
            }

            var bitset = new BitSet(truesSpan);
            var start = trues[0];
            for (var n : trues) {
                bitset.set(n - start);
            }

            return o -> {
                if (!isStack.test(o)) {
                    return false;
                }

                var id = ((AtomicIdExtension) o).invasiveOpts$getId();
                if (id < start) {
                    return false;
                }
                return bitset.get(id - start);
            };
        } else {
            if (falsesSpan > trues.length * Integer.SIZE) {
                // Sparse
                return o -> {
                    if (!isStack.test(o)) {
                        return false;
                    }

                    var id = ((AtomicIdExtension) o).invasiveOpts$getId();
                    return IntArrays.binarySearch(falses, id) < 0;
                };
            }

            var bitset = new BitSet(falsesSpan);
            var start = trues[0];
            for (var n : falses) {
                bitset.set(n - start);
            }

            return o -> {
                if (!isStack.test(o)) {
                    return false;
                }

                var id = ((AtomicIdExtension) o).invasiveOpts$getId();
                if (id < start) {
                    return true;
                }
                return !bitset.get(id - start);
            };
        }
    }
}
