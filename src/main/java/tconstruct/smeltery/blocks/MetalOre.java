package tconstruct.smeltery.blocks;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.world.World;
import tconstruct.blocks.TConstructBlock;

public class MetalOre extends TConstructBlock
{
    public MetalOre(Material material, float hardness, String[] tex)
    {
        super(material, hardness, tex);
    }

    @Override
    public float getBlockHardness (World world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta <= 2)
            return 10f;
        else
            return 3f;
        // return this.blockHardness;
    }

    @Override
    public void getSubBlocks (Item b, CreativeTabs tab, List list)
    {
        for (int iter = 1; iter < 6; iter++)
        {
            list.add(new ItemStack(b, 1, iter));
        }
    }
}
