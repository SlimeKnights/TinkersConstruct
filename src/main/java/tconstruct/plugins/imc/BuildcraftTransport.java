package tconstruct.plugins.imc;

import cpw.mods.fml.common.event.FMLInterModComms;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.plugins.ICompatPlugin;

public class BuildcraftTransport implements ICompatPlugin {

    @Override
    public String getModId() {
        return "BuildCraft|Transport";
    }

    @Override
    public void preInit() {

    }

    @Override
    public void init() {
        TConstruct.logger.info("[BC|Transport] Registering facades.");
        // Smeltery Blocks
        addFacade(TRepo.smeltery.blockID, 2);
        for (int sc = 4; sc < 11; sc++)
        {
            addFacade(TRepo.smeltery.blockID, sc);
        }
        // Multi Brick + Fancy
        for (int sc = 0; sc < 13; sc++)
        {
            addFacade(TRepo.multiBrick.blockID, sc);
        }
        for (int sc = 0; sc < 16; sc++)
        {
            addFacade(TRepo.multiBrickFancy.blockID, sc);
        }
        // Special Soil
        for (int sc = 0; sc < 6; sc++)
        {
            addFacade(TRepo.craftedSoil.blockID, sc);
        }
        // Metal Storage
        for (int sc = 0; sc < 11; sc++)
        {
            addFacade(TRepo.metalBlock.blockID, sc);
        }
        // Speed Block
        for (int sc = 0; sc < 7; sc++)
        {
            addFacade(TRepo.speedBlock.blockID, sc);
        }
        // Hambone
        addFacade(TRepo.meatBlock.blockID, 4);
        addFacade(TRepo.meatBlock.blockID, 8);

        // Slime Gel
        addFacade(TRepo.slimeGel.blockID, 0);
        addFacade(TRepo.slimeGel.blockID, 1);
    }

    @Override
    public void postInit() {

    }

    private void addFacade (int blockId, int meta)
    {
        FMLInterModComms.sendMessage("BuildCraft|Transport", "add-facade", String.format("%d@%d", blockId, meta));
    }

}
