package dev.qther.invasiveopts.mixin.pipez.early_exits;

import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.GasPipeType;
import dev.qther.invasiveopts.testers.pipez.PipezConstantFullnessChecksTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(value = "mekanism"),
                @Condition(type = Condition.Type.TESTER, tester = PipezConstantFullnessChecksTester.class)
        }
)
@Mixin(GasPipeType.class)
public abstract class GasPipeTypeMixin {
    @Inject(method = "insertOrdered", at = @At("HEAD"), cancellable = true)
    public void cancelOrderedInsert(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections, IChemicalHandler gasHandler, CallbackInfo ci) {
        if (connections.isEmpty()) {
            ci.cancel();
        }
    }
}
