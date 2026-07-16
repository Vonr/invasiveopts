package dev.qther.invasiveopts.mixin.xycraft_machines.redstone_checks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.ExtractorBlockEntityExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tv.soaryn.xycraft.machines.content.systems.ExtractorTickSystem;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.RedstoneChecks.class)
        }
)
@Mixin(ExtractorTickSystem.class)
public class ExtractorTickSystemMixin {
    @WrapOperation(method = "tickBlocks", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;hasNeighborSignal(Lnet/minecraft/core/BlockPos;)Z"))
    private boolean cachedRedstone(ServerLevel instance, BlockPos pos, Operation<Boolean> original, @Local(name = "blockEntity") BlockEntity blockEntity) {
        if (blockEntity instanceof ExtractorBlockEntityExtension ext) {
            return ext.invasiveopts$getHasSignal();
        }

        return original.call(instance, pos);
    }
}
