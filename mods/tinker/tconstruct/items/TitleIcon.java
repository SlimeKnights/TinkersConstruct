package mods.tinker.tconstruct.items;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.library.tools.ToolCore;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;

public class TitleIcon extends Item
{
	public TitleIcon(int par1)
	{
		super(par1);
	}

	@Override
	public void registerIcons (IconRegister iconRegister)
	{
		ToolCore.blankSprite = iconRegister.registerIcon("tinker:blanksprite");
		TProxyClient.metalBall = iconRegister.registerIcon("tinker:metalball");
		itemIcon = iconRegister.registerIcon("tinker:tparts");
	}
}
