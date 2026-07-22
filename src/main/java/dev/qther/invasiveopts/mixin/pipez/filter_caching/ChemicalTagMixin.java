package dev.qther.invasiveopts.mixin.pipez.filter_caching;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.utils.ChemicalTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.HolderSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntPredicate;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(value = "mekanism"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.FilterCaching.class)
        }
)
@Mixin(ChemicalTag.class)
public class ChemicalTagMixin implements FilterCachingExtension {
    @Shadow
    @Final
    private HolderSet.Named<Chemical> tag;
    @Unique
    IntPredicate invasiveOpts$predicate;

    @Override
    public void invasiveOpts$setPredicate(IntPredicate predicate) {
        invasiveOpts$predicate = predicate;
    }

    @Override
    public IntPredicate invasiveOpts$getPredicate() {
        return invasiveOpts$predicate;
    }

    @WrapMethod(method = "contains(Lmekanism/api/chemical/Chemical;)Z")
    private boolean contains(Chemical block, Operation<Boolean> original) {
        if (invasiveOpts$predicate == null) {
            var key = this.tag.key();
            invasiveOpts$predicate = FilterCachingHelper.makePredicate(key, e -> e.is(key), MekanismAPI.CHEMICAL_REGISTRY.holders());
            FilterCachingHelper.registerExtension(this);
        }

        if (invasiveOpts$predicate != null) {
            return invasiveOpts$predicate.test(((AtomicIdExtension) block).invasiveOpts$getId());
        }

        return original.call(block);
    }
}
