package tinker.tconstruct.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class MetalOre extends TConstructBlock
{
	public MetalOre(int id, int tex, Material material, float hardness, int sub)
	{
		super(id, tex, material, hardness, sub);
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
}
