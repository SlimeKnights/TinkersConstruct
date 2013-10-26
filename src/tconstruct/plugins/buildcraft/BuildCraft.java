package tconstruct.plugins.buildcraft;

import java.util.logging.Logger;

import tconstruct.common.TContent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = "TConstruct|CompatBC", name = "TConstruct Compat: BC", version = "0.0.1", dependencies = "after:BuildCraft|Transport;required-after:TConstruct")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class BuildCraft
{
    public static final String BCTransport = "BuildCraft|Transport";

    @EventHandler
    public static void load (FMLInitializationEvent ev)
    {
        if (Loader.isModLoaded(BCTransport))
        {
            Logger logger = Logger.getLogger("TConstruct BC");
            logger.setParent(FMLCommonHandler.instance().getFMLLogger());
            logger.info("BuildCraft detected. Registering for Facades.");
            registerFacades();
        }
    }

    private static void registerFacades ()
    {
        // Smeltery Blocks
        addFacade(TContent.smeltery.blockID, 2);
        for (int sc = 4; sc < 11; sc++)
        {
            addFacade(TContent.smeltery.blockID, sc);
        }
        // Multi Brick + Fancy
        for (int sc = 0; sc < 13; sc++)
        {
            addFacade(TContent.multiBrick.blockID, sc);
        }
        for (int sc = 0; sc < 16; sc++)
        {
            addFacade(TContent.multiBrickFancy.blockID, sc);
        }
        // Special Soil
        for (int sc = 0; sc < 6; sc++)
        {
            addFacade(TContent.craftedSoil.blockID, sc);
        }
        // Metal Storage
        for (int sc = 0; sc < 11; sc++)
        {
            addFacade(TContent.metalBlock.blockID, sc);
        }
        // Speed Block
        for (int sc = 0; sc < 7; sc++)
        {
            addFacade(TContent.speedBlock.blockID, sc);
        }

        // Hambone
        addFacade(TContent.meatBlock.blockID, 4);
        addFacade(TContent.meatBlock.blockID, 8);

        // Slime Gel
        addFacade(TContent.slimeGel.blockID, 0);
        addFacade(TContent.slimeGel.blockID, 1);
    }

    private static void addFacade (int blockId, int meta)
    {
        FMLInterModComms.sendMessage(BCTransport, "add-facade", String.format("%d@%d", blockId, meta));
    }
}
