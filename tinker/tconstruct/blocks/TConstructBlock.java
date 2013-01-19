package tinker.tconstruct.blocks;

import tinker.tconstruct.TConstructContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TConstructBlock extends Block
{

	public TConstructBlock(int id, int tex, Material material, float hardness)
	{
		super(id, tex, material);
		setHardness(hardness);
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
		return TConstructContent.blockTexture;
	}

}
