package tconstruct.plugins.imc;

import net.minecraft.block.Block;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.plugins.ICompatPlugin;
import cpw.mods.fml.common.event.FMLInterModComms;

public class BuildcraftTransport implements ICompatPlugin
{

    @Override
    public String getModId ()
    {
        return "BuildCraft|Transport";
    }

    @Override
    public void preInit ()
    {

    }

    @Override
    public void init ()
    {
        TConstruct.logger.info("[BC|Transport] Registering facades.");
        // Smeltery Blocks
        addFacade(TRepo.smeltery, 2);
        for (int sc = 4; sc < 11; sc++)
        {
            addFacade(TRepo.smeltery, sc);
        }
        // Multi Brick + Fancy
        for (int sc = 0; sc < 13; sc++)
        {
            addFacade(TRepo.multiBrick, sc);
        }
        for (int sc = 0; sc < 16; sc++)
        {
            addFacade(TRepo.multiBrickFancy, sc);
        }
        // Special Soil
        for (int sc = 0; sc < 6; sc++)
        {
            addFacade(TRepo.craftedSoil, sc);
        }
        // Metal Storage
        for (int sc = 0; sc < 11; sc++)
        {
            addFacade(TRepo.metalBlock, sc);
        }
        // Speed Block
        for (int sc = 0; sc < 7; sc++)
        {
            addFacade(TRepo.speedBlock, sc);
        }
        // Hambone
        addFacade(TRepo.meatBlock, 4);
        addFacade(TRepo.meatBlock, 8);

        // Slime Gel
        addFacade(TRepo.slimeGel, 0);
        addFacade(TRepo.slimeGel, 1);
    }

    @Override
    public void postInit ()
    {

    }

    private void addFacade (Block b, int meta)
    {
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", "REPLACE W/ UNLOCALIZED NAME", meta));
    }

}
