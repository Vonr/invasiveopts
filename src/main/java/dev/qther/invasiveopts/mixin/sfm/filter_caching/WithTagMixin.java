package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.TagMatcher;
import ca.teamdman.sfml.ast.WithClause;
import ca.teamdman.sfml.ast.WithTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Restriction(
        require = {
                @Condition(value = "sfm", versionPredicates = ">=4.30.0"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.SFM.FilterCaching.class)
        }
)
@Mixin(WithTag.class)
public abstract class WithTagMixin implements WithClause {
    @Shadow
    @Final
    private TagMatcher tagMatcher;

    @Inject(method = "matchesStack", at = @At("HEAD"), cancellable = true)
    private <STACK> void matchesStack(ResourceType<STACK, ?, ?> resourceType, STACK stack, CallbackInfoReturnable<Boolean> cir) {
        Objects.requireNonNull(this.tagMatcher);

        var pex = (FilterCachingExtension) this.tagMatcher;
        var pred = pex.invasiveOpts$getPredicate();
        if (pred == null) {
            if (stack instanceof ItemStack) {
                //noinspection unchecked
                pred = FilterCachingHelper.makePredicate(new FilterCachingHelper.SFMTagMatcherWrapper(this.tagMatcher), o -> o instanceof ItemStack, e -> resourceType.getTagsForStack((STACK) e.getValue().getDefaultInstance()).anyMatch(this.tagMatcher::testResourceLocation), BuiltInRegistries.ITEM.entrySet());
                pex.invasiveOpts$setPredicate(pred);
            } else if (stack instanceof FluidStack) {
                //noinspection unchecked
                pred = FilterCachingHelper.makePredicate(new FilterCachingHelper.SFMTagMatcherWrapper(this.tagMatcher), o -> o instanceof FluidStack, e -> resourceType.getTagsForStack((STACK) new FluidStack(e.getValue(), 1000)).anyMatch(this.tagMatcher::testResourceLocation), BuiltInRegistries.FLUID.entrySet());
                pex.invasiveOpts$setPredicate(pred);
            }
        }

        if (pred != null) {
            cir.setReturnValue(pred.test(stack));
        }
    }
}
