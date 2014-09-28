package tconstruct.world.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class PunjiRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.setRenderBounds(0.4375, 0.0, 0.4375, 0.5625, 1.0f, 0.5625);
            renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            renderer.setRenderBounds(0.4375, 0.0, 0.4375, 0.5625, 0.375f, 0.5625);
            renderer.renderStandardBlock(block, x, y, z);
            if (metadata >= 1)
            {
                renderer.setRenderBounds(0.125, 0.0, 0.125, 0.25, 0.375f, 0.25);
                renderer.renderStandardBlock(block, x, y, z);
            }
            if (metadata >= 2)
            {
                renderer.setRenderBounds(0.75, 0.0, 0.75, 0.875, 0.375f, 0.875);
                renderer.renderStandardBlock(block, x, y, z);
            }
            if (metadata >= 3)
            {
                renderer.setRenderBounds(0.125, 0.0, 0.75, 0.25, 0.375f, 0.875);
                renderer.renderStandardBlock(block, x, y, z);
            }
            if (metadata >= 4)
            {
                renderer.setRenderBounds(0.75, 0.0, 0.125, 0.875, 0.375f, 0.25);
                renderer.renderStandardBlock(block, x, y, z);
            }
            /*
             * if (metadata == 5) { renderer.setRenderBounds(0.0F, 0.0, 0.0F,
             * 1.0F, 0.875F, 1.0F); renderer.renderStandardBlock(block, x, y,
             * z); } else { renderer.setRenderBounds(0.0F, 0.75F, 0.0F, 1.0F,
             * 1.0F, 1.0F); renderer.renderStandardBlock(block, x, y, z);
             * renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
             * renderer.renderStandardBlock(block, x, y, z);
             * renderer.setRenderBounds(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
             * renderer.renderStandardBlock(block, x, y, z);
             * renderer.setRenderBounds(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
             * renderer.renderStandardBlock(block, x, y, z);
             * renderer.setRenderBounds(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
             * renderer.renderStandardBlock(block, x, y, z); }
             */
        }
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

    public static void renderStandardInvBlock (RenderBlocks renderblocks, Block block, int meta)
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
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
