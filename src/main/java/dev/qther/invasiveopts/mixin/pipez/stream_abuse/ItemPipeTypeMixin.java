package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import de.maxhenkel.pipez.datacomponents.ItemData;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.PipezStreamAbuseHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.PipezStreamAbuseTester.class)
        }
)
@Mixin(ItemPipeType.class)
public abstract class ItemPipeTypeMixin extends PipeTypeMixin<Item, ItemData> {
    @Shadow
    protected abstract boolean matches(HolderLookup.Provider provider, Filter<?, Item> filter, ItemStack stack);

    @WrapMethod(method = "canInsert")
    public boolean stopStreamAbuse(HolderLookup.Provider provider, PipeTileEntity.Connection connection, ItemStack stack, List<Filter<?, ?>> filters, Operation<Boolean> original) {
        return PipezStreamAbuseHelper.canInsertProto(provider, this::matchesConnection, connection, stack, filters, this::matches);
    }
}
