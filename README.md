# Invasive Optimizations

![Logo](https://github.com/Vonr/invasiveopts/raw/1.21/logo.png)

Invasive optimizations for other mods, aiming to reduce server-side (tick) lag.  
The invasive nature of these changes makes them more likely to crash or otherwise break.  
Please report such issues to the [issue tracker](https://github.com/Vonr/invasiveopts/issues) and I will try to respond to them in a timely manner.  
Each change can also be individually toggled through the config and should solve any issues temporarily while you wait for a fix.

Most of the optimizations implemented are Merge/Pull Requests that I opened but have yet to be merged.

Current optimizations:

- Botany Pots
  - Hopper Botany Pot exponential insertion backoff and emptiness tracking (botanypots.hopper\_insertions) [(PR)](https://github.com/Darkhax-Minecraft/BotanyPots/pull/499)
