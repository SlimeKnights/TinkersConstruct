package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BrickRender implements ISimpleBlockRenderingHandler
{
	public static int model = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
	    renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        TProxyClient.renderStandardInvBlock(renderer, block, metadata);
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
	    int meta = world.getBlockMetadata(x, y, z);
		renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.5F, 0.5F, 0.5F, 1.0F, 1.0F, 1.0F);
        BlockSkinRenderHelper.renderFakeBlock(block.getIcon(0, meta), x-1, y, z, renderer, world);
        //renderer.renderStandardBlock(block, x-1, y, z);
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
		return model;
	}
}
