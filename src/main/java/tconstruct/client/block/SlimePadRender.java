package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.SlimePad;
import tconstruct.client.TProxyClient;
import tconstruct.common.TContent;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class SlimePadRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            TProxyClient.renderStandardInvBlock(renderer, TContent.slimeGel, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0f, 0f, 0f, 1f, 0.5f, 1f);
            int metadata = world.getBlockMetadata(x, y, z);
            BlockSkinRenderHelper.renderLiquidBlock(((SlimePad) block).getFluidIcon(metadata), ((SlimePad) block).getFluidIcon(metadata), x, y, z, renderer, world);

            float[] size = size(metadata % 8);
            renderer.setRenderBounds(size[0], 0.5f, size[1], size[2], 0.6875f, size[3]);
            BlockSkinRenderHelper.renderLiquidBlock(((SlimePad) block).getNubIcon(metadata), ((SlimePad) block).getNubIcon(metadata), x, y, z, renderer, world);
        }
        return true;
    }

    float[] size (int meta)
    {
        //xMin, zMin, xMax, zMax
        switch (meta)
        {
        case 0:
            return new float[] { 0.375f, 0.6875f, 0.625f, 0.9375f };
        case 1:
            return new float[] { 0.0625f, 0.6875f, 0.3125f, 0.9375f };
        case 2:
            return new float[] { 0.0625f, 0.375f, 0.3125f, 0.625f };
        case 3:
            return new float[] { 0.0625f, 0.0625f, 0.3125f, 0.3125f };
        case 4:
            return new float[] { 0.375f, 0.0625f, 0.625f, 0.3125f };
        case 5:
            return new float[] { 0.6875f, 0.0625f, 0.9375f, 0.3125f };
        case 6:
            return new float[] { 0.6875f, 0.375f, 0.9375f, 0.625f };
        case 7:
            return new float[] { 0.6875f, 0.6875f, 0.9375f, 0.9375f };
        }
        return new float[] { 0.375f, 0.375f, 0.625f, 0.625f };
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
}
