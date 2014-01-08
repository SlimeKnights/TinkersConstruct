package tconstruct.plugins.fmp;

import tconstruct.TConstruct;
import tconstruct.common.TContent;
import tconstruct.plugins.ICompatPlugin;
import tconstruct.plugins.fmp.register.RegisterWithFMP;

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
        RegisterWithFMP.registerBlock(TContent.clearGlass);
        RegisterWithFMP.registerBlock(TContent.stainedGlassClear, 0, 15);
        RegisterWithFMP.registerBlock(TContent.multiBrick, 0, 13);
        RegisterWithFMP.registerBlock(TContent.metalBlock, 0, 10);
        RegisterWithFMP.registerBlock(TContent.multiBrickFancy, 0, 15);
        RegisterWithFMP.registerBlock(TContent.smeltery, 2, 2);
        RegisterWithFMP.registerBlock(TContent.smeltery, 4, 10);
    }

    @Override
    public void postInit() {
        // Nothing
    }

}
