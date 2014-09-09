package tconstruct.smeltery.model;

import cpw.mods.fml.client.registry.*;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;
import org.lwjgl.opengl.GL11;
import tconstruct.client.BlockSkinRenderHelper;
import tconstruct.smeltery.logic.CastingChannelLogic;

/**
 * @author BluSunrize
 */

public class BlockRenderCastingChannel implements ISimpleBlockRenderingHandler
{
    public static int renderID = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        //Floor
        renderer.setRenderBounds(0.3125D, 0.375D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
        this.renderStandardBlock(block, metadata, renderer);
        //Channel Z-
        renderer.setRenderBounds(0.3125D, 0.375D, 0D, 0.6875D, 0.5D, 0.3125D);
        this.renderStandardBlock(block, metadata, renderer);
        renderer.setRenderBounds(0.3125D, 0.5D, 0D, 0.375D, 0.625D, 0.3125D);
        this.renderStandardBlock(block, metadata, renderer);
        renderer.setRenderBounds(0.625D, 0.5D, 0D, 0.6875D, 0.625D, 0.3125D);
        this.renderStandardBlock(block, metadata, renderer);
        //Channel Z+
        renderer.setRenderBounds(0.3125D, 0.375D, 0.6875D, 0.6875D, 0.5D, 1D);
        this.renderStandardBlock(block, metadata, renderer);
        renderer.setRenderBounds(0.3125D, 0.5D, 0.6875D, 0.375D, 0.625D, 1D);
        this.renderStandardBlock(block, metadata, renderer);
        renderer.setRenderBounds(0.625D, 0.5D, 0.6875D, 0.6875D, 0.625D, 1D);
        this.renderStandardBlock(block, metadata, renderer);
        //Wall X-
        renderer.setRenderBounds(0.3125D, 0.375D, 0.3125D, 0.375D, 0.625D, 0.6875D);
        this.renderStandardBlock(block, metadata, renderer);
        //Wall X+
        renderer.setRenderBounds(0.625D, 0.375D, 0.3125D, 0.6875D, 0.625D, 0.6875D);
        this.renderStandardBlock(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == renderID)
        {
            CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);

            Set<ForgeDirection> outputs = tile.getOutputs().keySet();

            if (!outputs.contains(ForgeDirection.DOWN))//CentrePiece, floor is removed if tank below is found
            {
                renderer.setRenderBounds(0.3125D, 0.375D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            //"Guiding Borders" when tank below is found
            {
                renderer.setRenderBounds(0.375D, 0.125D, 0.3125D, 0.625D, 0.5D, 0.375D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.375D, 0.125D, 0.625D, 0.625D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.3125D, 0.125D, 0.3125D, 0.375D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.625D, 0.125D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            if (outputs.contains(ForgeDirection.NORTH))//Channel to Z-
            {
                renderer.setRenderBounds(0.3125D, 0.375D, 0D, 0.6875D, 0.5D, 0.3125D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.3125D, 0.5D, 0D, 0.375D, 0.625D, 0.3125D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.625D, 0.5D, 0D, 0.6875D, 0.625D, 0.3125D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            //Wall to Z-
            {
                renderer.setRenderBounds(0.375D, 0.5D, 0.3125D, 0.625D, 0.625D, 0.375D);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (outputs.contains(ForgeDirection.SOUTH))//Channel to Z+
            {
                renderer.setRenderBounds(0.3125D, 0.375D, 0.6875D, 0.6875D, 0.5D, 1D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.3125D, 0.5D, 0.6875D, 0.375D, 0.625D, 1D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.625D, 0.5D, 0.6875D, 0.6875D, 0.625D, 1D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            //Wall to Z+
            {
                renderer.setRenderBounds(0.375D, 0.5D, 0.625D, 0.625D, 0.625D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (outputs.contains(ForgeDirection.WEST))//Channel to X-
            {
                renderer.setRenderBounds(0D, 0.375D, 0.3125D, 0.3125D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0D, 0.5D, 0.3125D, 0.375D, 0.625D, 0.375D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0D, 0.5D, 0.625D, 0.375D, 0.625D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            //Wall to X-
            {
                renderer.setRenderBounds(0.3125D, 0.5D, 0.3125D, 0.375D, 0.625D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }

            if (outputs.contains(ForgeDirection.EAST))//Channel to X+
            {
                renderer.setRenderBounds(0.6875D, 0.375D, 0.3125D, 1D, 0.5D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.625D, 0.5D, 0.3125D, 1D, 0.625D, 0.375D);
                renderer.renderStandardBlock(block, x, y, z);
                renderer.setRenderBounds(0.625D, 0.5D, 0.625D, 1D, 0.625D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            else
            //Wall to X+
            {
                renderer.setRenderBounds(0.625D, 0.5D, 0.3125D, 0.6875D, 0.625D, 0.6875D);
                renderer.renderStandardBlock(block, x, y, z);
            }
            FluidTankInfo tankMain = tile.getTankInfo(null)[0];
            if (tankMain.fluid != null)
            {
                float liquidAmount = (float) tankMain.fluid.amount / (float) tankMain.capacity * 0.125f;
                double startY = tile.tankBelow();
                renderer.setRenderBounds(0.375D, startY, 0.375D, 0.625D, 0.51 + liquidAmount, 0.625D); //Center
                renderLiquidPart(world, x, y, z, block, renderer, tankMain.fluid, false);
            }
            for (ForgeDirection dir : outputs)
            {
                double[] bounds = getRenderboundsForLiquid(dir);
                if (bounds == null)
                    break;
                FluidTankInfo tankSub = tile.getTankInfo(dir)[0];
                if (tankSub == null || tankSub.fluid == null)
                    break;
                float liquidAmount = (float) tankSub.fluid.amount / (float) tankSub.capacity * 0.125f / 2;
                renderer.setRenderBounds(bounds[0], 0.51, bounds[1], bounds[2], 0.5 + liquidAmount, bounds[3]);
                renderLiquidPart(world, x, y, z, block, renderer, tankSub.fluid, false);
            }
        }
        return true;
    }

    private double[] getRenderboundsForLiquid (ForgeDirection dir)
    {
        switch (dir)
        {
        case NORTH:
            return new double[] { 0.375, 0, 0.625, 0.375 };
        case SOUTH:
            return new double[] { 0.375, 0.625, 0.625, 1 };
        case WEST:
            return new double[] { 0, 0.375, 0.375, 0.625 };
        case EAST:
            return new double[] { 0.625, 0.375, 1, 0.625 };
        default:
            return null;
        }
    }

    private void renderLiquidPart (IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, FluidStack fluidStack, boolean useFlowingTexture)
    {
        int color = block.colorMultiplier(world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        Fluid fluid = fluidStack.getFluid();
        if (fluid.canBePlacedInWorld() && !useFlowingTexture)
            BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, x, y, z, renderer, world);
        else if (useFlowingTexture)
            BlockSkinRenderHelper.renderLiquidBlock(fluid.getFlowingIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
        else
            BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
    }

    private void renderStandardBlock (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, block.getIcon(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, block.getIcon(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public int getRenderId ()
    {
        return renderID;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelId)
    {
        return true;
    }
}