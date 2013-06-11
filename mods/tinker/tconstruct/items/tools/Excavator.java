package mods.tinker.tconstruct.items.tools;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.tools.HarvestTool;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Excavator extends HarvestTool
{
	public Excavator(int itemID)
	{
		super(itemID, 2);
		this.setUnlocalizedName("InfiTool.Excavator");
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
	public Item getHeadItem ()
	{
		return  TContent.excavatorHead;
	}
	
    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

	@Override
	public Item getAccessoryItem ()
	{
		return TContent.toughBinding;
	}
    
    @Override
    public Item getExtraItem ()
    {
        return TContent.toughRod;
    }

    @Override
    public float getRepairModifier ()
    {
        return 4.0f;
    }

    public float getDurabilityModifier ()
    {
        return 2.75f;
    }
    
    @Override
    public int durabilityTypeAccessory ()
    {
        return 1;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderPasses (int metadata)
	{
		return 10;
	}
	
	@Override
	public int getPartAmount()
	{
		return 4;
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_excavator_head";
		case 1:
			return "_excavator_head_broken";
		case 2:
			return "_excavator_handle";
        case 3:
            return "_excavator_binding";
        case 4:
            return "_excavator_grip";
		default:
			return "";
		}
	}

	@Override
	public String getEffectSuffix ()
	{
		return "_excavator_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "excavator";
	}
}
