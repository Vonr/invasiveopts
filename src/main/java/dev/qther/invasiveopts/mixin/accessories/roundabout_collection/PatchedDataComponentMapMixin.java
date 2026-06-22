package dev.qther.invasiveopts.mixin.accessories.roundabout_collection;

import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.mixin.DataComponentPatchAccessor;
import io.wispforest.accessories.pond.stack.PatchedDataComponentMapExtension;
import io.wispforest.accessories.utils.ItemStackMutation;
import io.wispforest.owo.util.EventStream;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

// https://github.com/wisp-forest/accessories/pull/400
@Restriction(
        require = {
                @Condition(value = "accessories", versionPredicates = ">=1.1.0-beta"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Accessories.RoundaboutCollection.class)
        }
)
@Mixin(PatchedDataComponentMap.class)
public abstract class PatchedDataComponentMapMixin implements PatchedDataComponentMapExtension {
    @Unique
    private boolean invasiveOpts$accessories$roundabout_collection$changeCheckStack = false;

    @Nullable
    @Unique
    private ItemStack invasiveOpts$accessories$roundabout_collection$itemStack = null;

    @Nullable
    @Unique
    private EventStream<ItemStackMutation> invasiveOpts$AccessoriesmutationEvent = null;

    @Override
    public EventStream<ItemStackMutation> accessories$getMutationEvent(ItemStack itemStack) {
        Objects.requireNonNull(itemStack);

        this.invasiveOpts$accessories$roundabout_collection$itemStack = itemStack;

        if (invasiveOpts$AccessoriesmutationEvent == null) {
            invasiveOpts$AccessoriesmutationEvent = new EventStream<>(invokers -> (stack, types) -> {
                for (ItemStackMutation itemStackMutation : invokers) {
                    itemStackMutation.onMutation(stack, types);
                }
            });
        }

        return invasiveOpts$AccessoriesmutationEvent;
    }

    @Override
    public boolean accessories$hasChanged() {
        var bl = invasiveOpts$accessories$roundabout_collection$changeCheckStack;

        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = false;

        return bl;
    }

    @Inject(method = "set", at = @At("HEAD"))
    private <T> void invasiveOpts$accessories$roundabout_collection$updateChangeValue_set(DataComponentType<? super T> component, @Nullable T value, CallbackInfoReturnable<T> cir){
        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = true;

        if (this.invasiveOpts$AccessoriesmutationEvent != null) {
            this.invasiveOpts$accessories$roundabout_collection$handleMutationEvent(List.of(component));
        }
    }

    @Inject(method = "remove", at = @At("HEAD"))
    private <T> void invasiveOpts$accessories$roundabout_collection$updateChangeValue_remove(DataComponentType<? super T> component, CallbackInfoReturnable<T> cir){
        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = true;

        if (this.invasiveOpts$AccessoriesmutationEvent != null) {
            this.invasiveOpts$accessories$roundabout_collection$handleMutationEvent(List.of(component));
        }
    }

    @Unique
    private boolean invasiveOpts$accessories$roundabout_collection$inApplyPatchLock = false;

    @Inject(method = "applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", at = @At("HEAD"))
    private void invasiveOpts$accessories$roundabout_collection$updateChangeValue_applyPatchHead(DataComponentPatch patch, CallbackInfo ci){
        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = true;

        this.invasiveOpts$accessories$roundabout_collection$inApplyPatchLock = true;
    }

    @Inject(method = "applyPatch(Lnet/minecraft/core/component/DataComponentPatch;)V", at = @At("TAIL"))
    private void invasiveOpts$accessories$roundabout_collection$updateChangeValue_applyPatchTail(DataComponentPatch patch, CallbackInfo ci){
        this.invasiveOpts$accessories$roundabout_collection$inApplyPatchLock = false;

        if (this.invasiveOpts$AccessoriesmutationEvent != null) {
            var changedDataTypes = List.copyOf(((DataComponentPatchAccessor) (Object) patch).getMap().keySet());

            this.invasiveOpts$accessories$roundabout_collection$handleMutationEvent(changedDataTypes);
        }
    }

    @Inject(method = "applyPatch(Lnet/minecraft/core/component/DataComponentType;Ljava/util/Optional;)V", at = @At("HEAD"))
    private void invasiveOpts$accessories$roundabout_collection$updateChangeValue_applyPatch(DataComponentType<?> component, Optional<?> value, CallbackInfo ci){
        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = true;

        if (this.invasiveOpts$AccessoriesmutationEvent != null && !this.invasiveOpts$accessories$roundabout_collection$inApplyPatchLock) {
            this.invasiveOpts$accessories$roundabout_collection$handleMutationEvent(List.of(component));
        }
    }

    @Inject(method = "restorePatch", at = @At("HEAD"))
    private void invasiveOpts$accessories$roundabout_collection$updateChangeValue_restorePatch(DataComponentPatch patch, CallbackInfo ci){
        this.invasiveOpts$accessories$roundabout_collection$changeCheckStack = true;

        if (this.invasiveOpts$AccessoriesmutationEvent != null) {
            var changedDataTypes = List.copyOf(((DataComponentPatchAccessor) (Object) patch).getMap().keySet());
            this.invasiveOpts$accessories$roundabout_collection$handleMutationEvent(changedDataTypes);
        }
    }

    @Unique
    private void invasiveOpts$accessories$roundabout_collection$handleMutationEvent(List<DataComponentType<?>> changedDataTypes) {
        if (this.invasiveOpts$AccessoriesmutationEvent == null) {
            return;
        }

        this.invasiveOpts$AccessoriesmutationEvent.sink().onMutation(this.invasiveOpts$accessories$roundabout_collection$itemStack, changedDataTypes);
    }
}