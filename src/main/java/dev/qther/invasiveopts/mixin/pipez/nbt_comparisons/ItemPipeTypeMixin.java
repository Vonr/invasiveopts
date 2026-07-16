package dev.qther.invasiveopts.mixin.pipez.nbt_comparisons;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.PipezFilterExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Objects;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.NbtComparisons.class)
        }
)
@Mixin(ItemPipeType.class)
public class ItemPipeTypeMixin {
    @WrapMethod(method = "matches")
    private boolean matches(HolderLookup.Provider provider, Filter<?, Item> filter, ItemStack stack, Operation<Boolean> original) {
        if (filter.getMetadata() == null) {
            return filter.getTag() == null || filter.getTag().contains(stack.getItem());
        }

        var patch = ((PipezFilterExtension) filter).invasiveopts$getComponentsPatch();
        if (patch == null) {
            return false;
        }

        var stackComponents = stack.getComponentsPatch();

        if (stackComponents.isEmpty()) {
            return patch.isEmpty();
        } else if (filter.isExactMetadata()) {
            return patch.equals(stackComponents);
        } else {
            for (var entry : patch.entrySet()) {
                if (!Objects.equals(entry.getValue(), stackComponents.get(entry.getKey()))) {
                    return false;
                }
            }

            return filter.getTag() == null || filter.getTag().contains(stack.getItem());
        }
    }
}
