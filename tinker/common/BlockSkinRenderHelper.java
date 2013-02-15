package tinker.common;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
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
        boolean var8 = false;
        float var9 = render.lightValueOwn;
        float var10 = render.lightValueOwn;
        float var11 = render.lightValueOwn;
        float var12 = render.lightValueOwn;
        boolean var13 = true;
        boolean var14 = true;
        boolean var15 = true;
        boolean var16 = true;
        boolean var17 = true;
        boolean var18 = true;
        render.lightValueOwn = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin);
        render.aoLightValueXNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
        render.aoLightValueYNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
        render.aoLightValueZNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
        render.aoLightValueXPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
        render.aoLightValueYPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
        render.aoLightValueZPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
        int var19 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        int var20 = var19;
        int var21 = var19;
        int var22 = var19;
        int var23 = var19;
        int var24 = var19;
        int var25 = var19;

        if (render.renderMinY <= 0.0D)
        {
            var21 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
        }

        if (render.renderMaxY >= 1.0D)
        {
            var24 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
        }

        if (render.renderMinX <= 0.0D)
        {
            var20 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
        }

        if (render.renderMaxX >= 1.0D)
        {
            var23 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
        }

        if (render.renderMinZ <= 0.0D)
        {
            var22 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
        }

        if (render.renderMaxZ >= 1.0D)
        {
            var25 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
        }

        Tessellator var26 = Tessellator.instance;
        var26.setBrightness(983055);
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

        if (block.blockIndexInTexture == 3)
        {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (render.overrideBlockTexture >= 0)
        {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
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

                var9 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + render.aoLightValueYNeg) / 4.0F;
                var12 = (render.aoLightValueScratchYZNP + render.aoLightValueYNeg + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
                var11 = (render.aoLightValueYNeg + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
                var10 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + render.aoLightValueYNeg + render.aoLightValueScratchYZNN) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, var21);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, var21);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, var21);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, var21);
            }
            else
            {
                var12 = render.aoLightValueYNeg;
                var11 = render.aoLightValueYNeg;
                var10 = render.aoLightValueYNeg;
                var9 = render.aoLightValueYNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = render.aoBrightnessXYNN;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var13 ? xMax : 1.0F) * 0.5F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var13 ? yMax : 1.0F) * 0.5F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var13 ? zMax : 1.0F) * 0.5F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(0, metadata));
            var8 = true;
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

                var12 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + render.aoLightValueYPos) / 4.0F;
                var9 = (render.aoLightValueScratchYZPP + render.aoLightValueYPos + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
                var10 = (render.aoLightValueYPos + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
                var11 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + render.aoLightValueYPos + render.aoLightValueScratchYZPN) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, var24);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, var24);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, var24);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, var24);
            }
            else
            {
                var12 = render.aoLightValueYPos;
                var11 = render.aoLightValueYPos;
                var10 = render.aoLightValueYPos;
                var9 = render.aoLightValueYPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var24;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = var14 ? xMax : 1.0F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = var14 ? yMax : 1.0F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = var14 ? zMax : 1.0F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTextureFromSideAndMetadata(1, metadata));
            var8 = true;
        }

        int var27;

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

                var9 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                var10 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                var11 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                var12 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, var22);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, var22);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, var22);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, var22);
            }
            else
            {
                var12 = render.aoLightValueZNeg;
                var11 = render.aoLightValueZNeg;
                var10 = render.aoLightValueZNeg;
                var9 = render.aoLightValueZNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var22;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var15 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var15 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var15 ? zMax : 1.0F) * 0.8F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            var27 = block.getBlockTextureFromSideAndMetadata(2, metadata);
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, var27);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var27 == 3 && render.overrideBlockTexture < 0)
            {
                render.colorRedTopLeft *= xMax;
                render.colorRedBottomLeft *= xMax;
                render.colorRedBottomRight *= xMax;
                render.colorRedTopRight *= xMax;
                render.colorGreenTopLeft *= yMax;
                render.colorGreenBottomLeft *= yMax;
                render.colorGreenBottomRight *= yMax;
                render.colorGreenTopRight *= yMax;
                render.colorBlueTopLeft *= zMax;
                render.colorBlueBottomLeft *= zMax;
                render.colorBlueBottomRight *= zMax;
                render.colorBlueTopRight *= zMax;
                render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, 38);
            }

            var8 = true;
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

                var9 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                var12 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                var11 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                var10 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, var25);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, var25);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, var25);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, var25);
            }
            else
            {
                var12 = render.aoLightValueZPos;
                var11 = render.aoLightValueZPos;
                var10 = render.aoLightValueZPos;
                var9 = render.aoLightValueZPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var25;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var16 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var16 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var16 ? zMax : 1.0F) * 0.8F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            var27 = block.getBlockTextureFromSideAndMetadata(3, metadata);
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, block.getBlockTexture(render.blockAccess, xMin, yMin, zMin, 3));

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var27 == 3 && render.overrideBlockTexture < 0)
            {
                render.colorRedTopLeft *= xMax;
                render.colorRedBottomLeft *= xMax;
                render.colorRedBottomRight *= xMax;
                render.colorRedTopRight *= xMax;
                render.colorGreenTopLeft *= yMax;
                render.colorGreenBottomLeft *= yMax;
                render.colorGreenBottomRight *= yMax;
                render.colorGreenTopRight *= yMax;
                render.colorBlueTopLeft *= zMax;
                render.colorBlueBottomLeft *= zMax;
                render.colorBlueBottomRight *= zMax;
                render.colorBlueTopRight *= zMax;
                render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, 38);
            }

            var8 = true;
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

                var12 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                var9 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                var10 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                var11 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, var20);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, var20);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, var20);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, var20);
            }
            else
            {
                var12 = render.aoLightValueXNeg;
                var11 = render.aoLightValueXNeg;
                var10 = render.aoLightValueXNeg;
                var9 = render.aoLightValueXNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var20;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var17 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var17 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var17 ? zMax : 1.0F) * 0.6F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            var27 = block.getBlockTextureFromSideAndMetadata(4, metadata);
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, var27);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var27 == 3 && render.overrideBlockTexture < 0)
            {
                render.colorRedTopLeft *= xMax;
                render.colorRedBottomLeft *= xMax;
                render.colorRedBottomRight *= xMax;
                render.colorRedTopRight *= xMax;
                render.colorGreenTopLeft *= yMax;
                render.colorGreenBottomLeft *= yMax;
                render.colorGreenBottomRight *= yMax;
                render.colorGreenTopRight *= yMax;
                render.colorBlueTopLeft *= zMax;
                render.colorBlueBottomLeft *= zMax;
                render.colorBlueBottomRight *= zMax;
                render.colorBlueTopRight *= zMax;
                render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, 38);
            }

            var8 = true;
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

                var9 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                var12 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                var11 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                var10 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, var23);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, var23);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, var23);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, var23);
            }
            else
            {
                var12 = render.aoLightValueXPos;
                var11 = render.aoLightValueXPos;
                var10 = render.aoLightValueXPos;
                var9 = render.aoLightValueXPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var23;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var18 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var18 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var18 ? zMax : 1.0F) * 0.6F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            var27 = block.getBlockTextureFromSideAndMetadata(5, metadata);
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, var27);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var27 == 3 && render.overrideBlockTexture < 0)
            {
                render.colorRedTopLeft *= xMax;
                render.colorRedBottomLeft *= xMax;
                render.colorRedBottomRight *= xMax;
                render.colorRedTopRight *= xMax;
                render.colorGreenTopLeft *= yMax;
                render.colorGreenBottomLeft *= yMax;
                render.colorGreenBottomRight *= yMax;
                render.colorGreenTopRight *= yMax;
                render.colorBlueTopLeft *= zMax;
                render.colorBlueBottomLeft *= zMax;
                render.colorBlueBottomRight *= zMax;
                render.colorBlueTopRight *= zMax;
                render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, 38);
            }

            var8 = true;
        }

        render.enableAO = false;
        return var8;
    }
	
	static boolean renderMetadataBlockWithColorMultiplier(Block block, int metadata, int minX, int minY, int minZ, float maxX, float maxY, float maxZ, RenderBlocks render, IBlockAccess world)
    {
        render.enableAO = false;
        Tessellator var8 = Tessellator.instance;
        boolean var9 = false;
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var11 * maxX;
        float var15 = var11 * maxY;
        float var16 = var11 * maxZ;
        float var17 = var10;
        float var18 = var12;
        float var19 = var13;
        float var20 = var10;
        float var21 = var12;
        float var22 = var13;
        float var23 = var10;
        float var24 = var12;
        float var25 = var13;

        if (block != Block.grass)
        {
            var17 = var10 * maxX;
            var18 = var12 * maxX;
            var19 = var13 * maxX;
            var20 = var10 * maxY;
            var21 = var12 * maxY;
            var22 = var13 * maxY;
            var23 = var10 * maxZ;
            var24 = var12 * maxZ;
            var25 = var13 * maxZ;
        }

        int var26 = block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY - 1, minZ, 0))
        {
            var8.setBrightness(render.renderMinY > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY - 1, minZ));
            var8.setColorOpaque_F(var17, var20, var23);
            render.renderBottomFace(block, (double)minX, (double)minY, (double)minZ, block.getBlockTextureFromSideAndMetadata(0, metadata));
            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY + 1, minZ, 1))
        {
            var8.setBrightness(render.renderMaxY < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY + 1, minZ));
            var8.setColorOpaque_F(var14, var15, var16);
            render.renderTopFace(block, (double)minX, (double)minY, (double)minZ, block.getBlockTextureFromSideAndMetadata(1, metadata));
            var9 = true;
        }

        int var28;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY, minZ - 1, 2))
        {
            var8.setBrightness(render.renderMinZ > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ - 1));
            var8.setColorOpaque_F(var18, var21, var24);
            var28 = block.getBlockTextureFromSideAndMetadata(2, metadata);
            render.renderEastFace(block, (double)minX, (double)minY, (double)minZ, var28);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var28 == 3 && render.overrideBlockTexture < 0)
            {
                var8.setColorOpaque_F(var18 * maxX, var21 * maxY, var24 * maxZ);
                render.renderEastFace(block, (double)minX, (double)minY, (double)minZ, 38);
            }

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY, minZ + 1, 3))
        {
            var8.setBrightness(render.renderMaxZ < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ + 1));
            var8.setColorOpaque_F(var18, var21, var24);
            var28 = block.getBlockTextureFromSideAndMetadata(3, metadata);
            render.renderWestFace(block, (double)minX, (double)minY, (double)minZ, var28);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var28 == 3 && render.overrideBlockTexture < 0)
            {
                var8.setColorOpaque_F(var18 * maxX, var21 * maxY, var24 * maxZ);
                render.renderWestFace(block, (double)minX, (double)minY, (double)minZ, 38);
            }

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX - 1, minY, minZ, 4))
        {
            var8.setBrightness(render.renderMinX > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX - 1, minY, minZ));
            var8.setColorOpaque_F(var19, var22, var25);
            var28 = block.getBlockTextureFromSideAndMetadata(4, metadata);
            render.renderNorthFace(block, (double)minX, (double)minY, (double)minZ, var28);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var28 == 3 && render.overrideBlockTexture < 0)
            {
                var8.setColorOpaque_F(var19 * maxX, var22 * maxY, var25 * maxZ);
                render.renderNorthFace(block, (double)minX, (double)minY, (double)minZ, 38);
            }

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX + 1, minY, minZ, 5))
        {
            var8.setBrightness(render.renderMaxX < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX + 1, minY, minZ));
            var8.setColorOpaque_F(var19, var22, var25);
            var28 = block.getBlockTextureFromSideAndMetadata(5, metadata);
            render.renderSouthFace(block, (double)minX, (double)minY, (double)minZ, var28);

            if (Tessellator.instance.defaultTexture && render.fancyGrass && var28 == 3 && render.overrideBlockTexture < 0)
            {
                var8.setColorOpaque_F(var19 * maxX, var22 * maxY, var25 * maxZ);
                render.renderSouthFace(block, (double)minX, (double)minY, (double)minZ, 38);
            }

            var9 = true;
        }

        return var9;
    }
	
	public static boolean renderFakeBlock (int texturePos, int metadata, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
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
				renderFakeBlockWithAmbientOcclusion(texturePos, metadata, x, y, z, var6, var7, var8, renderer, world)
				: renderFakeBlockWithColorMultiplier(texturePos, metadata, x, y, z, var6, var7, var8, renderer, world);
	}
	
	static boolean renderFakeBlockWithAmbientOcclusion(int texturePos, int metadata, int xMin, int yMin, int zMin, 
			float xMax, float yMax, float zMax, RenderBlocks render, IBlockAccess world)
    {
		Block block = Block.stone;
        render.enableAO = true;
        boolean var8 = false;
        float var9 = render.lightValueOwn;
        float var10 = render.lightValueOwn;
        float var11 = render.lightValueOwn;
        float var12 = render.lightValueOwn;
        boolean var13 = true;
        boolean var14 = true;
        boolean var15 = true;
        boolean var16 = true;
        boolean var17 = true;
        boolean var18 = true;
        render.lightValueOwn = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin);
        render.aoLightValueXNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin - 1, yMin, zMin);
        render.aoLightValueYNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin - 1, zMin);
        render.aoLightValueZNeg = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin - 1);
        render.aoLightValueXPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin + 1, yMin, zMin);
        render.aoLightValueYPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin + 1, zMin);
        render.aoLightValueZPos = block.getAmbientOcclusionLightValue(render.blockAccess, xMin, yMin, zMin + 1);
        int var19 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin);
        int var20 = var19;
        int var21 = var19;
        int var22 = var19;
        int var23 = var19;
        int var24 = var19;
        int var25 = var19;

        if (render.renderMinY <= 0.0D)
        {
            var21 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin - 1, zMin);
        }

        if (render.renderMaxY >= 1.0D)
        {
            var24 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin + 1, zMin);
        }

        if (render.renderMinX <= 0.0D)
        {
            var20 = block.getMixedBrightnessForBlock(render.blockAccess, xMin - 1, yMin, zMin);
        }

        if (render.renderMaxX >= 1.0D)
        {
            var23 = block.getMixedBrightnessForBlock(render.blockAccess, xMin + 1, yMin, zMin);
        }

        if (render.renderMinZ <= 0.0D)
        {
            var22 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin - 1);
        }

        if (render.renderMaxZ >= 1.0D)
        {
            var25 = block.getMixedBrightnessForBlock(render.blockAccess, xMin, yMin, zMin + 1);
        }

        Tessellator var26 = Tessellator.instance;
        var26.setBrightness(983055);
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

        if (block.blockIndexInTexture == 3)
        {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
        }

        if (render.overrideBlockTexture >= 0)
        {
            var18 = false;
            var17 = false;
            var16 = false;
            var15 = false;
            var13 = false;
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

                var9 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXYNN + render.aoLightValueScratchYZNP + render.aoLightValueYNeg) / 4.0F;
                var12 = (render.aoLightValueScratchYZNP + render.aoLightValueYNeg + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXYPN) / 4.0F;
                var11 = (render.aoLightValueYNeg + render.aoLightValueScratchYZNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNN) / 4.0F;
                var10 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNN + render.aoLightValueYNeg + render.aoLightValueScratchYZNN) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXYNN, render.aoBrightnessYZNP, var21);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXYPN, var21);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYPN, render.aoBrightnessXYZPNN, var21);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNN, render.aoBrightnessYZNN, var21);
            }
            else
            {
                var12 = render.aoLightValueYNeg;
                var11 = render.aoLightValueYNeg;
                var10 = render.aoLightValueYNeg;
                var9 = render.aoLightValueYNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = render.aoBrightnessXYNN;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var13 ? xMax : 1.0F) * 0.5F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var13 ? yMax : 1.0F) * 0.5F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var13 ? zMax : 1.0F) * 0.5F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderBottomFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);
            var8 = true;
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

                var12 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + render.aoLightValueYPos) / 4.0F;
                var9 = (render.aoLightValueScratchYZPP + render.aoLightValueYPos + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
                var10 = (render.aoLightValueYPos + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
                var11 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + render.aoLightValueYPos + render.aoLightValueScratchYZPN) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, var24);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, var24);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, var24);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, var24);
            }
            else
            {
                var12 = render.aoLightValueYPos;
                var11 = render.aoLightValueYPos;
                var10 = render.aoLightValueYPos;
                var9 = render.aoLightValueYPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var24;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = var14 ? xMax : 1.0F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = var14 ? yMax : 1.0F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = var14 ? zMax : 1.0F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderTopFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);
            var8 = true;
        }

        int var27;

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

                var9 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + render.aoLightValueZNeg + render.aoLightValueScratchYZPN) / 4.0F;
                var10 = (render.aoLightValueZNeg + render.aoLightValueScratchYZPN + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
                var11 = (render.aoLightValueScratchYZNN + render.aoLightValueZNeg + render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXZPN) / 4.0F;
                var12 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXZNN + render.aoLightValueScratchYZNN + render.aoLightValueZNeg) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, var22);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, var22);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNN, render.aoBrightnessXYZPNN, render.aoBrightnessXZPN, var22);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXZNN, render.aoBrightnessYZNN, var22);
            }
            else
            {
                var12 = render.aoLightValueZNeg;
                var11 = render.aoLightValueZNeg;
                var10 = render.aoLightValueZNeg;
                var9 = render.aoLightValueZNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var22;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var15 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var15 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var15 ? zMax : 1.0F) * 0.8F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderEastFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);
            var8 = true;
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

                var9 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + render.aoLightValueZPos + render.aoLightValueScratchYZPP) / 4.0F;
                var12 = (render.aoLightValueZPos + render.aoLightValueScratchYZPP + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                var11 = (render.aoLightValueScratchYZNP + render.aoLightValueZPos + render.aoLightValueScratchXYZPNP + render.aoLightValueScratchXZPP) / 4.0F;
                var10 = (render.aoLightValueScratchXYZNNP + render.aoLightValueScratchXZNP + render.aoLightValueScratchYZNP + render.aoLightValueZPos) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.aoBrightnessYZPP, var25);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, var25);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessYZNP, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, var25);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, render.aoBrightnessYZNP, var25);
            }
            else
            {
                var12 = render.aoLightValueZPos;
                var11 = render.aoLightValueZPos;
                var10 = render.aoLightValueZPos;
                var9 = render.aoLightValueZPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var25;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var16 ? xMax : 1.0F) * 0.8F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var16 ? yMax : 1.0F) * 0.8F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var16 ? zMax : 1.0F) * 0.8F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderWestFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);

            var8 = true;
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

                var12 = (render.aoLightValueScratchXYNN + render.aoLightValueScratchXYZNNP + render.aoLightValueXNeg + render.aoLightValueScratchXZNP) / 4.0F;
                var9 = (render.aoLightValueXNeg + render.aoLightValueScratchXZNP + render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPP) / 4.0F;
                var10 = (render.aoLightValueScratchXZNN + render.aoLightValueXNeg + render.aoLightValueScratchXYZNPN + render.aoLightValueScratchXYNP) / 4.0F;
                var11 = (render.aoLightValueScratchXYZNNN + render.aoLightValueScratchXYNN + render.aoLightValueScratchXZNN + render.aoLightValueXNeg) / 4.0F;
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYNN, render.aoBrightnessXYZNNP, render.aoBrightnessXZNP, var20);
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYNP, render.aoBrightnessXYZNPP, var20);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.aoBrightnessXYNP, var20);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYZNNN, render.aoBrightnessXYNN, render.aoBrightnessXZNN, var20);
            }
            else
            {
                var12 = render.aoLightValueXNeg;
                var11 = render.aoLightValueXNeg;
                var10 = render.aoLightValueXNeg;
                var9 = render.aoLightValueXNeg;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var20;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var17 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var17 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var17 ? zMax : 1.0F) * 0.6F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderNorthFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);

            var8 = true;
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

                var9 = (render.aoLightValueScratchXYPN + render.aoLightValueScratchXYZPNP + render.aoLightValueXPos + render.aoLightValueScratchXZPP) / 4.0F;
                var12 = (render.aoLightValueXPos + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPP) / 4.0F;
                var11 = (render.aoLightValueScratchXZPN + render.aoLightValueXPos + render.aoLightValueScratchXYZPPN + render.aoLightValueScratchXYPP) / 4.0F;
                var10 = (render.aoLightValueScratchXYZPNN + render.aoLightValueScratchXYPN + render.aoLightValueScratchXZPN + render.aoLightValueXPos) / 4.0F;
                render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXYPN, render.aoBrightnessXYZPNP, render.aoBrightnessXZPP, var23);
                render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.aoBrightnessXYPP, render.aoBrightnessXYZPPP, var23);
                render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.aoBrightnessXYPP, var23);
                render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXYZPNN, render.aoBrightnessXYPN, render.aoBrightnessXZPN, var23);
            }
            else
            {
                var12 = render.aoLightValueXPos;
                var11 = render.aoLightValueXPos;
                var10 = render.aoLightValueXPos;
                var9 = render.aoLightValueXPos;
                render.brightnessTopLeft = render.brightnessBottomLeft = render.brightnessBottomRight = render.brightnessTopRight = var23;
            }

            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = (var18 ? xMax : 1.0F) * 0.6F;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = (var18 ? yMax : 1.0F) * 0.6F;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = (var18 ? zMax : 1.0F) * 0.6F;
            render.colorRedTopLeft *= var9;
            render.colorGreenTopLeft *= var9;
            render.colorBlueTopLeft *= var9;
            render.colorRedBottomLeft *= var10;
            render.colorGreenBottomLeft *= var10;
            render.colorBlueBottomLeft *= var10;
            render.colorRedBottomRight *= var11;
            render.colorGreenBottomRight *= var11;
            render.colorBlueBottomRight *= var11;
            render.colorRedTopRight *= var12;
            render.colorGreenTopRight *= var12;
            render.colorBlueTopRight *= var12;
            render.renderSouthFace(block, (double)xMin, (double)yMin, (double)zMin, texturePos);

            var8 = true;
        }

        render.enableAO = false;
        return var8;
    }
	
	static boolean renderFakeBlockWithColorMultiplier(int texturePos, int metadata, int minX, int minY, int minZ, float maxX, float maxY, float maxZ, RenderBlocks render, IBlockAccess world)
    {
		Block block = Block.stone;
        render.enableAO = false;
        Tessellator var8 = Tessellator.instance;
        boolean var9 = false;
        float var10 = 0.5F;
        float var11 = 1.0F;
        float var12 = 0.8F;
        float var13 = 0.6F;
        float var14 = var11 * maxX;
        float var15 = var11 * maxY;
        float var16 = var11 * maxZ;
        float var17 = var10;
        float var18 = var12;
        float var19 = var13;
        float var20 = var10;
        float var21 = var12;
        float var22 = var13;
        float var23 = var10;
        float var24 = var12;
        float var25 = var13;

        if (block != Block.grass)
        {
            var17 = var10 * maxX;
            var18 = var12 * maxX;
            var19 = var13 * maxX;
            var20 = var10 * maxY;
            var21 = var12 * maxY;
            var22 = var13 * maxY;
            var23 = var10 * maxZ;
            var24 = var12 * maxZ;
            var25 = var13 * maxZ;
        }

        int var26 = block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY - 1, minZ, 0))
        {
            var8.setBrightness(render.renderMinY > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY - 1, minZ));
            var8.setColorOpaque_F(var17, var20, var23);
            render.renderBottomFace(block, (double)minX, (double)minY, (double)minZ, texturePos);
            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY + 1, minZ, 1))
        {
            var8.setBrightness(render.renderMaxY < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY + 1, minZ));
            var8.setColorOpaque_F(var14, var15, var16);
            render.renderTopFace(block, (double)minX, (double)minY, (double)minZ, texturePos);
            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY, minZ - 1, 2))
        {
            var8.setBrightness(render.renderMinZ > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ - 1));
            var8.setColorOpaque_F(var18, var21, var24);
            render.renderEastFace(block, (double)minX, (double)minY, (double)minZ, texturePos);

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX, minY, minZ + 1, 3))
        {
            var8.setBrightness(render.renderMaxZ < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX, minY, minZ + 1));
            var8.setColorOpaque_F(var18, var21, var24);
            render.renderWestFace(block, (double)minX, (double)minY, (double)minZ, texturePos);

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX - 1, minY, minZ, 4))
        {
            var8.setBrightness(render.renderMinX > 0.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX - 1, minY, minZ));
            var8.setColorOpaque_F(var19, var22, var25);
            render.renderNorthFace(block, (double)minX, (double)minY, (double)minZ, texturePos);

            var9 = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, minX + 1, minY, minZ, 5))
        {
            var8.setBrightness(render.renderMaxX < 1.0D ? var26 : block.getMixedBrightnessForBlock(render.blockAccess, minX + 1, minY, minZ));
            var8.setColorOpaque_F(var19, var22, var25);
            render.renderSouthFace(block, (double)minX, (double)minY, (double)minZ, texturePos);

            var9 = true;
        }

        return var9;
    }
}
