package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.datacomponents.AbstractPipeTypeData;
import dev.qther.invasiveopts.testers.pipez.PipezStreamAbuseTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezStreamAbuseTester.class)
        }
)
@Mixin(PipeType.class)
public abstract class PipeTypeMixin<T, D extends AbstractPipeTypeData<T>> {
    @Shadow
    public abstract boolean matchesConnection(PipeTileEntity.Connection connection, Filter<?, T> filter);
}
