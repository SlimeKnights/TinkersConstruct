package tconstruct.client.entity.projectile;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

public abstract class RangedRenderBase extends Render
{
    public RangedRenderBase()
    {
    }

    public void doRender (Entity entity, double d, double d1, double d2, float f, float f1)
    {
    }

    public void renderItem (Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5)
    {
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 0.0D - (double) f5, 0.0D, f1, f3);
        tessellator.addVertexWithUV(f4, 0.0D - (double) f5, 0.0D, f, f3);
        tessellator.addVertexWithUV(f4, 0.0D - (double) f5, 1.0D, f, f2);
        tessellator.addVertexWithUV(0.0D, 0.0D - (double) f5, 1.0D, f1, f2);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1F);
        tessellator.addVertexWithUV(0.0D, 0.0D, 1.0D, f1, f2);
        tessellator.addVertexWithUV(f4, 0.0D, 1.0D, f, f2);
        tessellator.addVertexWithUV(f4, 0.0D, 0.0D, f, f3);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, f1, f3);
        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(-1F, 0.0F, 0.0F);
        for (int i = 0; i < 16; i++)
        {
            float f6 = (float) i / 16F;
            float f10 = (f1 + (f - f1) * f6) - 0.001953125F;
            float f14 = f4 * f6 + 0.0625F;
            tessellator.addVertexWithUV(f14, 0.0F - f5, 0.0D, f10, f3);
            tessellator.addVertexWithUV(f14, 0.0D, 0.0D, f10, f3);
            tessellator.addVertexWithUV(f14, 0.0D, 1.0D, f10, f2);
            tessellator.addVertexWithUV(f14, 0.0F - f5, 1.0D, f10, f2);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        for (int j = 0; j < 16; j++)
        {
            float f7 = (float) j / 16F;
            float f11 = (f1 + (f - f1) * f7) - 0.001953125F;
            float f15 = f4 * f7;
            tessellator.addVertexWithUV(f15, 0.0F - f5, 1.0D, f11, f2);
            tessellator.addVertexWithUV(f15, 0.0D, 1.0D, f11, f2);
            tessellator.addVertexWithUV(f15, 0.0D, 0.0D, f11, f3);
            tessellator.addVertexWithUV(f15, 0.0F - f5, 0.0D, f11, f3);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        for (int k = 0; k < 16; k++)
        {
            float f8 = (float) k / 16F;
            float f12 = (f3 + (f2 - f3) * f8) - 0.001953125F;
            float f16 = f4 * f8;
            tessellator.addVertexWithUV(0.0D, 0.0D, f16, f1, f12);
            tessellator.addVertexWithUV(f4, 0.0D, f16, f, f12);
            tessellator.addVertexWithUV(f4, 0.0F - f5, f16, f, f12);
            tessellator.addVertexWithUV(0.0D, 0.0F - f5, f16, f1, f12);
        }

        tessellator.draw();
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, -1F, 0.0F);
        for (int l = 0; l < 16; l++)
        {
            float f9 = (float) l / 16F;
            float f13 = (f3 + (f2 - f3) * f9) - 0.001953125F;
            float f17 = f4 * f9 + 0.0625F;
            tessellator.addVertexWithUV(f4, 0.0D, f17, f, f13);
            tessellator.addVertexWithUV(0.0D, 0.0D, f17, f1, f13);
            tessellator.addVertexWithUV(0.0D, 0.0F - f5, f17, f1, f13);
            tessellator.addVertexWithUV(f4, 0.0F - f5, f17, f, f13);
        }

        tessellator.draw();
    }
}
