package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

/**
 * @author BluSunrize
 */

public class RenderLiquid
{
    public static boolean renderMetadataBlock (Block block, int metadata, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
    {
        int var5 = block.colorMultiplier(world, x, y, z);
        float var6 = (var5 >> 16 & 0xFF) / 255.0F;
        float var7 = (var5 >> 8 & 0xFF) / 255.0F;
        float var8 = (var5 & 0xFF) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
            float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
            float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
            var6 = var9;
            var7 = var10;
            var8 = var11;
        }

        return (Minecraft.isAmbientOcclusionEnabled()) && (Block.lightValue[block.blockID] == 0) ? renderMetadataBlockWithAmbientOcclusion(block, metadata, x, y, z, var6, var7, var8, renderer, world)
                : renderMetadataBlockWithColorMultiplier(block, metadata, x, y, z, var6, var7, var8, renderer, world);
    }

    static boolean renderMetadataBlockWithAmbientOcclusion (Block block, int metadata, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render,
            IBlockAccess world)
    {
        render.field_147863_w = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos);
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

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0)))
        {
            if (render.renderMinY <= 0.0D)
            {
                yPos--;
            }

            render.field_147831_S = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147828_V = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147835_X = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos - 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos - 1)];

            if ((!flag4) && (!flag2))
            {
                render.field_147888_x = render.field_147886_y;
                render.field_147832_R = render.field_147831_S;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag2))
            {
                render.field_147884_z = render.field_147886_y;
                render.field_147826_T = render.field_147831_S;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.field_147816_C = render.field_147810_D;
                render.field_147827_W = render.field_147835_X;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.field_147811_E = render.field_147810_D;
                render.field_147834_Y = render.field_147835_X;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                yPos++;
            }

            int i1 = l;

            if ((render.renderMinY <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos - 1, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            f3 = (render.field_147884_z + render.field_147886_y + render.field_147815_B + f7) / 4.0F;
            f6 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147810_D) / 4.0F;
            f5 = (f7 + render.field_147814_A + render.field_147810_D + render.field_147816_C) / 4.0F;
            f4 = (render.field_147886_y + render.field_147888_x + f7 + render.field_147814_A) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.field_147826_T, render.field_147831_S, render.field_147828_V, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147835_X, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147825_U, render.field_147835_X, render.field_147827_W, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147831_S, render.field_147832_R, render.field_147825_U, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.5F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.5F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.5F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.5F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.5F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.5F);
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
            render.renderFaceYNeg(block, xPos, yPos, zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1)))
        {
            if (render.renderMaxY >= 1.0D)
            {
                yPos++;
            }

            render.field_147880_aa = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147885_ae = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147878_ac = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147887_af = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos + 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos - 1)];

            if ((!flag4) && (!flag2))
            {
                render.aoLightValueScratchXYZNPN = render.field_147813_G;
                render.aoBrightnessXYZNPN = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.aoLightValueScratchXYZPPN = render.field_147824_K;
                render.aoBrightnessXYZPPN = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag2))
            {
                render.aoLightValueScratchXYZNPP = render.field_147813_G;
                render.aoBrightnessXYZNPP = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.aoLightValueScratchXYZPPP = render.field_147824_K;
                render.aoBrightnessXYZPPP = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                yPos--;
            }

            int i1 = l;

            if ((render.renderMaxY >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos + 1, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.field_147813_G + render.field_147817_L + f7) / 4.0F;
            f3 = (render.field_147817_L + f7 + render.aoLightValueScratchXYZPPP + render.field_147824_K) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.field_147824_K + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147813_G + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.field_147880_aa, render.field_147887_af, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.field_147887_af, render.aoBrightnessXYZPPP, render.field_147885_ae, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147878_ac, render.field_147885_ae, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147880_aa, render.aoBrightnessXYZNPN, render.field_147878_ac, i1);
            render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed);
            render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen);
            render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue);
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
            render.renderFaceYPos(block, xPos, yPos, zPos, block.getIcon(1, metadata));
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2)))
        {
            if (render.renderMinZ <= 0.0D)
            {
                zPos--;
            }

            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147878_ac = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos - 1)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos - 1)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos - 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos - 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147888_x = render.aoLightValueScratchXZNN;
                render.field_147832_R = render.aoBrightnessXZNN;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if ((!flag2) && (!flag5))
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if ((!flag3) && (!flag4))
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.aoBrightnessXZPN;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                zPos++;
            }

            int i1 = l;

            if ((render.renderMinZ <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos - 1)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147814_A + f7 + render.field_147816_C + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.field_147888_x + render.aoLightValueScratchXZNN + render.field_147814_A + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.field_147878_ac, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147878_ac, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147825_U, render.field_147827_W, render.aoBrightnessXZPN, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147832_R, render.aoBrightnessXZNN, render.field_147825_U, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F);
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
            Icon icon = block.getIcon(2, metadata);
            render.renderFaceZNeg(block, xPos, yPos, zPos, icon);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3)))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                zPos++;
            }

            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147828_V = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147887_af = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos + 1)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos + 1)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos + 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147884_z = render.aoLightValueScratchXZNP;
                render.field_147826_T = render.aoBrightnessXZNP;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if ((!flag2) && (!flag5))
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if ((!flag3) && (!flag4))
            {
                render.field_147811_E = render.aoLightValueScratchXZPP;
                render.field_147834_Y = render.aoBrightnessXZPP;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                zPos--;
            }

            int i1 = l;

            if ((render.renderMaxZ >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos + 1)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + f7 + render.field_147817_L) / 4.0F;
            f6 = (f7 + render.field_147817_L + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (render.field_147815_B + f7 + render.field_147811_E + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.field_147884_z + render.aoLightValueScratchXZNP + render.field_147815_B + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.field_147887_af, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147887_af, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.aoBrightnessXZPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147826_T, render.aoBrightnessXZNP, render.field_147828_V, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F);
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
            Icon icon = block.getIcon(3, metadata);
            render.renderFaceZPos(block, xPos, yPos, zPos, icon);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4)))
        {
            if (render.renderMinX <= 0.0D)
            {
                xPos--;
            }

            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147831_S = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147880_aa = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos - 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos + 1)];

            if ((!flag5) && (!flag2))
            {
                render.field_147888_x = render.aoLightValueScratchXZNN;
                render.field_147832_R = render.aoBrightnessXZNN;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if ((!flag4) && (!flag2))
            {
                render.field_147884_z = render.aoLightValueScratchXZNP;
                render.field_147826_T = render.aoBrightnessXZNP;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                xPos++;
            }

            int i1 = l;

            if ((render.renderMinX <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos - 1, yPos, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            f6 = (render.field_147886_y + render.field_147884_z + f7 + render.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + render.aoLightValueScratchXZNP + render.field_147813_G + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.aoLightValueScratchXZNN + f7 + render.aoLightValueScratchXYZNPN + render.field_147813_G) / 4.0F;
            f5 = (render.field_147888_x + render.field_147886_y + render.aoLightValueScratchXZNN + f7) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.field_147831_S, render.field_147826_T, render.aoBrightnessXZNP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.field_147880_aa, render.aoBrightnessXYZNPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.field_147880_aa, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147832_R, render.field_147831_S, render.aoBrightnessXZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F);
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
            Icon icon = block.getIcon(4, metadata);
            render.renderFaceXNeg(block, xPos, yPos, zPos, icon);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5)))
        {
            if (render.renderMaxX >= 1.0D)
            {
                xPos++;
            }

            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147835_X = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147885_ae = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos - 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.aoBrightnessXZPN;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if ((!flag2) && (!flag5))
            {
                render.field_147811_E = render.aoLightValueScratchXZPP;
                render.field_147834_Y = render.aoBrightnessXZPP;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if ((!flag3) && (!flag4))
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                xPos--;
            }

            int i1 = l;

            if ((render.renderMaxX >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos + 1, yPos, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            f3 = (render.field_147810_D + render.field_147811_E + f7 + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.field_147816_C + render.field_147810_D + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.field_147824_K) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchXZPP + render.field_147824_K + render.aoLightValueScratchXYZPPP) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.field_147835_X, render.field_147834_Y, render.aoBrightnessXZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.field_147885_ae, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.field_147885_ae, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147827_W, render.field_147835_X, render.aoBrightnessXZPN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F);
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
            Icon icon = block.getIcon(5, metadata);
            render.renderFaceXPos(block, xPos, yPos, zPos, icon);

            flag = true;
        }

        render.field_147863_w = false;
        return flag;
    }

    static boolean renderMetadataBlockWithColorMultiplier (Block block, int metadata, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render,
            IBlockAccess world)
    {
        render.field_147863_w = false;
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

        int l = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos);

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0)))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, xPos, yPos, zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1)))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, xPos, yPos, zPos, block.getIcon(1, metadata));
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2)))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, xPos, yPos, zPos, block.getIcon(2, metadata));

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3)))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, xPos, yPos, zPos, block.getIcon(3, metadata));

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4)))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, xPos, yPos, zPos, block.getIcon(4, metadata));

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5)))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, xPos, yPos, zPos, block.getIcon(5, metadata));

            flag = true;
        }

        return flag;
    }

    public static boolean renderFakeBlock (IIcon texture, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
    {
        Block block = Blocks.stone;
        int var5 = block.colorMultiplier(world, x, y, z);
        float var6 = (var5 >> 16 & 0xFF) / 255.0F;
        float var7 = (var5 >> 8 & 0xFF) / 255.0F;
        float var8 = (var5 & 0xFF) / 255.0F;

        if (EntityRenderer.anaglyphEnable)
        {
            float var9 = (var6 * 30.0F + var7 * 59.0F + var8 * 11.0F) / 100.0F;
            float var10 = (var6 * 30.0F + var7 * 70.0F) / 100.0F;
            float var11 = (var6 * 30.0F + var8 * 70.0F) / 100.0F;
            var6 = var9;
            var7 = var10;
            var8 = var11;
        }

        return Minecraft.isAmbientOcclusionEnabled() ? renderFakeBlockWithAmbientOcclusion(texture, x, y, z, var6, var7, var8, renderer, world) : renderFakeBlockWithColorMultiplier(texture, x, y, z,
                var6, var7, var8, renderer, world);
    }

    static boolean renderFakeBlockWithAmbientOcclusion (IIcon texture, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        Block block = Blocks.stone;
        render.field_147863_w = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos);
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

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0)))
        {
            if (render.renderMinY <= 0.0D)
            {
                yPos--;
            }

            render.field_147831_S = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147828_V = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147835_X = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos - 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos - 1)];

            if ((!flag4) && (!flag2))
            {
                render.field_147888_x = render.field_147886_y;
                render.field_147832_R = render.field_147831_S;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag2))
            {
                render.field_147884_z = render.field_147886_y;
                render.field_147826_T = render.field_147831_S;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.field_147816_C = render.field_147810_D;
                render.field_147827_W = render.field_147835_X;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.field_147811_E = render.field_147810_D;
                render.field_147834_Y = render.field_147835_X;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                yPos++;
            }

            int i1 = l;

            if ((render.renderMinY <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos - 1, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            f3 = (render.field_147884_z + render.field_147886_y + render.field_147815_B + f7) / 4.0F;
            f6 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147810_D) / 4.0F;
            f5 = (f7 + render.field_147814_A + render.field_147810_D + render.field_147816_C) / 4.0F;
            f4 = (render.field_147886_y + render.field_147888_x + f7 + render.field_147814_A) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.field_147826_T, render.field_147831_S, render.field_147828_V, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147835_X, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147825_U, render.field_147835_X, render.field_147827_W, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147831_S, render.field_147832_R, render.field_147825_U, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.5F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.5F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.5F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.5F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.5F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.5F);
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
            render.renderFaceYNeg(block, xPos, yPos, zPos, texture);
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1)))
        {
            if (render.renderMaxY >= 1.0D)
            {
                yPos++;
            }

            render.field_147880_aa = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147885_ae = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147878_ac = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147887_af = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos + 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos - 1)];

            if ((!flag4) && (!flag2))
            {
                render.aoLightValueScratchXYZNPN = render.field_147813_G;
                render.aoBrightnessXYZNPN = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.aoLightValueScratchXYZPPN = render.field_147824_K;
                render.aoBrightnessXYZPPN = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if ((!flag5) && (!flag2))
            {
                render.aoLightValueScratchXYZNPP = render.field_147813_G;
                render.aoBrightnessXYZNPP = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.aoLightValueScratchXYZPPP = render.field_147824_K;
                render.aoBrightnessXYZPPP = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                yPos--;
            }

            int i1 = l;

            if ((render.renderMaxY >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos + 1, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.field_147813_G + render.field_147817_L + f7) / 4.0F;
            f3 = (render.field_147817_L + f7 + render.aoLightValueScratchXYZPPP + render.field_147824_K) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.field_147824_K + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147813_G + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXYZNPP, render.field_147880_aa, render.field_147887_af, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.field_147887_af, render.aoBrightnessXYZPPP, render.field_147885_ae, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147878_ac, render.field_147885_ae, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147880_aa, render.aoBrightnessXYZNPN, render.field_147878_ac, i1);
            render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed);
            render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen);
            render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue);
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
            render.renderFaceYPos(block, xPos, yPos, zPos, texture);
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2)))
        {
            if (render.renderMinZ <= 0.0D)
            {
                zPos--;
            }

            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147878_ac = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos - 1)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos - 1)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos - 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos - 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147888_x = render.aoLightValueScratchXZNN;
                render.field_147832_R = render.aoBrightnessXZNN;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if ((!flag2) && (!flag5))
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if ((!flag3) && (!flag4))
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.aoBrightnessXZPN;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                zPos++;
            }

            int i1 = l;

            if ((render.renderMinZ <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos - 1)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            f3 = (render.aoLightValueScratchXZNN + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147814_A + f7 + render.field_147816_C + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.field_147888_x + render.aoLightValueScratchXZNN + render.field_147814_A + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.field_147878_ac, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147878_ac, render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147825_U, render.field_147827_W, render.aoBrightnessXZPN, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147832_R, render.aoBrightnessXZNN, render.field_147825_U, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F);
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
            render.renderFaceZNeg(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3)))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                zPos++;
            }

            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147828_V = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147887_af = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos + 1)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos + 1)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos + 1, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos, yPos - 1, zPos + 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147884_z = render.aoLightValueScratchXZNP;
                render.field_147826_T = render.aoBrightnessXZNP;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if ((!flag2) && (!flag5))
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if ((!flag3) && (!flag4))
            {
                render.field_147811_E = render.aoLightValueScratchXZPP;
                render.field_147834_Y = render.aoBrightnessXZPP;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                zPos--;
            }

            int i1 = l;

            if ((render.renderMaxZ >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos + 1)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            f3 = (render.aoLightValueScratchXZNP + render.aoLightValueScratchXYZNPP + f7 + render.field_147817_L) / 4.0F;
            f6 = (f7 + render.field_147817_L + render.aoLightValueScratchXZPP + render.aoLightValueScratchXYZPPP) / 4.0F;
            f5 = (render.field_147815_B + f7 + render.field_147811_E + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.field_147884_z + render.aoLightValueScratchXZNP + render.field_147815_B + f7) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.aoBrightnessXYZNPP, render.field_147887_af, i1);
            render.brightnessTopRight = render.getAoBrightness(render.field_147887_af, render.aoBrightnessXZPP, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.aoBrightnessXZPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147826_T, render.aoBrightnessXZNP, render.field_147828_V, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.8F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.8F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.8F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.8F);
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
            render.renderFaceZPos(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4)))
        {
            if (render.renderMinX <= 0.0D)
            {
                xPos--;
            }

            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZNN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZNP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147831_S = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoBrightnessXZNN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoBrightnessXZNP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147880_aa = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos - 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos - 1, yPos, zPos + 1)];

            if ((!flag5) && (!flag2))
            {
                render.field_147888_x = render.aoLightValueScratchXZNN;
                render.field_147832_R = render.aoBrightnessXZNN;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147832_R = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if ((!flag4) && (!flag2))
            {
                render.field_147884_z = render.aoLightValueScratchXZNP;
                render.field_147826_T = render.aoBrightnessXZNP;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147826_T = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if ((!flag5) && (!flag3))
            {
                render.aoLightValueScratchXYZNPN = render.aoLightValueScratchXZNN;
                render.aoBrightnessXYZNPN = render.aoBrightnessXZNN;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if ((!flag4) && (!flag3))
            {
                render.aoLightValueScratchXYZNPP = render.aoLightValueScratchXZNP;
                render.aoBrightnessXYZNPP = render.aoBrightnessXZNP;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                xPos++;
            }

            int i1 = l;

            if ((render.renderMinX <= 0.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos - 1, yPos, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            f6 = (render.field_147886_y + render.field_147884_z + f7 + render.aoLightValueScratchXZNP) / 4.0F;
            f3 = (f7 + render.aoLightValueScratchXZNP + render.field_147813_G + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.aoLightValueScratchXZNN + f7 + render.aoLightValueScratchXYZNPN + render.field_147813_G) / 4.0F;
            f5 = (render.field_147888_x + render.field_147886_y + render.aoLightValueScratchXZNN + f7) / 4.0F;
            render.brightnessTopRight = render.getAoBrightness(render.field_147831_S, render.field_147826_T, render.aoBrightnessXZNP, i1);
            render.brightnessTopLeft = render.getAoBrightness(render.aoBrightnessXZNP, render.field_147880_aa, render.aoBrightnessXYZNPP, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.aoBrightnessXZNN, render.aoBrightnessXYZNPN, render.field_147880_aa, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.field_147832_R, render.field_147831_S, render.aoBrightnessXZNN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F);
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
            render.renderFaceXNeg(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5)))
        {
            if (render.renderMaxX >= 1.0D)
            {
                xPos++;
            }

            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoLightValueScratchXZPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147835_X = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoBrightnessXZPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1);
            render.aoBrightnessXZPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147885_ae = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos);
            boolean flag3 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos + 1, zPos)];
            boolean flag2 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos - 1, zPos)];
            boolean flag5 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos + 1)];
            boolean flag4 = Block.canBlockGrass[render.field_147845_a.getBlockId(xPos + 1, yPos, zPos - 1)];

            if ((!flag2) && (!flag4))
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.aoBrightnessXZPN;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147827_W = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if ((!flag2) && (!flag5))
            {
                render.field_147811_E = render.aoLightValueScratchXZPP;
                render.field_147834_Y = render.aoBrightnessXZPP;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147834_Y = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if ((!flag3) && (!flag4))
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.aoBrightnessXYZPPN = render.aoBrightnessXZPN;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if ((!flag3) && (!flag5))
            {
                render.aoLightValueScratchXYZPPP = render.aoLightValueScratchXZPP;
                render.aoBrightnessXYZPPP = render.aoBrightnessXZPP;
            }
            else
            {
                render.aoLightValueScratchXYZPPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                xPos--;
            }

            int i1 = l;

            if ((render.renderMaxX >= 1.0D) || (!render.field_147845_a.isBlockOpaqueCube(xPos + 1, yPos, zPos)))
            {
                i1 = block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos);
            }

            float f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            f3 = (render.field_147810_D + render.field_147811_E + f7 + render.aoLightValueScratchXZPP) / 4.0F;
            f4 = (render.field_147816_C + render.field_147810_D + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.field_147824_K) / 4.0F;
            f6 = (f7 + render.aoLightValueScratchXZPP + render.field_147824_K + render.aoLightValueScratchXYZPPP) / 4.0F;
            render.brightnessTopLeft = render.getAoBrightness(render.field_147835_X, render.field_147834_Y, render.aoBrightnessXZPP, i1);
            render.brightnessTopRight = render.getAoBrightness(render.aoBrightnessXZPP, render.field_147885_ae, render.aoBrightnessXYZPPP, i1);
            render.brightnessBottomRight = render.getAoBrightness(render.aoBrightnessXZPN, render.aoBrightnessXYZPPN, render.field_147885_ae, i1);
            render.brightnessBottomLeft = render.getAoBrightness(render.field_147827_W, render.field_147835_X, render.aoBrightnessXZPN, i1);

            if (flag1)
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = colorRed * 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = colorGreen * 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = colorBlue * 0.6F);
            }
            else
            {
                render.colorRedTopLeft = (render.colorRedBottomLeft = render.colorRedBottomRight = render.colorRedTopRight = 0.6F);
                render.colorGreenTopLeft = (render.colorGreenBottomLeft = render.colorGreenBottomRight = render.colorGreenTopRight = 0.6F);
                render.colorBlueTopLeft = (render.colorBlueBottomLeft = render.colorBlueBottomRight = render.colorBlueTopRight = 0.6F);
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
            render.renderFaceXPos(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        render.field_147863_w = false;
        return flag;
    }

    static boolean renderFakeBlockWithColorMultiplier (IIcon texture, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render, IBlockAccess world)
    {
        Block block = Blocks.stone;
        render.field_147863_w = false;
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

        int l = block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos);

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0)))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, xPos, yPos, zPos, texture);
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1)))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, xPos, yPos, zPos, texture);
            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2)))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3)))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4)))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        if ((render.renderAllFaces) || (block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5)))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.getMixedBrightnessForBlock(render.field_147845_a, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, xPos, yPos, zPos, texture);

            flag = true;
        }

        return flag;
    }
}
