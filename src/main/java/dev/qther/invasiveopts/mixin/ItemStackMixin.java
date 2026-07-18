package dev.qther.invasiveopts.mixin;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements AtomicIdExtension {
    @Shadow
    public abstract Item getItem();

    @Override
    public int invasiveOpts$getId() {
        return ((AtomicIdExtension) this.getItem()).invasiveOpts$getId();
    }
}
