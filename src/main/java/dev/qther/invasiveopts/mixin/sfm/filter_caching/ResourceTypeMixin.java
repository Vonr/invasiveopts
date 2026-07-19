package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(value = "sfm", versionPredicates = ">=4.30.0"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.SFM.FilterCaching.class)
        }
)
@Mixin(ResourceType.class)
public class ResourceTypeMixin<STACK, ITEM, CAP> {
    @Inject(method = "matchesStack", at = @At("HEAD"), cancellable = true)
    private void cacheResourceLocation(ResourceIdentifier<STACK, ITEM, CAP> resourceId, Object stack, CallbackInfoReturnable<Boolean> cir) {
        var pex = (FilterCachingExtension) resourceId;
        var pred = pex.invasiveOpts$getPredicate();
        if (pred == null) {
            if (stack instanceof ItemStack) {
                pred = FilterCachingHelper.makePredicate(resourceId, o -> o instanceof ItemStack, e -> resourceId.matchesResourceLocation(e.getKey().location()), BuiltInRegistries.ITEM.entrySet());
                pex.invasiveOpts$setPredicate(pred);
            } else if (stack instanceof FluidStack) {
                pred = FilterCachingHelper.makePredicate(resourceId, o -> o instanceof FluidStack, e -> resourceId.matchesResourceLocation(e.getKey().location()), BuiltInRegistries.FLUID.entrySet());
                pex.invasiveOpts$setPredicate(pred);
            }
        }

        if (pred != null) {
            cir.setReturnValue(pred.test(stack));
        }
    }
}
