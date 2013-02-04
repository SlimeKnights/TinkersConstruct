package tinker.tconstruct.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import tinker.tconstruct.TConstruct;
import tinker.tconstruct.TContent;

public class TConstructBlock extends Block
{
	int subblocks;
	public TConstructBlock(int id, int tex, Material material, float hardness, int sub)
	{
		super(id, tex, material);
		setHardness(hardness);
		this.setCreativeTab(TConstruct.blockTab);
		subblocks = sub;
	}

	@Override
	public int damageDropped (int meta)
	{
		return meta;
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata (int side, int meta)
	{
		return blockIndexInTexture + meta;
	}
	
	@Override
	public String getTextureFile ()
	{
		return TContent.blockTexture;
	}

	@Override
	public void getSubBlocks (int id, CreativeTabs tab, List list)
	{
		for (int iter = 0; iter < subblocks; iter++)
		{
			list.add(new ItemStack(id, 1, iter));
		}
	}
}
