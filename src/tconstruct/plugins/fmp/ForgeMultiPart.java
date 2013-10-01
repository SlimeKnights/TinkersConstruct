package tconstruct.plugins.fmp;

import tconstruct.common.TContent;
import tconstruct.plugins.fmp.register.RegisterWithFMP;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TConstruct|ForgeMuliPart", name = "TConstruct Compat: FMP", version = "0.1", dependencies = "after:ForgeMultipart;after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ForgeMultiPart
{
    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        if (!Loader.isModLoaded("ForgeMultipart"))
        {
            FMLLog.warning("Forgemultipart missing - TConstruct Compat: FMP not loading.");

            return;
        }
        try
        {
            FMLLog.fine("ForgeMultipart detected. Registering TConstruct decorative blocks with FMP.");
            //register blocks without metadata here
            RegisterWithFMP.registerBlock(TContent.clearGlass);
            //register blocks w/ metadata here
            RegisterWithFMP.registerBlock(TContent.stainedGlassClear, 0, 15);
            RegisterWithFMP.registerBlock(TContent.multiBrick, 0,13);
            RegisterWithFMP.registerBlock(TContent.metalBlock, 0, 10);
            RegisterWithFMP.registerBlock(TContent.multiBrickFancy, 0, 15);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
