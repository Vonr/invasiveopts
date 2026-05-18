package dev.qther.invasiveopts.mixin.xycraft_machines.redstone_checks;

import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.ExtractorBlockEntityExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tv.soaryn.xycraft.machines.content.blocks.extractor.ExtractorBlockEntity;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.RedstoneChecks.class)
        }
)
@Mixin(ExtractorBlockEntity.class)
public class ExtractorBlockEntityMixin implements ExtractorBlockEntityExtension {
    @Unique
    public boolean invasiveopts$hasSignal = false;

    @Override
    public void invasiveopts$setHasSignal(boolean hasSignal) {
        this.invasiveopts$hasSignal = hasSignal;
    }

    @Override
    public boolean invasiveopts$getHasSignal() {
        return this.invasiveopts$hasSignal;
    }
}
