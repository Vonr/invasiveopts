package dev.qther.invasiveopts.mixin.xycraft_machines.redstone_checks;

import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.RedstoneChecks.class)
        }
)
@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
    @Inject(method = "neighborChanged", at = @At("RETURN"))
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving, CallbackInfo ci) {}

    @Inject(method = "onPlace", at = @At("HEAD"))
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {}
}
