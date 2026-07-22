package dev.qther.invasiveopts.mixin.mekanism;

import dev.qther.invasiveopts.extensions.AtomicIdExtension;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChemicalStack.class)
public abstract class ChemicalStackMixin implements AtomicIdExtension {
    @Shadow
    public abstract Chemical getChemical();

    @Override
    public int invasiveOpts$getId() {
        return ((AtomicIdExtension) this.getChemical()).invasiveOpts$getId();
    }
}
