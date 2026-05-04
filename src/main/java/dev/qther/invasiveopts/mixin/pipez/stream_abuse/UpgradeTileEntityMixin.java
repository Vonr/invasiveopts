package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.UpgradeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import dev.qther.invasiveopts.testers.pipez.PipezStreamAbuseTester;
import dev.qther.invasiveopts.util.CachedSort;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collections;
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
    @Unique
    UpgradeTileEntity.Distribution invasiveOpts$sortedDistributionCache = null;
    @Unique
    List<PipeTileEntity.Connection> invasiveOpts$sortedConnectionsCache = null;

    @Shadow
    public abstract UpgradeTileEntity.Distribution getDistribution(Direction side, PipeType pipeType);

    @WrapMethod(method = "getSortedConnections")
    public List<PipeTileEntity.Connection> stopStreamAbuse(Direction side, PipeType pipeType, Operation<List<PipeTileEntity.Connection>> original) {
        UpgradeTileEntity.Distribution distribution = getDistribution(side, pipeType);

        List<PipeTileEntity.Connection> sorted = invasiveOpts$sortedConnectionsCache;

        if (this.connectionCache == null || invasiveOpts$sortedConnectionsCache == null || invasiveOpts$sortedDistributionCache != distribution) {
            sorted = new ObjectArrayList<>(getConnections());
            if (sorted.size() > 1) {
                switch (distribution) {
                    case FURTHEST -> CachedSort.sortByCachedIntKey(sorted, con -> ~con.getDistance());
                    case RANDOM -> Collections.shuffle(sorted);
                    default -> CachedSort.sortByCachedIntKey(sorted, PipeTileEntity.Connection::getDistance);
                }
            }

            invasiveOpts$sortedDistributionCache = distribution;
            invasiveOpts$sortedConnectionsCache = sorted;
        } else if (distribution == UpgradeTileEntity.Distribution.RANDOM) {
            Collections.shuffle(sorted);
        }

        return new ObjectArrayList<>(sorted);
    }
}
