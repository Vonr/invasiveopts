package dev.qther.invasiveopts.mixin.sfm.filter_caching;

import ca.teamdman.sfml.ast.ResourceIdentifier;
import dev.qther.invasiveopts.MixinTesters;
import dev.qther.invasiveopts.extensions.SFMPredicateExtension;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.function.Predicate;

@Restriction(
        require = {
                @Condition(value = "sfm", versionPredicates = ">=4.30.0"),
                @Condition(type = Condition.Type.TESTER, tester = MixinTesters.SFM.FilterCaching.class)
        }
)
@Mixin(ResourceIdentifier.class)
public class ResourceIdentifierMixin implements SFMPredicateExtension {
    @Unique
    Predicate<Object> invasiveOpts$predicate;

    @Override
    public void invasiveOpts$setPredicate(Predicate<Object> predicate) {
        invasiveOpts$predicate = predicate;
    }

    @Override
    public Predicate<Object> invasiveOpts$getPredicate() {
        return invasiveOpts$predicate;
    }
}
