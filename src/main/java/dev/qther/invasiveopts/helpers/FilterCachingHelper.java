package dev.qther.invasiveopts.helpers;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.resources.ResourceKey;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class FilterCachingHelper {
    public static <Ext, Stack> Predicate<Object> makePredicate(Class<Stack> stackClass, Function<Stack, Ext> stackToExt, Predicate<Map.Entry<ResourceKey<Ext>, Ext>> entryPredicate, Set<Map.Entry<ResourceKey<Ext>, Ext>> registry) {
        var trues = new IntArrayList();
        var falses = new IntArrayList();

        for (var entry : registry) {
            var id = ((AtomicIdExtension) entry.getValue()).invasiveOpts$getId();
            if (entryPredicate.test(entry)) {
                trues.push(id);
            } else {
                falses.push(id);
            }
        }

        if (trues.isEmpty()) {
            return o -> false;
        }
        if (falses.isEmpty()) {
            return stackClass::isInstance;
        }

        trues.unstableSort(IntComparators.NATURAL_COMPARATOR);
        falses.unstableSort(IntComparators.NATURAL_COMPARATOR);

        var truesSpan = Integer.MAX_VALUE;
        if (!trues.isEmpty()) {
            truesSpan = trues.getInt(trues.size() - 1) - trues.getInt(0);
        }
        var falsesSpan = Integer.MAX_VALUE;
        if (!falses.isEmpty()) {
            falsesSpan = falses.getInt(falses.size() - 1) - falses.getInt(0);
        }

        BitSet bitset;
        if (truesSpan <= falsesSpan) {
            bitset = new BitSet(truesSpan);
            var start = trues.getInt(0);
            for (var n : trues) {
                bitset.set(n - start);
            }

            return o -> {
                if (!stackClass.isInstance(o)) {
                    return false;
                }

                // noinspection unchecked
                var id = ((AtomicIdExtension) stackToExt.apply((Stack) o)).invasiveOpts$getId();
                if (id < start) {
                    return false;
                }
                return bitset.get(id - start);
            };
        } else {
            bitset = new BitSet(falsesSpan);
            var start = trues.getInt(0);
            for (var n : falses) {
                bitset.set(n - start);
            }

            return o -> {
                if (!stackClass.isInstance(o)) {
                    return false;
                }

                // noinspection unchecked
                var id = ((AtomicIdExtension) stackToExt.apply((Stack) o)).invasiveOpts$getId();
                if (id < start) {
                    return true;
                }
                return !bitset.get(id - start);
            };
        }
    }
}
