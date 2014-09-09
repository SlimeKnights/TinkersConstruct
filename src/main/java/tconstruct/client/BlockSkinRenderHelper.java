package tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidRegistry;

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

        return Minecraft.isAmbientOcclusionEnabled() && block.getLightValue() == 0 ? renderMetadataBlockWithAmbientOcclusion(block, metadata, x, y, z, var6, var7, var8, renderer, world) : renderMetadataBlockWithColorMultiplier(block, metadata, x, y, z, var6, var7, var8, renderer, world);
    }

    static boolean renderMetadataBlockWithAmbientOcclusion (Block block, int metadata, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        render.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos);
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

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos - 1, zPos, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yPos;
            }

            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchXYNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchYZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchYZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos - 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos - 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos + 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yPos;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.blockAccess.getBlock(xPos, yPos - 1, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.5F;
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
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos + 1, zPos, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yPos;
            }

            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXYPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchYZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchYZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos + 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos + 1, zPos - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yPos;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.getBlock(xPos, yPos + 1, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (render.aoLightValueScratchYZPP + f7 + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue;
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
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(1, metadata));
            flag = true;
        }

        IIcon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zPos;
            }

            render.aoLightValueScratchXZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchYZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchYZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos, zPos - 1).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos, zPos - 1).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos - 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos - 1, zPos);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos - 1, zPos);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zPos;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.getBlock(xPos, yPos, zPos - 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F;
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
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zPos;
            }

            render.aoLightValueScratchXZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchYZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchYZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos, zPos + 1).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos, zPos + 1).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos + 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos - 1, zPos);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos - 1, zPos);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zPos;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.getBlock(xPos, yPos, zPos + 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F;
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
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos - 1, yPos, zPos, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xPos;
            }

            render.aoLightValueScratchXYNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos - 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos - 1, yPos, zPos - 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos - 1, yPos, zPos + 1).getCanBlockGrass();

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos - 1);
            }

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xPos;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.blockAccess.getBlock(xPos - 1, yPos, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F;
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
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos + 1, yPos, zPos, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xPos;
            }

            render.aoLightValueScratchXYPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos + 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos + 1, yPos, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos + 1, yPos, zPos - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos - 1);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos - 1);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xPos;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.getBlock(xPos + 1, yPos, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F;
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
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        render.enableAO = false;
        return flag;
    }

    static boolean renderMetadataBlockWithColorMultiplier (Block block, int metadata, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        render.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * colorRed;
        float f8 = f4 * colorGreen;
        float f9 = f4 * colorBlue;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Blocks.grass)
        {
            f10 = f3 * colorRed;
            f11 = f5 * colorRed;
            f12 = f6 * colorRed;
            f13 = f3 * colorGreen;
            f14 = f5 * colorGreen;
            f15 = f6 * colorGreen;
            f16 = f3 * colorBlue;
            f17 = f5 * colorBlue;
            f18 = f6 * colorBlue;
        }

        int l = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos - 1, zPos, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos + 1, zPos, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(1, metadata));
            flag = true;
        }

        IIcon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(2, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(3, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos - 1, yPos, zPos, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(4, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos + 1, yPos, zPos, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(5, metadata));

            flag = true;
        }

        return flag;
    }

    public static boolean renderLiquidBlock (IIcon stillIcon, IIcon flowingIcon, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
    {
        return renderLiquidBlock(stillIcon, flowingIcon, x, y, z, renderer, world, false);
    }

    public static boolean renderLiquidBlock (IIcon stillIcon, IIcon flowingIcon, int x, int y, int z, RenderBlocks renderer, IBlockAccess world, boolean extraBright)
    {
        Block block = Blocks.stone;
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

        if (extraBright)
        {
            var6 = Math.max(1.0f, var6 + 0.5f);
            var7 = Math.max(1.0f, var7 + 0.5f);
            var8 = Math.max(1.0f, var8 + 0.5f);
        }

        // safety
        if (stillIcon == null)
            stillIcon = FluidRegistry.WATER.getStillIcon();
        if (flowingIcon == null)
            flowingIcon = FluidRegistry.WATER.getFlowingIcon();

        boolean raf = renderer.renderAllFaces;
        renderer.renderAllFaces = true;

        boolean ret;
        if (Minecraft.isAmbientOcclusionEnabled())
            ret = renderFakeBlockWithAmbientOcclusion(stillIcon, flowingIcon, x, y, z, var6, var7, var8, renderer, world);
        else
            ret = renderFakeBlockWithColorMultiplier(stillIcon, flowingIcon, x, y, z, var6, var7, var8, renderer, world);

        renderer.renderAllFaces = raf;
        return ret;
    }

    static boolean renderFakeBlockWithAmbientOcclusion (IIcon stillIcon, IIcon flowingIcon, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        Block block = Blocks.stone;
        render.enableAO = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos);
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

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos - 1, zPos, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yPos;
            }

            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchXYNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchYZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchYZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos - 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos - 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXYNN;
                render.aoBrightnessXYZNNP = render.aoBrightnessXYNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos + 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXYPN;
                render.aoBrightnessXYZPNP = render.aoBrightnessXYPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yPos;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.blockAccess.getBlock(xPos, yPos - 1, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.5F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.5F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.5F;
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
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos + 1, zPos, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yPos;
            }

            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXYPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchYZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchYZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos + 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos + 1, zPos - 1).getCanBlockGrass();

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPN = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPN = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXYNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXYNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXYPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXYPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yPos;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.blockAccess.getBlock(xPos, yPos + 1, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.aoLightValueScratchXYNP + render.aoLightValueScratchYZPP + f7) / 4.0F;
            f3 = (render.aoLightValueScratchYZPP + f7 + render.aoLightValueScratchXYZPPP + render.aoLightValueScratchXYPP) / 4.0F;
            f4 = (f7 + render.aoLightValueScratchYZPN + render.aoLightValueScratchXYPP + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.aoLightValueScratchXYNP + render.aoLightValueScratchXYZNPN + f7 + render.aoLightValueScratchYZPN) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.aoBrightnessXYNP, render.aoBrightnessYZPP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessYZPP, render.aoBrightnessXYZPPP, render.aoBrightnessXYPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessYZPN, render.aoBrightnessXYPP, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXYNP, render.aoBrightnessXYZNPN, render.aoBrightnessYZPN, i1);
            render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed;
            render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen;
            render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue;
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
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zPos;
            }

            render.aoLightValueScratchXZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchYZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchYZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessYZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessYZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos, zPos - 1).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos, zPos - 1).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos - 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos - 1, zPos);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos - 1, zPos);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zPos;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.blockAccess.getBlock(xPos, yPos, zPos - 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F;
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
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zPos;
            }

            render.aoLightValueScratchXZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoLightValueScratchYZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchYZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            render.aoBrightnessYZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessYZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos, zPos + 1).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos, zPos + 1).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos, yPos + 1, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos, yPos - 1, zPos + 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos - 1, zPos);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos - 1, zPos);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zPos;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.blockAccess.getBlock(xPos, yPos, zPos + 1).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F;
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
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos - 1, yPos, zPos, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xPos;
            }

            render.aoLightValueScratchXYNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXYNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos - 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos - 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos - 1, yPos, zPos - 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos - 1, yPos, zPos + 1).getCanBlockGrass();

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNNN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNNN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos - 1);
                render.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos - 1);
            }

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNNP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNNP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos + 1);
                render.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xPos;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.blockAccess.getBlock(xPos - 1, yPos, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos - 1, yPos, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F;
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
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos + 1, yPos, zPos, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xPos;
            }

            render.aoLightValueScratchXYPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoLightValueScratchXYPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos);
            render.aoBrightnessXYPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1);
            render.aoBrightnessXYPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos);
            flag3 = render.blockAccess.getBlock(xPos + 1, yPos + 1, zPos).getCanBlockGrass();
            flag2 = render.blockAccess.getBlock(xPos + 1, yPos - 1, zPos).getCanBlockGrass();
            flag5 = render.blockAccess.getBlock(xPos + 1, yPos, zPos + 1).getCanBlockGrass();
            flag4 = render.blockAccess.getBlock(xPos + 1, yPos, zPos - 1).getCanBlockGrass();

            if (!flag2 && !flag4)
            {
                render.aoLightValueScratchXYZPNN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPNN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPNN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos - 1);
                render.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos - 1);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZPNP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPNP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPNP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos - 1, zPos + 1);
                render.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos - 1);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = getAmbientOcclusionLightValue(render.blockAccess, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xPos;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.blockAccess.getBlock(xPos + 1, yPos, zPos).isOpaqueCube())
            {
                i1 = block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos);
            }

            f7 = getAmbientOcclusionLightValue(render.blockAccess, xPos + 1, yPos, zPos);
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
                render.colorRedTopLeft = render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F;
                render.colorGreenTopLeft = render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F;
                render.colorBlueTopLeft = render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F;
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
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        render.enableAO = false;
        return flag;
    }

    static boolean renderFakeBlockWithColorMultiplier (IIcon stillIcon, IIcon flowingIcon, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        Block block = Blocks.stone;
        render.enableAO = false;
        Tessellator tessellator = Tessellator.instance;
        boolean flag = false;
        float f3 = 0.5F;
        float f4 = 1.0F;
        float f5 = 0.8F;
        float f6 = 0.6F;
        float f7 = f4 * colorRed;
        float f8 = f4 * colorGreen;
        float f9 = f4 * colorBlue;
        float f10 = f3;
        float f11 = f5;
        float f12 = f6;
        float f13 = f3;
        float f14 = f5;
        float f15 = f6;
        float f16 = f3;
        float f17 = f5;
        float f18 = f6;

        if (block != Blocks.grass)
        {
            f10 = f3 * colorRed;
            f11 = f5 * colorRed;
            f12 = f6 * colorRed;
            f13 = f3 * colorGreen;
            f14 = f5 * colorGreen;
            f15 = f6 * colorGreen;
            f16 = f3 * colorBlue;
            f17 = f5 * colorBlue;
            f18 = f6 * colorBlue;
        }

        int l = block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos - 1, zPos, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos + 1, zPos, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        IIcon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos, yPos, zPos + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos - 1, yPos, zPos, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.blockAccess, xPos + 1, yPos, zPos, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.blockAccess, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        return flag;
    }

    public static float getAmbientOcclusionLightValue (IBlockAccess access, int x, int y, int z)
    {
        return access.getBlock(x, y, z).getAmbientOcclusionLightValue();
    }
}