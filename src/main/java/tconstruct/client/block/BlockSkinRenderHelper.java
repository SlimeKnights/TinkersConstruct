package tconstruct.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
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

        return Minecraft.isAmbientOcclusionEnabled() && block.lightValue == 0 ? renderMetadataBlockWithAmbientOcclusion(block, metadata, x, y, z, var6, var7, var8, renderer, world)
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
        int l = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos);
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

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yPos;
            }

            render.field_147831_S = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147828_V = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147835_X = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos - 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos - 1)];

            if (!flag4 && !flag2)
            {
                render.field_147888_x = render.field_147886_y;
                render.field_147832_R = render.field_147831_S;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.field_147884_z = render.field_147886_y;
                render.field_147826_T = render.field_147831_S;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if (!flag4 && !flag3)
            {
                render.field_147816_C = render.field_147810_D;
                render.field_147827_W = render.field_147835_X;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag3)
            {
                render.field_147811_E = render.field_147810_D;
                render.field_147834_Y = render.field_147835_X;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yPos;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos - 1, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            f3 = (render.field_147884_z + render.field_147886_y + render.field_147815_B + f7) / 4.0F;
            f6 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147810_D) / 4.0F;
            f5 = (f7 + render.field_147814_A + render.field_147810_D + render.field_147816_C) / 4.0F;
            f4 = (render.field_147886_y + render.field_147888_x + f7 + render.field_147814_A) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147826_T, render.field_147831_S, render.field_147828_V, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147835_X, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147825_U, render.field_147835_X, render.field_147827_W, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147831_S, render.field_147832_R, render.field_147825_U, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.5F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.5F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.5F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.5F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.5F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.5F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yPos;
            }

            render.field_147880_aa = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147885_ae = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147878_ac = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147887_af = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos + 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.field_147813_G;
                render.field_147836_Z = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.field_147824_K;
                render.field_147879_ad = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.field_147813_G;
                render.field_147881_ab = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.field_147819_N = render.field_147824_K;
                render.field_147882_ag = render.field_147885_ae;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yPos;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos + 1, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.field_147813_G + render.field_147817_L + f7) / 4.0F;
            f3 = (render.field_147817_L + f7 + render.field_147819_N + render.field_147824_K) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.field_147824_K + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147813_G + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            render.field_147870_ao = render.getAoBrightness(render.field_147881_ab, render.field_147880_aa, render.field_147887_af, i1);
            render.field_147864_al = render.getAoBrightness(render.field_147887_af, render.field_147882_ag, render.field_147885_ae, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147878_ac, render.field_147885_ae, render.field_147879_ad, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147880_aa, render.field_147836_Z, render.field_147878_ac, i1);
            render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed;
            render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen;
            render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue;
            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(1, metadata));
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zPos;
            }

            render.field_147820_O = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147883_ah = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147878_ac = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147866_ai = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos - 1)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos - 1)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos - 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos - 1)];

            if (!flag2 && !flag4)
            {
                render.field_147888_x = render.field_147820_O;
                render.field_147832_R = render.field_147883_ah;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.field_147820_O;
                render.field_147836_Z = render.field_147883_ah;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.field_147866_ai;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.field_147879_ad = render.field_147866_ai;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zPos;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos - 1))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            f3 = (render.field_147820_O + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147814_A + f7 + render.field_147816_C + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.field_147888_x + render.field_147820_O + render.field_147814_A + f7) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147883_ah, render.field_147836_Z, render.field_147878_ac, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147878_ac, render.field_147866_ai, render.field_147879_ad, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147825_U, render.field_147827_W, render.field_147866_ai, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147832_R, render.field_147883_ah, render.field_147825_U, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.8F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.8F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            icon = block.getIcon(2, metadata);
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zPos;
            }

            render.field_147830_P = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147829_Q = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147868_aj = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147862_ak = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147828_V = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147887_af = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos + 1)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos + 1)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos + 1)];

            if (!flag2 && !flag4)
            {
                render.field_147884_z = render.field_147830_P;
                render.field_147826_T = render.field_147868_aj;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.field_147830_P;
                render.field_147881_ab = render.field_147868_aj;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.field_147811_E = render.field_147829_Q;
                render.field_147834_Y = render.field_147862_ak;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.field_147819_N = render.field_147829_Q;
                render.field_147882_ag = render.field_147862_ak;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zPos;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos + 1))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            f3 = (render.field_147830_P + render.aoLightValueScratchXYZNPP + f7 + render.field_147817_L) / 4.0F;
            f6 = (f7 + render.field_147817_L + render.field_147829_Q + render.field_147819_N) / 4.0F;
            f5 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147829_Q) / 4.0F;
            f4 = (render.field_147884_z + render.field_147830_P + render.field_147815_B + f7) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147868_aj, render.field_147881_ab, render.field_147887_af, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147887_af, render.field_147862_ak, render.field_147882_ag, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147862_ak, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147826_T, render.field_147868_aj, render.field_147828_V, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.8F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.8F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            icon = block.getIcon(3, metadata);
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xPos;
            }

            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147820_O = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147830_P = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147831_S = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147883_ah = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147868_aj = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147880_aa = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos - 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos + 1)];

            if (!flag5 && !flag2)
            {
                render.field_147888_x = render.field_147820_O;
                render.field_147832_R = render.field_147883_ah;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if (!flag4 && !flag2)
            {
                render.field_147884_z = render.field_147830_P;
                render.field_147826_T = render.field_147868_aj;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.field_147820_O;
                render.field_147836_Z = render.field_147883_ah;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.field_147830_P;
                render.field_147881_ab = render.field_147868_aj;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xPos;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos - 1, yPos, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            f6 = (render.field_147886_y + render.field_147884_z + f7 + render.field_147830_P) / 4.0F;
            f3 = (f7 + render.field_147830_P + render.field_147813_G + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.field_147820_O + f7 + render.aoLightValueScratchXYZNPN + render.field_147813_G) / 4.0F;
            f5 = (render.field_147888_x + render.field_147886_y + render.field_147820_O + f7) / 4.0F;
            render.field_147870_ao = render.getAoBrightness(render.field_147831_S, render.field_147826_T, render.field_147868_aj, i1);
            render.field_147864_al = render.getAoBrightness(render.field_147868_aj, render.field_147880_aa, render.field_147881_ab, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147883_ah, render.field_147836_Z, render.field_147880_aa, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147832_R, render.field_147831_S, render.field_147883_ah, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.6F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.6F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            icon = block.getIcon(4, metadata);
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, icon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xPos;
            }

            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147829_Q = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147835_X = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147866_ai = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147862_ak = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147885_ae = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos - 1)];

            if (!flag2 && !flag4)
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.field_147866_ai;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if (!flag2 && !flag5)
            {
                render.field_147811_E = render.field_147829_Q;
                render.field_147834_Y = render.field_147862_ak;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.field_147879_ad = render.field_147866_ai;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if (!flag3 && !flag5)
            {
                render.field_147819_N = render.field_147829_Q;
                render.field_147882_ag = render.field_147862_ak;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xPos;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos + 1, yPos, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            f3 = (render.field_147810_D + render.field_147811_E + f7 + render.field_147829_Q) / 4.0F;
            f4 = (render.field_147816_C + render.field_147810_D + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.field_147824_K) / 4.0F;
            f6 = (f7 + render.field_147829_Q + render.field_147824_K + render.field_147819_N) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147835_X, render.field_147834_Y, render.field_147862_ak, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147862_ak, render.field_147885_ae, render.field_147882_ag, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147866_ai, render.field_147879_ad, render.field_147885_ae, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147827_W, render.field_147835_X, render.field_147866_ai, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.6F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.6F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            icon = block.getIcon(5, metadata);
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, icon);

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

        int l = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(0, metadata));
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(1, metadata));
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(2, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(3, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(4, metadata));

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, block.getIcon(5, metadata));

            flag = true;
        }

        return flag;
    }

    public static boolean renderLiquidBlock (IIcon stillIcon, IIcon flowingIcon, int x, int y, int z, RenderBlocks renderer, IBlockAccess world)
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

        return Minecraft.isAmbientOcclusionEnabled() ? renderFakeBlockWithAmbientOcclusion(stillIcon, flowingIcon, x, y, z, var6, var7, var8, renderer, world) : renderFakeBlockWithColorMultiplier(
                stillIcon, flowingIcon, x, y, z, var6, var7, var8, renderer, world);
    }

    static boolean renderFakeBlockWithAmbientOcclusion (IIcon stillIcon, IIcon flowingIcon, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render,
            IBlockAccess world)
    {
        Block block = Blocks.stone;
        render.field_147863_w = true;
        boolean flag = false;
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 0.0F;
        boolean flag1 = true;
        int l = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos);
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

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0))
        {
            if (render.renderMinY <= 0.0D)
            {
                --yPos;
            }

            render.field_147831_S = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147828_V = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147835_X = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos - 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos - 1)];

            if (!flag4 && !flag2)
            {
                render.field_147888_x = render.field_147886_y;
                render.field_147832_R = render.field_147831_S;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.field_147884_z = render.field_147886_y;
                render.field_147826_T = render.field_147831_S;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if (!flag4 && !flag3)
            {
                render.field_147816_C = render.field_147810_D;
                render.field_147827_W = render.field_147835_X;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag3)
            {
                render.field_147811_E = render.field_147810_D;
                render.field_147834_Y = render.field_147835_X;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMinY <= 0.0D)
            {
                ++yPos;
            }

            i1 = l;

            if (render.renderMinY <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos - 1, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            f3 = (render.field_147884_z + render.field_147886_y + render.field_147815_B + f7) / 4.0F;
            f6 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147810_D) / 4.0F;
            f5 = (f7 + render.field_147814_A + render.field_147810_D + render.field_147816_C) / 4.0F;
            f4 = (render.field_147886_y + render.field_147888_x + f7 + render.field_147814_A) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147826_T, render.field_147831_S, render.field_147828_V, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147835_X, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147825_U, render.field_147835_X, render.field_147827_W, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147831_S, render.field_147832_R, render.field_147825_U, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.5F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.5F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.5F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.5F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.5F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.5F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1))
        {
            if (render.renderMaxY >= 1.0D)
            {
                ++yPos;
            }

            render.field_147880_aa = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147885_ae = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147878_ac = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147887_af = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos + 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos - 1)];

            if (!flag4 && !flag2)
            {
                render.aoLightValueScratchXYZNPN = render.field_147813_G;
                render.field_147836_Z = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos - 1);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZPPN = render.field_147824_K;
                render.field_147879_ad = render.field_147885_ae;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos - 1);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos - 1);
            }

            if (!flag5 && !flag2)
            {
                render.aoLightValueScratchXYZNPP = render.field_147813_G;
                render.field_147881_ab = render.field_147880_aa;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos + 1);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.field_147819_N = render.field_147824_K;
                render.field_147882_ag = render.field_147885_ae;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos + 1);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos + 1);
            }

            if (render.renderMaxY >= 1.0D)
            {
                --yPos;
            }

            i1 = l;

            if (render.renderMaxY >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos + 1, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            f6 = (render.aoLightValueScratchXYZNPP + render.field_147813_G + render.field_147817_L + f7) / 4.0F;
            f3 = (render.field_147817_L + f7 + render.field_147819_N + render.field_147824_K) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.field_147824_K + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147813_G + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            render.field_147870_ao = render.getAoBrightness(render.field_147881_ab, render.field_147880_aa, render.field_147887_af, i1);
            render.field_147864_al = render.getAoBrightness(render.field_147887_af, render.field_147882_ag, render.field_147885_ae, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147878_ac, render.field_147885_ae, render.field_147879_ad, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147880_aa, render.field_147836_Z, render.field_147878_ac, i1);
            render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed;
            render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen;
            render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue;
            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2))
        {
            if (render.renderMinZ <= 0.0D)
            {
                --zPos;
            }

            render.field_147820_O = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147814_A = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147822_I = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147883_ah = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147825_U = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147878_ac = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147866_ai = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos - 1)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos - 1)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos - 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos - 1)];

            if (!flag2 && !flag4)
            {
                render.field_147888_x = render.field_147820_O;
                render.field_147832_R = render.field_147883_ah;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPN = render.field_147820_O;
                render.field_147836_Z = render.field_147883_ah;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.field_147866_ai;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.field_147879_ad = render.field_147866_ai;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMinZ <= 0.0D)
            {
                ++zPos;
            }

            i1 = l;

            if (render.renderMinZ <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos - 1))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            f3 = (render.field_147820_O + render.aoLightValueScratchXYZNPN + f7 + render.field_147822_I) / 4.0F;
            f4 = (f7 + render.field_147822_I + render.aoLightValueScratchXZPN + render.aoLightValueScratchXYZPPN) / 4.0F;
            f5 = (render.field_147814_A + f7 + render.field_147816_C + render.aoLightValueScratchXZPN) / 4.0F;
            f6 = (render.field_147888_x + render.field_147820_O + render.field_147814_A + f7) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147883_ah, render.field_147836_Z, render.field_147878_ac, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147878_ac, render.field_147866_ai, render.field_147879_ad, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147825_U, render.field_147827_W, render.field_147866_ai, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147832_R, render.field_147883_ah, render.field_147825_U, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.8F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.8F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3))
        {
            if (render.renderMaxZ >= 1.0D)
            {
                ++zPos;
            }

            render.field_147830_P = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147829_Q = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147815_B = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147817_L = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147868_aj = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            render.field_147862_ak = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            render.field_147828_V = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147887_af = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos + 1)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos + 1)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos + 1, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos, yPos - 1, zPos + 1)];

            if (!flag2 && !flag4)
            {
                render.field_147884_z = render.field_147830_P;
                render.field_147826_T = render.field_147868_aj;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos - 1, zPos);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos - 1, yPos - 1, zPos);
            }

            if (!flag2 && !flag5)
            {
                render.aoLightValueScratchXYZNPP = render.field_147830_P;
                render.field_147881_ab = render.field_147868_aj;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos + 1, zPos);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos - 1, yPos + 1, zPos);
            }

            if (!flag3 && !flag4)
            {
                render.field_147811_E = render.field_147829_Q;
                render.field_147834_Y = render.field_147862_ak;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos - 1, zPos);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos + 1, yPos - 1, zPos);
            }

            if (!flag3 && !flag5)
            {
                render.field_147819_N = render.field_147829_Q;
                render.field_147882_ag = render.field_147862_ak;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos + 1, zPos);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos + 1, yPos + 1, zPos);
            }

            if (render.renderMaxZ >= 1.0D)
            {
                --zPos;
            }

            i1 = l;

            if (render.renderMaxZ >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos, yPos, zPos + 1))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            f3 = (render.field_147830_P + render.aoLightValueScratchXYZNPP + f7 + render.field_147817_L) / 4.0F;
            f6 = (f7 + render.field_147817_L + render.field_147829_Q + render.field_147819_N) / 4.0F;
            f5 = (render.field_147815_B + f7 + render.field_147811_E + render.field_147829_Q) / 4.0F;
            f4 = (render.field_147884_z + render.field_147830_P + render.field_147815_B + f7) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147868_aj, render.field_147881_ab, render.field_147887_af, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147887_af, render.field_147862_ak, render.field_147882_ag, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147828_V, render.field_147834_Y, render.field_147862_ak, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147826_T, render.field_147868_aj, render.field_147828_V, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.8F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.8F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.8F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.8F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4))
        {
            if (render.renderMinX <= 0.0D)
            {
                --xPos;
            }

            render.field_147886_y = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147820_O = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147830_P = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147813_G = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147831_S = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147883_ah = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147868_aj = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147880_aa = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos - 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos - 1, yPos, zPos + 1)];

            if (!flag5 && !flag2)
            {
                render.field_147888_x = render.field_147820_O;
                render.field_147832_R = render.field_147883_ah;
            }
            else
            {
                render.field_147888_x = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147832_R = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if (!flag4 && !flag2)
            {
                render.field_147884_z = render.field_147830_P;
                render.field_147826_T = render.field_147868_aj;
            }
            else
            {
                render.field_147884_z = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147826_T = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if (!flag5 && !flag3)
            {
                render.aoLightValueScratchXYZNPN = render.field_147820_O;
                render.field_147836_Z = render.field_147883_ah;
            }
            else
            {
                render.aoLightValueScratchXYZNPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.field_147836_Z = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if (!flag4 && !flag3)
            {
                render.aoLightValueScratchXYZNPP = render.field_147830_P;
                render.field_147881_ab = render.field_147868_aj;
            }
            else
            {
                render.aoLightValueScratchXYZNPP = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.field_147881_ab = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMinX <= 0.0D)
            {
                ++xPos;
            }

            i1 = l;

            if (render.renderMinX <= 0.0D || !render.field_147845_a.isBlockOpaqueCube(xPos - 1, yPos, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos - 1, yPos, zPos);
            f6 = (render.field_147886_y + render.field_147884_z + f7 + render.field_147830_P) / 4.0F;
            f3 = (f7 + render.field_147830_P + render.field_147813_G + render.aoLightValueScratchXYZNPP) / 4.0F;
            f4 = (render.field_147820_O + f7 + render.aoLightValueScratchXYZNPN + render.field_147813_G) / 4.0F;
            f5 = (render.field_147888_x + render.field_147886_y + render.field_147820_O + f7) / 4.0F;
            render.field_147870_ao = render.getAoBrightness(render.field_147831_S, render.field_147826_T, render.field_147868_aj, i1);
            render.field_147864_al = render.getAoBrightness(render.field_147868_aj, render.field_147880_aa, render.field_147881_ab, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147883_ah, render.field_147836_Z, render.field_147880_aa, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147832_R, render.field_147831_S, render.field_147883_ah, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.6F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.6F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5))
        {
            if (render.renderMaxX >= 1.0D)
            {
                ++xPos;
            }

            render.field_147810_D = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos);
            render.aoLightValueScratchXZPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147829_Q = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147824_K = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos);
            render.field_147835_X = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos);
            render.field_147866_ai = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1);
            render.field_147862_ak = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1);
            render.field_147885_ae = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos);
            flag3 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos + 1, zPos)];
            flag2 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos - 1, zPos)];
            flag5 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos + 1)];
            flag4 = Block.canBlockGrass[render.field_147845_a.getBlock(xPos + 1, yPos, zPos - 1)];

            if (!flag2 && !flag4)
            {
                render.field_147816_C = render.aoLightValueScratchXZPN;
                render.field_147827_W = render.field_147866_ai;
            }
            else
            {
                render.field_147816_C = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos - 1);
                render.field_147827_W = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos - 1);
            }

            if (!flag2 && !flag5)
            {
                render.field_147811_E = render.field_147829_Q;
                render.field_147834_Y = render.field_147862_ak;
            }
            else
            {
                render.field_147811_E = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos - 1, zPos + 1);
                render.field_147834_Y = block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos + 1);
            }

            if (!flag3 && !flag4)
            {
                render.aoLightValueScratchXYZPPN = render.aoLightValueScratchXZPN;
                render.field_147879_ad = render.field_147866_ai;
            }
            else
            {
                render.aoLightValueScratchXYZPPN = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos - 1);
                render.field_147879_ad = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos - 1);
            }

            if (!flag3 && !flag5)
            {
                render.field_147819_N = render.field_147829_Q;
                render.field_147882_ag = render.field_147862_ak;
            }
            else
            {
                render.field_147819_N = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos, yPos + 1, zPos + 1);
                render.field_147882_ag = block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos + 1);
            }

            if (render.renderMaxX >= 1.0D)
            {
                --xPos;
            }

            i1 = l;

            if (render.renderMaxX >= 1.0D || !render.field_147845_a.isBlockOpaqueCube(xPos + 1, yPos, zPos))
            {
                i1 = block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos);
            }

            f7 = block.getAmbientOcclusionLightValue(render.field_147845_a, xPos + 1, yPos, zPos);
            f3 = (render.field_147810_D + render.field_147811_E + f7 + render.field_147829_Q) / 4.0F;
            f4 = (render.field_147816_C + render.field_147810_D + render.aoLightValueScratchXZPN + f7) / 4.0F;
            f5 = (render.aoLightValueScratchXZPN + f7 + render.aoLightValueScratchXYZPPN + render.field_147824_K) / 4.0F;
            f6 = (f7 + render.field_147829_Q + render.field_147824_K + render.field_147819_N) / 4.0F;
            render.field_147864_al = render.getAoBrightness(render.field_147835_X, render.field_147834_Y, render.field_147862_ak, i1);
            render.field_147870_ao = render.getAoBrightness(render.field_147862_ak, render.field_147885_ae, render.field_147882_ag, i1);
            render.field_147876_an = render.getAoBrightness(render.field_147866_ai, render.field_147879_ad, render.field_147885_ae, i1);
            render.field_147874_am = render.getAoBrightness(render.field_147827_W, render.field_147835_X, render.field_147866_ai, i1);

            if (flag1)
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = colorRed * 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = colorGreen * 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = colorBlue * 0.6F;
            }
            else
            {
                render.field_147872_ap = render.field_147852_aq = render.field_147850_ar = render.field_147848_as = 0.6F;
                render.field_147846_at = render.field_147860_au = render.field_147858_av = render.field_147856_aw = 0.6F;
                render.field_147854_ax = render.field_147841_ay = render.field_147839_az = render.field_147833_aA = 0.6F;
            }

            render.field_147872_ap *= f3;
            render.field_147846_at *= f3;
            render.field_147854_ax *= f3;
            render.field_147852_aq *= f4;
            render.field_147860_au *= f4;
            render.field_147841_ay *= f4;
            render.field_147850_ar *= f5;
            render.field_147858_av *= f5;
            render.field_147839_az *= f5;
            render.field_147848_as *= f6;
            render.field_147856_aw *= f6;
            render.field_147833_aA *= f6;
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        render.field_147863_w = false;
        return flag;
    }

    static boolean renderFakeBlockWithColorMultiplier (IIcon stillIcon, IIcon flowingIcon, int xPos, int yPos, int zPos, float colorRed, float colorGreen, float colorBlue, RenderBlocks render,
            IBlockAccess world)
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

        int l = block.func_149677_c(render.field_147845_a, xPos, yPos, zPos);

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos - 1, zPos, 0))
        {
            tessellator.setBrightness(render.renderMinY > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos - 1, zPos));
            tessellator.setColorOpaque_F(f10, f13, f16);
            render.renderFaceYNeg(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos + 1, zPos, 1))
        {
            tessellator.setBrightness(render.renderMaxY < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos + 1, zPos));
            tessellator.setColorOpaque_F(f7, f8, f9);
            render.renderFaceYPos(block, (double) xPos, (double) yPos, (double) zPos, stillIcon);
            flag = true;
        }

        Icon icon;

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos - 1, 2))
        {
            tessellator.setBrightness(render.renderMinZ > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos, zPos - 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos, yPos, zPos + 1, 3))
        {
            tessellator.setBrightness(render.renderMaxZ < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos, yPos, zPos + 1));
            tessellator.setColorOpaque_F(f11, f14, f17);
            render.renderFaceZPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos - 1, yPos, zPos, 4))
        {
            tessellator.setBrightness(render.renderMinX > 0.0D ? l : block.func_149677_c(render.field_147845_a, xPos - 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXNeg(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        if (render.renderAllFaces || block.shouldSideBeRendered(render.field_147845_a, xPos + 1, yPos, zPos, 5))
        {
            tessellator.setBrightness(render.renderMaxX < 1.0D ? l : block.func_149677_c(render.field_147845_a, xPos + 1, yPos, zPos));
            tessellator.setColorOpaque_F(f12, f15, f18);
            render.renderFaceXPos(block, (double) xPos, (double) yPos, (double) zPos, flowingIcon);

            flag = true;
        }

        return flag;
    }
}
