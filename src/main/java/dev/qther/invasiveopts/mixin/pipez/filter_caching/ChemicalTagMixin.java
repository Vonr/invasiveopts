package dev.qther.invasiveopts.mixin.pipez.filter_caching;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.utils.ChemicalTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(value = "mekanism"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.FilterCaching.class)
        }
)
@Mixin(ChemicalTag.class)
public class ChemicalTagMixin implements FilterCachingExtension {
    @Unique
    Predicate<Object> invasiveOpts$predicate;

    @Override
    public void invasiveOpts$setPredicate(Predicate<Object> predicate) {
        invasiveOpts$predicate = predicate;
    }

    @Override
    public Predicate<Object> invasiveOpts$getPredicate() {
        return invasiveOpts$predicate;
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(HolderSet.Named<Chemical> tag, ResourceLocation id, CallbackInfo ci) {
        if (invasiveOpts$predicate == null) {
            var key = tag.key();
            invasiveOpts$predicate = FilterCachingHelper.makePredicate(key, e -> e.is(key), MekanismAPI.CHEMICAL_REGISTRY.holders());
        }
    }

    @WrapMethod(method = "contains(Lmekanism/api/chemical/Chemical;)Z")
    private boolean contains(Chemical block, Operation<Boolean> original) {
        if (invasiveOpts$predicate != null) {
            return invasiveOpts$predicate.test(block);
        }

        return original.call(block);
    }
}
