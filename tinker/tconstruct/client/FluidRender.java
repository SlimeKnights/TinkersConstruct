package tinker.tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import tinker.tconstruct.blocks.liquids.LiquidMetalBase;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class FluidRender implements ISimpleBlockRenderingHandler
{
	public static int fluidModel = RenderingRegistry.getNextAvailableRenderId();
	@Override
	public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		//Inventory should be an item. renderer is not here!
	}

	@Override
	public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
	{
		return renderBlockFluids(renderer, world, block, x, y, z);
	}

	@Override
	public boolean shouldRender3DInInventory ()
	{
		return false;
	}

	@Override
	public int getRenderId ()
	{
		return fluidModel;
	}
	
	public boolean renderBlockFluids(RenderBlocks renderer, IBlockAccess world, Block block, int x, int y, int z)
    {
        Tessellator tessellator = Tessellator.instance;
        int blockColor = block.colorMultiplier(world, x, y, z);
        float var7 = (float)(blockColor >> 16 & 255) / 255.0F;
        float var8 = (float)(blockColor >> 8 & 255) / 255.0F;
        float var9 = (float)(blockColor & 255) / 255.0F;
        boolean renderTop = block.shouldSideBeRendered(world, x, y + 1, z, 1);
        boolean renderBottom = block.shouldSideBeRendered(world, x, y - 1, z, 0);
        boolean[] var12 = new boolean[] {block.shouldSideBeRendered(world, x, y, z - 1, 2), block.shouldSideBeRendered(world, x, y, z + 1, 3), block.shouldSideBeRendered(world, x - 1, y, z, 4), block.shouldSideBeRendered(world, x + 1, y, z, 5)};

        if (!renderTop && !renderBottom && !var12[0] && !var12[1] && !var12[2] && !var12[3])
        {
            return false;
        }
        else
        {
            boolean var13 = false;
            float var14 = 0.5F;
            float var15 = 1.0F;
            float var16 = 0.8F;
            float var17 = 0.6F;
            double var18 = 0.0D;
            double var20 = 1.0D;
            Material var22 = block.blockMaterial;
            //int var23 = world.getBlockMetadata(x, y, z);
            double var24 = (double)renderer.getFluidHeight(x, y, z, var22);
            double var26 = (double)renderer.getFluidHeight(x, y, z + 1, var22);
            double var28 = (double)renderer.getFluidHeight(x + 1, y, z + 1, var22);
            double var30 = (double)renderer.getFluidHeight(x + 1, y, z, var22);
            double var32 = 0.0010000000474974513D;
            int texturePos;
            int textureBase = block.getBlockTexture(world, x, y, z, 0);
            int var37;

            if (renderer.renderAllFaces || renderTop)
            {
                var13 = true;
                texturePos = block.getBlockTextureFromSide(1) + textureBase;
                float var35 = (float)LiquidMetalBase.getFlowDirection(world, x, y, z, var22);

                if (var35 > -999.0F)
                {
                    texturePos = block.getBlockTextureFromSide(2) + textureBase;
                }

                var24 -= var32;
                var26 -= var32;
                var28 -= var32;
                var30 -= var32;
                int var36 = (texturePos & 15) << 4;
                var37 = texturePos & 240;
                double var38 = ((double)var36 + 8.0D) / 256.0D;
                double var40 = ((double)var37 + 8.0D) / 256.0D;

                if (var35 < -999.0F)
                {
                    var35 = 0.0F;
                }
                else
                {
                    var38 = (double)((float)(var36 + 16) / 256.0F);
                    var40 = (double)((float)(var37 + 16) / 256.0F);
                }

                double var42 = (double)(MathHelper.sin(var35) * 8.0F) / 256.0D;
                double var44 = (double)(MathHelper.cos(var35) * 8.0F) / 256.0D;
                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
                float var46 = 1.0F;
                tessellator.setColorOpaque_F(var15 * var46 * var7, var15 * var46 * var8, var15 * var46 * var9);
                tessellator.addVertexWithUV((double)(x + 0), (double)y + var24, (double)(z + 0), var38 - var44 - var42, var40 - var44 + var42);
                tessellator.addVertexWithUV((double)(x + 0), (double)y + var26, (double)(z + 1), var38 - var44 + var42, var40 + var44 + var42);
                tessellator.addVertexWithUV((double)(x + 1), (double)y + var28, (double)(z + 1), var38 + var44 + var42, var40 + var44 - var42);
                tessellator.addVertexWithUV((double)(x + 1), (double)y + var30, (double)(z + 0), var38 + var44 - var42, var40 - var44 - var42);
            }

            if (renderer.renderAllFaces || renderBottom)
            {
                tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
                float var65 = 1.0F;
                tessellator.setColorOpaque_F(var14 * var65, var14 * var65, var14 * var65);
                renderer.renderBottomFace(block, (double)x, (double)y + var32, (double)z, block.getBlockTextureFromSide(0));
                var13 = true;
            }

            for (texturePos = 0; texturePos < 4; ++texturePos)
            {
                int var64 = x;
                var37 = z;

                if (texturePos == 0)
                {
                    var37 = z - 1;
                }

                if (texturePos == 1)
                {
                    ++var37;
                }

                if (texturePos == 2)
                {
                    var64 = x - 1;
                }

                if (texturePos == 3)
                {
                    ++var64;
                }

                int var66 = block.getBlockTextureFromSide(texturePos + 2)  + textureBase;
                int var39 = (var66 & 15) << 4;
                int var67 = var66 & 240;

                if (renderer.renderAllFaces || var12[texturePos])
                {
                    double var43;
                    double var41;
                    double var47;
                    double var45;
                    double var51;
                    double var49;

                    if (texturePos == 0)
                    {
                        var41 = var24;
                        var43 = var30;
                        var45 = (double)x;
                        var49 = (double)(x + 1);
                        var47 = (double)z + var32;
                        var51 = (double)z + var32;
                    }
                    else if (texturePos == 1)
                    {
                        var41 = var28;
                        var43 = var26;
                        var45 = (double)(x + 1);
                        var49 = (double)x;
                        var47 = (double)(z + 1) - var32;
                        var51 = (double)(z + 1) - var32;
                    }
                    else if (texturePos == 2)
                    {
                        var41 = var26;
                        var43 = var24;
                        var45 = (double)x + var32;
                        var49 = (double)x + var32;
                        var47 = (double)(z + 1);
                        var51 = (double)z;
                    }
                    else
                    {
                        var41 = var30;
                        var43 = var28;
                        var45 = (double)(x + 1) - var32;
                        var49 = (double)(x + 1) - var32;
                        var47 = (double)z;
                        var51 = (double)(z + 1);
                    }

                    var13 = true;
                    double var53 = (double)((float)(var39 + 0) / 256.0F);
                    double var55 = ((double)(var39 + 16) - 0.01D) / 256.0D;
                    double var57 = ((double)var67 + (1.0D - var41) * 16.0D) / 256.0D;
                    double var59 = ((double)var67 + (1.0D - var43) * 16.0D) / 256.0D;
                    double var61 = ((double)(var67 + 16) - 0.01D) / 256.0D;
                    tessellator.setBrightness(block.getMixedBrightnessForBlock(world, var64, y, var37));
                    float var63 = 1.0F;

                    if (texturePos < 2)
                    {
                        var63 *= var16;
                    }
                    else
                    {
                        var63 *= var17;
                    }

                    tessellator.setColorOpaque_F(var15 * var63 * var7, var15 * var63 * var8, var15 * var63 * var9);
                    tessellator.addVertexWithUV(var45, (double)y + var41, var47, var53, var57);
                    tessellator.addVertexWithUV(var49, (double)y + var43, var51, var55, var59);
                    tessellator.addVertexWithUV(var49, (double)(y + 0), var51, var55, var61);
                    tessellator.addVertexWithUV(var45, (double)(y + 0), var47, var53, var61);
                }
            }

            renderer.renderMinY = var18;
            renderer.renderMaxY = var20;
            return var13;
        }
    }
}
