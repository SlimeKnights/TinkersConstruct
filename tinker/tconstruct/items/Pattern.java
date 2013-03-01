package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import tinker.common.IPattern;
import tinker.tconstruct.TContent;
import tinker.tconstruct.crafting.PatternBuilder.MaterialSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Pattern extends CraftingItem
	implements IPattern
{
	public Pattern(int id, int icon, String tex)
	{
		super(id, icon, tex);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setContainerItem(this);
		this.setMaxStackSize(1);
		
	}

	public String getItemNameIS (ItemStack stack)
	{
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, 12);
		return super.getItemName() + "." + patternName[arr];
	}

	public static final String[] patternName = new String[] { 
		"ingot", "rod", "pickaxe", "shovel", "axe", "blade", "largeguard", "medguard", "crossbar", "binding", "frypan", "sign" };

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
		case 12: return 10;
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
