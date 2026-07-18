package dev.qther.invasiveopts.extensions;

import java.util.function.Predicate;

public interface FilterCachingExtension {
    void invasiveOpts$setPredicate(Predicate<Object> predicate);
    Predicate<Object> invasiveOpts$getPredicate();
}
