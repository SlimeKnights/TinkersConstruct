package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class PunjiRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.func_147782_a(0.4375, 0.0, 0.4375, 0.5625, 1.0f, 0.5625);
            renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            renderer.func_147782_a(0.4375, 0.0, 0.4375, 0.5625, 0.375f, 0.5625);
            renderer.func_147784_q(block, x, y, z);
            if (metadata >= 1)
            {
                renderer.func_147782_a(0.125, 0.0, 0.125, 0.25, 0.375f, 0.25);
                renderer.func_147784_q(block, x, y, z);
            }
            if (metadata >= 2)
            {
                renderer.func_147782_a(0.75, 0.0, 0.75, 0.875, 0.375f, 0.875);
                renderer.func_147784_q(block, x, y, z);
            }
            if (metadata >= 3)
            {
                renderer.func_147782_a(0.125, 0.0, 0.75, 0.25, 0.375f, 0.875);
                renderer.func_147784_q(block, x, y, z);
            }
            if (metadata >= 4)
            {
                renderer.func_147782_a(0.75, 0.0, 0.125, 0.875, 0.375f, 0.25);
                renderer.func_147784_q(block, x, y, z);
            }
            /*if (metadata == 5)
            {
                renderer.func_147782_a(0.0F, 0.0, 0.0F, 1.0F, 0.875F, 1.0F);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            {
                renderer.func_147782_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
                renderer.func_147784_q(block, x, y, z);
            }*/
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
        renderblocks.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
