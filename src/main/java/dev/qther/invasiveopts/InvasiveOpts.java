package dev.qther.invasiveopts;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(InvasiveOpts.MODID)
public class InvasiveOpts {
    public static final String MODID = "invasiveopts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public InvasiveOpts(IEventBus modEventBus, ModContainer modContainer) {}
}
