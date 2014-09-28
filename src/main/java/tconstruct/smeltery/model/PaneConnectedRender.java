package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import tconstruct.smeltery.blocks.GlassPaneConnected;

import static net.minecraftforge.common.util.ForgeDirection.*;

public class PaneConnectedRender implements ISimpleBlockRenderingHandler
{

    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {

    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean temp = renderer.renderAllFaces;
        renderer.renderAllFaces = true;

        GlassPaneConnected pane = (GlassPaneConnected) block;

        boolean flag = pane.canPaneConnectTo(world, x, y, z, EAST);
        boolean flag1 = pane.canPaneConnectTo(world, x, y, z, WEST);
        boolean flag2 = pane.canPaneConnectTo(world, x, y, z, SOUTH);
        boolean flag3 = pane.canPaneConnectTo(world, x, y, z, NORTH);

        IIcon sideTexture = pane.getSideTextureIndex();

        if (!flag && !flag1 && !flag2 && !flag3)
        {
            renderer.setRenderBounds(0D, 0D, 0.45D, 1D, 1D, 0.55D);
            renderer.renderStandardBlock(block, x, y, z);
            renderer.setRenderBounds(0.45D, 0D, 0D, 0.55D, 1D, 1D);
            renderer.renderStandardBlock(block, x, y, z);
        }
        else
        {
            // renderer.setRenderBounds(0.45D, 0D, 0.45D, 0.55D, 1D, 0.55D);
            // renderer.renderStandardBlock(block, x, y, z);
        }

        // renderer.setOverrideBlockTexture(sideTexture);

        if (flag)
        {
            renderer.setRenderBounds(0.45D, 0D, 0.45D, 1D, 1D, 0.55D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (flag1)
        {
            renderer.setRenderBounds(0D, 0D, 0.45D, 0.45D, 1D, 0.55D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (flag2)
        {
            renderer.setRenderBounds(0.45D, 0D, 0.45D, 0.55D, 1D, 1D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        if (flag3)
        {
            renderer.setRenderBounds(0.45D, 0D, 0D, 0.55D, 1D, 0.45D);
            renderer.renderStandardBlock(block, x, y, z);
        }

        renderer.clearOverrideBlockTexture();

        renderer.renderAllFaces = false;
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return false;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }

}
