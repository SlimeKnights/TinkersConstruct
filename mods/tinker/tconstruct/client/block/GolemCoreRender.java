package mods.tinker.tconstruct.client.block;

import org.lwjgl.opengl.GL11;

import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.ModLoader;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class GolemCoreRender implements ISimpleBlockRenderingHandler
{
	public static int model = RenderingRegistry.getNextAvailableRenderId();

	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		renderer.setRenderBounds(0.0F, 0F, 0.0F, 1.0F, 0.1875F, 1.0F);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		renderer.setRenderBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		renderer.setRenderBounds(0.0F, 0.1875F, 0.0F, 0.1875F, 0.8125F, 0.1875F);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		renderer.setRenderBounds(0.8175, 0.1875F, 0.0F, 1f, 0.8125F, 0.1875F);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		renderer.setRenderBounds(0.0F, 0.1875F, 0.8175, 0.1875F, 0.8125F, 1f);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		renderer.setRenderBounds(0.8175F, 0.1875F, 0.8175, 1f, 0.8125F, 1f);
		TProxyClient.renderStandardInvBlock(renderer, block, metadata);
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
	{
		renderer.setRenderBounds(0.0F, 0F, 0.0F, 1.0F, 0.1875F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.0F, 0.1875F, 0.0F, 0.1875F, 0.8125F, 0.1875F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.8175, 0.1875F, 0.0F, 1f, 0.8125F, 0.1875F);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.0F, 0.1875F, 0.8175, 0.1875F, 0.8125F, 1f);
		renderer.renderStandardBlock(block, x, y, z);
		renderer.setRenderBounds(0.8175F, 0.1875F, 0.8175, 1f, 0.8125F, 1f);
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
		return model;
	}
}
