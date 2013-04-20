package mods.tinker.tconstruct.blocks;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MetalOre extends TConstructBlock
{
	public MetalOre(int id, Material material, float hardness, String[] tex)
	{
		super(id, material, hardness, tex);
	}

	public float getBlockHardness (World world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta <= 2)
			return 10f;
		else
			return 3f;
		//return this.blockHardness;
	}
	
	@Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 1; iter < 6; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }
}
