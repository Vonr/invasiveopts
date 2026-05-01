package dev.qther.invasiveopts.mixin.pipez.extract_looped_work;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeLogicTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.UpgradeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.FluidPipeType;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import dev.qther.invasiveopts.testers.pipez.PipezExtractLoopedWorkTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezExtractLoopedWorkTester.class)
        }
)
@Mixin(FluidPipeType.class)
public abstract class FluidPipeTypeMixin {
    @Inject(method = {"insertEqually", "insertOrdered"}, at = @At("HEAD"))
    public void store(PipeLogicTileEntity tileEntity, Direction side, List<PipeTileEntity.Connection> connections, IFluidHandler fluidHandler, CallbackInfo ci, @Share("tileFilters") LocalRef<List<Filter<?, ?>>> tileFilters, @Share("filterMode") LocalRef<UpgradeTileEntity.FilterMode> filterMode) {
        if (!connections.isEmpty()) {
            var self = (FluidPipeType) (Object) this;
            tileFilters.set(tileEntity.getFilters(side, self));
            filterMode.set(tileEntity.getFilterMode(side, self));
        }
    }

    @WrapOperation(method = {"insertEqually", "insertOrdered"}, at = @At(value = "INVOKE", target = "Lde/maxhenkel/pipez/blocks/tileentity/PipeLogicTileEntity;getFilters(Lnet/minecraft/core/Direction;Lde/maxhenkel/pipez/blocks/tileentity/types/PipeType;)Ljava/util/List;"))
    public List<Filter<?, ?>> readTileFilters(PipeLogicTileEntity instance, Direction direction, PipeType<Fluid, ?> pipeType, Operation<List<Filter<?, ?>>> original, @Share("tileFilters") LocalRef<List<Filter<?, ?>>> tileFilters) {
        return tileFilters.get();
    }

    @WrapOperation(method = {"insertEqually", "insertOrdered"}, at = @At(value = "INVOKE", target = "Lde/maxhenkel/pipez/blocks/tileentity/PipeLogicTileEntity;getFilterMode(Lnet/minecraft/core/Direction;Lde/maxhenkel/pipez/blocks/tileentity/types/PipeType;)Lde/maxhenkel/pipez/blocks/tileentity/UpgradeTileEntity$FilterMode;"))
    public UpgradeTileEntity.FilterMode readFilterMode(PipeLogicTileEntity instance, Direction direction, PipeType<Fluid, ?> pipeType, Operation<List<Filter<?, ?>>> original, @Share("filterMode") LocalRef<UpgradeTileEntity.FilterMode> filterMode) {
        return filterMode.get();
    }
}
