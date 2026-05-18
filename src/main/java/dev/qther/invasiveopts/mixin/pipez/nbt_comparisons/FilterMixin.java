package dev.qther.invasiveopts.mixin.pipez.nbt_comparisons;

import de.maxhenkel.pipez.DirectionalPosition;
import de.maxhenkel.pipez.Filter;
import de.maxhenkel.pipez.corelib.tag.Tag;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.helpers.PipezFilterExtension;
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
                @Condition(value = "pipez", versionPredicates = "<=1.2.19"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.Pipez.NbtComparisons.class)
        }
)
@Mixin(Filter.class)
public class FilterMixin<F extends Filter<F, T>, T> implements PipezFilterExtension {
    @Shadow
    @Nullable
    protected CompoundTag metadata;
    @Unique
    public DataComponentPatch invasiveOpts$metadataPatch;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void initCache(UUID id, Tag<T> tag, CompoundTag metadata, boolean exactMetadata, DirectionalPosition destination, boolean invert, CallbackInfo ci) {
        invasiveOpts$decodeAndCache();
    }

    @Inject(method = "copy", at = @At("RETURN"))
    private void copyCache(CallbackInfoReturnable<F> cir) {
        ((PipezFilterExtension) cir.getReturnValue()).invasiveopts$setComponentsPatch(this.invasiveOpts$metadataPatch);
    }

    @Inject(method = "setMetadata", at = @At("RETURN"))
    private void cachePatch(CompoundTag metadata, CallbackInfo ci) {
        invasiveOpts$decodeAndCache();
    }

    @Unique
    private void invasiveOpts$decodeAndCache() {
        var server = ServerLifecycleHooks.getCurrentServer();
        assert server != null;
        var result = DataComponentPatch.CODEC.decode(server.registryAccess().createSerializationContext(NbtOps.INSTANCE), this.metadata).result();
        if (result.isPresent()) {
            invasiveOpts$metadataPatch = result.get().getFirst();
        } else {
            invasiveOpts$metadataPatch = null;
        }
    }

    @Override
    public DataComponentPatch invasiveopts$getComponentsPatch() {
        return this.invasiveOpts$metadataPatch;
    }

    @Override
    public void invasiveopts$setComponentsPatch(DataComponentPatch patch) {
        this.invasiveOpts$metadataPatch = patch;
    }
}
