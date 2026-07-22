package dev.qther.invasiveopts.mixin.mekanism;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.api.chemical.Chemical;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.concurrent.atomic.AtomicInteger;

@Restriction(
        require = {
                @Condition(value = "mekanism"),
        }
)
@Mixin(Chemical.class)
public class ChemicalMixin implements AtomicIdExtension {
    @Unique
    private static final AtomicInteger invasiveOpts$counter = new AtomicInteger(0);

    @Unique
    private final int invasiveOpts$id = invasiveOpts$counter.getAndAdd(1);

    @Override
    public int invasiveOpts$getId() {
        return invasiveOpts$id;
    }
}
