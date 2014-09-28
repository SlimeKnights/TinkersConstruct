package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import tconstruct.util.ItemHelper;

public class PaneRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            renderer.setRenderBounds(0.0F, 0.0F, 0.4375F, 1.0F, 1.0F, 0.5625F);
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            //renderer.setOverrideBlockTexture(((PaneBase)block).getIcon(0, world.getBlockMetadata(x,y,z)));
            return renderer.renderBlockStainedGlassPane(block, x, y, z);
            //return renderPaneInWorld(renderer, world, x, y, z, (PaneBase) block);
        }

        else
        {
            return false;
        }
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
}
