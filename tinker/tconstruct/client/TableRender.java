package tinker.tconstruct.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TableRender implements ISimpleBlockRenderingHandler
{
	public static int tabelModelID = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == tabelModelID)
		{
			if (metadata == 5)
			{
				renderer.setRenderBounds(0.0F, 0.0, 0.0F, 1.0F, 0.875F, 1.0F);
				renderDo(renderer, block, metadata);
			}
			else
			{
				renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
				renderDo(renderer, block, metadata);
				renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
				renderDo(renderer, block, metadata);
			}
		}
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		if (modelID == tabelModelID)
		{
			int metadata = world.getBlockMetadata(x, y, z);
			if (metadata == 5)
			{
				renderer.setRenderBounds(0.0F, 0.0, 0.0F, 1.0F, 0.875F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
			}
			else
			{
				renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
				renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
				renderer.renderStandardBlock(block, x, y, z);
			}
		}
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
		return tabelModelID;
	}

	private void renderDo(RenderBlocks renderblocks, Block block, int meta)
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
