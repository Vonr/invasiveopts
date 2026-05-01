# Invasive Optimizations

![Logo](https://github.com/Vonr/invasiveopts/raw/1.21/logo.png)

Invasive optimizations for other mods, aiming to reduce server-side (tick) lag.  
The invasive nature of these changes makes them more likely to crash or otherwise break.  
Please report such issues to the [issue tracker](https://github.com/Vonr/invasiveopts/issues) and I will try to respond to them in a timely manner.  
Each change can also be individually toggled through the config and should solve any issues temporarily while you wait for a fix.

Most of the optimizations implemented are Merge/Pull Requests that I opened but have yet to be merged.

Current optimizations:

- Botany Pots
  - `botanypots.hopper_insertions`: Hopper Botany Pot exponential insertion backoff and emptiness tracking [(PR)](https://github.com/Darkhax-Minecraft/BotanyPots/pull/499)
- Pipez
  - `pipez.constant_fullness_checks`: Turns connection/inventory fullness into O(1) operations
  - `pipez.extract_looped_work`: Move some work out of loops to avoid duplicated work
  - `pipez.stream_abuse`: Reduces abuse of Streams in hot paths to reduce allocation rate [(PR)](https://github.com/henkelmax/pipez/pull/296)

