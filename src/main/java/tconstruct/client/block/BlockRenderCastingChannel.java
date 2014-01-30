package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import tconstruct.blocks.logic.CastingChannelLogic;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

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
        renderer.func_147782_a(0.3125D, 0.375D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
        this.func_147784_q(block, metadata, renderer);
        //Channel Z-
        renderer.func_147782_a(0.3125D, 0.375D, 0D, 0.6875D, 0.5D, 0.3125D);
        this.func_147784_q(block, metadata, renderer);
        renderer.func_147782_a(0.3125D, 0.5D, 0D, 0.375D, 0.625D, 0.3125D);
        this.func_147784_q(block, metadata, renderer);
        renderer.func_147782_a(0.625D, 0.5D, 0D, 0.6875D, 0.625D, 0.3125D);
        this.func_147784_q(block, metadata, renderer);
        //Channel Z+
        renderer.func_147782_a(0.3125D, 0.375D, 0.6875D, 0.6875D, 0.5D, 1D);
        this.func_147784_q(block, metadata, renderer);
        renderer.func_147782_a(0.3125D, 0.5D, 0.6875D, 0.375D, 0.625D, 1D);
        this.func_147784_q(block, metadata, renderer);
        renderer.func_147782_a(0.625D, 0.5D, 0.6875D, 0.6875D, 0.625D, 1D);
        this.func_147784_q(block, metadata, renderer);
        //Wall X-
        renderer.func_147782_a(0.3125D, 0.375D, 0.3125D, 0.375D, 0.625D, 0.6875D);
        this.func_147784_q(block, metadata, renderer);
        //Wall X+
        renderer.func_147782_a(0.625D, 0.375D, 0.3125D, 0.6875D, 0.625D, 0.6875D);
        this.func_147784_q(block, metadata, renderer);
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == renderID)
        {
            CastingChannelLogic tile = (CastingChannelLogic) world.func_147438_o(x, y, z);

            if (!tile.hasTankConnected(ForgeDirection.DOWN))//CentrePiece, floor is removed if tank below is found
            {
                renderer.func_147782_a(0.3125D, 0.375D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            //"Guiding Borders" when tank below is found
            {
                renderer.func_147782_a(0.375D, 0.125D, 0.3125D, 0.625D, 0.5D, 0.375D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.375D, 0.125D, 0.625D, 0.625D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.3125D, 0.125D, 0.3125D, 0.375D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.625D, 0.125D, 0.3125D, 0.6875D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }
            /*renderer.func_147782_a(0.3125D, 0.375D, 0.3125D, 0.375D, 0.625D, 0.375D);
            renderer.func_147784_q(block, x, y, z);
            renderer.func_147782_a(0.3125D, 0.375D, 0.625D, 0.375D, 0.625D, 0.6875D);
            renderer.func_147784_q(block, x, y, z);
            renderer.func_147782_a(0.625D, 0.375D, 0.3125D, 0.6875D, 0.625D, 0.375D);
            renderer.func_147784_q(block, x, y, z);
            renderer.func_147782_a(0.625D, 0.375D, 0.625D, 0.6875D, 0.625D, 0.6875D);
            renderer.func_147784_q(block, x, y, z);*/
            if (tile.hasTankConnected(ForgeDirection.NORTH))//Channel to Z-
            {
                renderer.func_147782_a(0.3125D, 0.375D, 0D, 0.6875D, 0.5D, 0.3125D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.3125D, 0.5D, 0D, 0.375D, 0.625D, 0.3125D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.625D, 0.5D, 0D, 0.6875D, 0.625D, 0.3125D);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            //Wall to Z-
            {
                renderer.func_147782_a(0.375D, 0.5D, 0.3125D, 0.625D, 0.625D, 0.375D);
                renderer.func_147784_q(block, x, y, z);
            }

            if (tile.hasTankConnected(ForgeDirection.SOUTH))//Channel to Z+
            {
                renderer.func_147782_a(0.3125D, 0.375D, 0.6875D, 0.6875D, 0.5D, 1D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.3125D, 0.5D, 0.6875D, 0.375D, 0.625D, 1D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.625D, 0.5D, 0.6875D, 0.6875D, 0.625D, 1D);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            //Wall to Z+
            {
                renderer.func_147782_a(0.375D, 0.5D, 0.625D, 0.625D, 0.625D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }

            if (tile.hasTankConnected(ForgeDirection.WEST))//Channel to X-
            {
                renderer.func_147782_a(0D, 0.375D, 0.3125D, 0.3125D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0D, 0.5D, 0.3125D, 0.375D, 0.625D, 0.375D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0D, 0.5D, 0.625D, 0.375D, 0.625D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            //Wall to X-
            {
                renderer.func_147782_a(0.3125D, 0.5D, 0.3125D, 0.375D, 0.625D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }

            if (tile.hasTankConnected(ForgeDirection.EAST))//Channel to X+
            {
                renderer.func_147782_a(0.6875D, 0.375D, 0.3125D, 1D, 0.5D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.625D, 0.5D, 0.3125D, 1D, 0.625D, 0.375D);
                renderer.func_147784_q(block, x, y, z);
                renderer.func_147782_a(0.625D, 0.5D, 0.625D, 1D, 0.625D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }
            else
            //Wall to X+
            {
                renderer.func_147782_a(0.625D, 0.5D, 0.3125D, 0.6875D, 0.625D, 0.6875D);
                renderer.func_147784_q(block, x, y, z);
            }

            if (tile.liquid != null)
            {
                float height = tile.getLiquidAmount() / (tile.getCapacity() * 1.05F);
                float j = tile.liquid.amount / tile.fillMax;
                double startY = tile.tankBelow();
                //if(tile.nearbyChannel(ForgeDirection.DOWN))startY = -0.5D;
                //else if(tile.hasTankConnected(ForgeDirection.DOWN))startY = 0D;
                float liquidAmount = tile.liquid.amount / 288f * 0.125f;
                renderer.func_147782_a(0.375D, startY, 0.375D, 0.625D, 0.5 + liquidAmount, 0.625D); //Center
                renderLiquidPart(world, x, y, z, block, renderer, tile);
                //TConstruct.logger.info(tile.recentlyFilledDelay);

                if (tile.hasTankConnected(ForgeDirection.NORTH))
                {
                    if (tile.lastProvider == ForgeDirection.NORTH)
                    {
                        renderer.func_147782_a(0.375D, 0.5D, 0D, 0.625D, 0.5 + liquidAmount, 0.375D);
                        renderLiquidPart(world, x, y, z, block, renderer, tile);
                    }
                    else
                    {
                        if (tile.recentlyFilledDelay == 0)
                        {
                            renderer.func_147782_a(0.375D, 0.5D, 0D, 0.625D, 0.5 + liquidAmount, 0.375D);
                            renderLiquidPart(world, x, y, z, block, renderer, tile);
                        }
                    }
                }
                if (tile.hasTankConnected(ForgeDirection.SOUTH))
                {
                    if (tile.lastProvider == ForgeDirection.SOUTH)
                    {
                        renderer.func_147782_a(0.375D, 0.5D, 0.625D, 0.625D, 0.5 + liquidAmount, 1D);
                        renderLiquidPart(world, x, y, z, block, renderer, tile);
                    }
                    else
                    {
                        if (tile.recentlyFilledDelay == 0)
                        {
                            renderer.func_147782_a(0.375D, 0.5D, 0.625D, 0.625D, 0.5 + liquidAmount, 1D);
                            renderLiquidPart(world, x, y, z, block, renderer, tile);
                        }
                    }
                }
                if (tile.hasTankConnected(ForgeDirection.WEST))
                {
                    if (tile.lastProvider == ForgeDirection.WEST)
                    {
                        renderer.func_147782_a(0D, 0.5D, 0.375D, 0.375D, 0.5 + liquidAmount, 0.625D);
                        renderLiquidPart(world, x, y, z, block, renderer, tile);
                    }
                    else
                    {
                        if (tile.recentlyFilledDelay == 0)
                        {
                            renderer.func_147782_a(0D, 0.5D, 0.375D, 0.375D, 0.5 + liquidAmount, 0.625D);
                            renderLiquidPart(world, x, y, z, block, renderer, tile);
                        }
                    }
                }
                if (tile.hasTankConnected(ForgeDirection.EAST))
                {
                    if (tile.lastProvider == ForgeDirection.EAST)
                    {
                        renderer.func_147782_a(0.625D, 0.5D, 0.375D, 1D, 0.5 + liquidAmount, 0.625D);
                        renderLiquidPart(world, x, y, z, block, renderer, tile);
                    }
                    else
                    {
                        if (tile.recentlyFilledDelay == 0)
                        {
                            renderer.func_147782_a(0.625D, 0.5D, 0.375D, 1D, 0.5 + liquidAmount, 0.625D);
                            renderLiquidPart(world, x, y, z, block, renderer, tile);
                        }
                    }

                }
                /*if(tile.lastProvider==ForgeDirection.UP && tile.recentlyFilledDelay != 0)
                {
                	renderer.func_147782_a(0.375D, 0.5D, 0.375D, 0.625D, 1D, 0.625D);
                	renderLiquidPart(world, x, y, z, block, renderer, tile);
                }*/

                /*if(tile.hasTankConnected(ForgeDirection.NORTH)&&tile.lastProvider!=ForgeDirection.NORTH)//Channel to Z-
                {
                	if(tile.recentlyFilledDelay == 0 || tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.375D, 0.5D, 0D, 0.625D, 0.625D, 0.375D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                	if(tile.recentlyFilledDelay != 0 && tile.lastProvider == ForgeDirection.UP)					
                	{
                			renderer.func_147782_a(0.375D, 0.375D, 0.375D, 0.625D, 1D, 0.625D);
                			renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                }
                if(tile.hasTankConnected(ForgeDirection.SOUTH)&&tile.lastProvider!=ForgeDirection.SOUTH)//Channel to Z+
                {
                	if(tile.recentlyFilledDelay == 0 || tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.375D, 0.5D, 0.625D, 0.625D, 0.625D, 1D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                	if(tile.recentlyFilledDelay != 0 && tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.375D, 0.375D, 0.375D, 0.625D, 1D, 0.625D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                }
                if(tile.hasTankConnected(ForgeDirection.WEST)&&tile.lastProvider!=ForgeDirection.WEST)//Channel to X-
                {
                	if(tile.recentlyFilledDelay == 0 || tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0D, 0.5D, 0.375D, 0.375D, 0.625D, 0.625D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                	if(tile.recentlyFilledDelay != 0 && tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.375D, 0.375D, 0.375D, 0.625D, 1D, 0.625D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                }
                if(tile.hasTankConnected(ForgeDirection.EAST)&&tile.lastProvider!=ForgeDirection.EAST)//Channel to X+
                {
                	if(tile.recentlyFilledDelay == 0 || tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.625D, 0.5D, 0.375D, 1D, 0.625D, 0.625D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                	if(tile.recentlyFilledDelay != 0 && tile.lastProvider == ForgeDirection.UP)
                	{
                		renderer.func_147782_a(0.375D, 0.375D, 0.375D, 0.625D, 1D, 0.625D);
                		renderLiquidPart(world, x, y, z, block, renderer, tile);
                	}
                }
                if(tile.lastProvider==ForgeDirection.NORTH && tile.recentlyFilledDelay != 0)
                {
                	renderer.func_147782_a(0.375D, 0.5D, 0.625D, 0.625D, 0.625D, 1D);
                	renderLiquidPart(world, x, y, z, block, renderer, tile);
                }
                if(tile.lastProvider==ForgeDirection.SOUTH && tile.recentlyFilledDelay != 0)
                {
                	renderer.func_147782_a(0.375D, 0.5D, 0D, 0.625D, 0.625D, 0.375D);
                	renderLiquidPart(world, x, y, z, block, renderer, tile);
                }
                if(tile.lastProvider==ForgeDirection.WEST && tile.recentlyFilledDelay != 0)
                {
                	renderer.func_147782_a(0.625D, 0.5D, 0.375D, 1D, 0.625D, 0.625D);
                	renderLiquidPart(world, x, y, z, block, renderer, tile);
                }
                if(tile.lastProvider==ForgeDirection.EAST && tile.recentlyFilledDelay != 0)
                {
                	renderer.func_147782_a(0D, 0.5D, 0.375D, 0.375D, 0.625D, 0.625D);
                	renderLiquidPart(world, x, y, z, block, renderer, tile);
                }*/
            }

        }
        return true;
    }

    private void renderLiquidPart (IBlockAccess world, int x, int y, int z, Block block, RenderBlocks renderer, CastingChannelLogic logic)
    {
        FluidStack liquid = logic.liquid;
        int color = block.func_149720_d(world, x, y, z);
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        Fluid fluid = liquid.getFluid();
        if (fluid.canBePlacedInWorld())
            BlockSkinRenderHelper.renderMetadataBlock(fluid.getBlock(), 0, x, y, z, renderer, world);
        else
            BlockSkinRenderHelper.renderLiquidBlock(fluid.getStillIcon(), fluid.getFlowingIcon(), x, y, z, renderer, world);
    }

    private void func_147784_q (Block block, int meta, RenderBlocks renderer)
    {
        Tessellator tessellator = Tessellator.instance;
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        renderer.func_147768_a(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(0, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        renderer.func_147806_b(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(1, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        renderer.func_147761_c(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(2, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        renderer.func_147734_d(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(3, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        renderer.func_147798_e(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(4, meta));
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        renderer.func_147764_f(block, 0.0D, 0.0D, 0.0D, block.func_149691_a(5, meta));
        tessellator.draw();
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return renderID;
    }
}
