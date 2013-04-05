package mods.tinker.tconstruct.client;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class SuperSoakerRender implements IItemRenderer
{

	@Override
	public boolean handleRenderType (ItemStack item, ItemRenderType type)
	{
		return type != ItemRenderType.INVENTORY;
	}

	@Override
	public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return true;
	}

	@Override
	public void renderItem (ItemRenderType type, ItemStack item, Object... data)
	{
		if (type == ItemRenderType.EQUIPPED)
			renderEquippedItem(item, (RenderBlocks) data[0], (EntityLiving) data[1]);
		if (type == ItemRenderType.ENTITY)
			renderEntityItem(item, (RenderBlocks) data[0], (EntityItem) data[1]);
	}

	void renderEquippedItem (ItemStack item, RenderBlocks renderBlocks, EntityLiving entityLiving)
	{
		
	}

	void renderEntityItem (ItemStack item, RenderBlocks renderBlocks, EntityItem entityItem)
	{
		
	}

}
