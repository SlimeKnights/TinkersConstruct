package tconstruct.plugins.fmp.register;

import net.minecraft.block.Block;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;

public class RegisterWithFMP
{
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
        BlockMicroMaterial.createAndRegister(block);
    }
}
