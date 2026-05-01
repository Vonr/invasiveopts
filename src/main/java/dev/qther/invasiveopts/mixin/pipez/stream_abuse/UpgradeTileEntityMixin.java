package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.UpgradeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import dev.qther.invasiveopts.testers.pipez.PipezStreamAbuseTester;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = PipezStreamAbuseTester.class)
        }
)
@Mixin(UpgradeTileEntity.class)
public abstract class UpgradeTileEntityMixin extends PipeTileEntityMixin {
    @Shadow
    public abstract UpgradeTileEntity.Distribution getDistribution(Direction side, PipeType pipeType);

    @WrapMethod(method = "getSortedConnections")
    public List<PipeTileEntity.Connection> stopStreamAbuse(Direction side, PipeType pipeType, Operation<List<PipeTileEntity.Connection>> original) {
        UpgradeTileEntity.Distribution distribution = getDistribution(side, pipeType);

        ArrayList<PipeTileEntity.Connection> sorted = new ArrayList<>(getConnections());

        if (sorted.size() <= 1) {
            return sorted;
        }

        switch (distribution) {
            case FURTHEST -> sorted.sort(Comparator.comparingInt(PipeTileEntity.Connection::getDistance).reversed());
            case RANDOM -> Collections.shuffle(sorted);
            default -> sorted.sort(Comparator.comparingInt(PipeTileEntity.Connection::getDistance));
        }

        return sorted;
    }
}
