package tinker.tconstruct.items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import tinker.common.IPattern;
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
		"blank", "rod", "pickaxe", "shovel", "axe", "blade", "largeguard", "medguard", "crossbar", "binding", "frypan", "sign", "lumber" };

	public void getSubItems (int id, CreativeTabs tab, List list)
	{
		for (int i = 0; i < patternName.length; i++)
			list.add(new ItemStack(id, 1, i));
	}

	public ItemStack getContainerItemStack (ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
			stack.stackSize--;
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
		/*else
		{
			list.add("Temporary: Craft any pattern to get another");
		}*/
	}

	//2 for full material, 1 for half.
	public int getPatternCost (int meta)
	{
		switch (meta)
		{
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
}
