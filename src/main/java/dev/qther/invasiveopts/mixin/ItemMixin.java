package dev.qther.invasiveopts.mixin;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicInteger;

@Mixin(Item.class)
public class ItemMixin implements AtomicIdExtension {
    @Unique
    private static final AtomicInteger invasiveOpts$counter = new AtomicInteger(0);

    @Unique
    private final int invasiveOpts$id = invasiveOpts$counter.getAndAdd(1);

    @Override
    public int invasiveOpts$getId() {
        return invasiveOpts$id;
    }
}
