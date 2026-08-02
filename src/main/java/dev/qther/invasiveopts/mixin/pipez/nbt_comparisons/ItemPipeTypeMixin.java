package dev.qther.invasiveopts.mixin.pipez.nbt_comparisons;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.utils.ComponentUtils;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.PipezFilterExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
public abstract class ItemPipeTypeMixin extends PipeType {
    @WrapMethod(method = "matches")
    private boolean matches(HolderLookup.Provider provider, Filter<?, Item> filter, ItemStack stack, Operation<Boolean> original) {
        var tag = filter.getTag();
        CompoundTag metadata = filter.getMetadata();
        if (metadata == null) {
            return tag == null || tag.contains(stack.getItem());
        }
            
        var patch = ((PipezFilterExtension) filter).invasiveopts$getComponentsPatch();
        if (patch == null) {
            CompoundTag stackNBT = ComponentUtils.getTag(provider, stack);
            if (filter.isExactMetadata()) {
                if (!this.deepExactCompare(metadata, stackNBT)) {
                    return false;
                } else {
                    return filter.getTag() == null || filter.getTag().contains(stack.getItem());
                }
            } else if (stackNBT.isEmpty()) {
                return metadata.size() <= 0;
            } else if (!this.deepFuzzyCompare(metadata, stackNBT)) {
                return false;
            } else {
                return filter.getTag() == null || filter.getTag().contains(stack.getItem());
            }
        }

        if (tag != null && !tag.contains(stack.getItem())) {
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

            return true;
        }
    }
}
