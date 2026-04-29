package dev.qther.invasiveopts;

import net.neoforged.fml.CrashReportCallables;
import org.apache.commons.lang3.StringUtils;

public class CrashReportUpgrade {
    public static void registerCrashLogInfo() {
        CrashReportCallables.registerCrashCallable("Invasive Optimizations", () -> {
            var mods = Config.getAffectedMods().toList();

            StringBuilder builder = new StringBuilder();
            builder.append('\n');
            builder.append(StringUtils.repeat('=', 25));
            builder.append("\nInvasive Optimizations is installed!");
            builder.append("\nIf the crash relates to one of the affected mods and you are unable to reproduce it with Invasive Optimizations disabled,");
            builder.append("\nReport the crash to Invasive Optimizations here: https://github.com/Vonr/invasiveopts/issues");
            if (mods.isEmpty()) {
                builder.append("\nNo mods are affected by Invasive Optimizations with the current config.");
            } else {
                builder.append("\nMods Affected by Invasive Optimizations:");
                for (var mod : mods) {
                    builder.append("\n - ");
                    builder.append(mod.getKey());
                    for (var option : mod.getValue()) {
                        builder.append("\n   - ");
                        builder.append(option);
                    }
                }
            }

            builder.append('\n');
            builder.append(StringUtils.repeat('=', 25));
            builder.append('\n');

            return builder.toString();
        });
    }
}
