package tconstruct.plugins.fmp.register;

import net.minecraft.block.Block;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;

public class RegisterWithFMP
{
    public static void registerBlock (Block block, int metastart, int metaend)
    {
        // meta = number 0->15
        for (int meta = metastart; meta <= metaend; meta++)
        {
        String identifier = new String(block.getUnlocalizedName());
        MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(block, meta), identifier + meta);
        }
    }

    public static void registerBlock (Block block)
    {
        String identifier = new String(block.getUnlocalizedName());
        BlockMicroMaterial.createAndRegister(block);
    }
}
