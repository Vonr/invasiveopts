package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.FluidPipeType;
import de.maxhenkel.pipez.datacomponents.FluidData;
import dev.qther.invasiveopts.helpers.PipezStreamAbuseHelper;
import dev.qther.invasiveopts.testers.PipezStreamAbuseTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezStreamAbuseTester.class)
        }
)
@Mixin(FluidPipeType.class)
public abstract class FluidPipeTypeMixin extends PipeTypeMixin<Fluid, FluidData> {
    @Shadow
    protected abstract boolean matches(HolderLookup.Provider provider, Filter<?, Fluid> filter, FluidStack stack);

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void stopStreamAbuse(HolderLookup.Provider provider, PipeTileEntity.Connection connection, FluidStack stack, List<Filter<?, ?>> filters, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(PipezStreamAbuseHelper.canInsertProto(provider, this::matchesConnection, connection, stack, filters, this::matches));
    }
}
