package tconstruct.world.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import tconstruct.util.ItemHelper;

public class BarricadeRender implements ISimpleBlockRenderingHandler
{
    public static int model;

    public BarricadeRender()
    {
        model = RenderingRegistry.getNextAvailableRenderId();
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block barricade, int modelId, RenderBlocks renderer)
    {
        if (modelId == model)
        {
            int meta = world.getBlockMetadata(x, y, z);
            int type = meta % 4;

            if (meta / 4 == 0)
            {
                // if (type >= 0)
                // TODO setRenderBounds
                renderer.setRenderBounds(0.125, 0.0, 0.5, 0.375, 1.0, 0.75);
                renderer.renderStandardBlock(barricade, x, y, z);

                if (type >= 1)
                {
                    renderer.setRenderBounds(0.625, 0.0, 0.5, 0.875, 1.0, 0.75);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 2)
                {
                    renderer.setRenderBounds(0.0, 0.125, 0.25, 1.0, 0.375, 0.5);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 3)
                {
                    renderer.setRenderBounds(0.0, 0.625, 0.25, 1.0, 0.875, 0.5);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }
            }

            if (meta / 4 == 1)
            {
                // if (type >= 0)
                renderer.setRenderBounds(0.25, 0.0, 0.125, 0.5, 1.0, 0.375);
                renderer.renderStandardBlock(barricade, x, y, z);

                if (type >= 1)
                {
                    renderer.setRenderBounds(0.25, 0.0, 0.625, 0.5, 1.0, 0.875);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 2)
                {
                    renderer.setRenderBounds(0.5, 0.125, 0.0, 0.75, 0.375, 1.0);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 3)
                {
                    renderer.setRenderBounds(0.5, 0.625, 0.0, 0.75, 0.875, 1.0);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }
            }

            if (meta / 4 == 2)
            {
                // if (type >= 0)
                renderer.setRenderBounds(0.125, 0.0, 0.25, 0.375, 1.0, 0.5);
                renderer.renderStandardBlock(barricade, x, y, z);

                if (type >= 1)
                {
                    renderer.setRenderBounds(0.625, 0.0, 0.25, 0.875, 1.0, 0.5);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 2)
                {
                    renderer.setRenderBounds(0.0, 0.125, 0.5, 1.0, 0.375, 0.75);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 3)
                {
                    renderer.setRenderBounds(0.0, 0.625, 0.5, 1.0, 0.875, 0.75);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }
            }

            if (meta / 4 == 3)
            {
                // if (type >= 0)
                renderer.setRenderBounds(0.5, 0.0, 0.125, 0.75, 1.0, 0.375);
                renderer.renderStandardBlock(barricade, x, y, z);

                if (type >= 1)
                {
                    renderer.setRenderBounds(0.5, 0.0, 0.625, 0.75, 1.0, 0.875);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 2)
                {
                    renderer.setRenderBounds(0.25, 0.125, 0.0, 0.5, 0.375, 1.0);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }

                if (type >= 3)
                {
                    renderer.setRenderBounds(0.25, 0.625, 0.0, 0.5, 0.875, 1.0);
                    renderer.renderStandardBlock(barricade, x, y, z);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.setRenderBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
            renderer.setRenderBounds(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

}
