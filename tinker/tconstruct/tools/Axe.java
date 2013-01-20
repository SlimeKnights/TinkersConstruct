package tinker.tconstruct.tools;

import tinker.tconstruct.AbilityHelper;
import tinker.tconstruct.TConstructContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Axe extends HarvestTool
{
	public Axe(int itemID, String tex)
	{
		super(itemID, 3, tex);
		this.setItemName("InfiTool.Axe");
	}

	@Override
	public int getHeadType ()
	{
		return 1;
	}

	@Override
	protected Material[] getEffectiveMaterials ()
	{
		return materials;
	}

	@Override
	protected String getHarvestType ()
	{
		return "axe";
	}

	@Override
	public boolean onBlockDestroyed (ItemStack itemstack, World world, int bID, int x, int y, int z, EntityLiving player)
	{
		Block block = Block.blocksList[bID];
		if (block != null && block.blockMaterial == Material.leaves)
			return false;

		return AbilityHelper.onBlockChanged(itemstack, world, bID, x, y, z, player, random);
	}

	static Material[] materials = { Material.wood, Material.circuits, Material.cactus, Material.pumpkin };

	@Override
	protected Item getHeadItem ()
	{
		return TConstructContent.axeHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return null;
	}
}
