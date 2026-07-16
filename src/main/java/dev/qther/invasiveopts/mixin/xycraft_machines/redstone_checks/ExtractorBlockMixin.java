package dev.qther.invasiveopts.mixin.xycraft_machines.redstone_checks;

import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.ExtractorBlockEntityExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.soaryn.xycraft.machines.content.blocks.extractor.ExtractorBlock;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.RedstoneChecks.class)
        }
)
@Mixin(ExtractorBlock.class)
public class ExtractorBlockMixin extends BlockBehaviourMixin {
    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean p_60514_, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof ExtractorBlockEntityExtension ext) {
            ext.invasiveopts$setHasSignal(level.hasNeighborSignal(pos));
        }
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving, CallbackInfo ci) {
        if (level.getBlockEntity(pos) instanceof ExtractorBlockEntityExtension ext) {
            ext.invasiveopts$setHasSignal(level.hasNeighborSignal(pos));
        }
    }
}
