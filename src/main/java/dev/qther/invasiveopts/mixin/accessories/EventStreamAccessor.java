package dev.qther.invasiveopts.mixin.accessories;

import io.wispforest.owo.util.EventStream;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Restriction(require = @Condition(value = "accessories"))
@Mixin(EventStream.class)
public interface EventStreamAccessor<T> {
    @Accessor
    List<T> getSubscribers();
}
