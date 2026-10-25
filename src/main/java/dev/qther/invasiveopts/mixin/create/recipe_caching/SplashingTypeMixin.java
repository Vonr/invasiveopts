package dev.qther.invasiveopts.mixin.create.recipe_caching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.util.GenericRecipeCache;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Restriction(
        require = {
                @Condition(value = "create", versionPredicates = "<=6.0.10"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Create.RecipeCaching.class)
        }
)
@Mixin(value = AllFanProcessingTypes.SplashingType.class, priority = 1500)
public class SplashingTypeMixin {
    @Unique
    private static GenericRecipeCache<HauntingRecipe, SingleRecipeInput> invasiveOpts$RECIPE_CACHE;

    @Unique
    private static GenericRecipeCache<HauntingRecipe, SingleRecipeInput> invasiveOpts$getCache() {
        if (invasiveOpts$RECIPE_CACHE == null) {
            invasiveOpts$RECIPE_CACHE = new GenericRecipeCache<>(AllRecipeTypes.SPLASHING.getType(), 16);
        }

        return invasiveOpts$RECIPE_CACHE;
    }

    @WrapOperation(method = "canProcess", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> useCacheForCheck(AllRecipeTypes instance, I inv, Level world, Operation<Optional<RecipeHolder<T>>> original) {
        if (!(inv instanceof SingleRecipeInput sir)) {
            return original.call(instance, inv, world);
        }

        if (sir.isEmpty()) {
            return Optional.empty();
        }

        var type = instance.getType();
        if (type == AllRecipeTypes.SPLASHING.getType()) {
            //noinspection unchecked
            return Optional.ofNullable((RecipeHolder<T>) invasiveOpts$getCache().get(world, sir));
        }

        return original.call(instance, inv, world);
    }

    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> useCacheForProcess(AllRecipeTypes instance, I inv, Level world, Operation<Optional<RecipeHolder<T>>> original) {
        if (!(inv instanceof SingleRecipeInput sir)) {
            return original.call(instance, inv, world);
        }

        if (sir.isEmpty()) {
            return Optional.empty();
        }

        var type = instance.getType();
        if (type == AllRecipeTypes.SPLASHING.getType()) {
            //noinspection unchecked
            return Optional.ofNullable((RecipeHolder<T>) invasiveOpts$getCache().get(world, sir));
        }

        return original.call(instance, inv, world);
    }
}
