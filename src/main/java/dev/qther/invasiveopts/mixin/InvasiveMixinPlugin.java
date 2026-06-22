package dev.qther.invasiveopts.mixin;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import dev.qther.invasiveopts.Config;
import dev.qther.invasiveopts.CrashReportUpgrade;
import dev.qther.invasiveopts.InvasiveOptsMixinCanceller;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

import java.io.File;
import java.util.List;
import java.util.Set;

public class InvasiveMixinPlugin extends RestrictiveMixinConfigPlugin {
    private static boolean CONFIG_LOADED = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (!CONFIG_LOADED) {
            Config.load(new File("./config/invasive_optimizations.properties"));
            CONFIG_LOADED = true;
        }
        MixinCancellerRegistrar.register(new InvasiveOptsMixinCanceller());
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    static {
        CrashReportUpgrade.registerCrashLogInfo();
    }
}
