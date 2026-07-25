package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.With;
import ca.teamdman.sfml.ast.WithClause;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.IntPredicate;

@Restriction(
        require = {
                @Condition(value = "sfm", versionPredicates = ">=4.30.0"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.SFM.FilterCaching.class)
        }
)
@Mixin(With.class)
public abstract class WithMixin implements FilterCachingExtension {
    @Shadow
    @Final
    private WithClause condition;

    @Shadow
    @Final
    private With.WithMode mode;

    @Unique
    IntPredicate invasiveOpts$predicate;

    @Inject(method = "matchesStack", at = @At("HEAD"), cancellable = true)
    private <STACK> void matchesStack(ResourceType<STACK, ?, ?> resourceType, STACK stack, CallbackInfoReturnable<Boolean> cir) {
        if (invasiveOpts$predicate == null) {
            var whitelist = this.mode == With.WithMode.WITH;
            if (stack instanceof ItemStack) {
                //noinspection unchecked
                invasiveOpts$predicate = FilterCachingHelper.makePredicate(this, e -> this.condition.matchesStack(resourceType, (STACK) e.value().getDefaultInstance()) == whitelist, BuiltInRegistries.ITEM.holders());
                FilterCachingHelper.registerExtension(this);
            } else if (stack instanceof FluidStack) {
                //noinspection unchecked
                invasiveOpts$predicate = FilterCachingHelper.makePredicate(this, e -> this.condition.matchesStack(resourceType, (STACK) new FluidStack(e.value(), 1000)) == whitelist, BuiltInRegistries.FLUID.holders());
                FilterCachingHelper.registerExtension(this);
            }
        }

        if (invasiveOpts$predicate != null) {
            cir.setReturnValue(invasiveOpts$predicate.test(((AtomicIdExtension) stack).invasiveOpts$getId()));
        }
    }

    @Override
    public void invasiveOpts$setPredicate(IntPredicate predicate) {
        invasiveOpts$predicate = predicate;
    }

    @Override
    public IntPredicate invasiveOpts$getPredicate() {
        return invasiveOpts$predicate;
    }
}
