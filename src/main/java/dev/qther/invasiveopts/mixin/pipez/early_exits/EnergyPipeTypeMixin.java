package dev.qther.invasiveopts.mixin.pipez.early_exits;

import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.EnergyPipeType;
import dev.qther.invasiveopts.testers.pipez.PipezConstantFullnessChecksTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezConstantFullnessChecksTester.class)
        }
)
@Mixin(EnergyPipeType.class)
public abstract class EnergyPipeTypeMixin {
    @Inject(method = "insertOrdered", at = @At("HEAD"), cancellable = true)
    public void cancelOrderedInsert(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections, IEnergyStorage energyStorage, CallbackInfo ci) {
        if (connections.isEmpty()) {
            ci.cancel();
        }
    }
}
