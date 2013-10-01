package tconstruct.plugins.fmp;


import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import tconstruct.common.TContent;
import tconstruct.plugins.fmp.register.RegisterWithFMP;


@Mod(modid = "TConstruct|ForgeMuliPart", name = "TConstruct Compat: FMP", version = "0.1", dependencies = "after:ForgeMultipart;after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ForgeMultiPart {
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
            RegisterWithFMP.registerBlock(TContent.searedBlock);
            //register blocks w/ metadata here
            for (int x =0;x<=15;x++)
            {
                RegisterWithFMP.registerBlock(TContent.stainedGlassClear, x);
                RegisterWithFMP.registerBlock(TContent.multiBrick,x);
                RegisterWithFMP.registerBlock(TContent.metalBlock ,x);
                RegisterWithFMP.registerBlock(TContent.multiBrickFancy, x);
                
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
   

}
