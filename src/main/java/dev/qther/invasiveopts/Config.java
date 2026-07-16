package dev.qther.invasiveopts;

import com.google.common.io.Files;
import it.unimi.dsi.fastutil.objects.*;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Config {
    private static final Object2ObjectOpenHashMap<String, Key> keysById = new Object2ObjectOpenHashMap<>();
    private static final ObjectSet<Key> options = new ObjectAVLTreeSet<>();
    private static final Object2ObjectMap<String, List<String>> mods = new Object2ObjectOpenHashMap<>();

    public static class Keys {
        public static class BotanyPots {
            public static final Key HOPPER_INSERTIONS = key("botanypots.hopper_insertions");
        }

        public static class Pipez {
            public static final Key CONSTANT_FULLNESS_CHECKS = key("pipez.constant_fullness_checks");
            public static final Key EARLY_EXITS = key("pipez.early_exits");
            public static final Key EXTRACT_LOOPED_WORK = key("pipez.extract_looped_work");
            public static final Key NBT_COMPARISONS = key("pipez.nbt_comparisons");
            public static final Key STREAM_ABUSE = key("pipez.stream_abuse");
        }

        public static class XycraftMachines {
            public static final Key UNNECESSARY_RESORTING = key("xycraft_machines.unnecessary_resorting");
            public static final Key REDSTONE_CHECKS = key("xycraft_machines.redstone_checks");
        }

        public static class Pastel {
            public static final Key NUKE_ITEM_PREDICATE_MIXIN = key("pastel.nuke_item_predicate_mixin");
        }

        public static class Create {
            public static final Key FAIL_FAST_CLIPBOARD_MIGRATION = key("create.fail_fast_clipboard_migration");
        }

        public static class Accessories {
            public static final Key ROUNDABOUT_COLLECTION = key("accessories.roundabout_collection");
        }

        public static class AE2 {
            public static final Key CACHE_FUZZY_SEARCH_MAX_VALUE = key("ae2.cache_fuzzy_search_max_value");
        }

        public static class Placebo {
            public static final Key STREAMLESS_HASHING = key("placebo.streamless_hashing");
            public static final Key LAZY_STRING_CONCATENATION = key("placebo.lazy_string_concatenation");
        }

        public static class SFM {
            public static final Key FILTER_CACHING = key("sfm.filter_caching", false);
        }

        private static Key key(String id, boolean enabled) {
            var key = new Key(id, enabled);
            keysById.put(id, key);
            return key;
        }

        private static Key key(String id) {
            return key(id, true);
        }
    }

    public static final class Key implements Comparable<Key> {
        private final String id;
        public boolean enabled;

        public Key(String id, boolean enabled) {
            this.id = id;
            this.enabled = enabled;
        }

        @Override
        public int compareTo(@NotNull Config.Key o) {
            return this.id.compareTo(o.id);
        }
    }

    static {
        try {
            for (var member : Keys.class.getNestMembers()) {
                for (var field : member.getDeclaredFields()) {
                    if (Key.class.isAssignableFrom(field.getType())) {
                        var key = (Key) field.get(null);
                        options.add(key);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load(File file) {
        if (file.exists()) {
            try {
                var lines = Files.readLines(file, Charset.defaultCharset());
                for (var line : lines) {
                    if (line.isBlank()) {
                        continue;
                    }

                    var split = line.split("=", 2);
                    if (split.length != 2) {
                        InvasiveOpts.LOGGER.error("Ignoring malformed option {}", line);
                        continue;
                    }

                    var id = split[0];
                    var key = keysById.get(id);

                    if (key == null) {
                        InvasiveOpts.LOGGER.warn("Ignoring unknown key {}", id);
                        continue;
                    }

                    var value = split[1];

                    if (value.equals("true")) {
                        key.enabled = true;
                        options.add(key);

                        var modSplit = id.split("\\.", 2);
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
                        key.enabled = false;
                        options.add(key);
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
                var iter = options.iterator();
                String previousMod = null;
                while (iter.hasNext()) {
                    var key = iter.next();

                    var split = key.id.split("\\.", 2);
                    var mod = split[0];
                    if (previousMod == null) {
                        previousMod = mod;
                    } else if (!previousMod.equals(mod)) {
                        out.write('\n');
                        previousMod = mod;
                    }

                    out.write(key.id);
                    out.write('=');
                    out.write(key.enabled ? "true\n" : "false\n");
                }
            }
        } catch (IOException e) {
            InvasiveOpts.LOGGER.warn("Could not write configuration file", e);
        }
    }

    public static Stream<Map.Entry<String, List<String>>> getAffectedMods() {
        var modList = ModList.get();
        return modList == null ? Stream.empty() : mods.entrySet().stream().filter(e -> modList.isLoaded(e.getKey()));
    }
}
