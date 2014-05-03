package tconstruct.plugins.fmp;

import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;
import mantle.module.ILoadableModule;
import net.minecraft.block.Block;
import tconstruct.TConstruct;
import tconstruct.common.TRepo;

public class ForgeMultiPart implements ILoadableModule
{

    @SuppressWarnings("unused")
    public static String modId = "ForgeMultipart";

    @Override
    public void preInit() {
        // Nothing
    }

    @Override
    public void init()
    {
        TConstruct.logger.info("ForgeMultipart detected. Registering TConstruct decorative blocks with FMP.");
        registerBlock(TRepo.clearGlass);
        registerBlock(TRepo.stainedGlassClear, 0, 15);
        registerBlock(TRepo.multiBrick, 0, 13);
        registerBlock(TRepo.metalBlock, 0, 10);
        registerBlock(TRepo.multiBrickFancy, 0, 15);
        registerBlock(TRepo.smeltery, 2, 2);
        registerBlock(TRepo.smeltery, 4, 10);
    }

    @Override
    public void postInit() {
        // Nothing
    }

    //For blocks with metadata values only
    public static void registerBlock (Block block, int metastart, int metaend)
    {
        for (int meta = metastart; meta <= metaend; meta++)
        {
            String identifier = new String(block.getUnlocalizedName());
            MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(block, meta), identifier + meta);
        }
    }

    //For blocks without metadata values only.
    public static void registerBlock (Block block)
    {
        BlockMicroMaterial.createAndRegister(block, 0);
    }

    //For blocks with metadata values and special MicroMaterial only
    public static void registerBlock (Block block, int metastart, int metaend, MicroMaterialRegistry.IMicroMaterial material)
    {
        for (int meta = metastart; meta <= metaend; meta++)
        {
            String identifier = new String(block.getUnlocalizedName());
            MicroMaterialRegistry.registerMaterial(material, identifier + meta);
        }
    }

    //For blocks without metadata values and special MicroMaterial only.
    public static void registerBlock (Block block, MicroMaterialRegistry.IMicroMaterial material)
    {
        MicroMaterialRegistry.registerMaterial(material, new String(block.getUnlocalizedName()));
    }

}
