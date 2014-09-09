package tconstruct.plugins.nei;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry.ObjectHolder;
import mantle.pulsar.pulse.*;
import tconstruct.TConstruct;

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
