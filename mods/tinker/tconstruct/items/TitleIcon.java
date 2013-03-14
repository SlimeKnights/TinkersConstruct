package mods.tinker.tconstruct.items;

import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class TitleIcon extends Item
{
	public TitleIcon(int par1)
	{
		super(par1);
	}

	@Override
	public void func_94581_a (IconRegister iconRegister)
	{
		TProxyClient.blankSprite = iconRegister.func_94245_a("tinker:blanksprite");
		iconIndex = iconRegister.func_94245_a("tinker:tparts");
	}
}
