package tconstruct.client.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelMiniGardy extends ModelBase
{
    ModelRendererDegrees body;
    ModelRendererDegrees torso;
    ModelRendererDegrees head;
    ModelRendererDegrees rightArm;
    ModelRendererDegrees rightHand;
    ModelRendererDegrees leftArm;
    ModelRendererDegrees leftHand;
    ModelRendererDegrees hairTop;
    ModelRendererDegrees hairRight;
    ModelRendererDegrees lockLeft;
    ModelRendererDegrees lockRight;
    ModelRendererDegrees hairLeft;
    ModelRendererDegrees hairBack;
    ModelRendererDegrees hairFront;
    ModelRendererDegrees hairTopFront;
    ModelRendererDegrees hairTopBack;
    ModelRendererDegrees lockInsideRight;
    ModelRendererDegrees lockInsideLeft;

    public ModelMiniGardy()
    {
        body = new ModelRendererDegrees(this, 0, 0);
        body.addBox(-3F, 0F, -3F, 6, 6, 6);
        body.setRotationPoint(0F, 18F, 0F);
        body.mirror = true;
        setRotation(body, 0F, 0F, 0F);

        torso = new ModelRendererDegrees(this, 16, 12);
        torso.addBox(-1F, -5F, -1F, 2, 5, 2);
        torso.setRotationPoint(0F, 0F, 0F);
        torso.mirror = true;
        setRotation(torso, 0F, 0F, 0F);
        body.addChild(torso);

        rightArm = new ModelRendererDegrees(this, 24, 0);
        rightArm.addBox(-1F, -1F, -1F, 2, 4, 2);
        rightArm.setRotationPoint(-2F, -3F, 0F);
        rightArm.mirror = true;
        setRotation(rightArm, 0F, 0F, 15F);//convertDegrees(30.0f));//0.2617994F);
        torso.addChild(rightArm);

        rightHand = new ModelRendererDegrees(this, 24, 6);
        rightHand.addBox(-1F, 0F, -2F, 2, 4, 2);
        rightHand.setRotationPoint(0F, 3F, 1F);
        rightHand.setTextureSize(64, 32);
        rightHand.mirror = true;
        setRotation(rightHand, 0F, 0F, 0F);
        rightArm.addChild(rightHand);

        leftArm = new ModelRendererDegrees(this, 32, 0);
        leftArm.addBox(-1F, -1F, -1F, 2, 4, 2);
        leftArm.setRotationPoint(2F, -3F, 0F);
        leftArm.mirror = true;
        setRotation(leftArm, 0F, 0F, -15F);
        torso.addChild(leftArm);

        leftHand = new ModelRendererDegrees(this, 32, 6);
        leftHand.addBox(-1F, 0F, -2F, 2, 4, 2);
        leftHand.setRotationPoint(0F, 3F, 1F);
        leftHand.mirror = true;
        setRotation(leftHand, 0F, 0F, 0F);
        leftArm.addChild(leftHand);

        //Head
        head = new ModelRendererDegrees(this, 0, 12);
        head.addBox(-2F, -4F, -2F, 4, 4, 4);
        head.setRotationPoint(0F, -5F, 0F);
        head.mirror = true;
        setRotation(head, 0F, 0F, 0F);
        torso.addChild(head);

        hairTop = new ModelRendererDegrees(this, 40, 2);
        hairTop.addBox(-3F, 0F, -2F, 6, 1, 4);
        hairTop.setRotationPoint(0F, -5F, 0F);
        hairTop.mirror = true;
        setRotation(hairTop, 0F, 0F, 0F);
        head.addChild(hairTop);

        hairRight = new ModelRendererDegrees(this, 40, 7);
        hairRight.addBox(0F, 0F, 0F, 2, 5, 4);
        hairRight.setRotationPoint(-3F, -4F, -1F);
        hairRight.mirror = true;
        setRotation(hairRight, 0F, 0F, 0F);
        head.addChild(hairRight);

        lockLeft = new ModelRendererDegrees(this, 46, 19);
        lockLeft.addBox(0F, 0F, 0F, 1, 2, 2);
        lockLeft.setRotationPoint(2F, -1F, -3F);
        lockLeft.mirror = true;
        setRotation(lockLeft, 0F, 0F, 0F);
        head.addChild(lockLeft);

        lockRight = new ModelRendererDegrees(this, 40, 19);
        lockRight.addBox(-1F, 0F, 0F, 1, 2, 2);
        lockRight.setRotationPoint(-2F, -1F, -3F);
        lockRight.mirror = true;
        setRotation(lockRight, 0F, 0F, 0F);
        head.addChild(lockRight);

        hairLeft = new ModelRendererDegrees(this, 52, 7);
        hairLeft.addBox(0F, 0F, 0F, 2, 5, 4);
        hairLeft.setRotationPoint(1F, -4F, -1F);
        hairLeft.mirror = true;
        setRotation(hairLeft, 0F, 0F, 0F);
        head.addChild(hairLeft);

        hairBack = new ModelRendererDegrees(this, 56, 16);
        hairBack.addBox(-1F, 0F, 0F, 2, 4, 1);
        hairBack.setRotationPoint(0F, -4F, 2F);
        hairBack.mirror = true;
        setRotation(hairBack, 0F, 0F, 0F);
        head.addChild(hairBack);

        hairFront = new ModelRendererDegrees(this, 40, 16);
        hairFront.addBox(0F, 0F, 0F, 6, 1, 2);
        hairFront.setRotationPoint(-3F, -4F, -3F);
        hairFront.mirror = true;
        setRotation(hairFront, 0F, 0F, 0F);
        head.addChild(hairFront);

        hairTopFront = new ModelRendererDegrees(this, 50, 0);
        hairTopFront.addBox(0F, 0F, 0F, 4, 1, 1);
        hairTopFront.setRotationPoint(-2F, -5F, -3F);
        hairTopFront.mirror = true;
        setRotation(hairTopFront, 0F, 0F, 0F);
        head.addChild(hairTopFront);

        hairTopBack = new ModelRendererDegrees(this, 40, 0);
        hairTopBack.addBox(0F, 0F, 0F, 4, 1, 1);
        hairTopBack.setRotationPoint(-2F, -5F, 2F);
        hairTopBack.mirror = true;
        setRotation(hairTopBack, 0F, 0F, 0F);
        head.addChild(hairTopBack);

        lockInsideRight = new ModelRendererDegrees(this, 40, 23);
        lockInsideRight.addBox(0F, 0F, 0F, 1, 1, 2);
        lockInsideRight.setRotationPoint(-2F, 0F, -3F);
        lockInsideRight.mirror = true;
        setRotation(lockInsideRight, 0F, 0F, 0F);
        head.addChild(lockInsideRight);

        lockInsideLeft = new ModelRendererDegrees(this, 46, 23);
        lockInsideLeft.addBox(-1F, 0F, 0F, 1, 1, 2);
        lockInsideLeft.setRotationPoint(2F, 0F, -3F);
        lockInsideLeft.mirror = true;
        setRotation(lockInsideLeft, 0F, 0F, 0F);
        head.addChild(lockInsideLeft);
    }

    @Override
    public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.render(entity, f, f1, f2, f3, f4, f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        body.render(f5);
    }

    private void setRotation (ModelRendererDegrees model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    private float convertDegrees (float f)
    {
        return f * ((float) Math.PI / 180F);
    }

    private float convertRadians (float f)
    {
        return f * (180F / (float) Math.PI);
    }

    @Override
    public void setRotationAngles (float limbSwing, float limbDelta, float ticksExisted, float rotation, float rotationDelta, float par6, Entity entity)
    {
        float bodyRotation = convertRadians(MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbDelta * 0.5F);
        this.rightArm.rotateAngleX = bodyRotation;
        this.leftArm.rotateAngleX = -bodyRotation;
        this.torso.rotateAngleY = bodyRotation / 2;
        this.head.rotateAngleY = rotation - bodyRotation / 2;
        this.head.rotateAngleX = rotationDelta;
        if (bodyRotation <= 0)
        {
            this.rightHand.rotateAngleX = bodyRotation;
        }
        if (-bodyRotation <= 0)
        {
            this.leftHand.rotateAngleX = -bodyRotation;
        }
        this.rightArm.rotateAngleZ = 15F;
        this.leftArm.rotateAngleZ = -15F;
        this.torso.rotationPointY = 0F;
        this.rightArm.rotateAngleZ += convertRadians(MathHelper.cos(ticksExisted * 0.09F) * 0.05F + 0.05F);
        this.leftArm.rotateAngleZ -= convertRadians(MathHelper.cos(ticksExisted * 0.09F) * 0.05F + 0.05F);
        this.torso.rotationPointY += convertRadians(MathHelper.cos(ticksExisted * 0.09F) * 0.01F + 0.01F);
    }

}
