package dev.qther.invasiveopts.mixin.pipez.constant_fullness_checks;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.ItemPipeType;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.ConstantFullnessChecks.class)
        }
)
@Mixin(ItemPipeType.class)
public abstract class ItemPipeTypeMixin {
    @Inject(method = "insertEqually", at = @At("HEAD"))
    public void initFullCounter(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections, IItemHandler itemHandler, CallbackInfo ci, @Share("numFilled") LocalIntRef numFilled) {
        if (!connections.isEmpty()) {
            numFilled.set(0);
        }
    }

    @Definition(id = "inventoriesFull", local = @Local(name = "inventoriesFull", type = boolean[].class))
    @Expression("inventoriesFull[?] = true")
    @Inject(method = "insertEqually", at = @At("MIXINEXTRAS:EXPRESSION"))
    public void incrementNumFilled(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections, IItemHandler itemHandler, CallbackInfo ci, @Share("numFilled") LocalIntRef numFilled) {
        numFilled.set(numFilled.get() + 1);
    }

    @WrapOperation(method = "insertEqually", at = @At(value = "INVOKE", target = "Lde/maxhenkel/pipez/blocks/tileentity/types/ItemPipeType;hasNotInserted([Z)Z"))
    public boolean constantTimeFullnessChecks(ItemPipeType instance, boolean[] inventoriesFull, Operation<Boolean> original, @Share("numFilled") LocalIntRef numFilled) {
        return numFilled.get() < inventoriesFull.length;
    }
}
