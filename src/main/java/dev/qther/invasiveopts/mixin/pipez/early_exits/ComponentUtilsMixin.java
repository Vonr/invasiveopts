package dev.qther.invasiveopts.mixin.pipez.early_exits;

import de.maxhenkel.pipez.utils.ComponentUtils;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.PipezEarlyExitsTester.class)
        }
)
@Mixin(ComponentUtils.class)
public class ComponentUtilsMixin {
    @Inject(method = "getTag(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"), cancellable = true)
    private static void skipSerializingItemEmpty(HolderLookup.Provider provider, ItemStack stack, CallbackInfoReturnable<CompoundTag> cir) {
        if (stack.isComponentsPatchEmpty()) {
            cir.setReturnValue(new CompoundTag());
        }
    }

    @Inject(method = "getTag(Lnet/minecraft/core/HolderLookup$Provider;Lnet/neoforged/neoforge/fluids/FluidStack;)Lnet/minecraft/nbt/CompoundTag;", at = @At("HEAD"), cancellable = true)
    private static void skipSerializingFluidEmpty(HolderLookup.Provider provider, FluidStack stack, CallbackInfoReturnable<CompoundTag> cir) {
        if (stack.isComponentsPatchEmpty()) {
            cir.setReturnValue(new CompoundTag());
        }
    }
}
