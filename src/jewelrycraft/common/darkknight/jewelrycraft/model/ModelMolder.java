package common.darkknight.jewelrycraft.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelMolder extends ModelBase
{
    //fields
    ModelRenderer Base;
    ModelRenderer Side;
    ModelRenderer Side1;
    ModelRenderer Side2;
    ModelRenderer Side3;
    
    public ModelMolder()
    {
        textureWidth = 64;
        textureHeight = 32;
        
        Base = new ModelRenderer(this, 0, 0);
        Base.addBox(0F, 0F, 0F, 10, 1, 10);
        Base.setRotationPoint(-5F, 23F, -5F);
        Base.setTextureSize(64, 32);
        Base.mirror = true;
        setRotation(Base, 0F, 0F, 0F);
        Side = new ModelRenderer(this, 0, 13);
        Side.addBox(0F, 0F, 0F, 10, 2, 1);
        Side.setRotationPoint(-5F, 21F, 5F);
        Side.setTextureSize(64, 32);
        Side.mirror = true;
        setRotation(Side, 0F, 0F, 0F);
        Side1 = new ModelRenderer(this, 0, 13);
        Side1.addBox(0F, 0F, 0F, 10, 2, 1);
        Side1.setRotationPoint(-5F, 21F, -6F);
        Side1.setTextureSize(64, 32);
        Side1.mirror = true;
        setRotation(Side1, 0F, 0F, 0F);
        Side2 = new ModelRenderer(this, 41, 0);
        Side2.addBox(0F, 0F, 0F, 1, 2, 10);
        Side2.setRotationPoint(-6F, 21F, -5F);
        Side2.setTextureSize(64, 32);
        Side2.mirror = true;
        setRotation(Side2, 0F, 0F, 0F);
        Side3 = new ModelRenderer(this, 41, 0);
        Side3.addBox(0F, 0F, 0F, 1, 2, 10);
        Side3.setRotationPoint(5F, 21F, -5F);
        Side3.setTextureSize(64, 32);
        Side3.mirror = true;
        setRotation(Side3, 0F, 0F, 0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5);
        Base.render(f5);
        Side.render(f5);
        Side1.render(f5);
        Side2.render(f5);
        Side3.render(f5);
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
