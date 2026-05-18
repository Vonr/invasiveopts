package dev.qther.invasiveopts;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

public class MixinTesters {
    public static class BotanyPots {
        public static class HopperInsertions implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.BotanyPots.HOPPER_INSERTIONS.enabled;
            }
        }
    }

    public static class Pipez {
        public static class ConstantFullnessChecks implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.Pipez.CONSTANT_FULLNESS_CHECKS.enabled;
            }
        }

        public static class EarlyExits implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.Pipez.EARLY_EXITS.enabled;
            }
        }

        public static class ExtractLoopedWork implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.Pipez.EXTRACT_LOOPED_WORK.enabled;
            }
        }

        public static class StreamAbuse implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.Pipez.STREAM_ABUSE.enabled;
            }
        }

        public static class NbtComparisons implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.Pipez.NBT_COMPARISONS.enabled;
            }
        }
    }

    public static class XycraftMachines {
        public static class UnnecessarySorting implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.XycraftMachines.UNNECESSARY_RESORTING.enabled;
            }
        }

        public static class RedstoneChecks implements ConditionTester {
            @Override
            public boolean isSatisfied(String mixinClassName) {
                return Config.Keys.XycraftMachines.REDSTONE_CHECKS.enabled;
            }
        }
    }
}
