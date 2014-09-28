package tconstruct.armor.modelblock;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;
import tconstruct.util.ItemHelper;

public class DryingRackRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.setRenderBounds(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            if (metadata == 0)
            {
                renderer.setRenderBounds(0.0F, 0.0, 0.375F, 1.0F, 0.25F, 0.625F);
            }
            if (metadata == 1)
            {
                renderer.setRenderBounds(0.375F, 0.0, 0.0f, 0.625F, 0.25F, 1F);
            }

            if (metadata == 2)
            {
                renderer.setRenderBounds(0.0F, 0.75F, 0.75F, 1F, 1.0F, 1F);
            }
            if (metadata == 3)
            {
                renderer.setRenderBounds(0.0F, 0.75F, 0F, 1F, 1.0F, 0.25F);
            }
            if (metadata == 4)
            {
                renderer.setRenderBounds(0.75F, 0.75F, 0.0f, 1F, 1.0F, 1F);
            }
            if (metadata == 5)
            {
                renderer.setRenderBounds(0F, 0.75F, 0.0f, 0.25F, 1.0F, 1F);
            }
            renderer.renderStandardBlock(block, x, y, z);
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
