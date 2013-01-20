package tinker.tconstruct.tools;

import tinker.tconstruct.TConstructContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

public class Pickaxe extends HarvestTool
{
	public Pickaxe(int itemID, String tex)
	{
		super(itemID, 1, tex);
		this.setItemName("InfiTool.Pickaxe");
	}
	
	@Override
	public int getHeadType ()
	{
		return 1;
	}
	
	@Override
	protected String getHarvestType()
	{
		return "pickaxe";
	}

	@Override
	protected Material[] getEffectiveMaterials()
	{
		return materials;
	}

	static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil };

	@Override
	protected Item getHeadItem ()
	{
		return  TConstructContent.pickaxeHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return  TConstructContent.binding;
	}
}
