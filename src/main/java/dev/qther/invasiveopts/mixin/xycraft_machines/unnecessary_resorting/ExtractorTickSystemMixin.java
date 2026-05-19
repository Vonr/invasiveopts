package dev.qther.invasiveopts.mixin.xycraft_machines.unnecessary_resorting;

import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.XycraftMachinesEvents;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.soaryn.xycraft.machines.content.recipes.producers.extractor.ExtractorRecipe;
import tv.soaryn.xycraft.machines.content.systems.ExtractorTickSystem;

import java.util.ArrayList;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.UnnecessarySorting.class)
        }
)
@Mixin(ExtractorTickSystem.class)
public class ExtractorTickSystemMixin {
    @Final
    @Shadow
    private ArrayList<RecipeHolder<ExtractorRecipe>> _recipeList;

    @Inject(method = "resortList", at = @At("HEAD"), cancellable = true)
    private void cachedSort(ServerLevel level, CallbackInfo ci) {
        ci.cancel();
        if (XycraftMachinesEvents.extractorRecipesChanged) {
            this._recipeList.clear();
            this._recipeList.addAll(XycraftMachinesEvents.extractorRecipes);
            XycraftMachinesEvents.extractorRecipesChanged = false;
        }
    }
}
