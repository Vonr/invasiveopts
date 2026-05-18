package dev.qther.invasiveopts.mixin.pipez.early_exits;

import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.datacomponents.AbstractPipeTypeData;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.EarlyExits.class)
        }
)
@Mixin(PipeType.class)
public abstract class PipeTypeMixin<T, D extends AbstractPipeTypeData<T>> {
    @SuppressWarnings("UnnecessaryReturnStatement")
    @Inject(method = "deepExactCompare", at = @At("HEAD"), cancellable = true)
    public void earlyExitCompoundComparison(Tag meta, Tag item, CallbackInfoReturnable<Boolean> cir) {
        if (meta instanceof CompoundTag a && item instanceof CompoundTag b) {
            if (a.isEmpty()) {
                cir.setReturnValue(true);
                return;
            }

            if (a.size() != b.size()) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (meta instanceof ListTag a && item instanceof ListTag b) {
            if (a.isEmpty()) {
                cir.setReturnValue(true);
                return;
            }

            if (a.size() != b.size()) {
                cir.setReturnValue(false);
                return;
            }
        }
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    @Inject(method = "deepFuzzyCompare", at = @At("HEAD"), cancellable = true)
    public void earlyExitFuzzyCompoundComparison(Tag meta, Tag item, CallbackInfoReturnable<Boolean> cir) {
        if (meta instanceof CompoundTag a && item instanceof CompoundTag b) {
            if (a.size() > b.size()) {
                cir.setReturnValue(false);
                return;
            }
        }

        if (meta instanceof ListTag a && item instanceof ListTag b) {
            if (a.size() > b.size()) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
