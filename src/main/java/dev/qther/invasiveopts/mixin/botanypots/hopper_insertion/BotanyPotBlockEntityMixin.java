package dev.qther.invasiveopts.mixin.botanypots.hopper_insertion;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.darkhax.bookshelf.common.api.util.TickAccumulator;
import net.darkhax.botanypots.common.impl.block.entity.BotanyPotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// https://github.com/Darkhax-Minecraft/BotanyPots/pull/499
@Restriction(
        require = {
                @Condition(value = "botanypots", versionPredicates = "26.1.x"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.BotanyPots.BotanyPotsHopperInsertionTester.class)
        }
)
@Mixin(BotanyPotBlockEntity.class)
public class BotanyPotBlockEntityMixin {
    @Shadow
    protected TickAccumulator exportCooldown;
    @Unique
    protected int invasiveOpts$exportBackoff = 0;
    @Unique
    protected boolean invasiveOpts$storageMayHaveItems = true;

    @Inject(method = "tickPot", at = @At(value = "HEAD"))
    private static void initShares(Level level, BlockPos pos, BlockState state, BotanyPotBlockEntity pot, CallbackInfo ci, @Share("nonEmptyRemaining") LocalBooleanRef nonEmptyRemaining, @Share("didWork") LocalBooleanRef didWork) {
        nonEmptyRemaining.set(true);
        didWork.set(false);
    }

    @Inject(method = "tickPot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/core/Holder;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/gameevent/GameEvent$Context;)V", ordinal = 0))
    private static void setMayHaveItems(Level level, BlockPos pos, BlockState state, BotanyPotBlockEntity pot, CallbackInfo ci) {
        ((BotanyPotBlockEntityMixin) (Object) pot).invasiveOpts$storageMayHaveItems = true;
    }

    @WrapOperation(method = "tickPot", at = @At(value = "INVOKE", target = "Lnet/darkhax/botanypots/common/impl/block/entity/BotanyPotBlockEntity;isHopper()Z", ordinal = 1))
    private static boolean checkMayHaveItems(BotanyPotBlockEntity instance, Operation<Boolean> original) {
        return ((BotanyPotBlockEntityMixin) (Object) instance).invasiveOpts$storageMayHaveItems && original.call(instance);
    }

    @WrapOperation(method = "tickPot", at = @At(value = "INVOKE", target = "Lnet/darkhax/botanypots/common/impl/block/entity/BotanyPotBlockEntity;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 0))
    private static void onInsert(BotanyPotBlockEntity instance, int i, ItemStack result, Operation<Void> original, @Local(name = "stack") ItemStack stack, @Share("nonEmptyRemaining") LocalBooleanRef nonEmptyRemaining, @Share("didWork") LocalBooleanRef didWork) {
        if (stack.getCount() != result.getCount()) {
            didWork.set(true);
        }

        if (!stack.isEmpty()) {
            nonEmptyRemaining.set(true);
        }

        original.call(instance, i, result);
    }

    @Inject(method = "tickPot", at = @At(value = "INVOKE", target = "Lnet/darkhax/bookshelf/common/api/util/TickAccumulator;reset()V", ordinal = 1), cancellable = true)
    private static void updateState(Level level, BlockPos pos, BlockState state, BotanyPotBlockEntity pot, CallbackInfo ci, @Share("nonEmptyRemaining") LocalBooleanRef nonEmptyRemaining, @Share("didWork") LocalBooleanRef didWork) {
        if (level instanceof ServerLevel) {
            ci.cancel();

            var mixin = ((BotanyPotBlockEntityMixin) (Object) pot);
            mixin.invasiveOpts$storageMayHaveItems = nonEmptyRemaining.get();

            if (mixin.invasiveOpts$storageMayHaveItems && didWork.get()) {
                mixin.invasiveOpts$exportBackoff = Math.max(0, mixin.invasiveOpts$exportBackoff >> 1);
            } else {
                mixin.invasiveOpts$exportBackoff = Math.min(128, mixin.invasiveOpts$exportBackoff == 0 ? 1 : mixin.invasiveOpts$exportBackoff << 1);
            }

            mixin.exportCooldown.setTicks(mixin.invasiveOpts$exportBackoff);
        }
    }

}
