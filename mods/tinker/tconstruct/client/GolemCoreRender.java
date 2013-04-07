package mods.tinker.tconstruct.client;

import org.lwjgl.opengl.GL11;

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
		/*Tessellator tessellator = Tessellator.instance;
        double d = 0.1875D;
        World world = ModLoader.getMinecraftInstance().theWorld;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderBottomFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(0));
        renderblocks.renderBottomFace(block, -0.5D, 0.5D - d, -0.5D, block.getBlockTextureFromSide(0));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderTopFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(1));
        renderblocks.renderTopFace(block, -0.5D, -1.5D + d, -0.5D, block.getBlockTextureFromSide(1));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderEastFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(2));
        renderblocks.renderEastFace(block, -0.5D, -0.5D, 0.5D - d, block.getBlockTextureFromSide(2));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 1.0F, 0.0F);
        renderblocks.renderWestFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(3));
        renderblocks.renderWestFace(block, -0.5D, -0.5D, -1.5D + d, block.getBlockTextureFromSide(3));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 1.0F);
        renderblocks.renderNorthFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(4));
        renderblocks.renderNorthFace(block, 0.5D - d, -0.5D, -0.5D, block.getBlockTextureFromSide(4));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, -1F);
        renderblocks.renderSouthFace(block, -0.5D, -0.5D, -0.5D, block.getBlockTextureFromSide(5));
        renderblocks.renderSouthFace(block, -1.5D + d, -0.5D, -0.5D, block.getBlockTextureFromSide(5));
        tessellator.draw();*/
		renderInvBlock(renderer, block, metadata);
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
	
	public static void renderInvBlock (RenderBlocks renderblocks, Block block, int meta)
	{
		Tessellator tessellator = Tessellator.instance;
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1F, 0.0F);
		renderblocks.renderBottomFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(0, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderblocks.renderTopFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(1, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1F);
		renderblocks.renderEastFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(2, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderblocks.renderWestFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(3, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1F, 0.0F, 0.0F);
		renderblocks.renderNorthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(4, meta));
		tessellator.draw();
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderblocks.renderSouthFace(block, 0.0D, 0.0D, 0.0D, block.getBlockTextureFromSideAndMetadata(5, meta));
		tessellator.draw();
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}
