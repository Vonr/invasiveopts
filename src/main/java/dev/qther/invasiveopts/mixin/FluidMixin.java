package dev.qther.invasiveopts.mixin;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Fluid.class)
public class FluidMixin implements AtomicIdExtension {
    @Unique
    private static final AtomicInteger invasiveOpts$counter = new AtomicInteger(0);

    @Unique
    int invasiveOpts$id = invasiveOpts$counter.getAndAdd(1);

    @Override
    public int invasiveOpts$getId() {
        return invasiveOpts$id;
    }
}
