package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.DualHarvestTool;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
	
	static Material[] axeMaterials = { Material.wood, Material.cactus, Material.pumpkin, Material.plants, Material.vine };
	static Material[] shovelMaterials = { Material.grass, Material.ground, Material.clay };

	@Override
	public Item getHeadItem ()
	{
		return TContent.axeHead;
	}

	@Override
	public Item getAccessoryItem ()
	{
		return TContent.shovelHead;
	}
	
	public int durabilityTypeAccessory ()
	{
		return 2;
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
	
	/* Mattock specific */
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ)
    {
		NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
		if (tags.getBoolean("Broken"))
			return false;
		
        return AbilityHelper.hoeGround(stack, player, world, x, y, z, side, random);
    }
}