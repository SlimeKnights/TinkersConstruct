package mods.tinker.tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class FrypanRender implements ISimpleBlockRenderingHandler
{
	public static int frypanModelID = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		//Inventory should be an item. This is not here!
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.0F, 0.125F, 0.0F, 0.125F, 0.25F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.125F, 0.125F, 0.875F, 0.875F, 0.25F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.875F, 0.125F, 0.0F, 1.0F, 0.25F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.125F, 0.125F, 0.0F, 0.875F, 0.25F, 0.125F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(1F, 0.0F, 0.4375F, 2F, 0.125F, 0.5625F);
		renderer.renderStandardBlock(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory ()
	{
		return true;
	}

	@Override
	public int getRenderId ()
	{
		return frypanModelID;
	}
}
