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

        return false;
    }
}
