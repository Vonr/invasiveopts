package dev.qther.invasiveopts.mixin.pipez.filter_caching;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.corelib.tag.BlockTag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.FilterCachingExtension;
import dev.qther.invasiveopts.helpers.FilterCachingHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.FilterCaching.class)
        }
)
@Mixin(BlockTag.class)
public class BlockTagMixin implements FilterCachingExtension {
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
    private void init(HolderSet.Named<Block> tagKey, CallbackInfo ci) {
        if (invasiveOpts$predicate == null) {
            var key = tagKey.key();
            invasiveOpts$predicate = FilterCachingHelper.makePredicate(key, e -> e.is(key), BuiltInRegistries.BLOCK.holders());
        }
    }

    @WrapMethod(method = "contains(Lnet/minecraft/world/level/block/Block;)Z")
    private boolean contains(Block block, Operation<Boolean> original) {
        if (invasiveOpts$predicate != null) {
            return invasiveOpts$predicate.test(block);
        }

        return original.call(block);
    }
}
