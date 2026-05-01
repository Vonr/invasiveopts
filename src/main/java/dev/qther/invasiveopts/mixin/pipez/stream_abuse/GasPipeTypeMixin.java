package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.GasPipeType;
import de.maxhenkel.pipez.datacomponents.GasData;
import dev.qther.invasiveopts.helpers.PipezStreamAbuseHelper;
import dev.qther.invasiveopts.testers.PipezStreamAbuseTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
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
                @Condition(value = "mekanism"),
                @Condition(type = Condition.Type.TESTER, tester = PipezStreamAbuseTester.class)
        }
)
@Mixin(GasPipeType.class)
public abstract class GasPipeTypeMixin extends PipeTypeMixin<Chemical, GasData> {
    @Shadow
    protected abstract boolean matches(Filter<?, Chemical> filter, ChemicalStack stack);

    @Inject(method = "canInsert", at = @At("HEAD"), cancellable = true)
    public void stopStreamAbuse(PipeTileEntity.Connection connection, ChemicalStack stack, List<Filter<?, ?>> filters, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(PipezStreamAbuseHelper.canInsertProto(this::matchesConnection, connection, stack, filters, this::matches));
    }
}
