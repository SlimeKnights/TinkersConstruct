package tinker.tconstruct.items;

import java.util.List;

import tinker.tconstruct.TConstruct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class Materials extends Item
{
	public Materials(int id, int icon, String tex)
	{
		super(id);
		this.setCreativeTab(TConstruct.materialTab);
		this.iconIndex = icon;
		this.setTextureFile(tex);
	}

	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int meta)
	{
		return this.iconIndex + meta;
	}
	
	public String getItemNameIS(ItemStack stack)
	{
		int arr = MathHelper.clamp_int(stack.getItemDamage(), 0, 6);
		return getItemName() + "." +materialNames[arr];
	}
	
	public void getSubItems(int id, CreativeTabs tab, List list)
    {
		for (int i = 0; i < 6; i++)
			list.add(new ItemStack(id, 1, i));
    }
	
	public static final String[] materialNames = new String[] { 
		"PaperStack", "SlimeCrystal", "HardenedBrick", "CobaltIngot", "ArditeIngot", "ManyullynIngot" };
}
