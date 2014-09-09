package tconstruct.tools.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

public class BattlesignRender implements ISimpleBlockRenderingHandler
{
    public static int battlesignModelID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        // Inventory should be an item. This is not here!
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        renderer.setRenderBounds(0.45F, 0.0F, 0.45F, 0.55F, 1.125F, 0.55F);
        renderer.renderStandardBlock(block, x, y, z);
        renderFace(world, x, y, z, block, renderer);
        return true;
    }

    public void renderFace (IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer)
    {
        switch (world.getBlockMetadata(x, y, z))
        {
        case 0:
            renderer.setRenderBounds(0.42F, 0.5F, 0F, 0.50F, 1.1F, 1F);
            break;
        case 1:
            renderer.setRenderBounds(0.50F, 0.5F, 0F, 0.58F, 1.1F, 1F);
            break;
        case 2:
            renderer.setRenderBounds(0F, 0.5F, 0.50F, 1F, 1.1F, 0.58F);
            break;
        case 3:
            renderer.setRenderBounds(0F, 0.5F, 0.42F, 1F, 1.1F, 0.50F);
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
        return battlesignModelID;
    }
}
