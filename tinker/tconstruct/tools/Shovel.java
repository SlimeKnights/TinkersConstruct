package tinker.tconstruct.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tinker.tconstruct.TContent;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class Shovel extends HarvestTool
{
	public Shovel(int itemID, String tex)
	{
		super(itemID, 2, tex);
		this.setItemName("InfiTool.Shovel");
	}
	
	@Override
	public int getHeadType ()
	{
		return 1;
	}
	
	@Override
	protected Material[] getEffectiveMaterials()
	{
		return materials;
	}

	@Override
	protected String getHarvestType()
	{
		return "shovel";
	}
	
	static Material[] materials = { Material.grass, Material.ground, Material.sand, Material.snow, Material.craftedSnow, Material.clay };

	@Override
	protected Item getHeadItem ()
	{
		return  TContent.shovelHead;
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
		return 2;
	}
	
	protected String getRenderString (int renderPass, boolean broken)
	{
		switch (renderPass)
		{
		case 0:
			return "_shovel_handle.png";
		case 1:
			if (broken)
				return "_shovel_head_broken.png";
			else
				return "_shovel_head.png";
		default:
			return "";
		}
	}

	protected String getEffectString (int renderPass)
	{
		return "_shovel_effect.png";
	}
}
