package tconstruct.world.model;

import cpw.mods.fml.client.registry.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import tconstruct.util.ItemHelper;

public class SlimeChannelRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            ItemHelper.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {

            renderRotatedBlock(block, x, y, z, world, renderer);
        }
        return true;
    }

    static final float LIGHT_Y_NEG = 0.5F;
    static final float LIGHT_Y_POS = 1.0F;
    static final float LIGHT_XZ_NEG = 0.8F;
    static final float LIGHT_XZ_POS = 0.6F;

    public boolean renderRotatedBlock (Block block, int x, int y, int z, IBlockAccess world, RenderBlocks renderer)
    {
        if (true)
        {
            Tessellator tessellator = Tessellator.instance;
            int bMeta = world.getBlockMetadata(x, y, z);
            IIcon iconStill = block.getIcon(1, bMeta);
            float flowDir = (float) (bMeta / 8f * 2 * Math.PI); // Tau, radians

            double u1, u2, u3, u4, v1, v2, v3, v4;

            if (flowDir < -999.0F)
            {
                u2 = iconStill.getInterpolatedU(0.0D);
                v2 = iconStill.getInterpolatedV(0.0D);
                u1 = u2;
                v1 = iconStill.getInterpolatedV(16.0D);
                u4 = iconStill.getInterpolatedU(16.0D);
                v4 = v1;
                u3 = u4;
                v3 = v2;
            }
            else
            {
                float xFlow = MathHelper.sin(flowDir) * 0.25F;
                float zFlow = MathHelper.cos(flowDir) * 0.25F;
                u2 = iconStill.getInterpolatedU(8.0F + (-zFlow - xFlow) * 16.0F);
                v2 = iconStill.getInterpolatedV(8.0F + (-zFlow + xFlow) * 16.0F);
                u1 = iconStill.getInterpolatedU(8.0F + (-zFlow + xFlow) * 16.0F);
                v1 = iconStill.getInterpolatedV(8.0F + (zFlow + xFlow) * 16.0F);
                u4 = iconStill.getInterpolatedU(8.0F + (zFlow + xFlow) * 16.0F);
                v4 = iconStill.getInterpolatedV(8.0F + (zFlow - xFlow) * 16.0F);
                u3 = iconStill.getInterpolatedU(8.0F + (zFlow - xFlow) * 16.0F);
                v3 = iconStill.getInterpolatedV(8.0F + (-zFlow - xFlow) * 16.0F);
            }
            int color = block.colorMultiplier(world, x, y, z);
            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;
            tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z));
            tessellator.setColorOpaque_F(LIGHT_Y_POS * red, LIGHT_Y_POS * green, LIGHT_Y_POS * blue);

            tessellator.addVertexWithUV(x + 0, y + 0.5, z + 0, u2, v2);
            tessellator.addVertexWithUV(x + 0, y + 0.5, z + 1, u1, v1);
            tessellator.addVertexWithUV(x + 1, y + 0.5, z + 1, u4, v4);
            tessellator.addVertexWithUV(x + 1, y + 0.5, z + 0, u3, v3);
        }
        renderer.renderStandardBlock(block, x, y, z);
        /*
         * int direction = world.getBlockMetadata(x, y, z) % 4; if (direction ==
         * 0) renderer.uvRotateTop = 2; if (direction == 1) renderer.uvRotateTop
         * = 1; if (direction == 2) renderer.uvRotateTop = 0; if (direction ==
         * 3) renderer.uvRotateTop = 3;
         * 
         * boolean flag = renderer.renderStandardBlock(block, x, y, z);
         * renderer.uvRotateTop = 0; return flag;
         */
        return true;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }
}
