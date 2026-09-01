package dev.qther.invasiveopts.mixin.pipez.nbt_comparisons;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.utils.ComponentUtils;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.PipezFilterExtension;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

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
        if (patch != null) {
            if (tag != null && !tag.contains(stack.getItem())) {
                return false;
            }

            var stackComponents = stack.getComponentsPatch();
            return patch.size() == stackComponents.size() && patch.equals(stackComponents);
        }

        if (filter.getTag() != null && !filter.getTag().contains(stack.getItem())) {
            return false;
        }

        if (filter.isExactMetadata()) {
            if (metadata.size() != stack.getComponentsPatch().size()) {
                return false;
            }

            CompoundTag stackNBT = ComponentUtils.getTag(provider, stack);
            return this.deepExactCompare(metadata, stackNBT);
        }

        if (stack.getComponentsPatch().size() < metadata.size()) {
            return false;
        }

        if (stack.isComponentsPatchEmpty()) {
            return metadata.size() <= 0;
        }

        var keys = metadata.getAllKeys();
        var cap = metadata.size();
        var ks = new DataComponentType<?>[cap];
        var vs = new Optional<?>[cap];
        var len = 0;
        var iter = Reference2ObjectMaps.fastIterator(stack.getComponentsPatch().map);

        while (len < cap && iter.hasNext()) {
            var entry = iter.next();
            var keyLoc = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey());
            if (keyLoc == null || !keys.contains(keyLoc.toString())) {
                continue;
            }

            ks[len] = entry.getKey();
            vs[len] = entry.getValue();
            len++;
        }

        if (len < cap) {
            return false;
        }

        var relevant = new DataComponentPatch(new Reference2ObjectArrayMap<>(ks, vs));
        var stackNBT = (CompoundTag) DataComponentPatch.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), relevant).result().orElseGet(CompoundTag::new);

        return this.deepFuzzyCompare(metadata, stackNBT);
    }
}
