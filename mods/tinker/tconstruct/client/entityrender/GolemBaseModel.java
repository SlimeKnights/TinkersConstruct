package mods.tinker.tconstruct.client.entityrender;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class GolemBaseModel extends ModelPig
{
    ModelRenderer rightArm;
    ModelRenderer leftArm;

    public GolemBaseModel()
    {
        float f = 0.0F;
        float f1 = 0.0F;
        head = new ModelRenderer(this, 0, 0);
        head.addBox(-4F, -8F, -4F, 8, 8, 8, f);
        head.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        head.showModel = false;
        body = new ModelRenderer(this, 16, 16);
        body.addBox(-4F, 0.0F, -2F, 8, 12, 4, f);
        body.setRotationPoint(0.0F, 0.0F + f1, 0.0F);
        body.showModel = false;
        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-3F, -2F, -2F, 4, 12, 4, f);
        rightArm.setRotationPoint(-5F, 2.0F + f1, 0.0F);
        leftArm = new ModelRenderer(this, 40, 16);
        leftArm.mirror = true;
        leftArm.addBox(-1F, -2F, -2F, 4, 12, 4, f);
        leftArm.setRotationPoint(5F, 2.0F + f1, 0.0F);
        leg3 = new ModelRenderer(this, 0, 16);
        leg3.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
        leg3.setRotationPoint(-2F, 12F + f1, 0.0F);
        leg4 = new ModelRenderer(this, 0, 16);
        leg4.mirror = true;
        leg4.addBox(-2F, 0.0F, -2F, 4, 12, 4, f);
        leg4.setRotationPoint(2.0F, 12F + f1, 0.0F);
        rightArm = new ModelRenderer(this, 40, 16);
        rightArm.addBox(-1F, -2F, -1F, 2, 12, 2, f);
        rightArm.setRotationPoint(-5F, 2.0F, 0.0F);
        rightArm.showModel = false;
        leftArm = new ModelRenderer(this, 40, 16);
        leftArm.mirror = true;
        leftArm.addBox(-1F, -2F, -1F, 2, 12, 2, f);
        leftArm.setRotationPoint(5F, 2.0F, 0.0F);
        leftArm.showModel = false;
        leg3 = new ModelRenderer(this, 0, 16);
        leg3.addBox(-1F, 0.0F, -1F, 2, 12, 2, f);
        leg3.setRotationPoint(-2F, 12F, 0.0F);
        leg3.showModel = false;
        leg4 = new ModelRenderer(this, 0, 16);
        leg4.mirror = true;
        leg4.addBox(-1F, 0.0F, -1F, 2, 12, 2, f);
        leg4.setRotationPoint(2.0F, 12F, 0.0F);
        leg4.showModel = false;
        leg1.showModel = leg2.showModel = false;
    }

    @Override
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        float f6 = 0.0F;
        float f7 = 0.0F;
        rightArm.rotateAngleZ = 0.0F;
        leftArm.rotateAngleZ = 0.0F;
        rightArm.rotateAngleY = -(0.1F - f6 * 0.6F) + head.rotateAngleY;
        leftArm.rotateAngleY = (0.1F - f6 * 0.6F) + head.rotateAngleY + 0.4F;
        rightArm.rotateAngleX = -1.570796F + head.rotateAngleX;
        leftArm.rotateAngleX = -1.570796F + head.rotateAngleX;
        rightArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        leftArm.rotateAngleX -= f6 * 1.2F - f7 * 0.4F;
        rightArm.rotateAngleZ += MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        leftArm.rotateAngleZ -= MathHelper.cos(f2 * 0.09F) * 0.05F + 0.05F;
        rightArm.rotateAngleX += MathHelper.sin(f2 * 0.067F) * 0.05F;
        leftArm.rotateAngleX -= MathHelper.sin(f2 * 0.067F) * 0.05F;
    }
}
