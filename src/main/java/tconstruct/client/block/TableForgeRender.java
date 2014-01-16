package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TableForgeRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.func_147782_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            renderStandardInvBlock(renderer, block, metadata);
            renderer.func_147782_a(0.0F, 0.0F, 0.0F, 0.25F, 0.75F, 0.25F);
            renderStandardInvBlock(renderer, block, metadata);
            renderer.func_147782_a(0.75F, 0.0F, 0.0F, 1.0F, 0.75F, 0.25F);
            renderStandardInvBlock(renderer, block, metadata);
            renderer.func_147782_a(0.0F, 0.0F, 0.75F, 0.25F, 0.75F, 1.0F);
            renderStandardInvBlock(renderer, block, metadata);
            renderer.func_147782_a(0.75F, 0.0F, 0.75F, 1.0F, 0.75F, 1.0F);
            renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
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
        renderblocks.func_147768_a(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderblocks.func_147806_b(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        renderblocks.func_147761_c(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderblocks.func_147734_d(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        renderblocks.func_147798_e(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderblocks.func_147764_f(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }
}
