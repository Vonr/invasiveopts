package dev.qther.invasiveopts.testers.pipez;

import dev.qther.invasiveopts.Config;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class PipezConstantFullnessChecksTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return Config.get(Config.Keys.Pipez.CONSTANT_FULLNESS_CHECKS);
    }
}
