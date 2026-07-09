package dev.qther.invasiveopts.mixin.ae2.cache_fuzzy_search_max_value;

import appeng.api.stacks.AEItemKey;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/8891
@Restriction(
        require = {
                @Condition(value = "ae2", versionPredicates = "<=19.2.14"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.AE2.CacheFuzzySearchMaxValue.class)
        }
)
@Mixin(AEItemKey.class)
public class AEItemKeyMixin {
    @Unique
    private int invasiveOpts$maxDamage;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cacheFuzzySearchMaxValue(ItemStack stack, CallbackInfo ci) {
        invasiveOpts$maxDamage = stack.getMaxDamage();
    }

    @WrapMethod(method = "getFuzzySearchMaxValue")
    private int useCachedFuzzySearchMaxValue(Operation<Integer> original) {
        return invasiveOpts$maxDamage;
    }
}
