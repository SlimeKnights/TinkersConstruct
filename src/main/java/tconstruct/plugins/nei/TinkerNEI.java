package tconstruct.plugins.nei;

import mantle.pulsar.pulse.Handler;
import mantle.pulsar.pulse.Pulse;
import tconstruct.TConstruct;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import cpw.mods.fml.relauncher.Side;

@ObjectHolder(TConstruct.modID)
@Pulse(id = "Tinkers NEI Compatibility", description = "Tinkers Construct compatibility for NEI", modsRequired = "NotEnoughItems")
public class TinkerNEI
{

    @Handler
    public void init (FMLInitializationEvent event)
    {
        TConstruct.logger.info("NotEnoughItems detected. Registering TConstruct NEI plugin.");
        NEICompat.registerNEICompat();
    }

}
