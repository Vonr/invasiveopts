package dev.qther.invasiveopts;

import com.bawnorton.mixinsquared.api.MixinCanceller;

import java.util.List;

public class InvasiveOptsMixinCanceller implements MixinCanceller {
    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        if (Config.Keys.Accessories.ROUNDABOUT_COLLECTION.enabled && mixinClassName.equals("io.wispforest.accessories.mixin.PatchedDataComponentMapMixin")) {
            return true;
        }

        if (Config.Keys.Pastel.NUKE_ITEM_PREDICATE_MIXIN.enabled && mixinClassName.equals("earth.terrarium.pastel.mixin.ItemPredicateMixin")) {
            return true;
        }

        return false;
    }
}
