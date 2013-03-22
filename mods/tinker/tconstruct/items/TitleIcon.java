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
	public void updateIcons (IconRegister iconRegister)
	{
		TProxyClient.blankSprite = iconRegister.registerIcon("tinker:blanksprite");
		TProxyClient.metalBall = iconRegister.registerIcon("tinker:metalball");
		iconIndex = iconRegister.registerIcon("tinker:tparts");
	}
}
