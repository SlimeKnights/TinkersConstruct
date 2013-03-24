package mods.tinker.tconstruct.tools;

import mods.tinker.tconstruct.TContent;
import mods.tinker.tconstruct.library.AbilityHelper;
import mods.tinker.tconstruct.library.DualHarvestTool;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Mattock extends DualHarvestTool
{
	public Mattock(int itemID)
	{
		super(itemID, 3);
		this.setUnlocalizedName("InfiTool.Mattock");
	}

	@Override
	protected Material[] getEffectiveMaterials ()
	{
		return axeMaterials;
	}

	@Override
	protected Material[] getEffectiveSecondaryMaterials ()
	{
		return shovelMaterials;
	}

	@Override
	protected String getHarvestType ()
	{
		return "axe";
	}

	@Override
	protected String getSecondHarvestType ()
	{
		return "shovel";
	}
	
	static Material[] axeMaterials = { Material.wood, Material.circuits, Material.cactus, Material.pumpkin, Material.plants };
	static Material[] shovelMaterials = { Material.grass, Material.ground, Material.clay };
	
	public float getDurabilityModifier ()
	{
		return 1.2f;
	}
	
	/* Mattock specific */
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
        return AbilityHelper.hoeGround(stack, player, world, x, y, z, side, random);
    }

	@Override
	protected Item getHeadItem ()
	{
		return TContent.axeHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return TContent.shovelHead;
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_mattock_head";
		case 1:
			return "_mattock_head_broken";
		case 2:
			return "_mattock_handle";
		case 3:
			return "_mattock_back";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_mattock_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "mattock";
	}
}