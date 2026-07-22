package dev.qther.invasiveopts.mixin.pipez.filter_caching;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.corelib.tag.ItemTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.IntPredicate;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.FilterCaching.class)
        }
)
@Mixin(ItemTag.class)
public class ItemTagMixin implements FilterCachingExtension {
    @Shadow
    @Final
    private HolderSet.Named<Item> holderSet;
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

    @WrapMethod(method = "contains(Lnet/minecraft/world/item/Item;)Z")
    private boolean contains(Item block, Operation<Boolean> original) {
        if (invasiveOpts$predicate == null) {
            var key = this.holderSet.key();
            invasiveOpts$predicate = FilterCachingHelper.makePredicate(key, e -> e.is(key), BuiltInRegistries.ITEM.holders());
            FilterCachingHelper.registerExtension(this);
        }

        if (invasiveOpts$predicate != null) {
            return invasiveOpts$predicate.test(((AtomicIdExtension) block).invasiveOpts$getId());
        }

        return original.call(block);
    }
}
