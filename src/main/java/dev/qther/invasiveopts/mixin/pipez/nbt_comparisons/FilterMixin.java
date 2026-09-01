package dev.qther.invasiveopts.mixin.pipez.nbt_comparisons;

import de.maxhenkel.pipez.DirectionalPosition;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.corelib.tag.Tag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.PipezFilterExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

@Restriction(
        require = {
                @Condition(value = "pipez"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.NbtComparisons.class)
        }
)
@Mixin(Filter.class)
public abstract class FilterMixin<F extends Filter<F, T>, T> implements PipezFilterExtension {
    @Unique
    private boolean invasiveOpts$cached;
    @Unique
    public DataComponentPatch invasiveOpts$metadataPatch;

    @Shadow
    @Nullable
    protected CompoundTag metadata;

    @Shadow
    public abstract boolean isExactMetadata();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCache(UUID id, Tag<T> tag, CompoundTag metadata, boolean exactMetadata, DirectionalPosition destination, boolean invert, CallbackInfo ci) {
        invasiveopts$getComponentsPatch();
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void copyCache(CallbackInfoReturnable<F> cir) {
        ((PipezFilterExtension) cir.getReturnValue()).invasiveopts$setComponentsPatch(this.invasiveOpts$metadataPatch);
    }

    @Inject(method = "setMetadata", at = @At("RETURN"))
    private void cachePatch(CompoundTag metadata, CallbackInfo ci) {
        invasiveopts$getComponentsPatch();
    }

    @Override
    public DataComponentPatch invasiveopts$getComponentsPatch() {
        if (!this.isExactMetadata()) {
            return null;
        }

        if (invasiveOpts$cached) {
            return invasiveOpts$metadataPatch;
        }

        var server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return null;
        }

        var result = DataComponentPatch.CODEC.decode(server.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.metadata).result();
        if (result.isPresent()) {
            invasiveOpts$metadataPatch = result.get().getFirst();
        } else {
            invasiveOpts$metadataPatch = null;
        }
        invasiveOpts$cached = true;

        return this.invasiveOpts$metadataPatch;
    }

    @Override
    public void invasiveopts$setComponentsPatch(DataComponentPatch patch) {
        this.invasiveOpts$metadataPatch = patch;
        this.invasiveOpts$cached = true;
    }
}
