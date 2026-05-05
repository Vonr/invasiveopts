package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.GasPipeType;
import de.maxhenkel.pipez.datacomponents.GasData;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.PipezStreamAbuseHelper;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(value = "mekanism"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.PipezStreamAbuseTester.class)
        }
)
@Mixin(GasPipeType.class)
public abstract class GasPipeTypeMixin extends PipeTypeMixin<Chemical, GasData> {
    @Shadow
    protected abstract boolean matches(Filter<?, Chemical> filter, ChemicalStack stack);

    @WrapMethod(method = "canInsert")
    public boolean stopStreamAbuse(PipeTileEntity.Connection connection, ChemicalStack stack, List<Filter<?, ?>> filters, Operation<Boolean> original) {
        return PipezStreamAbuseHelper.canInsertProto(this::matchesConnection, connection, stack, filters, this::matches);
    }
}
