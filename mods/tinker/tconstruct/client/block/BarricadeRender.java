package mods.tinker.tconstruct.client.block;

import mods.tinker.tconstruct.client.TProxyClient;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BarricadeRender implements ISimpleBlockRenderingHandler
{
	public static int model;

	public BarricadeRender()
	{
		model = RenderingRegistry.getNextAvailableRenderId();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block barricade, int modelId, RenderBlocks renderer)
	{
		if (modelId == model)
		{
			int meta = world.getBlockMetadata(x, y, z);
			Tessellator tessellator = Tessellator.instance;

			if ((meta < 4) || (meta == 10) || (meta == 11))
			{
				renderInnerEast(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if (((meta < 4) && (meta > 0)) || (meta == 11))
			{
				renderOuterEast(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if ((meta == 2) || (meta == 3) || ((meta >= 8) && (meta <= 11)))
			{
				renderInnerWest(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if ((meta == 3) || ((meta >= 9) && (meta <= 11)))
			{
				renderOuterWest(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if (((meta >= 4) && (meta <= 7)) || (meta >= 14))
			{
				renderInnerSouth(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if (((meta >= 5) && (meta <= 7)) || (meta == 15))
			{
				renderOuterSouth(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if ((meta == 6) || (meta == 7) || (meta >= 12))
			{
				renderInnerNorth(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			if ((meta == 7) || (meta >= 13))
			{
				renderOuterNorth(renderer, world, x, y, z, (Block)barricade, tessellator);
			}

			return true;
		}
		return false;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		if (modelID == model)
		{
			block.setBlockBounds(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
			TProxyClient.renderStandardInvBlock(renderer, block, metadata);
			block.setBlockBounds(0.375F, 0.375F, 0.0F, 0.625F, 0.625F, 1.0F);
			TProxyClient.renderStandardInvBlock(renderer, block, metadata);
		}
	}

	@Override
	public boolean shouldRender3DInInventory()
	{
		return true;
	}

	@Override
	public int getRenderId()
	{
		return model;
	}

	private static void renderInnerWest(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(renderblocks.blockAccess, x, y, z));
        float f3 = 1.0F;

        if (Block.lightValue[barricade.blockID] > 0)
        {
            f3 = 1.0F;
        }

        tessellator.setColorOpaque_F(f3, f3, f3);
        Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);

        if (renderblocks.hasOverrideBlockTexture())
        {
            icon = renderblocks.overrideBlockTexture;
        }

        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getMinV();
        double d2 = (double)icon.getMaxU();
        double d3 = (double)icon.getMaxV();
        Vec3[] vec1 = new Vec3[8];
        float minSize = 0.0625F;
        float maxSize = 0.0625F;
        float height = 0.625F;
        vec1[0] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-minSize), 0.0D, (double)(-maxSize));
        vec1[1] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)minSize, 0.0D, (double)(-maxSize));
        vec1[2] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)minSize, 0.0D, (double)maxSize);
        vec1[3] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-minSize), 0.0D, (double)maxSize);
        vec1[4] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-minSize), (double)height, (double)(-maxSize));
        vec1[5] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)minSize, (double)height, (double)(-maxSize));
        vec1[6] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)minSize, (double)height, (double)maxSize);
        vec1[7] = renderblocks.blockAccess.getWorldVec3Pool().getVecFromPool((double)(-minSize), (double)height, (double)maxSize);
        

        int l = renderblocks.blockAccess.getBlockMetadata(x, y, z);
        int i1 = l & 7;
        boolean flag = (l & 8) > 0;

        for (int j1 = 0; j1 < 8; ++j1)
        {
            if (flag)
            {
                vec1[j1].zCoord -= 0.0625D;
                vec1[j1].rotateAroundX(((float)Math.PI * 2F / 9F));
            }
            else
            {
                vec1[j1].zCoord += 0.0625D;
                vec1[j1].rotateAroundX(-((float)Math.PI * 2F / 9F));
            }

            if (i1 == 0 || i1 == 7)
            {
                vec1[j1].rotateAroundZ((float)Math.PI);
            }

            if (i1 == 6 || i1 == 0)
            {
                vec1[j1].rotateAroundY(((float)Math.PI / 2F));
            }

            if (i1 > 0 && i1 < 5)
            {
                vec1[j1].yCoord -= 0.375D;
                vec1[j1].rotateAroundX(((float)Math.PI / 2F));

                if (i1 == 4)
                {
                    vec1[j1].rotateAroundY(0.0F);
                }

                if (i1 == 3)
                {
                    vec1[j1].rotateAroundY((float)Math.PI);
                }

                if (i1 == 2)
                {
                    vec1[j1].rotateAroundY(((float)Math.PI / 2F));
                }

                if (i1 == 1)
                {
                    vec1[j1].rotateAroundY(-((float)Math.PI / 2F));
                }

                vec1[j1].xCoord += (double)x + 0.5D;
                vec1[j1].yCoord += (double)((float)y + 0.5F);
                vec1[j1].zCoord += (double)z + 0.5D;
            }
            else if (i1 != 0 && i1 != 7)
            {
                vec1[j1].xCoord += (double)x + 0.5D;
                vec1[j1].yCoord += (double)((float)y + 0.125F);
                vec1[j1].zCoord += (double)z + 0.5D;
            }
            else
            {
                vec1[j1].xCoord += (double)x + 0.5D;
                vec1[j1].yCoord += (double)((float)y + 0.875F);
                vec1[j1].zCoord += (double)z + 0.5D;
            }
        }

        Vec3 vec3 = null;
        Vec3 vec31 = null;
        Vec3 vec32 = null;
        Vec3 vec33 = null;

        for (int k1 = 0; k1 < 6; ++k1)
        {
            if (k1 == 0)
            {
                d0 = (double)icon.getInterpolatedU(7.0D);
                d1 = (double)icon.getInterpolatedV(6.0D);
                d2 = (double)icon.getInterpolatedU(9.0D);
                d3 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (k1 == 2)
            {
                d0 = (double)icon.getInterpolatedU(7.0D);
                d1 = (double)icon.getInterpolatedV(6.0D);
                d2 = (double)icon.getInterpolatedU(9.0D);
                d3 = (double)icon.getMaxV();
            }

            if (k1 == 0)
            {
                vec3 = vec1[0];
                vec31 = vec1[1];
                vec32 = vec1[2];
                vec33 = vec1[3];
            }
            else if (k1 == 1)
            {
                vec3 = vec1[7];
                vec31 = vec1[6];
                vec32 = vec1[5];
                vec33 = vec1[4];
            }
            else if (k1 == 2)
            {
                vec3 = vec1[1];
                vec31 = vec1[0];
                vec32 = vec1[4];
                vec33 = vec1[5];
            }
            else if (k1 == 3)
            {
                vec3 = vec1[2];
                vec31 = vec1[1];
                vec32 = vec1[5];
                vec33 = vec1[6];
            }
            else if (k1 == 4)
            {
                vec3 = vec1[3];
                vec31 = vec1[2];
                vec32 = vec1[6];
                vec33 = vec1[7];
            }
            else if (k1 == 5)
            {
                vec3 = vec1[0];
                vec31 = vec1[3];
                vec32 = vec1[7];
                vec33 = vec1[4];
            }

            tessellator.addVertexWithUV(vec3.xCoord, vec3.yCoord, vec3.zCoord, d0, d3);
            tessellator.addVertexWithUV(vec31.xCoord, vec31.yCoord, vec31.zCoord, d2, d3);
            tessellator.addVertexWithUV(vec32.xCoord, vec32.yCoord, vec32.zCoord, d2, d1);
            tessellator.addVertexWithUV(vec33.xCoord, vec33.yCoord, vec33.zCoord, d0, d1);
        }

		/*tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec1 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec1[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec1[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec1[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec1[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec1[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec1[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec1[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec1[7] = Vec3.createVectorHelper(-minSize, height, maxSize);
		for (int var25 = 0; var25 < 8; var25++)
		{
			vec1[var25].zCoord -= 0.0625D;
			vec1[var25].rotateAroundX(-0.7853982F);
			vec1[var25].yCoord -= 0.5D;
			vec1[var25].rotateAroundX(1.570796F);

			vec1[var25].xCoord += x + 0.625D;
			vec1[var25].yCoord += y;
			vec1[var25].zCoord += z + 0.5D;
		}

		Vec3 vec1a = null;
		Vec3 vec1b = null;
		Vec3 vec1c = null;
		Vec3 vec1d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec1a = vec1[0];
				vec1b = vec1[1];
				vec1c = vec1[2];
				vec1d = vec1[3];
			}
			else if (iter == 1)
			{
				vec1a = vec1[7];
				vec1b = vec1[6];
				vec1c = vec1[5];
				vec1d = vec1[4];
			}
			else if (iter == 2)
			{
				vec1a = vec1[1];
				vec1b = vec1[0];
				vec1c = vec1[4];
				vec1d = vec1[5];
			}
			else if (iter == 3)
			{
				vec1a = vec1[2];
				vec1b = vec1[1];
				vec1c = vec1[5];
				vec1d = vec1[6];
			}
			else if (iter == 4)
			{
				vec1a = vec1[3];
				vec1b = vec1[2];
				vec1c = vec1[6];
				vec1d = vec1[7];
			}
			else if (iter == 5)
			{
				vec1a = vec1[0];
				vec1b = vec1[3];
				vec1c = vec1[7];
				vec1d = vec1[4];
			}

			tessellator.addVertexWithUV(vec1a.xCoord, vec1a.yCoord, vec1a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec1b.xCoord, vec1b.yCoord, vec1b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec1c.xCoord, vec1c.yCoord, vec1c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec1d.xCoord, vec1d.yCoord, vec1d.zCoord, tex1, tex3);
		}*/
	}

	private static void renderOuterWest(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec2 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;

		vec2[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec2[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec2[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec2[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec2[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec2[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec2[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec2[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec2[var25].zCoord -= 0.0625D;
			vec2[var25].rotateAroundX(-0.7853982F);
			vec2[var25].yCoord -= 0.5D;
			vec2[var25].rotateAroundX(1.570796F);

			vec2[var25].xCoord += x + 0.125D;
			vec2[var25].yCoord += y;
			vec2[var25].zCoord += z + 0.5D;
		}
		Vec3 vec2a = null;
		Vec3 vec2b = null;
		Vec3 vec2c = null;
		Vec3 vec2d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec2a = vec2[0];
				vec2b = vec2[1];
				vec2c = vec2[2];
				vec2d = vec2[3];
			}
			else if (iter == 1)
			{
				vec2a = vec2[7];
				vec2b = vec2[6];
				vec2c = vec2[5];
				vec2d = vec2[4];
			}
			else if (iter == 2)
			{
				vec2a = vec2[1];
				vec2b = vec2[0];
				vec2c = vec2[4];
				vec2d = vec2[5];
			}
			else if (iter == 3)
			{
				vec2a = vec2[2];
				vec2b = vec2[1];
				vec2c = vec2[5];
				vec2d = vec2[6];
			}
			else if (iter == 4)
			{
				vec2a = vec2[3];
				vec2b = vec2[2];
				vec2c = vec2[6];
				vec2d = vec2[7];
			}
			else if (iter == 5)
			{
				vec2a = vec2[0];
				vec2b = vec2[3];
				vec2c = vec2[7];
				vec2d = vec2[4];
			}

			tessellator.addVertexWithUV(vec2a.xCoord, vec2a.yCoord, vec2a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec2b.xCoord, vec2b.yCoord, vec2b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec2c.xCoord, vec2c.yCoord, vec2c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec2d.xCoord, vec2d.yCoord, vec2d.zCoord, tex1, tex3);
		}
	}

	private static void renderInnerEast(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec3 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec3[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec3[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec3[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec3[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec3[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec3[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec3[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec3[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec3[var25].zCoord -= 0.0625D;
			vec3[var25].rotateAroundX(-0.7853982F);
			vec3[var25].yCoord -= 0.0625D;
			vec3[var25].rotateAroundX(6.283186F);

			vec3[var25].xCoord += x + 0.375D;
			vec3[var25].yCoord += y;
			vec3[var25].zCoord += z;
		}

		Vec3 vec3a = null;
		Vec3 vec3b = null;
		Vec3 vec3c = null;
		Vec3 Vec3 = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec3a = vec3[0];
				vec3b = vec3[1];
				vec3c = vec3[2];
				Vec3 = vec3[3];
			}
			else if (iter == 1)
			{
				vec3a = vec3[7];
				vec3b = vec3[6];
				vec3c = vec3[5];
				Vec3 = vec3[4];
			}
			else if (iter == 2)
			{
				vec3a = vec3[1];
				vec3b = vec3[0];
				vec3c = vec3[4];
				Vec3 = vec3[5];
			}
			else if (iter == 3)
			{
				vec3a = vec3[2];
				vec3b = vec3[1];
				vec3c = vec3[5];
				Vec3 = vec3[6];
			}
			else if (iter == 4)
			{
				vec3a = vec3[2];
				vec3b = vec3[1];
				vec3c = vec3[5];
				Vec3 = vec3[6];
			}
			else if (iter == 5)
			{
				vec3a = vec3[0];
				vec3b = vec3[3];
				vec3c = vec3[7];
				Vec3 = vec3[4];
			}

			tessellator.addVertexWithUV(vec3a.xCoord, vec3a.yCoord, vec3a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec3b.xCoord, vec3b.yCoord, vec3b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec3c.xCoord, vec3c.yCoord, vec3c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(Vec3.xCoord, Vec3.yCoord, Vec3.zCoord, tex1, tex3);
		}
	}

	private static void renderOuterEast(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec4 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;

		vec4[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec4[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec4[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec4[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec4[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec4[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec4[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec4[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec4[var25].zCoord -= 0.0625D;
			vec4[var25].rotateAroundX(-0.7853982F);
			vec4[var25].yCoord -= 0.0625D;
			vec4[var25].rotateAroundX(6.283186F);

			vec4[var25].xCoord += x + 0.875D;
			vec4[var25].yCoord += y;
			vec4[var25].zCoord += z;
		}

		Vec3 vec4a = null;
		Vec3 vec4b = null;
		Vec3 vec4c = null;
		Vec3 vec4d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec4a = vec4[0];
				vec4b = vec4[1];
				vec4c = vec4[2];
				vec4d = vec4[3];
			}
			else if (iter == 1)
			{
				vec4a = vec4[7];
				vec4b = vec4[6];
				vec4c = vec4[5];
				vec4d = vec4[4];
			}
			else if (iter == 2)
			{
				vec4a = vec4[1];
				vec4b = vec4[0];
				vec4c = vec4[4];
				vec4d = vec4[5];
			}
			else if (iter == 3)
			{
				vec4a = vec4[2];
				vec4b = vec4[1];
				vec4c = vec4[5];
				vec4d = vec4[6];
			}
			else if (iter == 4)
			{
				vec4a = vec4[2];
				vec4b = vec4[1];
				vec4c = vec4[5];
				vec4d = vec4[6];
			}
			else if (iter == 5)
			{
				vec4a = vec4[0];
				vec4b = vec4[3];
				vec4c = vec4[7];
				vec4d = vec4[4];
			}

			tessellator.addVertexWithUV(vec4a.xCoord, vec4a.yCoord, vec4a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec4b.xCoord, vec4b.yCoord, vec4b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec4c.xCoord, vec4c.yCoord, vec4c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec4d.xCoord, vec4d.yCoord, vec4d.zCoord, tex1, tex3);
		}
	}

	private static void renderInnerNorth(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec1 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec1[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec1[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec1[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec1[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec1[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec1[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec1[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec1[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec1[var25].zCoord -= 0.4375D;
			vec1[var25].rotateAroundX(-0.7853982F);
			vec1[var25].yCoord -= 0.5D;
			vec1[var25].rotateAroundY(1.570796F);

			vec1[var25].xCoord += x + 0.375D;
			vec1[var25].yCoord += y + 0.1875D;
			vec1[var25].zCoord += z + 0.625D;
		}

		Vec3 vec1a = null;
		Vec3 vec1b = null;
		Vec3 vec1c = null;
		Vec3 vec1d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec1a = vec1[0];
				vec1b = vec1[1];
				vec1c = vec1[2];
				vec1d = vec1[3];
			}
			else if (iter == 1)
			{
				vec1a = vec1[7];
				vec1b = vec1[6];
				vec1c = vec1[5];
				vec1d = vec1[4];
			}
			else if (iter == 2)
			{
				vec1a = vec1[1];
				vec1b = vec1[0];
				vec1c = vec1[4];
				vec1d = vec1[5];
			}
			else if (iter == 3)
			{
				vec1a = vec1[2];
				vec1b = vec1[1];
				vec1c = vec1[5];
				vec1d = vec1[6];
			}
			else if (iter == 4)
			{
				vec1a = vec1[3];
				vec1b = vec1[2];
				vec1c = vec1[6];
				vec1d = vec1[7];
			}
			else if (iter == 5)
			{
				vec1a = vec1[0];
				vec1b = vec1[3];
				vec1c = vec1[7];
				vec1d = vec1[4];
			}

			tessellator.addVertexWithUV(vec1a.xCoord, vec1a.yCoord, vec1a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec1b.xCoord, vec1b.yCoord, vec1b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec1c.xCoord, vec1c.yCoord, vec1c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec1d.xCoord, vec1d.yCoord, vec1d.zCoord, tex1, tex3);
		}
	}

	private static void renderOuterNorth(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec2 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec2[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec2[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec2[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec2[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec2[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec2[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec2[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec2[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec2[var25].zCoord -= 0.4375D;
			vec2[var25].rotateAroundX(-0.7853982F);
			vec2[var25].yCoord -= 0.5D;
			vec2[var25].rotateAroundY(1.570796F);

			vec2[var25].xCoord += x + 0.375D;
			vec2[var25].yCoord += y + 0.1875D;
			vec2[var25].zCoord += z + 0.125D;
		}

		Vec3 vec2a = null;
		Vec3 vec2b = null;
		Vec3 vec2c = null;
		Vec3 vec2d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec2a = vec2[0];
				vec2b = vec2[1];
				vec2c = vec2[2];
				vec2d = vec2[3];
			}
			else if (iter == 1)
			{
				vec2a = vec2[7];
				vec2b = vec2[6];
				vec2c = vec2[5];
				vec2d = vec2[4];
			}
			else if (iter == 2)
			{
				vec2a = vec2[1];
				vec2b = vec2[0];
				vec2c = vec2[4];
				vec2d = vec2[5];
			}
			else if (iter == 3)
			{
				vec2a = vec2[2];
				vec2b = vec2[1];
				vec2c = vec2[5];
				vec2d = vec2[6];
			}
			else if (iter == 4)
			{
				vec2a = vec2[3];
				vec2b = vec2[2];
				vec2c = vec2[6];
				vec2d = vec2[7];
			}
			else if (iter == 5)
			{
				vec2a = vec2[0];
				vec2b = vec2[3];
				vec2c = vec2[7];
				vec2d = vec2[4];
			}

			tessellator.addVertexWithUV(vec2a.xCoord, vec2a.yCoord, vec2a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec2b.xCoord, vec2b.yCoord, vec2b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec2c.xCoord, vec2c.yCoord, vec2c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec2d.xCoord, vec2d.yCoord, vec2d.zCoord, tex1, tex3);
		}
	}

	private static void renderInnerSouth(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);

		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec3 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec3[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec3[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec3[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec3[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec3[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec3[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec3[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec3[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec3[var25].zCoord -= 0.4375D;
			vec3[var25].rotateAroundX(-0.7853982F);
			vec3[var25].yCoord -= 0.5D;
			vec3[var25].rotateAroundY(4.712389F);

			vec3[var25].xCoord += x + 0.625D;
			vec3[var25].yCoord += y + 0.1875D;
			vec3[var25].zCoord += z + 0.375D;
		}
		Vec3 vec3a = null;
		Vec3 vec3b = null;
		Vec3 vec3c = null;
		Vec3 Vec3 = null;
		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec3a = vec3[0];
				vec3b = vec3[1];
				vec3c = vec3[2];
				Vec3 = vec3[3];
			}
			else if (iter == 1)
			{
				vec3a = vec3[7];
				vec3b = vec3[6];
				vec3c = vec3[5];
				Vec3 = vec3[4];
			}
			else if (iter == 2)
			{
				vec3a = vec3[1];
				vec3b = vec3[0];
				vec3c = vec3[4];
				Vec3 = vec3[5];
			}
			else if (iter == 3)
			{
				vec3a = vec3[2];
				vec3b = vec3[1];
				vec3c = vec3[5];
				Vec3 = vec3[6];
			}
			else if (iter == 4)
			{
				vec3a = vec3[2];
				vec3b = vec3[1];
				vec3c = vec3[5];
				Vec3 = vec3[6];
			}
			else if (iter == 5)
			{
				vec3a = vec3[0];
				vec3b = vec3[3];
				vec3c = vec3[7];
				Vec3 = vec3[4];
			}

			tessellator.addVertexWithUV(vec3a.xCoord, vec3a.yCoord, vec3a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec3b.xCoord, vec3b.yCoord, vec3b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec3c.xCoord, vec3c.yCoord, vec3c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(Vec3.xCoord, Vec3.yCoord, Vec3.zCoord, tex1, tex3);
		}
	}

	private static void renderOuterSouth(RenderBlocks renderblocks, IBlockAccess iblockaccess, int x, int y, int z, Block barricade, Tessellator tessellator)
	{
		tessellator.setBrightness(barricade.getMixedBrightnessForBlock(iblockaccess, x, y, z));
		float color = 1.0F;

		tessellator.setColorOpaque_F(color, color, color);
		Icon icon = renderblocks.getBlockIconFromSide(barricade, 0);
		double tex1 = (double)icon.getMinU();
        double tex2 = (double)icon.getMinV();
        double tex3 = (double)icon.getMaxU();
        double tex4 = (double)icon.getMaxV();
		Vec3[] vec4 = new Vec3[8];
		float minSize = 0.125F;
		float maxSize = 0.125F;
		float height = 1.416F;
		vec4[0] = Vec3.createVectorHelper(-minSize, 0.0D, -maxSize);
		vec4[1] = Vec3.createVectorHelper(minSize, 0.0D, -maxSize);
		vec4[2] = Vec3.createVectorHelper(minSize, 0.0D, maxSize);
		vec4[3] = Vec3.createVectorHelper(-minSize, 0.0D, maxSize);
		vec4[4] = Vec3.createVectorHelper(-minSize, height, -maxSize);
		vec4[5] = Vec3.createVectorHelper(minSize, height, -maxSize);
		vec4[6] = Vec3.createVectorHelper(minSize, height, maxSize);
		vec4[7] = Vec3.createVectorHelper(-minSize, height, maxSize);

		for (int var25 = 0; var25 < 8; var25++)
		{
			vec4[var25].zCoord -= 0.4375D;
			vec4[var25].rotateAroundX(-0.7853982F);
			vec4[var25].yCoord -= 0.5D;
			vec4[var25].rotateAroundY(4.712389F);

			vec4[var25].xCoord += x + 0.625D;
			vec4[var25].yCoord += y + 0.1875D;
			vec4[var25].zCoord += z + 0.875D;
		}

		Vec3 vec4a = null;
		Vec3 vec4b = null;
		Vec3 vec4c = null;
		Vec3 vec4d = null;

		for (int iter = 0; iter < 6; iter++)
		{
			if (iter == 0)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getInterpolatedV(8.0D);
            }
            else if (iter == 2)
            {
                tex1 = (double)icon.getInterpolatedU(7.0D);
                tex2 = (double)icon.getInterpolatedV(6.0D);
                tex3 = (double)icon.getInterpolatedU(9.0D);
                tex4 = (double)icon.getMaxV();
            }

			if (iter == 0)
			{
				vec4a = vec4[0];
				vec4b = vec4[1];
				vec4c = vec4[2];
				vec4d = vec4[3];
			}
			else if (iter == 1)
			{
				vec4a = vec4[7];
				vec4b = vec4[6];
				vec4c = vec4[5];
				vec4d = vec4[4];
			}
			else if (iter == 2)
			{
				vec4a = vec4[1];
				vec4b = vec4[0];
				vec4c = vec4[4];
				vec4d = vec4[5];
			}
			else if (iter == 3)
			{
				vec4a = vec4[2];
				vec4b = vec4[1];
				vec4c = vec4[5];
				vec4d = vec4[6];
			}
			else if (iter == 4)
			{
				vec4a = vec4[2];
				vec4b = vec4[1];
				vec4c = vec4[5];
				vec4d = vec4[6];
			}
			else if (iter == 5)
			{
				vec4a = vec4[0];
				vec4b = vec4[3];
				vec4c = vec4[7];
				vec4d = vec4[4];
			}

			tessellator.addVertexWithUV(vec4a.xCoord, vec4a.yCoord, vec4a.zCoord, tex1, tex4);
			tessellator.addVertexWithUV(vec4b.xCoord, vec4b.yCoord, vec4b.zCoord, tex2, tex4);
			tessellator.addVertexWithUV(vec4c.xCoord, vec4c.yCoord, vec4c.zCoord, tex2, tex3);
			tessellator.addVertexWithUV(vec4d.xCoord, vec4d.yCoord, vec4d.zCoord, tex1, tex3);
		}
	}
}
