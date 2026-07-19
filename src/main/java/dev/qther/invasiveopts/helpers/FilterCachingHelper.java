package dev.qther.invasiveopts.helpers;

import ca.teamdman.sfml.ast.TagMatcher;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import org.apache.commons.lang3.ArrayUtils;

import java.util.BitSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

public class FilterCachingHelper {
    private static final Object2ObjectOpenHashMap<Object, Predicate<Object>> PREDICATE_CACHE = new Object2ObjectOpenHashMap<>();

    private static final AlwaysTrue ALWAYS_TRUE = new AlwaysTrue();
    private static final AlwaysFalse ALWAYS_FALSE = new AlwaysFalse();

    private static class AlwaysTrue implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            return true;
        }
    }

    private static class AlwaysFalse implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            return false;
        }
    }

    private record BitSetMatcher(BitSet bitset, int start) implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            var id = ((AtomicIdExtension) o).invasiveOpts$getId();
            if (id < start) {
                return false;
            }

            return bitset.get(id);
        }
    }

    private record InvertedBitSetMatcher(BitSet bitset, int start) implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            var id = ((AtomicIdExtension) o).invasiveOpts$getId();
            if (id < start) {
                return false;
            }

            return !bitset.get(id);
        }
    }

    private record ArrayMatcher(int[] array) implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            var id = ((AtomicIdExtension) o).invasiveOpts$getId();
            return array.length <= 8 ? ArrayUtils.contains(array, id) : IntArrays.binarySearch(array, id) >= 0;
        }
    }

    private record InvertedArrayMatcher(int[] array) implements Predicate<Object> {
        @Override
        public boolean test(Object o) {
            var id = ((AtomicIdExtension) o).invasiveOpts$getId();
            return array.length <= 8 ? !ArrayUtils.contains(array, id) : IntArrays.binarySearch(array, id) < 0;
        }
    }

    public static <Ext> Predicate<Object> makePredicate(Object key, Predicate<Map.Entry<ResourceKey<Ext>, Ext>> entryPredicate, Set<Map.Entry<ResourceKey<Ext>, Ext>> registry) {
        return PREDICATE_CACHE.computeIfAbsent(key, ignored -> {
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

            if (falseList.isEmpty()) {
                return ALWAYS_TRUE;
            }
            if (trueList.isEmpty()) {
                return ALWAYS_FALSE;
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
                    return new ArrayMatcher(trues);
                }

                var bitset = new BitSet(truesSpan);
                var start = trues[0];
                for (var n : trues) {
                    bitset.set(n - start);
                }

                return new BitSetMatcher(bitset, start);
            } else {
                if (falsesSpan > trues.length * Integer.SIZE) {
                    // Sparse
                    return new InvertedArrayMatcher(falses);
                }

                var bitset = new BitSet(falsesSpan);
                var start = trues[0];
                for (var n : falses) {
                    bitset.set(n - start);
                }

                return new InvertedBitSetMatcher(bitset, start);
            }
        });
    }

    public static class SFMTagMatcherWrapper {
        public TagMatcher inner;

        public SFMTagMatcherWrapper(TagMatcher inner) {
            this.inner = inner;
        }

        @SuppressWarnings("SlowListContainsAll")
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof SFMTagMatcherWrapper that) {
                return Objects.equals(this.inner.namespacePattern, that.inner.namespacePattern) && this.inner.pathElementPatterns.containsAll(that.inner.pathElementPatterns) && that.inner.pathElementPatterns.containsAll(this.inner.pathElementPatterns);
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.inner.namespacePattern, this.inner.pathElementPatterns);
        }
    }
}
