package common.darkknight.jewelrycraft.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelDisplayer extends ModelBase
{
    //fields
    ModelRenderer Base;
    ModelRenderer Ring11;
    ModelRenderer Ring12;
    ModelRenderer Ring13;
    ModelRenderer Ring14;
    ModelRenderer Ring31;
    ModelRenderer Ring21;
    ModelRenderer Ring32;
    ModelRenderer Ring22;
    ModelRenderer Ring33;
    ModelRenderer Ring23;
    ModelRenderer Ring34;
    ModelRenderer Ring24;
    ModelRenderer Ring25;
    ModelRenderer Ring26;
    ModelRenderer Ring27;
    ModelRenderer Ring28;
    ModelRenderer Ring35;
    ModelRenderer Ring36;
    ModelRenderer Ring37;
    ModelRenderer Ring38;
    ModelRenderer Ring39;
    ModelRenderer Ring310;
    ModelRenderer Ring311;
    ModelRenderer Ring312;

    public ModelDisplayer()
    {
        textureWidth = 64;
        textureHeight = 32;

        Base = new ModelRenderer(this, 0, 0);
        Base.addBox(0F, 0F, 0F, 16, 3, 16);
        Base.setRotationPoint(-8F, 21F, -8F);
        Base.setTextureSize(64, 32);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        Ring11 = new ModelRenderer(this, 0, 26);
        Ring11.addBox(-2F, 0F, 2F, 4, 1, 1);
        Ring11.setRotationPoint(0F, 19F, 0F);
        Ring11.setTextureSize(64, 32);
        Ring11.mirror = true;
        setRotation(Ring11, 0F, 0F, 0F);
        Ring12 = new ModelRenderer(this, 0, 20);
        Ring12.addBox(-3F, 0F, -2F, 1, 1, 4);
        Ring12.setRotationPoint(0F, 19F, 0F);
        Ring12.setTextureSize(64, 32);
        Ring12.mirror = true;
        setRotation(Ring12, 0F, 0F, 0F);
        Ring13 = new ModelRenderer(this, 0, 20);
        Ring13.addBox(2F, 0F, -2F, 1, 1, 4);
        Ring13.setRotationPoint(0F, 19F, 0F);
        Ring13.setTextureSize(64, 32);
        Ring13.mirror = true;
        setRotation(Ring13, 0F, 0F, 0F);
        Ring14 = new ModelRenderer(this, 0, 26);
        Ring14.addBox(-2F, 0F, -3F, 4, 1, 1);
        Ring14.setRotationPoint(0F, 19F, 0F);
        Ring14.setTextureSize(64, 32);
        Ring14.mirror = true;
        setRotation(Ring14, 0F, 0F, 0F);
        Ring21 = new ModelRenderer(this, 0, 29);
        Ring21.addBox(-4F, 0F, 3F, 1, 1, 1);
        Ring21.setRotationPoint(0F, 19F, 0F);
        Ring21.setTextureSize(64, 32);
        Ring21.mirror = true;
        setRotation(Ring21, 0F, 0F, 0F);
        Ring22 = new ModelRenderer(this, 0, 29);
        Ring22.addBox(-4F, 0F, -4F, 1, 1, 1);
        Ring22.setRotationPoint(0F, 19F, 0F);
        Ring22.setTextureSize(64, 32);
        Ring22.mirror = true;
        setRotation(Ring22, 0F, 0F, 0F);
        Ring23 = new ModelRenderer(this, 0, 29);
        Ring23.addBox(3F, 0F, -4F, 1, 1, 1);
        Ring23.setRotationPoint(0F, 19F, 0F);
        Ring23.setTextureSize(64, 32);
        Ring23.mirror = true;
        setRotation(Ring23, 0F, 0F, 0F);
        Ring34 = new ModelRenderer(this, 26, 20);
        Ring34.addBox(6F, 0F, -4F, 1, 1, 8);
        Ring34.setRotationPoint(0F, 19F, 0F);
        Ring34.setTextureSize(64, 32);
        Ring34.mirror = true;
        setRotation(Ring34, 0F, 0F, 0F);
        Ring24 = new ModelRenderer(this, 0, 29);
        Ring24.addBox(3F, 0F, 3F, 1, 1, 1);
        Ring24.setRotationPoint(0F, 19F, 0F);
        Ring24.setTextureSize(64, 32);
        Ring24.mirror = true;
        setRotation(Ring24, 0F, 0F, 0F);
        Ring25 = new ModelRenderer(this, 11, 20);
        Ring25.addBox(4F, 0F, -3F, 1, 1, 6);
        Ring25.setRotationPoint(0F, 19F, 0F);
        Ring25.setTextureSize(64, 32);
        Ring25.mirror = true;
        setRotation(Ring25, 0F, 0F, 0F);
        Ring26 = new ModelRenderer(this, 11, 28);
        Ring26.addBox(-3F, 0F, -5F, 6, 1, 1);
        Ring26.setRotationPoint(0F, 19F, 0F);
        Ring26.setTextureSize(64, 32);
        Ring26.mirror = true;
        setRotation(Ring26, 0F, 0F, 0F);
        Ring27 = new ModelRenderer(this, 11, 20);
        Ring27.addBox(-5F, 0F, -3F, 1, 1, 6);
        Ring27.setRotationPoint(0F, 19F, 0F);
        Ring27.setTextureSize(64, 32);
        Ring27.mirror = true;
        setRotation(Ring27, 0F, 0F, 0F);
        Ring28 = new ModelRenderer(this, 11, 28);
        Ring28.addBox(-3F, 0F, 4F, 6, 1, 1);
        Ring28.setRotationPoint(0F, 19F, 0F);
        Ring28.setTextureSize(64, 32);
        Ring28.mirror = true;
        setRotation(Ring28, 0F, 0F, 0F);
        Ring31 = new ModelRenderer(this, 0, 29);
        Ring31.addBox(-6F, 0F, 4F, 1, 1, 1);
        Ring31.setRotationPoint(0F, 19F, 0F);
        Ring31.setTextureSize(64, 32);
        Ring31.mirror = true;
        setRotation(Ring31, 0F, 0F, 0F);
        Ring32 = new ModelRenderer(this, 26, 20);
        Ring32.addBox(-7F, 0F, -4F, 1, 1, 8);
        Ring32.setRotationPoint(0F, 19F, 0F);
        Ring32.setTextureSize(64, 32);
        Ring32.mirror = true;
        setRotation(Ring32, 0F, 0F, 0F);
        Ring33 = new ModelRenderer(this, 26, 30);
        Ring33.addBox(-4F, 0F, -7F, 8, 1, 1);
        Ring33.setRotationPoint(0F, 19F, 0F);
        Ring33.setTextureSize(64, 32);
        Ring33.mirror = true;
        setRotation(Ring33, 0F, 0F, 0F);
        Ring35 = new ModelRenderer(this, 26, 30);
        Ring35.addBox(-4F, 0F, 6F, 8, 1, 1);
        Ring35.setRotationPoint(0F, 19F, 0F);
        Ring35.setTextureSize(64, 32);
        Ring35.mirror = true;
        setRotation(Ring35, 0F, 0F, 0F);
        Ring36 = new ModelRenderer(this, 0, 29);
        Ring36.addBox(-5F, 0F, 5F, 1, 1, 1);
        Ring36.setRotationPoint(0F, 19F, 0F);
        Ring36.setTextureSize(64, 32);
        Ring36.mirror = true;
        setRotation(Ring36, 0F, 0F, 0F);
        Ring37 = new ModelRenderer(this, 0, 29);
        Ring37.addBox(5F, 0F, 4F, 1, 1, 1);
        Ring37.setRotationPoint(0F, 19F, 0F);
        Ring37.setTextureSize(64, 32);
        Ring37.mirror = true;
        setRotation(Ring37, 0F, 0F, 0F);
        Ring38 = new ModelRenderer(this, 0, 29);
        Ring38.addBox(4F, 0F, 5F, 1, 1, 1);
        Ring38.setRotationPoint(0F, 19F, 0F);
        Ring38.setTextureSize(64, 32);
        Ring38.mirror = true;
        setRotation(Ring38, 0F, 0F, 0F);
        Ring39 = new ModelRenderer(this, 0, 29);
        Ring39.addBox(4F, 0F, -6F, 1, 1, 1);
        Ring39.setRotationPoint(0F, 19F, 0F);
        Ring39.setTextureSize(64, 32);
        Ring39.mirror = true;
        setRotation(Ring39, 0F, 0F, 0F);
        Ring310 = new ModelRenderer(this, 0, 29);
        Ring310.addBox(5F, 0F, -5F, 1, 1, 1);
        Ring310.setRotationPoint(0F, 19F, 0F);
        Ring310.setTextureSize(64, 32);
        Ring310.mirror = true;
        setRotation(Ring310, 0F, 0F, 0F);
        Ring311 = new ModelRenderer(this, 0, 29);
        Ring311.addBox(-6F, 0F, -5F, 1, 1, 1);
        Ring311.setRotationPoint(0F, 19F, 0F);
        Ring311.setTextureSize(64, 32);
        Ring311.mirror = true;
        setRotation(Ring311, 0F, 0F, 0F);
        Ring312 = new ModelRenderer(this, 0, 29);
        Ring312.addBox(-5F, 0F, -6F, 1, 1, 1);
        Ring312.setRotationPoint(0F, 19F, 0F);
        Ring312.setTextureSize(64, 32);
        Ring312.mirror = true;
        setRotation(Ring312, 0F, 0F, 0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        GL11.glPushMatrix();
        Base.render(f5);
        GL11.glTranslatef(0.0F, 0F - f, 0.0F);
        Ring11.render(f5);
        Ring12.render(f5);
        Ring13.render(f5);
        Ring14.render(f5);
        GL11.glTranslatef(0.0F, 0F + f - f1, 0.0F);
        Ring21.render(f5);
        Ring22.render(f5);
        Ring23.render(f5);
        Ring24.render(f5);
        Ring25.render(f5);
        Ring26.render(f5);
        Ring27.render(f5);
        Ring28.render(f5);
        GL11.glTranslatef(0.0F, 0F + f1 - f2, 0.0F);
        Ring31.render(f5);
        Ring32.render(f5);
        Ring33.render(f5);
        Ring34.render(f5);
        Ring35.render(f5);
        Ring36.render(f5);
        Ring37.render(f5);
        Ring38.render(f5);
        Ring39.render(f5);
        Ring310.render(f5);
        Ring311.render(f5);
        Ring312.render(f5);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, null);
    }

}
