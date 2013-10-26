package tconstruct.util.config;

import cpw.mods.fml.common.event.FMLInterModComms;
import tconstruct.common.TContent;

public class BCImcHandler
{
    public static void registerFacades ()
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
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", blockId, meta));
    }
}
