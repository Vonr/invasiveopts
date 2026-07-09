package dev.qther.invasiveopts.mixin.placebo.lazy_string_concatenation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.qther.invasiveopts.MixinTesters;
import dev.shadowsoffire.placebo.codec.CodecProvider;
import dev.shadowsoffire.placebo.reload.DynamicHolder;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Objects;

// https://github.com/Shadows-of-Fire/Placebo/pull/122
@Restriction(
        require = {
                @Condition(value = "placebo", versionPredicates = "<=9.9.1"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Placebo.LazyStringConcatenation.class)
        }
)
@Mixin(DynamicHolder.class)
public abstract class DynamicHolderMixin<R extends CodecProvider<? super R>> {
    @Shadow
    abstract void bind();

    @Shadow
    @Nullable
    protected R value;

    @Shadow
    @Final
    protected ResourceLocation id;

    @WrapMethod(method = "get()Ldev/shadowsoffire/placebo/codec/CodecProvider;")
    private R lazyStringConcatenation(Operation<Object> original) {
        this.bind();
        Objects.requireNonNull(this.value, () -> "Trying to access unbound value: " + this.id);
        return this.value;
    }
}
