package dev.qther.invasiveopts.mixin.create.fail_fast_clipboard_migration;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.clipboard.ClipboardBlockItem;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(
        require = {
                @Condition(value = "create", versionPredicates = "<=6.0.10"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Create.FailFastClipboardMigration.class)
        }
)
@Mixin(value = ItemStack.class, priority = 1500)
public class ItemStackMixin {
    @TargetHandler(mixin = "com.simibubi.create.foundation.mixin.ItemStackMixin", name = "create$migrateOldClipboardComponents")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private void failFast(ItemLike item, int count, PatchedDataComponentMap components, CallbackInfo _ci, CallbackInfo ci) {
        if (!(item.asItem() instanceof ClipboardBlockItem) || components.isPatchEmpty() || components.has(AllDataComponents.CLIPBOARD_CONTENT)) {
            ci.cancel();
        }
    }

    @TargetHandler(mixin = "com.simibubi.create.foundation.mixin.ItemStackMixin", name = "create$migrateOldClipboardComponents")
    @WrapOperation(method = "@MixinSquared:Handler", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;equals(Ljava/lang/Object;)Z"))
    private boolean skipResourceLocationCheck(ResourceLocation instance, Object other, Operation<Boolean> original) {
        return true;
    }
}
