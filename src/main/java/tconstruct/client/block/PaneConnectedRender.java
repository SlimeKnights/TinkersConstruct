package tconstruct.client.block;

import static net.minecraftforge.common.util.ForgeDirection.EAST;
import static net.minecraftforge.common.util.ForgeDirection.NORTH;
import static net.minecraftforge.common.util.ForgeDirection.SOUTH;
import static net.minecraftforge.common.util.ForgeDirection.WEST;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import tconstruct.blocks.GlassPaneConnected;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
        boolean temp = renderer.field_147837_f;
        renderer.field_147837_f = true;

        GlassPaneConnected pane = (GlassPaneConnected) block;

        boolean flag = pane.canPaneConnectTo(world, x, y, z, EAST);
        boolean flag1 = pane.canPaneConnectTo(world, x, y, z, WEST);
        boolean flag2 = pane.canPaneConnectTo(world, x, y, z, SOUTH);
        boolean flag3 = pane.canPaneConnectTo(world, x, y, z, NORTH);

        IIcon sideTexture = pane.getSideTextureIndex();

        if (!flag && !flag1 && !flag2 && !flag3)
        {
            renderer.func_147782_a(0D, 0D, 0.45D, 1D, 1D, 0.55D);
            renderer.func_147784_q(block, x, y, z);
            renderer.func_147782_a(0.45D, 0D, 0D, 0.55D, 1D, 1D);
            renderer.func_147784_q(block, x, y, z);
        }
        else
        {
            //			renderer.func_147782_a(0.45D, 0D, 0.45D, 0.55D, 1D, 0.55D);
            //			renderer.func_147784_q(block, x, y, z);
        }

        //		renderer.setOverrideBlockTexture(sideTexture);

        if (flag)
        {
            renderer.func_147782_a(0.45D, 0D, 0.45D, 1D, 1D, 0.55D);
            renderer.func_147784_q(block, x, y, z);
        }

        if (flag1)
        {
            renderer.func_147782_a(0D, 0D, 0.45D, 0.45D, 1D, 0.55D);
            renderer.func_147784_q(block, x, y, z);
        }

        if (flag2)
        {
            renderer.func_147782_a(0.45D, 0D, 0.45D, 0.55D, 1D, 1D);
            renderer.func_147784_q(block, x, y, z);
        }

        if (flag3)
        {
            renderer.func_147782_a(0.45D, 0D, 0D, 0.55D, 1D, 0.45D);
            renderer.func_147784_q(block, x, y, z);
        }

        renderer.func_147771_a();

        renderer.field_147837_f = false;
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
