package mods.tinker.tconstruct.items;

import java.util.List;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.crafting.PatternBuilder.MaterialSet;
import mods.tinker.tconstruct.library.util.IPattern;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Pattern extends CraftingItem
	implements IPattern
{
	public Pattern(int id, String partType, String patternType, String folder)
	{
		super(id, patternName, getPatternNames(patternType), folder);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setContainerItem(this);
		this.setMaxStackSize(1);
		
	}
	
	private static String[] getPatternNames (String partType)
	{
		String[] names = new String[patternName.length];
		for (int i = 0; i < patternName.length; i++)
			names[i] = partType+patternName[i];
		return names;
	}

	public static final String[] patternName = new String[] { 
		"ingot", "rod", "pickaxe", "shovel", "axe", "swordblade", "largeguard", "mediumguard", "crossbar", "binding", "frypan", "sign", "knifeblade", "chisel" };
	
	/*@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister iconRegister)
    {
		this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:"+textureNames[i]);
        }
    }*/

	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		for (int i = 1; i < patternName.length; i++)
			list.add(new ItemStack(id, 1, i));
	}

	public ItemStack getContainerItemStack (ItemStack stack)
	{
		return stack;
	}

	public boolean doesContainerItemLeaveCraftingGrid (ItemStack stack)
	{
		return false;
	}
	
	/* Tags and information about the pattern */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		float cost = getPatternCost(stack.getItemDamage()) / 2f;
		if (cost > 0)
		{
			if (cost - (int)cost < 0.1)
				list.add("Material Cost: "+(int)cost);
			else
				list.add("Material Cost: "+cost);
		}
	}

	//2 for full material, 1 for half.
	public int getPatternCost (int meta)
	{
		switch (meta)
		{
		case 0: return 2;
		case 1: return 1;
		case 2: return 2;
		case 3: return 2;
		case 4: return 2;
		case 5: return 2;
		case 6: return 1;
		case 7: return 1;
		case 8: return 1;
		case 9: return 1;
		case 10: return 2;
		case 11: return 2;
		case 12: return 1;
		case 13: return 1;
		default: return 0;
		}
	}

	@Override
	public ItemStack getPatternOutput (ItemStack stack, MaterialSet set)
	{
		int type = stack.getItemDamage();
		if (type != 0 && type < TContent.patternOutputs.length + 1)
		{
			return new ItemStack(TContent.patternOutputs[type - 1], 1, set.materialID);
		}
		return null;
	}
}
