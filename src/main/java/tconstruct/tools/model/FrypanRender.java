package tconstruct.tools.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class FrypanRender implements ISimpleBlockRenderingHandler
{
    public static int frypanModelID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        // Inventory should be an item. This is not here!
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        renderer.setRenderBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.0F, 0.125F, 0.0F, 0.125F, 0.25F, 1.0F);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.125F, 0.125F, 0.875F, 0.875F, 0.25F, 1.0F);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.875F, 0.125F, 0.0F, 1.0F, 0.25F, 1.0F);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.setRenderBounds(0.125F, 0.125F, 0.0F, 0.875F, 0.25F, 0.125F);
        renderer.renderStandardBlock(block, x, y, z);
        // renderer.setRenderBounds(1F, 0.0F, 0.4375F, 2F, 0.125F, 0.5625F);
        // renderer.renderStandardBlock(block, x, y, z);
        renderHandle(world, x, y, z, block, renderer);
        return true;
    }

    public void renderHandle (IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer)
    {
        switch (world.getBlockMetadata(x, y, z))
        {
        case 0:
            renderer.setRenderBounds(1F, 0.0F, 0.4375F, 2F, 0.125F, 0.5625F);
            break;
        case 1:
            renderer.setRenderBounds(-1F, 0.0F, 0.4375F, 0F, 0.125F, 0.5625F);
            break;
        case 2:
            renderer.setRenderBounds(0.4375F, 0.0F, -1F, 0.5625F, 0.125F, 0F);
            break;
        case 3:
            renderer.setRenderBounds(0.4375F, 0.0F, 1F, 0.5625F, 0.125F, 2F);
            break;
        }

        renderer.renderStandardBlock(block, x, y, z);
    }

    @Override
    public boolean shouldRender3DInInventory (int modelid)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return frypanModelID;
    }
}
