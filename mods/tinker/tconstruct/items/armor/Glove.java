package mods.tinker.tconstruct.items.armor;

import mods.tinker.tconstruct.library.TConstructRegistry;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;

public class Glove extends Item
{
	public Glove(int par1)
	{
		super(par1);
		this.setCreativeTab(TConstructRegistry.materialTab);
	}

	@Override
	public void registerIcons (IconRegister iconRegister)
	{
		itemIcon = iconRegister.registerIcon("tinker:armor/dirthand");
	}
}
