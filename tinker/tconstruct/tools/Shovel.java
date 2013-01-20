package tinker.tconstruct.tools;

import tinker.tconstruct.TConstructContent;
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
		return  TConstructContent.shovelHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return null;
	}
}
