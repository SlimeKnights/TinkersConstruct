package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.client.TProxyClient;
import mods.tinker.tconstruct.library.util.IFacingLogic;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class MachineRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            TProxyClient.renderStandardInvBlock(renderer, block, metadata);
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
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            {
                renderRotatedBlock(block, x, y, z, world, renderer);
            }
        }
        return true;
    }
    
    public boolean renderRotatedBlock(Block block, int x, int y, int z, IBlockAccess world, RenderBlocks renderer)
    {
        IFacingLogic logic = (IFacingLogic) world.getBlockTileEntity(x, y, z);
        byte direction = logic.getRenderDirection();
        
        if (direction == 0)
        {
            renderer.uvRotateEast = 3;
            renderer.uvRotateWest = 3;
            renderer.uvRotateNorth = 3;
            renderer.uvRotateSouth = 3;
        }
        if (direction == 2)
        {
            renderer.uvRotateNorth = 2;
            renderer.uvRotateSouth = 1;
        }
        if (direction == 3)
        {
            renderer.uvRotateNorth = 1;
            renderer.uvRotateSouth = 2;
            renderer.uvRotateTop = 3;
            renderer.uvRotateBottom = 3;
        }
        if (direction == 4)
        {
            renderer.uvRotateEast = 1;
            renderer.uvRotateWest = 2;
            renderer.uvRotateTop = 2;
            renderer.uvRotateBottom = 1;
        }
        if (direction == 5)
        {
            renderer.uvRotateEast = 2;
            renderer.uvRotateWest = 1;
            renderer.uvRotateTop = 1;
            renderer.uvRotateBottom = 2;
        }

        boolean flag = renderer.renderStandardBlock(block, x, y, z);
        renderer.uvRotateSouth = 0;
        renderer.uvRotateEast = 0;
        renderer.uvRotateWest = 0;
        renderer.uvRotateNorth = 0;
        renderer.uvRotateTop = 0;
        renderer.uvRotateBottom = 0;
        return flag;
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
