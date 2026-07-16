package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.TagMatcher;
import ca.teamdman.sfml.ast.WithClause;
import ca.teamdman.sfml.ast.WithTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import dev.qther.invasiveopts.extensions.SFMPredicateExtension;
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

import java.util.BitSet;
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

        var pex = (SFMPredicateExtension) this.tagMatcher;
        var pred = pex.invasiveOpts$getPredicate();
        if (pred == null) {
            if (stack instanceof ItemStack) {
                var bitset = new BitSet();
                for (var entry : BuiltInRegistries.ITEM.entrySet()) {
                    var tags = resourceType.getTagsForStack((STACK) entry.getValue().getDefaultInstance());
                    if (tags.anyMatch(this.tagMatcher::testResourceLocation)) {
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
                var bitset = new BitSet();
                for (var entry : BuiltInRegistries.FLUID.entrySet()) {
                    var tags = resourceType.getTagsForStack((STACK) new FluidStack(entry.getValue(), 1000));
                    if (tags.anyMatch(this.tagMatcher::testResourceLocation)) {
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
