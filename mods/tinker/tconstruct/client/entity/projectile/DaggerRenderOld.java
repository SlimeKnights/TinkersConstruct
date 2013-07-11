package mods.tinker.tconstruct.client.entity.projectile;

import java.util.Random;

import mods.tinker.tconstruct.entity.projectile.DaggerEntity;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class DaggerRenderOld extends RangedRenderBase
{
    public DaggerRenderOld(double d)
    {
        System.out.println("new renderer");
        shadowSize = 0.15F;
        shadowOpaque = 0.75F;
        daggerRotation = (float) d;
    }

    public void doRender (DaggerEntity dagger, double d, double d1, double d2, float f, float f1)
    {
        if ((new Random()).nextFloat() > 0.8)
            System.out.println("rendering dagger " + dagger.texID + " " + dagger.tex2ID);
        GL11.glPushMatrix();
        GL11.glTranslatef((float) d, (float) d1, (float) d2);
        GL11.glRotatef(f, 0.0F, 1.0F, 0.0F);
        float f2 = dagger.prevRotationPitch + (dagger.rotationPitch - dagger.prevRotationPitch) * f1;
        GL11.glRotatef(-f2, 1.0F, 0.0F, 0.0F);
        if (!dagger.onGround)
        {
            float f3 = dagger.prevBoomerangRotation + (dagger.boomerangRotation - dagger.prevBoomerangRotation) * f1 * 1.1F;
            GL11.glRotatef(f3, 1.0F, 0.0F, 0.0F);
        }
        GL11.glRotatef(45F, 0.0F, 1.0F, 0.0F);
        loadTexture("/infi2x2/daggers.png");
        Tessellator tessellator = Tessellator.instance;
        int i = 0;//dagger.getTexID();
        float f4 = ((float) ((i % 16) * 16) + 0.0F) / 256F;
        float f5 = ((float) ((i % 16) * 16) + 15.99F) / 256F;
        float f6 = ((float) ((i / 16) * 16) + 0.0F) / 256F;
        float f7 = ((float) ((i / 16) * 16) + 15.99F) / 256F;
        float f8 = 1.0F;
        GL11.glEnable(32826 /*GL_RESCALE_NORMAL_EXT*/);
        float f9 = 0.0625F;
        GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
        renderItem(tessellator, f4, f5, f6, f7, f8, f9);
        GL11.glPopMatrix();

    }

    @Override
    public void doRender (Entity entity, double d, double d1, double d2, float f, float f1)
    {
        doRender((DaggerEntity) entity, d, d1, d2, f, f1);
    }

    public float daggerRotation;
}
