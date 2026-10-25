package dev.qther.invasiveopts.mixin.create.recipe_caching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.util.GenericRecipeCache;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Restriction(
        require = {
                @Condition(value = "create", versionPredicates = "<=6.0.10"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Create.RecipeCaching.class)
        }
)
@Mixin(value = AllFanProcessingTypes.SmokingType.class, priority = 1500)
public class SmokingTypeMixin {
    @WrapOperation(method = "canProcess", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> useCacheForCheck(RecipeManager instance, RecipeType<T> p_44016_, I p_345492_, Level p_44018_, Operation<Optional<RecipeHolder<T>>> original) {
        if (!(p_345492_ instanceof SingleRecipeInput sir)) {
            return original.call(instance, p_44016_, p_345492_, p_44018_);
        }

        if (sir.isEmpty()) {
            return Optional.empty();
        }

        if (p_44016_ == RecipeType.SMOKING) {
            //noinspection unchecked
            return Optional.ofNullable((RecipeHolder<T>) GenericRecipeCache.SMOKING_CACHE.get(p_44018_, sir));
        }

        return original.call(instance, p_44016_, p_345492_, p_44018_);
    }

    @WrapOperation(method = "process", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> useCacheForProcess(RecipeManager instance, RecipeType<T> p_44016_, I p_345492_, Level p_44018_, Operation<Optional<RecipeHolder<T>>> original) {
        if (!(p_345492_ instanceof SingleRecipeInput sir)) {
            return original.call(instance, p_44016_, p_345492_, p_44018_);
        }

        if (sir.isEmpty()) {
            return Optional.empty();
        }

        if (p_44016_ == RecipeType.SMOKING) {
            //noinspection unchecked
            return Optional.ofNullable((RecipeHolder<T>) GenericRecipeCache.SMOKING_CACHE.get(p_44018_, sir));
        }

        return original.call(instance, p_44016_, p_345492_, p_44018_);
    }
}
