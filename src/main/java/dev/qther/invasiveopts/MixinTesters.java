package dev.qther.invasiveopts;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class MixinTesters {
    public static class BotanyPots {
        public static class BotanyPotsHopperInsertionTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.BotanyPots.HOPPER_INSERTION);
            }
        }
    }

    public static class Pipez {
        public static class PipezConstantFullnessChecksTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.Pipez.CONSTANT_FULLNESS_CHECKS);
            }
        }

        public static class PipezEarlyExitsTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.Pipez.EARLY_EXITS);
            }
        }

        public static class PipezExtractLoopedWorkTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.Pipez.EXTRACT_LOOPED_WORK);
            }
        }

        public static class PipezStreamAbuseTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.Pipez.STREAM_ABUSE);
            }
        }

        public static class PipezNbtComparisonsTester implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.get(Config.Keys.Pipez.NBT_COMPARISONS);
            }
        }
    }
}
