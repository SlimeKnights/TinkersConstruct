package tconstruct.client.armor;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class WingModel extends ModelBiped
{
    ModelRenderer WingBaseRight;
    ModelRenderer WingEdgeRight;
    ModelRenderer WingInsetRight;
    ModelRenderer WingCenterRight;
    ModelRenderer WingFlangeRight;
    ModelRenderer WingAuxRight;
    ModelRenderer WingBaseLeft;
    ModelRenderer WingEdgeLeft;
    ModelRenderer WingInsetLeft;
    ModelRenderer WingCenterLeft;
    ModelRenderer WingFlangeLeft;
    ModelRenderer WingAuxLeft;

    public WingModel()
    {
        super(0.25f, 0, 64, 64);
        textureWidth = 64;
        textureHeight = 64;

        textureWidth = 64;
        textureHeight = 64;

        //Right Wing
        WingBaseRight = new ModelRenderer(this, 0, 41);
        WingBaseRight.addBox(-0.5F, -1F, 0F, 1, 2, 10);
        WingBaseRight.setRotationPoint(-1F, 1F, 0F);
        setRotation(WingBaseRight, 0.5235988F, -0.5235988F, 0F);

        WingEdgeRight = new ModelRenderer(this, 0, 53); //Texture position
        WingEdgeRight.addBox(0F, 0F, -2F, 1, 9, 2); //Offset, Size
        WingEdgeRight.setRotationPoint(-0.502F, -1F, 10F); //Negative x, y - 1, Position
        setRotation(WingEdgeRight, 0.5235988F, 0F, 0F); //Angle in radians

        WingInsetRight = new ModelRenderer(this, 6, 53);
        WingInsetRight.addBox(0F, 0F, -1F, 1, 9, 2);
        WingInsetRight.setRotationPoint(-0.504F, 0F, 7.8F);
        setRotation(WingInsetRight, 0.3490659F, 0F, 0F);

        WingCenterRight = new ModelRenderer(this, 12, 53);
        WingCenterRight.addBox(0F, 0F, -1F, 1, 9, 2);
        WingCenterRight.setRotationPoint(-0.506F, 0.3F, 6.3F);
        setRotation(WingCenterRight, 0.1745329F, 0F, 0F);

        WingFlangeRight = new ModelRenderer(this, 18, 53);
        WingFlangeRight.addBox(0F, 0F, -1F, 1, 8, 2);
        WingFlangeRight.setRotationPoint(-0.508F, 0.3F, 5.1F);
        setRotation(WingFlangeRight, 0F, 0F, 0F);

        WingAuxRight = new ModelRenderer(this, 24, 53);
        WingAuxRight.addBox(0F, 0F, -1F, 1, 7, 2);
        WingAuxRight.setRotationPoint(-0.51F, 0.1F, 4F);
        setRotation(WingAuxRight, -0.1745329F, 0F, 0F);

        WingBaseRight.addChild(WingEdgeRight);
        WingBaseRight.addChild(WingInsetRight);
        WingBaseRight.addChild(WingCenterRight);
        WingBaseRight.addChild(WingFlangeRight);
        WingBaseRight.addChild(WingAuxRight);

        //Left Wing
        WingBaseLeft = new ModelRenderer(this, 42, 41);
        WingBaseLeft.addBox(-0.5F, -1F, 0F, 1, 2, 10);
        WingBaseLeft.setRotationPoint(1F, 1F, 0F);
        setRotation(WingBaseLeft, 0.5235988F, 0.5235988F, 0F);

        WingEdgeLeft = new ModelRenderer(this, 58, 53);
        WingEdgeLeft.addBox(0F, 0F, -2F, 1, 9, 2);
        WingEdgeLeft.setRotationPoint(-0.502F, -1F, 10F);
        setRotation(WingEdgeLeft, 0.5235988F, 0F, 0F);

        WingInsetLeft = new ModelRenderer(this, 52, 53);
        WingInsetLeft.addBox(0F, 0F, -1F, 1, 9, 2);
        WingInsetLeft.setRotationPoint(-0.504F, 0F, 7.8F);
        setRotation(WingInsetLeft, 0.3490659F, 0F, 0F);

        WingCenterLeft = new ModelRenderer(this, 46, 53);
        WingCenterLeft.addBox(0F, 0F, -1F, 1, 9, 2);
        WingCenterLeft.setRotationPoint(-0.506F, 0.3F, 6.3F);
        setRotation(WingCenterLeft, 0.1745329F, 0F, 0F);

        WingFlangeLeft = new ModelRenderer(this, 40, 53);
        WingFlangeLeft.addBox(0F, 0F, -1F, 1, 8, 2);
        WingFlangeLeft.setRotationPoint(-0.508F, 0.3F, 5.1F);
        setRotation(WingFlangeLeft, 0F, 0F, 0F);

        WingAuxLeft = new ModelRenderer(this, 34, 53);
        WingAuxLeft.addBox(0F, 0F, -1F, 1, 7, 2);
        WingAuxLeft.setRotationPoint(-0.51F, 0.1F, 4F);
        setRotation(WingAuxLeft, -0.1745329F, 0F, 0F);

        WingBaseLeft.addChild(WingEdgeLeft);
        WingBaseLeft.addChild(WingInsetLeft);
        WingBaseLeft.addChild(WingCenterLeft);
        WingBaseLeft.addChild(WingFlangeLeft);
        WingBaseLeft.addChild(WingAuxLeft);

        this.bipedCloak.isHidden = true;
        this.bipedEars.isHidden = true;
        this.bipedHead.isHidden = true;
        this.bipedHeadwear.isHidden = true;
        this.bipedRightArm.isHidden = true;
        this.bipedLeftArm.isHidden = true;
        this.bipedRightLeg.isHidden = true;
        this.bipedLeftLeg.isHidden = true;
    }

    public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        WingBaseRight.render(f5);
        WingBaseLeft.render(f5);
    }

    private void setRotation (ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles (float f, float f1, float f2, float f3, float f4, float f5, Entity player)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, player);
        if (this.isRiding)
        {
            float slow = 17f;
            this.WingBaseRight.rotateAngleX = MathHelper.sin(f2 / slow) / 10f + 0.9F;
            this.WingBaseRight.rotateAngleY = MathHelper.sin(f2 / slow) / 5.0f - 0.7f;
            this.WingBaseRight.rotateAngleZ = MathHelper.sin(f2 / slow) / 2.5f - 0.3f;

            this.WingBaseLeft.rotateAngleX = MathHelper.sin(f2 / slow) / 10 + 0.9F;
            this.WingBaseLeft.rotateAngleY = -MathHelper.sin(f2 / slow) / 5.0f + 0.7f;
            this.WingBaseLeft.rotateAngleZ = -MathHelper.sin(f2 / slow) / 2.5f + 0.3f;
        }
        else if (player.isInWater())
        {
            float slow = 17f;
            this.WingBaseRight.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.5235988F;
            this.WingBaseRight.rotateAngleY = MathHelper.sin(f2 / slow) / 15f - 0.8f;
            this.WingBaseRight.rotateAngleZ = MathHelper.sin(f2 / slow) / 15f - 0.8f;

            this.WingBaseLeft.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.5235988F;
            this.WingBaseLeft.rotateAngleY = -MathHelper.sin(f2 / slow) / 15f + 0.8f;
            this.WingBaseLeft.rotateAngleZ = -MathHelper.sin(f2 / slow) / 15f + 0.8f;
        }
        else if (player.posY - player.prevPosY < 0f)
        {
            float slow = 20f;
            this.WingBaseRight.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.7F;
            this.WingBaseRight.rotateAngleY = MathHelper.sin(f2 / slow) / 15f - 0.8f;
            this.WingBaseRight.rotateAngleZ = MathHelper.sin(f2 / slow) / 15f - 0.3f;

            this.WingBaseLeft.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.7F;
            this.WingBaseLeft.rotateAngleY = -MathHelper.sin(f2 / slow) / 15f + 0.8f;
            this.WingBaseLeft.rotateAngleZ = -MathHelper.sin(f2 / slow) / 15f + 0.3f;
        }
        else if (player.isSprinting() || this.onGround > 0)
        {
            float slow = 1.73f;
            this.WingBaseRight.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.5235988F;
            this.WingBaseRight.rotateAngleY = MathHelper.sin(f2 / slow) / 15f - 0.6f;
            this.WingBaseRight.rotateAngleZ = MathHelper.sin(f2 / slow) / 15f - 0.3f;

            this.WingBaseLeft.rotateAngleX = MathHelper.sin(f2 / slow) / 15f + 0.5235988F;
            this.WingBaseLeft.rotateAngleY = -MathHelper.sin(f2 / slow) / 15f + 0.6f;
            this.WingBaseLeft.rotateAngleZ = -MathHelper.sin(f2 / slow) / 15f + 0.3f;
        }
        else
        {
            float slow = 17f;
            if (player.motionX != 0 || player.motionZ != 0)
                slow = 6f;
            this.WingBaseRight.rotateAngleX = MathHelper.sin(f2 / slow) / 5f + 0.5235988F;
            this.WingBaseRight.rotateAngleY = MathHelper.sin(f2 / slow) / 3.0f - 0.6f;
            this.WingBaseRight.rotateAngleZ = MathHelper.sin(f2 / slow) / 1.5f - 0.3f;

            this.WingBaseLeft.rotateAngleX = MathHelper.sin(f2 / slow) / 5f + 0.5235988F;
            this.WingBaseLeft.rotateAngleY = -MathHelper.sin(f2 / slow) / 3.0f + 0.6f;
            this.WingBaseLeft.rotateAngleZ = -MathHelper.sin(f2 / slow) / 1.5f + 0.3f;
        }

        if (this.isSneak)
        {
            this.WingBaseRight.rotateAngleX += 0.4f;
            this.WingBaseLeft.rotateAngleX += 0.4f;
        }
    }

}
