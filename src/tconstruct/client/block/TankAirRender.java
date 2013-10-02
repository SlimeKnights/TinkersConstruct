package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.LavaTankLogic;
import tconstruct.client.TProxyClient;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class TankAirRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //No inventory block
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            //Liquid
            LavaTankLogic logic = (LavaTankLogic) world.getBlockTileEntity(x, y, z);
            if (logic.containsFluid())
            {
                FluidStack liquid = logic.tank.getFluid();
                renderer.setRenderBounds(0.001, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
                Fluid fluid = liquid.getFluid();
                if (fluid.canBePlacedInWorld())
                    BlockSkinRenderHelper.renderMetadataBlock(Block.blocksList[fluid.getBlockID()], 0, x, y, z, renderer, world);
                else
                    BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);

                renderer.setRenderBounds(00, 0.001, 0.001, 0.999, logic.getFluidAmountScaled(), 0.999);
            }

            //Block
            int meta = world.getBlockMetadata(x, y, z);
            if (meta == 0 && world.getBlockId(x, y + 1, z) == 0)
            {
                renderer.setRenderBounds(0.1875, 0, 0.1875, 0.8125, 0.125, 0.8125);
                renderer.renderStandardBlock(block, x, y + 1, z);
            }
            renderer.setRenderBounds(0, 0, 0, 1, 1, 1);
            renderer.renderStandardBlock(block, x, y, z);
        }
        return true;
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
