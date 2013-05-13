package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Hatchet extends HarvestTool
{
	public Hatchet(int itemID)
	{
		super(itemID, 3);
		this.setUnlocalizedName("InfiTool.Axe");
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

	static Material[] materials = { Material.wood, Material.leaves, Material.vine, Material.circuits, Material.cactus, Material.pumpkin };

	@Override
	protected Item getHeadItem ()
	{
		return TContent.axeHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderPasses (int metadata)
	{
		return 8;
	}
	
	@Override
	public int getPartAmount()
	{
		return 2;
	}
	
	@Override
	public void registerPartPaths (int index, String[] location)
	{
		headStrings.put(index, location[0]);
		brokenHeadStrings.put(index, location[1]);
		handleStrings.put(index, location[2]);
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_axe_head";
		case 1:
			return "_axe_head_broken";
		case 2:
			return "_axe_handle";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_axe_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "axe";
	}
}
