package tconstruct.plugins.waila;

import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import tconstruct.TConstruct;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers Waila Compatibility", description = "Tinkers Construct compatibility for Waila", modsRequired = "Waila")
public class TinkerWaila
{
    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("Waila detected. Registering TConstruct tank blocks with Waila registry.");
        FMLInterModComms.sendMessage("Waila", "register", "tconstruct.plugins.waila.WailaRegistrar.wailaCallback");
    }
}
