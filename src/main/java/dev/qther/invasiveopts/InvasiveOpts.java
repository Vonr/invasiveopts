package dev.qther.invasiveopts;

import dev.qther.invasiveopts.helpers.XycraftMachinesEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InvasiveOpts.MODID)
public class InvasiveOpts {
    public static final String MODID = "invasiveopts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public InvasiveOpts(IEventBus modEventBus, ModContainer modContainer) {
        var bus = NeoForge.EVENT_BUS;
        var mods = ModList.get();
        if (mods.isLoaded("xycraft_machines") && Config.Keys.XycraftMachines.UNNECESSARY_RESORTING.enabled) {
            XycraftMachinesEvents.registerAll(bus);
        }
    }
}
