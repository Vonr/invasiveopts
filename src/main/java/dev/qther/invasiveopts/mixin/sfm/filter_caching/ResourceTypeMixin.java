package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import dev.qther.invasiveopts.extensions.SFMPredicateExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.BitSet;

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
        var pex = (SFMPredicateExtension) resourceId;
        var pred = pex.invasiveOpts$getPredicate();
        if (pred == null) {
            if (stack instanceof ItemStack) {
                var bitset = new BitSet(0);
                for (var entry : BuiltInRegistries.ITEM.entrySet()) {
                    if (resourceId.matchesResourceLocation(entry.getKey().location())) {
                        bitset.set(((AtomicIdExtension) entry.getValue()).invasiveOpts$getId());
                    }
                }

                if (bitset.cardinality() == BuiltInRegistries.ITEM.size()) {
                    pred = (Object o) -> o instanceof ItemStack;
                    pex.invasiveOpts$setPredicate(pred);
                } else {
                    // trimToSize() in a roundabout way because trimToSize() is private
                    final var bits = (BitSet) bitset.clone();
                    pred = (Object o) -> o instanceof ItemStack itemStack && bits.get(((AtomicIdExtension) itemStack.getItem()).invasiveOpts$getId());
                    pex.invasiveOpts$setPredicate(pred);
                }
            } else if (stack instanceof FluidStack) {
                var bitset = new BitSet(0);
                for (var entry : BuiltInRegistries.FLUID.entrySet()) {
                    if (resourceId.matchesResourceLocation(entry.getKey().location())) {
                        bitset.set(((AtomicIdExtension) entry.getValue()).invasiveOpts$getId());
                    }
                }

                if (bitset.cardinality() == BuiltInRegistries.FLUID.size()) {
                    pred = (Object o) -> o instanceof FluidStack;
                    pex.invasiveOpts$setPredicate(pred);
                } else {
                    // trimToSize() in a roundabout way because trimToSize() is private
                    final var bits = (BitSet) bitset.clone();
                    pred = (Object o) -> o instanceof FluidStack fluidStack && bits.get(((AtomicIdExtension) fluidStack.getFluid()).invasiveOpts$getId());
                    pex.invasiveOpts$setPredicate(pred);
                }
            }
        }

        if (pred != null) {
            cir.setReturnValue(pred.test(stack));
        }
    }
}
