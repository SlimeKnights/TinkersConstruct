package tconstruct.plugins.fmp;

import tconstruct.TConstruct;
import tconstruct.common.TRepo;
import tconstruct.plugins.ICompatPlugin;

public class ForgeMultiPart implements ICompatPlugin
{
    @Override
    public String getModId() {
        return "ForgeMultipart";
    }

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init()
    {
        TConstruct.logger.info("ForgeMultipart detected. Registering TConstruct decorative blocks with FMP.");
        RegisterWithFMP.registerBlock(TRepo.clearGlass);
        RegisterWithFMP.registerBlock(TRepo.stainedGlassClear, 0, 15);
        RegisterWithFMP.registerBlock(TRepo.multiBrick, 0, 13);
        RegisterWithFMP.registerBlock(TRepo.metalBlock, 0, 10);
        RegisterWithFMP.registerBlock(TRepo.multiBrickFancy, 0, 15);
        RegisterWithFMP.registerBlock(TRepo.smeltery, 2, 2);
        RegisterWithFMP.registerBlock(TRepo.smeltery, 4, 10);
    }

    @Override
    public void postInit() {
        // Nothing
    }

}
