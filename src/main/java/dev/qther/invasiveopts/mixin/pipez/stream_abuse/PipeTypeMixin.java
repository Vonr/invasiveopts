package dev.qther.invasiveopts.mixin.pipez.stream_abuse;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.blocks.tileentity.PipeTileEntity;
import de.maxhenkel.pipez.blocks.tileentity.types.PipeType;
import de.maxhenkel.pipez.datacomponents.AbstractPipeTypeData;
import dev.qther.invasiveopts.MixinTesters;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;
import java.util.stream.Stream;

// https://github.com/henkelmax/pipez/pull/296
@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.StreamAbuse.class)
        }
)
@Mixin(PipeType.class)
public abstract class PipeTypeMixin<T, D extends AbstractPipeTypeData<T>> {
    @Shadow
    public abstract boolean matchesConnection(PipeTileEntity.Connection connection, Filter<?, T> filter);

    @Shadow
    public abstract boolean deepExactCompare(Tag meta, Tag item);

    @Shadow
    public abstract boolean deepFuzzyCompare(Tag meta, Tag item);

    @WrapOperation(method = "deepExactCompare", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;allMatch(Ljava/util/function/Predicate;)Z", ordinal = 0))
    public boolean replaceAllMatch1(Stream instance, Predicate<? super T> predicate, Operation<Boolean> original, @Local(name = "l") ListTag meta, @Local(name = "il") ListTag item) {
        var allMatch = true;

        outer: for (var a : meta) {
            for (var b : item) {
                if (!this.deepExactCompare(a, b)) {
                    allMatch = false;
                    break outer;
                }
            }
        }

        return allMatch;
    }

    @WrapOperation(method = "deepExactCompare", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;allMatch(Ljava/util/function/Predicate;)Z", ordinal = 1))
    public boolean replaceAllMatch2(Stream instance, Predicate<? super T> predicate, Operation<Boolean> original, @Local(name = "l") ListTag meta, @Local(name = "il") ListTag item) {
        for (var a : item) {
            var anyMatch = false;
            for (var b : meta) {
                if (this.deepExactCompare(a, b)) {
                    anyMatch = true;
                    break;
                }
            }

            if (!anyMatch) {
                return false;
            }
        }

        return true;
    }

    @WrapOperation(method = "deepFuzzyCompare", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;allMatch(Ljava/util/function/Predicate;)Z", ordinal = 0))
    public boolean replaceFuzzyAllMatch(Stream instance, Predicate<? super T> predicate, Operation<Boolean> original, @Local(name = "l") ListTag meta, @Local(name = "il") ListTag item) {
        for (var a : meta) {
            var anyMatch = false;
            for (var b : item) {
                if (this.deepFuzzyCompare(a, b)) {
                    anyMatch = true;
                    break;
                }
            }

            if (!anyMatch) {
                return false;
            }
        }

        return true;
    }
}
