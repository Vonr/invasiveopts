package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.FluidPipeType;
import de.maxhenkel.pipez.datacomponents.FluidData;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.PipezStreamAbuseHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.StreamAbuse.class)
        }
)
@Mixin(FluidPipeType.class)
public abstract class FluidPipeTypeMixin extends PipeTypeMixin<Fluid, FluidData> {
    @Shadow
    protected abstract boolean matches(HolderLookup.Provider provider, Filter<?, Fluid> filter, FluidStack stack);

    @WrapMethod(method = "canInsert")
    public boolean stopStreamAbuse(HolderLookup.Provider provider, PipeTileEntity.Connection connection, FluidStack stack, List<Filter<?, ?>> filters, Operation<Boolean> original) {
        return PipezStreamAbuseHelper.canInsertProto(provider, this::matchesConnection, connection, stack, filters, this::matches);
    }
}
