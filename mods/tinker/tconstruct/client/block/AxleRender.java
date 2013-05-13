package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class AxleRender implements ISimpleBlockRenderingHandler
{
	public static int axleModelID = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		renderer.setRenderBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		renderer.setRenderBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
		renderer.renderStandardBlock(block, x, y, z);
		return false;
	}

	@Override
	public boolean shouldRender3DInInventory ()
	{
		return true;
	}

	@Override
	public int getRenderId ()
	{
		return axleModelID;
	}
}
