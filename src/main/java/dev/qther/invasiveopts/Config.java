package dev.qther.invasiveopts;

import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.*;
import net.neoforged.fml.ModList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Config {
    private static final Object2BooleanMap<String> defaults;
    private static final Object2BooleanMap<String> options;
    private static final Object2ObjectMap<String, List<String>> mods;

    public static class Keys {
        public static class BotanyPots {
            public static final String HOPPER_INSERTION = "botanypots.hopper_insertions";
        }

        public static class Pipez {
            public static final String CONSTANT_FULLNESS_CHECKS = "pipez.constant_fullness_checks";
            public static final String EARLY_EXITS = "pipez.early_exits";
            public static final String EXTRACT_LOOPED_WORK = "pipez.extract_looped_work";
            public static final String STREAM_ABUSE = "pipez.stream_abuse";
        }
    }

    static {
        defaults = new Object2BooleanLinkedOpenHashMap<>();

        defaults.put(Keys.BotanyPots.HOPPER_INSERTION, true);

        defaults.put(Keys.Pipez.CONSTANT_FULLNESS_CHECKS, true);
        defaults.put(Keys.Pipez.EARLY_EXITS, true);
        defaults.put(Keys.Pipez.EXTRACT_LOOPED_WORK, true);
        defaults.put(Keys.Pipez.STREAM_ABUSE, true);

        mods = new Object2ObjectOpenHashMap<>();

        options = new Object2BooleanLinkedOpenHashMap<>(defaults);
    }

    public static void load(File file) {
        if (file.exists()) {
            try {
                var lines = Files.readLines(file, Charset.defaultCharset());
                for (var line : lines) {
                    var split = line.split("=", 2);
                    if (split.length != 2) {
                        InvasiveOpts.LOGGER.error("Ignoring malformed option {}", line);
                        continue;
                    }

                    var key = split[0];

                    if (!defaults.containsKey(key)) {
                        InvasiveOpts.LOGGER.warn("Ignoring unknown key {}", key);
                        continue;
                    }

                    var value = split[1];

                    if (value.equals("true")) {
                        options.put(key, true);

                        var modSplit = key.split("\\.", 2);
                        if (modSplit.length != 2) {
                            InvasiveOpts.LOGGER.error("Ignoring malformed option key {}", key);
                            continue;
                        }
                        mods.compute(modSplit[0], (k, v) -> {
                            if (v == null) {
                                v = new ArrayList<>();
                            }
                            v.add(modSplit[1]);

                            return v;
                        });
                    } else if (value.equals("false")) {
                        options.put(key, false);
                    } else {
                        InvasiveOpts.LOGGER.error("Expecting boolean value for {} but got {}", key, value);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not read config file", e);
            }
        }

        try {
            try (var out = new FileWriter(file)) {
                var iter = Object2BooleanMaps.fastIterator(options);
                while (iter.hasNext()) {
                    var next = iter.next();
                    out.write(next.getKey());
                    out.write('=');
                    out.write(next.getBooleanValue() ? "true\n" : "false\n");
                }
            }
        } catch (IOException e) {
            InvasiveOpts.LOGGER.warn("Could not write configuration file", e);
        }
    }

    public static boolean get(String key) {
        return options.getOrDefault(key, false);
    }

    public static Stream<Map.Entry<String, List<String>>> getAffectedMods() {
        var modList = ModList.get();
        return mods.entrySet().stream().filter(e -> modList.isLoaded(e.getKey()));
    }
}