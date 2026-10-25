package dev.qther.invasiveopts.mixin.oritech.recipe_caching;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
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
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import rearth.oritech.block.entity.processing.PoweredFurnaceBlockEntity;

import java.util.Optional;

@Restriction(
        require = {
                @Condition(value = "oritech"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Oritech.RecipeCaching.class)
        }
)
@Mixin(PoweredFurnaceBlockEntity.class)
public abstract class PoweredFurnaceBlockEntityMixin {
    @Unique
    private static final GenericRecipeCache<SmeltingRecipe, SingleRecipeInput> invasiveOpts$SMELTING_CACHE = new GenericRecipeCache<>(RecipeType.SMELTING, 8);

    @WrapOperation(method = "serverTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/crafting/RecipeManager;getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private <T extends Recipe<I>, I extends RecipeInput> Optional<RecipeHolder<T>> useCache(RecipeManager instance, RecipeType<T> p_44016_, I p_345492_, Level p_44018_, Operation<Optional<RecipeHolder<T>>> original) {
        if (p_44016_ != RecipeType.SMELTING || !(p_345492_ instanceof SingleRecipeInput sir)) {
            return original.call(instance, p_44016_, p_345492_, p_44018_);
        }

        if (sir.isEmpty()) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.ofNullable((RecipeHolder<T>) invasiveOpts$SMELTING_CACHE.get(p_44018_, sir));
    }
}
