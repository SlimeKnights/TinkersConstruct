package tinker.tconstruct.client;

import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelMinecart;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import tinker.tconstruct.entity.EntityWagon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWagon extends Render
{
    /** instance of ModelMinecart for rendering */
    protected ModelBase modelMinecart;

    public RenderWagon()
    {
        this.shadowSize = 0.5F;
        this.modelMinecart = new ModelMinecart();
    }

    /**
     * Renders the Minecart.
     */
    public void renderTheMinecart(EntityWagon wagon, double par2, double par4, double par6, float par8, float par9)
    {
        GL11.glPushMatrix();
        long var10 = (long)wagon.entityId * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(var12, var13, var14);
        double var15 = wagon.lastTickPosX + (wagon.posX - wagon.lastTickPosX) * (double)par9;
        double var17 = wagon.lastTickPosY + (wagon.posY - wagon.lastTickPosY) * (double)par9;
        double var19 = wagon.lastTickPosZ + (wagon.posZ - wagon.lastTickPosZ) * (double)par9;
        double var21 = 0.30000001192092896D;
        Vec3 var23 = wagon.func_70489_a(var15, var17, var19);
        float var24 = wagon.prevRotationPitch + (wagon.rotationPitch - wagon.prevRotationPitch) * par9;

        if (var23 != null)
        {
            Vec3 var25 = wagon.func_70495_a(var15, var17, var19, var21);
            Vec3 var26 = wagon.func_70495_a(var15, var17, var19, -var21);

            if (var25 == null)
            {
                var25 = var23;
            }

            if (var26 == null)
            {
                var26 = var23;
            }

            par2 += var23.xCoord - var15;
            par4 += (var25.yCoord + var26.yCoord) / 2.0D - var17;
            par6 += var23.zCoord - var19;
            Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);

            if (var27.lengthVector() != 0.0D)
            {
                var27 = var27.normalize();
                par8 = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / Math.PI);
                var24 = (float)(Math.atan(var27.yCoord) * 73.0D);
            }
        }

        GL11.glTranslatef((float)par2, (float)par4, (float)par6);
        GL11.glRotatef(180.0F - par8, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        float var28 = (float)wagon.func_70496_j() - par9;
        float var30 = (float)wagon.getDamage() - par9;

        if (var30 < 0.0F)
        {
            var30 = 0.0F;
        }

        if (var28 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(var28) * var28 * var30 / 10.0F * (float)wagon.func_70493_k(), 1.0F, 0.0F, 0.0F);
        }

        if (wagon.minecartType != 0)
        {
            this.loadTexture("/terrain.png");
            float var29 = 0.75F;
            GL11.glScalef(var29, var29, var29);

            if (wagon.minecartType == 1)
            {
                GL11.glTranslatef(0.0F, 0.5F, 0.0F);
                (new RenderBlocks()).renderBlockAsItem(Block.chest, 0, wagon.getBrightness(par9));
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.5F, 0.0F, -0.5F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }
            else if (wagon.minecartType == 2)
            {
                GL11.glTranslatef(0.0F, 0.3125F, 0.0F);
                (new RenderBlocks()).renderBlockAsItem(Block.stoneOvenIdle, 0, wagon.getBrightness(par9));
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, -0.3125F, 0.0F);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            }

            GL11.glScalef(1.0F / var29, 1.0F / var29, 1.0F / var29);
        }

        this.loadTexture("/tinkertextures/entity/wagon.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(wagon, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
    {
        this.renderTheMinecart((EntityWagon)par1Entity, par2, par4, par6, par8, par9);
    }
}
