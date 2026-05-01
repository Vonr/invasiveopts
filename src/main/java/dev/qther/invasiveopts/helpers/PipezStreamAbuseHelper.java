package dev.qther.invasiveopts.helpers;

import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import net.neoforged.neoforge.common.util.TriPredicate;

import java.util.List;
import java.util.function.BiPredicate;

public class PipezStreamAbuseHelper {
    public static <T, S, C> boolean canInsertProto(C context, BiPredicate<PipeTileEntity.Connection, Filter<?, T>> connectionMatcher, PipeTileEntity.Connection connection, S stack, List<Filter<?, ?>> filters, TriPredicate<C, Filter<?, T>, S> matcher) {
        if (filters.isEmpty()) {
            return true;
        }

        var noNormalFilters = true;
        for (var erased : filters) {
            var filter = (Filter<?, T>) erased;
            if (!connectionMatcher.test(connection, filter)) {
                continue;
            }

            if (matcher.test(context, filter, stack)) {
                return !filter.isInvert();
            }

            noNormalFilters &= filter.isInvert();
        }

        return noNormalFilters;
    }

    public static <T, S> boolean canInsertProto(BiPredicate<PipeTileEntity.Connection, Filter<?, T>> connectionMatcher, PipeTileEntity.Connection connection, S stack, List<Filter<?, ?>> filters, BiPredicate<Filter<?, T>, S> matcher) {
        return canInsertProto(null, connectionMatcher, connection, stack, filters, (ignored, filter, st) -> matcher.test(filter, st));
    }
}
