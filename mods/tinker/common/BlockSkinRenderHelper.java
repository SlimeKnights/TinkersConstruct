package mods.tinker.common;

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
        float f3 = render.lightValueOwn;
        float f4 = render.lightValueOwn;
        float f5 = render.lightValueOwn;
        float f6 = render.lightValueOwn;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        boolean flag5 = true;
        boolean flag6 = true;
        render.lightValueOwn = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin);
        render.aoLightValueXNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
        render.aoLightValueYNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
        render.aoLightValueZNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
        render.aoLightValueXPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
        render.aoLightValueYPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
        render.aoLightValueZPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        int i1 = l;
        int j1 = l;
        int k1 = l;
        int l1 = l;
        int i2 = l;
        int j2 = l;

        if (render.renderMinY <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin - 1, zMin))
        {
            j1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
        }

        if (render.renderMaxY >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin + 1, zMin))
        {
            i2 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
        }

        if (render.renderMinX <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin - 1, yMin, zMin))
        {
            i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
        }

        if (render.renderMaxX >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin + 1, yMin, zMin))
        {
            l1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
        }

        if (render.renderMinZ <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin - 1))
        {
            k1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
        }

        if (render.renderMaxZ >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin + 1))
        {
            j2 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        render.aoGrassXYZPPC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
        render.aoGrassXYZPNC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
        render.aoGrassXYZPCP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
        render.aoGrassXYZPCN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];
        render.aoGrassXYZNPC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
        render.aoGrassXYZNNC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
        render.aoGrassXYZNCN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
        render.aoGrassXYZNCP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];
        render.aoGrassXYZCPP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
        render.aoGrassXYZCPN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];
        render.aoGrassXYZCNP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];
        render.aoGrassXYZCNN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

        if (render.func_94175_b(block).func_94215_i().equals("grass_top"))
        {
            flag6 = false;
            flag5 = false;
            flag4 = false;
            flag3 = false;
            flag1 = false;
        }

        if (render.func_94167_b())
        {
            flag6 = false;
            flag5 = false;
            flag4 = false;
            flag3 = false;
            flag1 = false;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZCNN && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCNP && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
                }

                if (!render.aoGrassXYZCNN && !render.aoGrassXYZPNC)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCNP && !render.aoGrassXYZPNC)
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

                f3 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + render.aoLightValueYNeg) / 4.0F;
                f6 = (render.aoLightValueScratchYZNP + render.aoLightValueYNeg + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
                f5 = (render.aoLightValueYNeg + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
                f4 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + render.aoLightValueYNeg + render.aoLightValueScratchYZNN) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, j1);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, j1);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, j1);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, j1);
            }
            else
            {
                f6 = render.aoLightValueYNeg;
                f5 = render.aoLightValueYNeg;
                f4 = render.aoLightValueYNeg;
                f3 = render.aoLightValueYNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = render.aoBrightnessXYNN;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag1 ? xMax : 1.0F) * 0.5F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag1 ? yMax : 1.0F) * 0.5F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag1 ? zMax : 1.0F) * 0.5F;
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
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZCPN && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCPN && !render.aoGrassXYZPPC)
                {
                    render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                    render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                    render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCPP && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                    render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                    render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
                }

                if (!render.aoGrassXYZCPP && !render.aoGrassXYZPPC)
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

                f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + render.aoLightValueYPos) / 4.0F;
                f3 = (render.aoLightValueScratchYZPP + render.aoLightValueYPos + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
                f4 = (render.aoLightValueYPos + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
                f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + render.aoLightValueYPos + render.aoLightValueScratchYZPN) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i2);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i2);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i2);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i2);
            }
            else
            {
                f6 = render.aoLightValueYPos;
                f5 = render.aoLightValueYPos;
                f4 = render.aoLightValueYPos;
                f3 = render.aoLightValueYPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = i2;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = flag2 ? xMax : 1.0F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = flag2 ? yMax : 1.0F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = flag2 ? zMax : 1.0F;
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
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(1, metadata));
            flag = true;
        }

        float f7;
        float f8;
        float f9;
        int k2;
        float f10;
        int l2;
        Icon icon;
        int i3;
        int j3;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZCNN)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZCPN)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
                }

                if (!render.aoGrassXYZPCN && !render.aoGrassXYZCNN)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZPCN && !render.aoGrassXYZCPN)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                    f9 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                    f8 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                    f3 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMinX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    f4 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMaxY * render.renderMaxX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    f5 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMinY * render.renderMaxX + (double)f8 * (1.0D - render.renderMinY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    f6 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMinY) * render.renderMinX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                    k2 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, k1);
                    i3 = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, k1);
                    j3 = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, k1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, k1);
                    render.brightnessTopLeft = render.func_96444_a(k2, i3, j3, l2, render.renderMaxY * (1.0D - render.renderMinX), render.renderMaxY * render.renderMinX, (1.0D - render.renderMaxY) * render.renderMinX, (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    render.brightnessBottomLeft = render.func_96444_a(k2, i3, j3, l2, render.renderMaxY * (1.0D - render.renderMaxX), render.renderMaxY * render.renderMaxX, (1.0D - render.renderMaxY) * render.renderMaxX, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    render.brightnessBottomRight = render.func_96444_a(k2, i3, j3, l2, render.renderMinY * (1.0D - render.renderMaxX), render.renderMinY * render.renderMaxX, (1.0D - render.renderMinY) * render.renderMaxX, (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    render.brightnessTopRight = render.func_96444_a(k2, i3, j3, l2, render.renderMinY * (1.0D - render.renderMinX), render.renderMinY * render.renderMinX, (1.0D - render.renderMinY) * render.renderMinX, (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                }
                else
                {
                    f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                    f4 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                    f5 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                    f6 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, k1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, k1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, k1);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, k1);
                }
            }
            else
            {
                f6 = render.aoLightValueZNeg;
                f5 = render.aoLightValueZNeg;
                f4 = render.aoLightValueZNeg;
                f3 = render.aoLightValueZNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = k1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag3 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag3 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag3 ? zMax : 1.0F) * 0.8F;
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
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(2, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZCNP)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZCPP)
                {
                    render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                    render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
                }

                if (!render.aoGrassXYZPCP && !render.aoGrassXYZCNP)
                {
                    render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                    render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                    render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZPCP && !render.aoGrassXYZCPP)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                    f9 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f8 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                    f3 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMinX) + (double)f9 * render.renderMaxY * render.renderMinX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMinX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    f4 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMinY) * render.renderMinX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                    f5 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMinY * render.renderMaxX + (double)f8 * (1.0D - render.renderMinY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    f6 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMaxY * render.renderMaxX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    k2 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, j2);
                    i3 = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, j2);
                    j3 = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, j2);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, j2);
                    render.brightnessTopLeft = render.func_96444_a(k2, l2, j3, i3, render.renderMaxY * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * render.renderMinX, render.renderMaxY * render.renderMinX);
                    render.brightnessBottomLeft = render.func_96444_a(k2, l2, j3, i3, render.renderMinY * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * render.renderMinX, render.renderMinY * render.renderMinX);
                    render.brightnessBottomRight = render.func_96444_a(k2, l2, j3, i3, render.renderMinY * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * render.renderMaxX, render.renderMinY * render.renderMaxX);
                    render.brightnessTopRight = render.func_96444_a(k2, l2, j3, i3, render.renderMaxY * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * render.renderMaxX, render.renderMaxY * render.renderMaxX);
                }
                else
                {
                    f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                    f6 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f5 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, j2);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, j2);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, j2);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, j2);
                }
            }
            else
            {
                f6 = render.aoLightValueZPos;
                f5 = render.aoLightValueZPos;
                f4 = render.aoLightValueZPos;
                f3 = render.aoLightValueZPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = j2;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag4 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag4 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag4 ? zMax : 1.0F) * 0.8F;
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
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(0, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
                }

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZNPC)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                    f9 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                    f8 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                    f3 = (float)((double)f9 * render.renderMaxY * render.renderMaxZ + (double)f8 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)f7 * (1.0D - render.renderMaxY) * render.renderMaxZ);
                    f4 = (float)((double)f9 * render.renderMaxY * render.renderMinZ + (double)f8 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)f7 * (1.0D - render.renderMaxY) * render.renderMinZ);
                    f5 = (float)((double)f9 * render.renderMinY * render.renderMinZ + (double)f8 * render.renderMinY * (1.0D - render.renderMinZ) + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)f7 * (1.0D - render.renderMinY) * render.renderMinZ);
                    f6 = (float)((double)f9 * render.renderMinY * render.renderMaxZ + (double)f8 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)f7 * (1.0D - render.renderMinY) * render.renderMaxZ);
                    k2 = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
                    i3 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
                    j3 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);
                    render.brightnessTopLeft = render.func_96444_a(i3, j3, l2, k2, render.renderMaxY * render.renderMaxZ, render.renderMaxY * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * render.renderMaxZ);
                    render.brightnessBottomLeft = render.func_96444_a(i3, j3, l2, k2, render.renderMaxY * render.renderMinZ, render.renderMaxY * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * render.renderMinZ);
                    render.brightnessBottomRight = render.func_96444_a(i3, j3, l2, k2, render.renderMinY * render.renderMinZ, render.renderMinY * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * render.renderMinZ);
                    render.brightnessTopRight = render.func_96444_a(i3, j3, l2, k2, render.renderMinY * render.renderMaxZ, render.renderMinY * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * render.renderMaxZ);
                }
                else
                {
                    f6 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                    f3 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                    f5 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);
                }
            }
            else
            {
                f6 = render.aoLightValueXNeg;
                f5 = render.aoLightValueXNeg;
                f4 = render.aoLightValueXNeg;
                f3 = render.aoLightValueXNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = i1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag5 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag5 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag5 ? zMax : 1.0F) * 0.6F;
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
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(4, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZPNC && !render.aoGrassXYZPCN)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
                }

                if (!render.aoGrassXYZPNC && !render.aoGrassXYZPCP)
                {
                    render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                    render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                    render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
                }

                if (!render.aoGrassXYZPPC && !render.aoGrassXYZPCN)
                {
                    render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                    render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
                }

                if (!render.aoGrassXYZPPC && !render.aoGrassXYZPCP)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                    f9 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                    f8 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                    f10 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f3 = (float)((double)f7 * (1.0D - render.renderMinY) * render.renderMaxZ + (double)f9 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)f8 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)f10 * render.renderMinY * render.renderMaxZ);
                    f4 = (float)((double)f7 * (1.0D - render.renderMinY) * render.renderMinZ + (double)f9 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)f8 * render.renderMinY * (1.0D - render.renderMinZ) + (double)f10 * render.renderMinY * render.renderMinZ);
                    f5 = (float)((double)f7 * (1.0D - render.renderMaxY) * render.renderMinZ + (double)f9 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)f8 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)f10 * render.renderMaxY * render.renderMinZ);
                    f6 = (float)((double)f7 * (1.0D - render.renderMaxY) * render.renderMaxZ + (double)f9 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)f8 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)f10 * render.renderMaxY * render.renderMaxZ);
                    k2 = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, l1);
                    i3 = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, l1);
                    j3 = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, l1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, l1);
                    render.brightnessTopLeft = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMinY) * render.renderMaxZ, (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), render.renderMinY * (1.0D - render.renderMaxZ), render.renderMinY * render.renderMaxZ);
                    render.brightnessBottomLeft = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMinY) * render.renderMinZ, (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), render.renderMinY * (1.0D - render.renderMinZ), render.renderMinY * render.renderMinZ);
                    render.brightnessBottomRight = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMaxY) * render.renderMinZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), render.renderMaxY * (1.0D - render.renderMinZ), render.renderMaxY * render.renderMinZ);
                    render.brightnessTopRight = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMaxY) * render.renderMaxZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), render.renderMaxY * (1.0D - render.renderMaxZ), render.renderMaxY * render.renderMaxZ);
                }
                else
                {
                    f3 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                    f5 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                    f6 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, l1);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, l1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, l1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, l1);
                }
            }
            else
            {
                f6 = render.aoLightValueXPos;
                f5 = render.aoLightValueXPos;
                f4 = render.aoLightValueXPos;
                f3 = render.aoLightValueXPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = l1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag6 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag6 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag6 ? zMax : 1.0F) * 0.6F;
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
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(5, metadata));

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
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(1, metadata));
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(2, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(3, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(4, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(5, metadata));

            flag = true;
        }

        return flag;
    }
	
	public static boolean renderFakeBlock (Icon texture, int metadata, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
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
				renderFakeBlockWithAmbientOcclusion(texture, metadata, x, y, z, var6, var7, var8, renderer, world)
				: renderFakeBlockWithColorMultiplier(texture, metadata, x, y, z, var6, var7, var8, renderer, world);
	}
	
	static boolean renderFakeBlockWithAmbientOcclusion(Icon texture, int metadata, int xMin, int yMin, int zMin, 
			float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		Block block = Block.stone;
		render.enableAO = true;
        boolean flag = false;
        float f3 = render.lightValueOwn;
        float f4 = render.lightValueOwn;
        float f5 = render.lightValueOwn;
        float f6 = render.lightValueOwn;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        boolean flag5 = true;
        boolean flag6 = true;
        render.lightValueOwn = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin);
        render.aoLightValueXNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
        render.aoLightValueYNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
        render.aoLightValueZNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
        render.aoLightValueXPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
        render.aoLightValueYPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
        render.aoLightValueZPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        int i1 = l;
        int j1 = l;
        int k1 = l;
        int l1 = l;
        int i2 = l;
        int j2 = l;

        if (render.renderMinY <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin - 1, zMin))
        {
            j1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
        }

        if (render.renderMaxY >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin + 1, zMin))
        {
            i2 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
        }

        if (render.renderMinX <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin - 1, yMin, zMin))
        {
            i1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
        }

        if (render.renderMaxX >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin + 1, yMin, zMin))
        {
            l1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
        }

        if (render.renderMinZ <= 0.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin - 1))
        {
            k1 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
        }

        if (render.renderMaxZ >= 1.0D || !render.blockAccess.isBlockOpaqueCube(xMin, yMin, zMin + 1))
        {
            j2 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
        }

        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(983055);
        render.aoGrassXYZPPC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin + 1, zMin)];
        render.aoGrassXYZPNC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin - 1, zMin)];
        render.aoGrassXYZPCP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin + 1)];
        render.aoGrassXYZPCN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin + 1, yMin, zMin - 1)];
        render.aoGrassXYZNPC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin + 1, zMin)];
        render.aoGrassXYZNNC = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin - 1, zMin)];
        render.aoGrassXYZNCN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin - 1)];
        render.aoGrassXYZNCP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin - 1, yMin, zMin + 1)];
        render.aoGrassXYZCPP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin + 1)];
        render.aoGrassXYZCPN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin + 1, zMin - 1)];
        render.aoGrassXYZCNP = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin + 1)];
        render.aoGrassXYZCNN = Block.canBlockGrass[render.blockAccess.getBlockId(xMin, yMin - 1, zMin - 1)];

        if (render.func_94175_b(block).func_94215_i().equals("grass_top"))
        {
            flag6 = false;
            flag5 = false;
            flag4 = false;
            flag3 = false;
            flag1 = false;
        }

        if (render.func_94167_b())
        {
            flag6 = false;
            flag5 = false;
            flag4 = false;
            flag3 = false;
            flag1 = false;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin - 1, zMin, 0))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZCNN && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCNP && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
                }

                if (!render.aoGrassXYZCNN && !render.aoGrassXYZPNC)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCNP && !render.aoGrassXYZPNC)
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

                f3 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + render.aoLightValueYNeg) / 4.0F;
                f6 = (render.aoLightValueScratchYZNP + render.aoLightValueYNeg + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
                f5 = (render.aoLightValueYNeg + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
                f4 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + render.aoLightValueYNeg + render.aoLightValueScratchYZNN) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, j1);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, j1);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, j1);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, j1);
            }
            else
            {
                f6 = render.aoLightValueYNeg;
                f5 = render.aoLightValueYNeg;
                f4 = render.aoLightValueYNeg;
                f3 = render.aoLightValueYNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = render.aoBrightnessXYNN;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag1 ? xMax : 1.0F) * 0.5F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag1 ? yMax : 1.0F) * 0.5F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag1 ? zMax : 1.0F) * 0.5F;
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
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZCPN && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin - 1);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCPN && !render.aoGrassXYZPPC)
                {
                    render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                    render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin - 1);
                    render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin - 1);
                }

                if (!render.aoGrassXYZCPP && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                    render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin + 1);
                    render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin + 1);
                }

                if (!render.aoGrassXYZCPP && !render.aoGrassXYZPPC)
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

                f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + render.aoLightValueYPos) / 4.0F;
                f3 = (render.aoLightValueScratchYZPP + render.aoLightValueYPos + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
                f4 = (render.aoLightValueYPos + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
                f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + render.aoLightValueYPos + render.aoLightValueScratchYZPN) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i2);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i2);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i2);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i2);
            }
            else
            {
                f6 = render.aoLightValueYPos;
                f5 = render.aoLightValueYPos;
                f4 = render.aoLightValueYPos;
                f3 = render.aoLightValueYPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = i2;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = flag2 ? xMax : 1.0F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = flag2 ? yMax : 1.0F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = flag2 ? zMax : 1.0F;
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
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        float f7;
        float f8;
        float f9;
        int k2;
        float f10;
        int l2;
        Icon icon;
        int i3;
        int j3;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZCNN)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZCPN)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
                }

                if (!render.aoGrassXYZPCN && !render.aoGrassXYZCNN)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZPCN && !render.aoGrassXYZCPN)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                    f9 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                    f8 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                    f3 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMinX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    f4 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMaxY * render.renderMaxX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    f5 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMinY * render.renderMaxX + (double)f8 * (1.0D - render.renderMinY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    f6 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMinY) * render.renderMinX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                    k2 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, k1);
                    i3 = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, k1);
                    j3 = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, k1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, k1);
                    render.brightnessTopLeft = render.func_96444_a(k2, i3, j3, l2, render.renderMaxY * (1.0D - render.renderMinX), render.renderMaxY * render.renderMinX, (1.0D - render.renderMaxY) * render.renderMinX, (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    render.brightnessBottomLeft = render.func_96444_a(k2, i3, j3, l2, render.renderMaxY * (1.0D - render.renderMaxX), render.renderMaxY * render.renderMaxX, (1.0D - render.renderMaxY) * render.renderMaxX, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    render.brightnessBottomRight = render.func_96444_a(k2, i3, j3, l2, render.renderMinY * (1.0D - render.renderMaxX), render.renderMinY * render.renderMaxX, (1.0D - render.renderMinY) * render.renderMaxX, (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    render.brightnessTopRight = render.func_96444_a(k2, i3, j3, l2, render.renderMinY * (1.0D - render.renderMinX), render.renderMinY * render.renderMinX, (1.0D - render.renderMinY) * render.renderMinX, (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                }
                else
                {
                    f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                    f4 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                    f5 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                    f6 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, k1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, k1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, k1);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, k1);
                }
            }
            else
            {
                f6 = render.aoLightValueZNeg;
                f5 = render.aoLightValueZNeg;
                f4 = render.aoLightValueZNeg;
                f3 = render.aoLightValueZNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = k1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag3 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag3 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag3 ? zMax : 1.0F) * 0.8F;
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
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZCNP)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin - 1, zMin);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZCPP)
                {
                    render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin + 1, zMin);
                    render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin + 1, zMin);
                }

                if (!render.aoGrassXYZPCP && !render.aoGrassXYZCNP)
                {
                    render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                    render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin - 1, zMin);
                    render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin - 1, zMin);
                }

                if (!render.aoGrassXYZPCP && !render.aoGrassXYZCPP)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                    f9 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f8 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                    f3 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMinX) + (double)f9 * render.renderMaxY * render.renderMinX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMinX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinX));
                    f4 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMinX) + (double)f9 * render.renderMinY * render.renderMinX + (double)f8 * (1.0D - render.renderMinY) * render.renderMinX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinX));
                    f5 = (float)((double)f7 * render.renderMinY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMinY * render.renderMaxX + (double)f8 * (1.0D - render.renderMinY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxX));
                    f6 = (float)((double)f7 * render.renderMaxY * (1.0D - render.renderMaxX) + (double)f9 * render.renderMaxY * render.renderMaxX + (double)f8 * (1.0D - render.renderMaxY) * render.renderMaxX + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX));
                    k2 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, j2);
                    i3 = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, j2);
                    j3 = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, j2);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, j2);
                    render.brightnessTopLeft = render.func_96444_a(k2, l2, j3, i3, render.renderMaxY * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * (1.0D - render.renderMinX), (1.0D - render.renderMaxY) * render.renderMinX, render.renderMaxY * render.renderMinX);
                    render.brightnessBottomLeft = render.func_96444_a(k2, l2, j3, i3, render.renderMinY * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * (1.0D - render.renderMinX), (1.0D - render.renderMinY) * render.renderMinX, render.renderMinY * render.renderMinX);
                    render.brightnessBottomRight = render.func_96444_a(k2, l2, j3, i3, render.renderMinY * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * (1.0D - render.renderMaxX), (1.0D - render.renderMinY) * render.renderMaxX, render.renderMinY * render.renderMaxX);
                    render.brightnessTopRight = render.func_96444_a(k2, l2, j3, i3, render.renderMaxY * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxX), (1.0D - render.renderMaxY) * render.renderMaxX, render.renderMaxY * render.renderMaxX);
                }
                else
                {
                    f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                    f6 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f5 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, j2);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, j2);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, j2);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, j2);
                }
            }
            else
            {
                f6 = render.aoLightValueZPos;
                f5 = render.aoLightValueZPos;
                f4 = render.aoLightValueZPos;
                f3 = render.aoLightValueZPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = j2;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag4 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag4 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag4 ? zMax : 1.0F) * 0.8F;
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
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                    render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZNNC)
                {
                    render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                    render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
                }
                else
                {
                    render.aoLightValueScratchXYZNNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                    render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
                }

                if (!render.aoGrassXYZNCN && !render.aoGrassXYZNPC)
                {
                    render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                    render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
                }
                else
                {
                    render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                    render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
                }

                if (!render.aoGrassXYZNCP && !render.aoGrassXYZNPC)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                    f9 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                    f8 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                    f10 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                    f3 = (float)((double)f9 * render.renderMaxY * render.renderMaxZ + (double)f8 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)f7 * (1.0D - render.renderMaxY) * render.renderMaxZ);
                    f4 = (float)((double)f9 * render.renderMaxY * render.renderMinZ + (double)f8 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)f10 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)f7 * (1.0D - render.renderMaxY) * render.renderMinZ);
                    f5 = (float)((double)f9 * render.renderMinY * render.renderMinZ + (double)f8 * render.renderMinY * (1.0D - render.renderMinZ) + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)f7 * (1.0D - render.renderMinY) * render.renderMinZ);
                    f6 = (float)((double)f9 * render.renderMinY * render.renderMaxZ + (double)f8 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)f10 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)f7 * (1.0D - render.renderMinY) * render.renderMaxZ);
                    k2 = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
                    i3 = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
                    j3 = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);
                    render.brightnessTopLeft = render.func_96444_a(i3, j3, l2, k2, render.renderMaxY * render.renderMaxZ, render.renderMaxY * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMaxY) * render.renderMaxZ);
                    render.brightnessBottomLeft = render.func_96444_a(i3, j3, l2, k2, render.renderMaxY * render.renderMinZ, render.renderMaxY * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), (1.0D - render.renderMaxY) * render.renderMinZ);
                    render.brightnessBottomRight = render.func_96444_a(i3, j3, l2, k2, render.renderMinY * render.renderMinZ, render.renderMinY * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), (1.0D - render.renderMinY) * render.renderMinZ);
                    render.brightnessTopRight = render.func_96444_a(i3, j3, l2, k2, render.renderMinY * render.renderMaxZ, render.renderMinY * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), (1.0D - render.renderMinY) * render.renderMaxZ);
                }
                else
                {
                    f6 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                    f3 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                    f5 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, i1);
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, i1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, i1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, i1);
                }
            }
            else
            {
                f6 = render.aoLightValueXNeg;
                f5 = render.aoLightValueXNeg;
                f4 = render.aoLightValueXNeg;
                f3 = render.aoLightValueXNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = i1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag5 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag5 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag5 ? zMax : 1.0F) * 0.6F;
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
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            if (render.aoType > 0)
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

                if (!render.aoGrassXYZPNC && !render.aoGrassXYZPCN)
                {
                    render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPNN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin - 1);
                    render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin - 1);
                }

                if (!render.aoGrassXYZPNC && !render.aoGrassXYZPCP)
                {
                    render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                    render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
                }
                else
                {
                    render.aoLightValueScratchXYZPNP = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin + 1);
                    render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin + 1);
                }

                if (!render.aoGrassXYZPPC && !render.aoGrassXYZPCN)
                {
                    render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                    render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
                }
                else
                {
                    render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin - 1);
                    render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin - 1);
                }

                if (!render.aoGrassXYZPPC && !render.aoGrassXYZPCP)
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

                if (render.field_98189_n && render.field_94177_n.gameSettings.ambientOcclusion >= 2)
                {
                    f7 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                    f9 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                    f8 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                    f10 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    f3 = (float)((double)f7 * (1.0D - render.renderMinY) * render.renderMaxZ + (double)f9 * (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ) + (double)f8 * render.renderMinY * (1.0D - render.renderMaxZ) + (double)f10 * render.renderMinY * render.renderMaxZ);
                    f4 = (float)((double)f7 * (1.0D - render.renderMinY) * render.renderMinZ + (double)f9 * (1.0D - render.renderMinY) * (1.0D - render.renderMinZ) + (double)f8 * render.renderMinY * (1.0D - render.renderMinZ) + (double)f10 * render.renderMinY * render.renderMinZ);
                    f5 = (float)((double)f7 * (1.0D - render.renderMaxY) * render.renderMinZ + (double)f9 * (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ) + (double)f8 * render.renderMaxY * (1.0D - render.renderMinZ) + (double)f10 * render.renderMaxY * render.renderMinZ);
                    f6 = (float)((double)f7 * (1.0D - render.renderMaxY) * render.renderMaxZ + (double)f9 * (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ) + (double)f8 * render.renderMaxY * (1.0D - render.renderMaxZ) + (double)f10 * render.renderMaxY * render.renderMaxZ);
                    k2 = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, l1);
                    i3 = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, l1);
                    j3 = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, l1);
                    l2 = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, l1);
                    render.brightnessTopLeft = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMinY) * render.renderMaxZ, (1.0D - render.renderMinY) * (1.0D - render.renderMaxZ), render.renderMinY * (1.0D - render.renderMaxZ), render.renderMinY * render.renderMaxZ);
                    render.brightnessBottomLeft = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMinY) * render.renderMinZ, (1.0D - render.renderMinY) * (1.0D - render.renderMinZ), render.renderMinY * (1.0D - render.renderMinZ), render.renderMinY * render.renderMinZ);
                    render.brightnessBottomRight = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMaxY) * render.renderMinZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMinZ), render.renderMaxY * (1.0D - render.renderMinZ), render.renderMaxY * render.renderMinZ);
                    render.brightnessTopRight = render.func_96444_a(k2, l2, j3, i3, (1.0D - render.renderMaxY) * render.renderMaxZ, (1.0D - render.renderMaxY) * (1.0D - render.renderMaxZ), render.renderMaxY * (1.0D - render.renderMaxZ), render.renderMaxY * render.renderMaxZ);
                }
                else
                {
                    f3 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                    f4 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                    f5 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                    f6 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                    render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, l1);
                    render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, l1);
                    render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, l1);
                    render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, l1);
                }
            }
            else
            {
                f6 = render.aoLightValueXPos;
                f5 = render.aoLightValueXPos;
                f4 = render.aoLightValueXPos;
                f3 = render.aoLightValueXPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = l1;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (flag6 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (flag6 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (flag6 ? zMax : 1.0F) * 0.6F;
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
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        render.enableAO = false;
        return flag;
    }
	
	static boolean renderFakeBlockWithColorMultiplier(Icon texture, int metadata, int xMin, int yMin, int zMin, float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
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
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin + 1, zMin, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, texture);
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin, yMin, zMin + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin - 1, yMin, zMin, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xMin + 1, yMin, zMin, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, texture);

            flag = true;
        }

        return flag;
    }
}
