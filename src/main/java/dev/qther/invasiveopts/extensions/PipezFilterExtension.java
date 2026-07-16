package dev.qther.invasiveopts.extensions;

import net.minecraft.core.component.DataComponentPatch;

public interface PipezFilterExtension {
    DataComponentPatch invasiveopts$getComponentsPatch();
    void invasiveopts$setComponentsPatch(DataComponentPatch patch);
}
