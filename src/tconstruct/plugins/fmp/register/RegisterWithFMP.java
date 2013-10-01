package tconstruct.plugins.fmp.register;

import net.minecraft.block.Block;
import codechicken.microblock.BlockMicroMaterial;
import codechicken.microblock.MicroMaterialRegistry;

public class RegisterWithFMP {
    public static void registerBlock (Block block,int meta)
    {
        // meta = number 0->15
        String identifier = new String( block.getUnlocalizedName());
        MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(block, meta), identifier + meta);
       // BlockMicroMaterial.createAndRegister(block);

        //MicroMaterialRegistry.registerMaterial((IMicroMaterial) block.blockMaterial, "tinkers:" + identifier + meta);
    }
    public static void registerBlock (Block block )
    {
        //s = string identifier for meta ex: brick...
        //MicroMaterialRegistry.registerMaterial(material, s + meta);
        String identifier = new String( block.getUnlocalizedName());
    
        //MicroMaterialRegistry.registerMaterial(new BlockMicroMaterial(block, 0), identifier );

        //MicroMaterialRegistry.registerMaterial((IMicroMaterial) block.blockMaterial,"tinkers:" + identifier);
        BlockMicroMaterial.createAndRegister(block);
    }
}
