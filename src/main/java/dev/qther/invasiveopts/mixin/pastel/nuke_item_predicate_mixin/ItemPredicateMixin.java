package dev.qther.invasiveopts.mixin.pastel.nuke_item_predicate_mixin;

import com.bawnorton.mixinsquared.TargetHandler;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.advancements.critereon.ItemPredicate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(
        require = {
                @Condition(value = "pastel", versionPredicates = "<=1.1.5.5"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pastel.NukeItemPredicateMixin.class)
        }
)
@Mixin(value = ItemPredicate.class, priority = 1500)
public class ItemPredicateMixin {
    @TargetHandler(mixin = "earth.terrarium.pastel.mixin.ItemPredicateMixin", name = "redirectShearsPredicates")
    @Inject(method = "@MixinSquared:Handler", at = @At(value = "HEAD"), cancellable = true)
    private void reduceLogLevel(boolean original, CallbackInfoReturnable<Boolean> ci) {
        ci.setReturnValue(original);
    }
}
