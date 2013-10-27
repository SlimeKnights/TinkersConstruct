package tconstruct.plugins.nei;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import tconstruct.common.TContent;
import tconstruct.plugins.fmp.register.RegisterWithFMP;

@Mod(modid = "TConstruct|NotEnoughItems", name = "TConstruct Compat: NEI", version = "0.1", dependencies = "after:NotEnoughItems;after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class NotEnoughItems
{
    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        if (!Loader.isModLoaded("NotEnoughItems"))
        {
            FMLLog.warning("NotEnoughItems missing - TConstruct Compat: NEI not loading.");

            return;
        }
        try
        {
            FMLLog.fine("NotEnoughItems detected. Registering TConstruct NEI plugin.");
            NEICompat.registerNEICompat();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
