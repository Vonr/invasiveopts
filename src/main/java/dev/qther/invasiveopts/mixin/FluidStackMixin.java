package dev.qther.invasiveopts.mixin;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidStack.class)
public abstract class FluidStackMixin implements AtomicIdExtension {
    @Shadow
    public abstract Fluid getFluid();

    @Override
    public int invasiveOpts$getId() {
        return ((AtomicIdExtension) this.getFluid()).invasiveOpts$getId();
    }
}
