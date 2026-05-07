package dev.qther.invasiveopts.helpers;

import net.minecraft.core.component.DataComponentPatch;

public interface PipezFilterExtension {
    DataComponentPatch invasiveopts$getComponentsPatch();
    void invasiveopts$setComponentsPatch(DataComponentPatch patch);
}
