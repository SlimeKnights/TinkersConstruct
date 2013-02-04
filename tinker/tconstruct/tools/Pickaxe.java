package tinker.tconstruct.tools;

import tinker.tconstruct.TContent;
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
		return  TContent.pickaxeHead;
	}

	@Override
	protected Item getAccessoryItem ()
	{
		return  TContent.binding;
	}
	
	protected String getRenderString (int renderPass, boolean broken)
	{
		switch (renderPass)
		{
		case 0:
			return "_pickaxe_handle.png";
		case 1:
			if (broken)
				return "_pickaxe_head_broken.png";
			else
				return "_pickaxe_head.png";
		case 2:
			return "_pickaxe_accessory.png";
		default:
			return "";
		}
	}

	protected String getEffectString (int renderPass)
	{
		return "_pickaxe_effect.png";
	}
}
