package dev.qther.invasiveopts.mixin.placebo.streamless_hashing;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.qther.invasiveopts.MixinTesters;
import dev.shadowsoffire.placebo.util.CachedObject;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.function.ToIntFunction;

// https://github.com/Shadows-of-Fire/Placebo/pull/122
@Restriction(
        require = {
                @Condition(value = "placebo", versionPredicates = "<=9.9.1"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Placebo.StreamlessHashing.class)
        }
)
@Mixin(CachedObject.class)
public class CachedObjectMixin {
    @WrapMethod(method = "hashComponents")
    private static ToIntFunction<ItemStack> streamlessHashing(DataComponentType<?>[] types, Operation<ToIntFunction<ItemStack>> original) {
        return stack -> {
            // adapted from Arrays#hashCode
            if (types == null) {
                return 0;
            }

            int result = 1;

            for (var ty : types) {
                var component = stack.get(ty);
                if (component == null) {
                    continue;
                }

                result = 31 * result + component.hashCode();
            }

            return result;
        };
    }
}
