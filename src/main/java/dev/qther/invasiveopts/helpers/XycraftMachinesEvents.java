package dev.qther.invasiveopts.helpers;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import tv.soaryn.xycraft.machines.content.recipes.producers.extractor.ExtractorRecipe;
import tv.soaryn.xycraft.machines.content.registries.MachinesRecipeTypes;

import java.util.ArrayList;
import java.util.Comparator;

public class XycraftMachinesEvents {
    public static final ArrayList<RecipeHolder<ExtractorRecipe>> extractorRecipes = new ArrayList<>();
    public static boolean extractorRecipesChanged = false;

    public static void registerAll(IEventBus bus) {
        bus.addListener(XycraftMachinesEvents::resortExtractorRecipes);
    }

    public static void resortExtractorRecipes(OnDatapackSyncEvent event) {
        extractorRecipes.clear();
        extractorRecipes.addAll(event.getPlayerList().getServer().getRecipeManager().getAllRecipesFor((RecipeType) MachinesRecipeTypes.Extractor.type().get()));
        extractorRecipes.sort(
                Comparator.comparing((RecipeHolder<ExtractorRecipe> recipe) -> recipe.value().catalyst().isPresent())
                        .thenComparing((recipe) -> recipe.value().adjacentRules().size()).reversed()
                        .thenComparing(RecipeHolder::id)
        );
        extractorRecipesChanged = true;
    }
}
