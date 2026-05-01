package dev.qther.invasiveopts.testers;

import dev.qther.invasiveopts.Config;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class PipezStreamAbuseTester implements ConditionTester {
    @Override
    public boolean isSatisfied(String mixinClassName) {
        return Config.get(Config.Keys.BotanyPots.HOPPER_INSERTION);
    }
}
