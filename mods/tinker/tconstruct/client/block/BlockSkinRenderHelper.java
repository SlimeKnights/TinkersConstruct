package mods.tinker.tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class BlockSkinRenderHelper
{
	public static boolean renderMetadataBlock (Block block, int metadata, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
	{
		int var5 = block.colorMultiplier(world, x, y, z);
		float var6 = (float) (var5 >> 16 & 255) / 255.0F;
		float var7 = (float) (var5 >> 8 & 255) / 255.0F;
		float var8 = (float) (var5 & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
			float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
			float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
			var6 = var9;
			var7 = var10;
			var8 = var11;
		}

		return Minecraft.isAmbientOcclusionEnabled() && Block.lightValue[block.blockID] == 0 ? 
				renderMetadataBlockWithAmbientOcclusion(block, metadata, x, y, z, var6, var7, var8, renderer, world)
				: renderMetadataBlockWithColorMultiplier(block, metadata, x, y, z, var6, var7, var8, renderer, world);
	}
	
	static boolean renderMetadataBlockWithAmbientOcclusion(Block block, int metadata, int xMin, int yMin, int zMin, 
			float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		render.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (render.getBlockIcon(block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (render.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yMin;
            }

            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchXYNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchYZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchYZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yMin;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin - 1, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            f3 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (render.aoLightValueScratchYZNP + f7 + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + f7 + render.aoLightValueScratchYZNN) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.5F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.5F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceYNeg(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yMin;
            }

            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchXYPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchYZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchYZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yMin;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin + 1, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (render.aoLightValueScratchYZPP + f7 + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax;
            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceYPos(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(1, metadata));
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zMin;
            }

            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchYZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchYZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin + 1, zMin);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin + 1, zMin);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zMin;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin - 1))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchYZNN + f7 + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.8F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            icon = block.getIcon(2, metadata);
            render.renderFaceXPos(block, (double)xMin, (double)yMin, (double)zMin, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zMin;
            }

            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchYZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchYZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin + 1, zMin);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin + 1, zMin);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zMin;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin + 1))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + f7 + render.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (render.aoLightValueScratchYZNP + f7 + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.8F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            icon = block.getIcon(3, metadata);
            render.renderFaceXNeg(block, (double)xMin, (double)yMin, (double)zMin, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xMin;
            }

            render.aoLightValueScratchXYNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
            }

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xMin;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin - 1, yMin, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            f6 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + f7 + render.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.aoLightValueScratchXZNN + f7 + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
            f5 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + f7) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.6F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            icon = block.getIcon(4, metadata);
            render.renderFaceZNeg(block, (double)xMin, (double)yMin, (double)zMin, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xMin;
            }

            render.aoLightValueScratchXYPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xMin;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin + 1, yMin, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            f3 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + f7 + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.6F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            icon = block.getIcon(5, metadata);
            render.renderFaceZPos(block, (double)xMin, (double)yMin, (double)zMin, icon);

            flag = true;
        }

        render.enableAO = false;
        return flag;
    }
	
	static boolean renderMetadataBlockWithColorMultiplier(Block block, int metadata, int xMin, int yMin, int zMin, float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		render.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * xMax;
        float f8 = f4 * yMax;
        float f9 = f4 * zMax;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Block.grass)
        {
            f10 = f3 * xMax;
            f11 = f5 * xMax;
            f12 = f6 * xMax;
            f13 = f3 * yMax;
            f14 = f5 * yMax;
            f15 = f6 * yMax;
            f16 = f3 * zMax;
            f17 = f5 * zMax;
            f18 = f6 * zMax;
        }

        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(1, metadata));
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceXPos(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(2, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceXNeg(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(3, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceZNeg(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(4, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceZPos(block, (double)xMin, (double)yMin, (double)zMin, block.getIcon(5, metadata));

            flag = true;
        }

        return flag;
    }
	
	public static boolean renderFakeBlock (Icon texture, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
	{
		Block block = Block.stone;
		int var5 = block.colorMultiplier(world, x, y, z);
		float var6 = (float) (var5 >> 16 & 255) / 255.0F;
		float var7 = (float) (var5 >> 8 & 255) / 255.0F;
		float var8 = (float) (var5 & 255) / 255.0F;

		if (EntityRenderer.anaglyphEnable)
		{
			float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
			float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
			float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
			var6 = var9;
			var7 = var10;
			var8 = var11;
		}

		return Minecraft.isAmbientOcclusionEnabled() ? 
				renderFakeBlockWithAmbientOcclusion(texture, x, y, z, var6, var7, var8, renderer, world)
				: renderFakeBlockWithColorMultiplier(texture, x, y, z, var6, var7, var8, renderer, world);
	}
	
	static boolean renderFakeBlockWithAmbientOcclusion(Icon texture, int xMin, int yMin, int zMin, 
			float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		Block block = Block.stone;
		render.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);

        if (render.getBlockIcon(block).getIconName().equals("grass_top"))
        {
            flag1 = false;
        }
        else if (render.hasOverrideBlockTexture())
        {
            flag1 = false;
        }

        boolean flag2;
        boolean flag3;
        boolean flag4;
        boolean flag5;
        float f7;
        int i1;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yMin;
            }

            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchXYNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchYZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchYZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yMin;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin - 1, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            f3 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + f7) / 4.0F;
            f6 = (render.aoLightValueScratchYZNP + f7 + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
            f5 = (f7 + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
            f4 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + f7 + render.aoLightValueScratchYZNN) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.5F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.5F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceYNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yMin;
            }

            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchXYPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchYZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchYZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yMin;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin + 1, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (render.aoLightValueScratchYZPP + f7 + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax;
            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceYPos(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zMin;
            }

            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchYZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchYZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin + 1, zMin);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin + 1, zMin);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zMin;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin - 1))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchYZNN + f7 + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.8F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceXPos(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zMin;
            }

            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoLightValueScratchYZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchYZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin + 1, zMin);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin + 1, zMin);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zMin;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin + 1))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + f7 + render.aoLightValueScratchYZPP) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (render.aoLightValueScratchYZNP + f7 + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.8F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceXNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xMin;
            }

            render.aoLightValueScratchXYNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
            }

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xMin;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin - 1, yMin, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
            f6 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + f7 + render.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.aoLightValueScratchXZNN + f7 + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
            f5 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + f7) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.6F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceZNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xMin;
            }

            render.aoLightValueScratchXYPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoLightValueScratchXYPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
            flag3 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
            flag2 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
            flag5 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
            flag4 = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xMin;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin + 1, yMin, zMin))
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
            }

            f7 = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
            f3 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + f7 + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = xMax * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = yMax * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = zMax * 0.6F;
            }
            else
            {
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F;
            }

            render.colorRedTopLeft *= f3;
            render.colorGreenTopLeft *= f3;
            render.colorBlueTopLeft *= f3;
            render.colorRedBottomLeft *= f4;
            render.colorGreenBottomLeft *= f4;
            render.colorBlueBottomLeft *= f4;
            render.colorRedBottomRight *= f5;
            render.colorGreenBottomRight *= f5;
            render.colorBlueBottomRight *= f5;
            render.colorRedTopRight *= f6;
            render.colorGreenTopRight *= f6;
            render.colorBlueTopRight *= f6;
            render.renderFaceZPos(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        render.enableAO = false;
        return flag;
    }
	
	static boolean renderFakeBlockWithColorMultiplier(Icon texture, int xMin, int yMin, int zMin, float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		Block block = Block.stone;
		render.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * xMax;
        float f8 = f4 * yMax;
        float f9 = f4 * zMax;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Block.grass)
        {
            f10 = f3 * xMax;
            f11 = f5 * xMax;
            f12 = f6 * xMax;
            f13 = f3 * yMax;
            f14 = f5 * yMax;
            f15 = f6 * yMax;
            f16 = f3 * zMax;
            f17 = f5 * zMax;
            f18 = f6 * zMax;
        }

        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceXPos(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceXNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceZNeg(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceZPos(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        return flag;
    }
}
