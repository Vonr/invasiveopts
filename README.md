# Invasive Optimizations

![Logo](https://github.com/Vonr/invasiveopts/raw/1.21/logo.png)

Invasive optimizations for other mods, aiming to reduce server-side (tick) lag.  
The invasive nature of these changes makes them more likely to crash or otherwise break.  
Please report such issues to the [issue tracker](https://github.com/Vonr/invasiveopts/issues) and I will try to respond to them in a timely manner.  
Each change can also be individually toggled through the config and should solve any issues temporarily while you wait for a fix.

Optimizations included in this mod fall under one of the following categories:
1. Patch has been sent but has not reviewed for a long time or expected to be useful in the time before the next release.
2. Patch denied due to reasons unrelated to quality, such as non-maintenance, in which case future optimizations will also not be sent to that mod.
3. Patch has no avenue of contribution (e.g. closed source mods)
4. Patch already applied but not available in released versions.
5. Patch is experimental or of dubious usefulness (these are disabled by default)

Current optimizations:

- Accessories
  - `accessories.roundabout_collection` (1): Avoid iterating Map entrySet by directly accessing underlying keySet. [(PR)](https://github.com/wisp-forest/accessories/pull/400)
- Applied Energistics 2
  - `ae2.cache_fuzzy_search_max_value` (4): Cache AEItemKey::fuzzySearchMaxValue during init. [(PR)](https://github.com/AppliedEnergistics/Applied-Energistics-2/pull/8891)
- Botany Pots
  - `botanypots.hopper_insertions` (1): Hopper Botany Pot exponential insertion backoff and emptiness tracking [(PR)](https://github.com/Darkhax-Minecraft/BotanyPots/pull/499)
- Create
  - `create.fail_fast_clipboard_migration` (4): Fails fast when attempting clipboard migration in `ItemStack.<init>`, reducing impact to this extremely common operation. [(PR)](https://github.com/Creators-of-Create/Create/pull/10390)
- Pastel
  - `pastel.nuke_item_predicate_mixin` (4): Nukes Pastel's ItemPredicateMixin as it is unnecessary and expensive
- Pipez (Also see [Pipez Lag Fix by AlmanaX21](https://www.curseforge.com/minecraft/mc-mods/pipez-lag-fix) for exponential backoff)
  - `pipez.constant_fullness_checks` (2): Turns connection/inventory fullness into O(1) operations
  - `pipez.early_exits` (2): Reduces unnecessary work done by exiting targeted functions early if the right conditions are met
  - `pipez.extract_looped_work` (2): Move some work out of loops to avoid duplicated work
  - `pipez.filter_caching` (2): Caches tag filters using BitSets, integer arrays, or single integers. Uses more memory and causes loading pipes to take marginally longer.
  - `pipez.nbt_comparisons` (2): Drastically improves performance of NBT comparisons by minimizing serialization/deserialization operations
  - `pipez.stream_abuse` (2): Reduces abuse of Streams in hot paths to reduce allocation rate [(PR)](https://github.com/henkelmax/pipez/pull/296)
- Placebo
  - `placebo.lazy_string_concatenation` (1): Pass a Supplier<String> to Object#requireNonNull in DynamicHolder#get as it is a cold path in hot code. [(PR)](https://github.com/Shadows-of-Fire/Placebo/pull/122)
  - `placebo.streamless_hashing` (1): Replaces CachedObject#hashComponents with equivalent code that does not use the Stream API. [(PR)](https://github.com/Shadows-of-Fire/Placebo/pull/122)
- SFM
  - `sfm.filter_caching` (5): Caches item and fluid filters (regular/regex and tag filters for now) using BitSets, integer arrays, or single integers. Uses more memory and causes first compiles to take longer.
- Xycraft Machines
  - `xycraft_machines.redstone_checks` (3): Extractors check for redstone signals every tick rather than only when their neighbours update
  - `xycraft_machines.unnecessary_resorting` (3): Extractors resort their recipes every so often but is usually not necessary
