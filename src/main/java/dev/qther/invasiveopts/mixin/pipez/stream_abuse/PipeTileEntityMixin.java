package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import dev.qther.invasiveopts.testers.pipez.PipezStreamAbuseTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezStreamAbuseTester.class)
        }
)
@Mixin(PipeTileEntity.class)
public abstract class PipeTileEntityMixin {
    @Shadow
    @Nullable
    protected List<PipeTileEntity.Connection> connectionCache;

    @Shadow
    public abstract List<PipeTileEntity.Connection> getConnections();
}
