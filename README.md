# Invasive Optimizations

![Logo](https://github.com/Vonr/invasiveopts/raw/1.21/logo.png)

Invasive optimizations for other mods, aiming to reduce server-side (tick) lag.  
The invasive nature of these changes makes them more likely to crash or otherwise break.  
Please report such issues to the [issue tracker](https://github.com/Vonr/invasiveopts/issues) and I will try to respond to them in a timely manner.  
Each change can also be individually toggled through the config and should solve any issues temporarily while you wait for a fix.

Optimizations included in this mod fall under one of the following categories:
1. Merge request open but has not been reviewed for a long time.
2. Merge request denied due to reasons unrelated to quality, such as non-maintenance, in which case future optimizations will also not be sent to that mod.
3. No avenue of contribution (e.g. closed source mods)
4. Patch already applied but not available in released versions

Current optimizations:

- Botany Pots (Category 1)
  - `botanypots.hopper_insertions`: Hopper Botany Pot exponential insertion backoff and emptiness tracking [(PR)](https://github.com/Darkhax-Minecraft/BotanyPots/pull/499)
- Pipez (Category 2, Also see [Pipez Lag Fix by AlmanaX21](https://www.curseforge.com/minecraft/mc-mods/pipez-lag-fix) for exponential backoff)
  - `pipez.constant_fullness_checks`: Turns connection/inventory fullness into O(1) operations
  - `pipez.early_exits`: Reduces unnecessary work done by exiting targeted functions early if the right conditions are met
  - `pipez.extract_looped_work`: Move some work out of loops to avoid duplicated work
  - `pipez.nbt_comparisons`: Drastically improves performance of NBT comparisons by minimizing serialization/deserialization operations
  - `pipez.stream_abuse`: Reduces abuse of Streams in hot paths to reduce allocation rate [(PR)](https://github.com/henkelmax/pipez/pull/296)
- Xycraft Machines (Category 3)
  - `xycraft_machines.unnecessary_resorting`: Extractors resort their recipes every so often but is usually not necessary
  - `xycraft_machines.redstone_checks`: Extractors check for redstone signals every tick rather than only when their neighbours update
- Pastel (Category 4)
  - `pastel.nuke_item_predicate_mixin`: Nukes Pastel's ItemPredicateMixin as it is unnecessary and expensive
