package dev.qther.invasiveopts.testers.pipez;

import dev.qther.invasiveopts.Config;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class PipezExtractLoopedWorkTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return Config.get(Config.Keys.Pipez.EXTRACT_LOOPED_WORK);
    }
}
