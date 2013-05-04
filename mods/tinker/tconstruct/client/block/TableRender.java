package mods.tinker.tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

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
                renderInvBlock(renderer, block, metadata);
            }
            else
            {
                renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                renderInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
                renderInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
                renderInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
                renderInvBlock(renderer, block, metadata);
                renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
                renderInvBlock(renderer, block, metadata);
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
        return tabelModelID;
    }

    public static void renderInvBlock (RenderBlocks renderblocks, Block block, int meta)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
