package tinker.tconstruct.items;

import java.util.List;

import tinker.tconstruct.TConstruct;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CraftingItem extends Item
{
	public CraftingItem(int id, int icon, String tex)
	{
		super(id);
		this.setCreativeTab(TConstruct.materialTab);
		this.iconIndex = icon;
		this.setTextureFile(tex);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	@SideOnly(Side.CLIENT)
	public int getIconFromDamage(int meta)
	{
		return this.iconIndex + meta;
	}
}
