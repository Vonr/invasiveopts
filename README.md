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

Current optimizations:

- Botany Pots
  - `botanypots.hopper_insertions` (1): Hopper Botany Pot exponential insertion backoff and emptiness tracking [(PR)](https://github.com/Darkhax-Minecraft/BotanyPots/pull/499)
- Create
  - `create.fail_fast_clipboard_migration` (1): Fails fast when attempting clipboard migration in `ItemStack.<init>`, reducing impact to this extremely common operation. [(PR)](https://github.com/Creators-of-Create/Create/pull/10390)
- Pastel
  - `pastel.nuke_item_predicate_mixin` (4): Nukes Pastel's ItemPredicateMixin as it is unnecessary and expensive
- Pipez (Also see [Pipez Lag Fix by AlmanaX21](https://www.curseforge.com/minecraft/mc-mods/pipez-lag-fix) for exponential backoff)
  - `pipez.constant_fullness_checks` (2): Turns connection/inventory fullness into O(1) operations
  - `pipez.early_exits` (2): Reduces unnecessary work done by exiting targeted functions early if the right conditions are met
  - `pipez.extract_looped_work` (2): Move some work out of loops to avoid duplicated work
  - `pipez.nbt_comparisons` (2): Drastically improves performance of NBT comparisons by minimizing serialization/deserialization operations
  - `pipez.stream_abuse` (2): Reduces abuse of Streams in hot paths to reduce allocation rate [(PR)](https://github.com/henkelmax/pipez/pull/296)
- Xycraft Machines
  - `xycraft_machines.unnecessary_resorting` (3): Extractors resort their recipes every so often but is usually not necessary
  - `xycraft_machines.redstone_checks` (3): Extractors check for redstone signals every tick rather than only when their neighbours update
