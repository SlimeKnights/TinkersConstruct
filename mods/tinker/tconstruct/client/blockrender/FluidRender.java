package mods.tinker.tconstruct.client.blockrender;

import mods.tinker.tconstruct.blocks.LiquidMetalBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
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
		return renderFluids(renderer, world, block, x, y, z);
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

	public boolean renderFluids (RenderBlocks renderer, IBlockAccess world, Block block, int x, int y, int z)
	{
		Tessellator tessellator = Tessellator.instance;
		int l = block.colorMultiplier(world, x, y, z);
		float f = (float) (l >> 16 & 255) / 255.0F;
		float f1 = (float) (l >> 8 & 255) / 255.0F;
		float f2 = (float) (l & 255) / 255.0F;
		boolean flag = block.shouldSideBeRendered(world, x, y + 1, z, 1);
		boolean flag1 = block.shouldSideBeRendered(world, x, y - 1, z, 0);
		boolean[] aboolean = new boolean[] { block.shouldSideBeRendered(world, x, y, z - 1, 2), block.shouldSideBeRendered(world, x, y, z + 1, 3), block.shouldSideBeRendered(world, x - 1, y, z, 4), block.shouldSideBeRendered(world, x + 1, y, z, 5) };

		if (!flag && !flag1 && !aboolean[0] && !aboolean[1] && !aboolean[2] && !aboolean[3])
		{
			return false;
		}
		else
		{
			boolean flag2 = false;
			float f3 = 0.5F;
			float f4 = 1.0F;
			float f5 = 0.8F;
			float f6 = 0.6F;
			double d0 = 0.0D;
			double d1 = 1.0D;
			Material material = block.blockMaterial;
			int i1 = world.getBlockMetadata(x, y, z);
			double d2 = (double) renderer.getFluidHeight(x, y, z, material);
			double d3 = (double) renderer.getFluidHeight(x, y, z + 1, material);
			double d4 = (double) renderer.getFluidHeight(x + 1, y, z + 1, material);
			double d5 = (double) renderer.getFluidHeight(x + 1, y, z, material);
			double d6 = 0.0010000000474974513D;
			float f7;

			if (renderer.renderAllFaces || flag)
			{
				flag2 = true;
				Icon icon = block.getBlockTexture(world, x, y, z, 1);//renderer.func_94165_a(block, 1, i1);
				float f8 = (float) LiquidMetalBase.getFlowDirection(world, x, y, z, material);

				if (f8 > -999.0F)
				{
					icon = block.getBlockTexture(world, x, y, z, 2);//renderer.func_94165_a(block, 2, i1);
				}

				d2 -= d6;
				d3 -= d6;
				d4 -= d6;
				d5 -= d6;
				double d7;
				double d8;
				double d9;
				double d10;
				double d11;
				double d12;
				double d13;
				double d14;

				if (f8 < -999.0F)
				{
					d8 = (double) icon.getInterpolatedU(0.0D);
					d12 = (double) icon.getInterpolatedV(0.0D);
					d7 = d8;
					d11 = (double) icon.getInterpolatedV(16.0D);
					d10 = (double) icon.getInterpolatedU(16.0D);
					d14 = d11;
					d9 = d10;
					d13 = d12;
				}
				else
				{
					f7 = MathHelper.sin(f8) * 0.25F;
					float f9 = MathHelper.cos(f8) * 0.25F;
					d8 = (double) icon.getInterpolatedU((double) (8.0F + (-f9 - f7) * 16.0F));
					d12 = (double) icon.getInterpolatedV((double) (8.0F + (-f9 + f7) * 16.0F));
					d7 = (double) icon.getInterpolatedU((double) (8.0F + (-f9 + f7) * 16.0F));
					d11 = (double) icon.getInterpolatedV((double) (8.0F + (f9 + f7) * 16.0F));
					d10 = (double) icon.getInterpolatedU((double) (8.0F + (f9 + f7) * 16.0F));
					d14 = (double) icon.getInterpolatedV((double) (8.0F + (f9 - f7) * 16.0F));
					d9 = (double) icon.getInterpolatedU((double) (8.0F + (f9 - f7) * 16.0F));
					d13 = (double) icon.getInterpolatedV((double) (8.0F + (-f9 - f7) * 16.0F));
				}

				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
				f7 = 1.0F;
				tessellator.setColorOpaque_F(f4 * f7 * f, f4 * f7 * f1, f4 * f7 * f2);
				tessellator.addVertexWithUV((double) (x + 0), (double) y + d2, (double) (z + 0), d8, d12);
				tessellator.addVertexWithUV((double) (x + 0), (double) y + d3, (double) (z + 1), d7, d11);
				tessellator.addVertexWithUV((double) (x + 1), (double) y + d4, (double) (z + 1), d10, d14);
				tessellator.addVertexWithUV((double) (x + 1), (double) y + d5, (double) (z + 0), d9, d13);
			}

			if (renderer.renderAllFaces || flag1)
			{
				tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
				float f10 = 1.0F;
				tessellator.setColorOpaque_F(f3 * f10, f3 * f10, f3 * f10);
				renderer.renderBottomFace(block, (double) x, (double) y + d6, (double) z, block.getBlockTexture(world, x, y, z, 0));
				flag2 = true;
			}

			for (int side = 0; side < 4; ++side)
			{
				int xPos = x;
				int zPos = z;

				if (side == 0)
				{
					zPos = z - 1;
				}

				if (side == 1)
				{
					++zPos;
				}

				if (side == 2)
				{
					xPos = x - 1;
				}

				if (side == 3)
				{
					++xPos;
				}

				Icon icon1 = block.getBlockTexture(world, x, y, z, side+2);//renderer.func_94165_a(block, j1 + 2, i1);

				if (renderer.renderAllFaces || aboolean[side])
				{
					double d15;
					double d16;
					double d17;
					double d18;
					double d19;
					double d20;

					if (side == 0)
					{
						d15 = d2;
						d17 = d5;
						d16 = (double) x;
						d18 = (double) (x + 1);
						d19 = (double) z + d6;
						d20 = (double) z + d6;
					}
					else if (side == 1)
					{
						d15 = d4;
						d17 = d3;
						d16 = (double) (x + 1);
						d18 = (double) x;
						d19 = (double) (z + 1) - d6;
						d20 = (double) (z + 1) - d6;
					}
					else if (side == 2)
					{
						d15 = d3;
						d17 = d2;
						d16 = (double) x + d6;
						d18 = (double) x + d6;
						d19 = (double) (z + 1);
						d20 = (double) z;
					}
					else
					{
						d15 = d5;
						d17 = d4;
						d16 = (double) (x + 1) - d6;
						d18 = (double) (x + 1) - d6;
						d19 = (double) z;
						d20 = (double) (z + 1);
					}

					flag2 = true;
					float f11 = icon1.getInterpolatedU(0.0D);
					f7 = icon1.getInterpolatedU(8.0D);
					float f12 = icon1.getInterpolatedV((1.0D - d15) * 16.0D * 0.5D);
					float f13 = icon1.getInterpolatedV((1.0D - d17) * 16.0D * 0.5D);
					float f14 = icon1.getInterpolatedV(8.0D);
					tessellator.setBrightness(block.getMixedBrightnessForBlock(world, xPos, y, zPos));
					float f15 = 1.0F;

					if (side < 2)
					{
						f15 *= f5;
					}
					else
					{
						f15 *= f6;
					}

					tessellator.setColorOpaque_F(f4 * f15 * f, f4 * f15 * f1, f4 * f15 * f2);
					tessellator.addVertexWithUV(d16, (double) y + d15, d19, (double) f11, (double) f12);
					tessellator.addVertexWithUV(d18, (double) y + d17, d20, (double) f7, (double) f13);
					tessellator.addVertexWithUV(d18, (double) (y + 0), d20, (double) f7, (double) f14);
					tessellator.addVertexWithUV(d16, (double) (y + 0), d19, (double) f11, (double) f14);
				}
			}

			renderer.renderMinY = d0;
			renderer.renderMaxY = d1;
			return flag2;
		}
	}

	/*public boolean renderLiquidMetalBases(RenderBlocks renderer, IBlockAccess world, Block block, int x, int y, int z)
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
	}*/
}
