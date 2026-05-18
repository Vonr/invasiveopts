package dev.qther.invasiveopts.mixin.xycraft_machines.unnecessary_resorting;

import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tv.soaryn.xycraft.machines.content.recipes.producers.extractor.ExtractorRecipe;
import tv.soaryn.xycraft.machines.content.registries.MachinesRecipeTypes;
import tv.soaryn.xycraft.machines.content.systems.ExtractorTickSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Restriction(
        require = {
                @Condition(value = "xycraft_machines"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.XycraftMachines.UnnecessarySorting.class)
        }
)
@Mixin(ExtractorTickSystem.class)
public class ExtractorTickSystemMixin {
    @Shadow
    @Final
    private ArrayList<RecipeHolder<ExtractorRecipe>> _recipeList;

    @Unique
    private List<RecipeHolder<ExtractorRecipe>> invasiveopts$beforeSort = null;

    @Inject(method = "resortList", at = @At("HEAD"), cancellable = true)
    private void cachedSort(ServerLevel level, CallbackInfo ci) {
        var recipes = level.getRecipeManager().getAllRecipesFor((RecipeType) MachinesRecipeTypes.Extractor.type().get());
        if (Objects.equals(recipes, this.invasiveopts$beforeSort)) {
            ci.cancel();
            return;
        }
        this.invasiveopts$beforeSort = recipes;
    }
}
