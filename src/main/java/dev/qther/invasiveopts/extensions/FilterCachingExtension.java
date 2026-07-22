package dev.qther.invasiveopts.extensions;

import java.util.function.IntPredicate;

public interface FilterCachingExtension {
    void invasiveOpts$setPredicate(IntPredicate predicate);
    IntPredicate invasiveOpts$getPredicate();
}
