package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import dev.qther.invasiveopts.extensions.SFMFilterCachingExtension;
import dev.qther.invasiveopts.helpers.SFMFilterCachingHelper;
import it.unimi.dsi.fastutil.ints.IntArrayList;
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
        var pex = (SFMFilterCachingExtension) resourceId;
        var pred = pex.invasiveOpts$getPredicate();
        if (pred == null) {
            if (stack instanceof ItemStack) {
                var bits = new IntArrayList();
                for (var entry : BuiltInRegistries.ITEM.entrySet()) {
                    if (resourceId.matchesResourceLocation(entry.getKey().location())) {
                        bits.add(((AtomicIdExtension) entry.getValue()).invasiveOpts$getId());
                    }
                }

                if (bits.size() == BuiltInRegistries.ITEM.size()) {
                    pred = (Object o) -> o instanceof ItemStack;
                    pex.invasiveOpts$setPredicate(pred);
                } else {
                    var skippedBitSet = SFMFilterCachingHelper.listToBitSet(bits);
                    var bitset = skippedBitSet.first();
                    var start = skippedBitSet.secondInt();
                    pred = (Object o) -> {
                        if (!(o instanceof ItemStack itemStack)) {
                            return false;
                        }

                        var id = ((AtomicIdExtension) itemStack.getItem()).invasiveOpts$getId() - start;
                        return id >= 0 && bitset.get(id);
                    };
                    pex.invasiveOpts$setPredicate(pred);
                }
            } else if (stack instanceof FluidStack) {
                var bits = new IntArrayList();
                for (var entry : BuiltInRegistries.FLUID.entrySet()) {
                    if (resourceId.matchesResourceLocation(entry.getKey().location())) {
                        bits.add(((AtomicIdExtension) entry.getValue()).invasiveOpts$getId());
                    }
                }

                if (bits.size() == BuiltInRegistries.FLUID.size()) {
                    pred = (Object o) -> o instanceof FluidStack;
                    pex.invasiveOpts$setPredicate(pred);
                } else {
                    var skippedBitSet = SFMFilterCachingHelper.listToBitSet(bits);
                    var bitset = skippedBitSet.first();
                    var start = skippedBitSet.secondInt();
                    pred = (Object o) -> {
                        if (!(o instanceof FluidStack fluidStack)) {
                            return false;
                        }

                        var id = ((AtomicIdExtension) fluidStack.getFluid()).invasiveOpts$getId() - start;
                        return id >= 0 && bitset.get(id);
                    };
                    pex.invasiveOpts$setPredicate(pred);
                }
            }
        }

        if (pred != null) {
            cir.setReturnValue(pred.test(stack));
        }
    }
}
