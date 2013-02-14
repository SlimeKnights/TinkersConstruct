package tinker.tconstruct.client.entityrender;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import tinker.tconstruct.client.tmt.ModelRendererTurbo;

public class SkylaModel extends ModelBase
{
	ModelRenderer Head;
	ModelRenderer Hair;
	ModelRenderer Neck;
	ModelRenderer Torso;
	ModelRendererTurbo Skirt;
	ModelRenderer Breast;
	ModelRenderer RightArm;
	ModelRenderer RightBand;
	ModelRendererTurbo RightSleeve;
	ModelRenderer RightHand;
	ModelRenderer LeftArm;
	ModelRenderer LeftBand;
	ModelRendererTurbo LeftSleeve;
	ModelRenderer LeftHand;
	ModelRenderer RightLeg;
	ModelRenderer RightFoot;
	ModelRenderer LeftLeg;
	ModelRenderer LeftFoot;
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

	public SkylaModel()
	{
		textureWidth = 64;
		textureHeight = 64;

		Head = new ModelRenderer(this, 0, 0);
		Head.addBox(-4F, -8F, -4F, 8, 8, 8);
		//Head.addCylinder(0, 0, 0, 8, 16, 24, 0, ModelRendererTurbo.MR_BOTTOM);
		Head.setRotationPoint(0F, -3F, 0F);
		setRotation(Head, 0F, 0F, 0F);

		Hair = new ModelRenderer(this, 32, 0);
		Hair.addBox(-4F, -4F, -4F, 8, 8, 8, 0.5F);
		Hair.setRotationPoint(0F, -7F, 0F);
		setRotation(Hair, 0F, 0F, 0F);

		Neck = new ModelRenderer(this, 0, 0);
		Neck.addBox(-1F, 0F, -1F, 2, 1, 2);
		Neck.setRotationPoint(0F, -1F, 0F);
		setRotation(Neck, 0F, 0F, 0F);

		Torso = new ModelRenderer(this, 0, 16);
		Torso.addBox(-1.5F, 0F, -1F, 3, 8, 2);
		Torso.setRotationPoint(0F, -2F, 0F);
		setRotation(Torso, 0F, 0F, 0F);

		Breast = new ModelRenderer(this, 0, 4);
		Breast.addBox(-1.5F, 0F, 0F, 3, 3, 1);
		Breast.setRotationPoint(0F, 1F, -1F);
		setRotation(Breast, -0.2617994F, 0F, 0F);
		
		Torso.addChild(Neck);
		Torso.addChild(Breast);

		Skirt = new ModelRendererTurbo(this, 22, 42, textureWidth, textureHeight);
		Skirt.addTrapezoid(-3F, 0F, -2F, 6, 4, 4, 0, -0.5f, 4);
		Skirt.setRotationPoint(0F, 5F, 0F);
		setRotation(Skirt, 0F, 0F, 0F);

		RightArm = new ModelRenderer(this, 14, 16);
		RightArm.addBox(-1F, -1F, -1F, 2, 7, 2);
		RightArm.setRotationPoint(-2.5F, -1F, 0.1F);
		setRotation(RightArm, 0F, 0F, 0F);

		RightBand = new ModelRenderer(this, 10, 25);
		RightBand.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		RightBand.setRotationPoint(0F, 0F, 0.1F);
		setRotation(RightBand, 0F, 0F, 0F);

		RightSleeve = new ModelRendererTurbo(this, 10, 30, textureWidth, textureHeight);
		RightSleeve.addTrapezoid(-1.5F, 0F, -2.5F, 3, 6, 3, 0, -0.5f, 4);
		RightSleeve.setRotationPoint(0F, 6F, 1.0F);
		setRotation(RightSleeve, 0F, 0F, 0F);

		RightHand = new ModelRenderer(this, 12, 39);
		RightHand.addBox(-1F, 0F, -1F, 2, 2, 2);
		RightHand.setRotationPoint(0F, 12F, 0F);
		setRotation(RightHand, 0F, 0F, 0F);

		RightArm.addChild(RightBand);
		RightArm.addChild(RightSleeve);
		RightArm.addChild(RightHand);

		LeftArm = new ModelRenderer(this, 22, 16);
		LeftArm.addBox(-1F, -1F, -1F, 2, 7, 2);
		LeftArm.setRotationPoint(2.5F, -1F, 0.1F);
		setRotation(LeftArm, 0F, 0F, 0F);

		LeftBand = new ModelRenderer(this, 22, 25);
		LeftBand.addBox(-1.5F, 0F, -1.5F, 3, 2, 3);
		LeftBand.setRotationPoint(0F, 0F, 0.1F);
		setRotation(LeftBand, 0F, 0F, 0F);

		LeftSleeve = new ModelRendererTurbo(this, 22, 30, textureWidth, textureHeight);
		LeftSleeve.addTrapezoid(-1.5F, 0F, -2.5F, 3, 6, 3, 0, -0.5f, 4);
		//LeftSleeve.addBox(-1.5F, 0F, -2.5F, 3, 6, 3);
		LeftSleeve.setRotationPoint(0F, 6F, 1.0F);
		setRotation(LeftSleeve, 0F, 0F, 0F);

		LeftHand = new ModelRenderer(this, 20, 39);
		LeftHand.addBox(-1F, 0F, -1F, 2, 2, 2);
		LeftHand.setRotationPoint(0F, 12F, 0F);
		setRotation(LeftHand, 0F, 0F, 0F);

		LeftArm.addChild(LeftBand);
		LeftArm.addChild(LeftSleeve);
		LeftArm.addChild(LeftHand);

		RightLeg = new ModelRenderer(this, 34, 16);
		RightLeg.addBox(-1F, 0F, -1F, 2, 8, 2);
		RightLeg.setRotationPoint(-1F, 6F, 0F);
		setRotation(RightLeg, 0F, 0F, 0F);

		RightFoot = new ModelRenderer(this, 34, 28);
		RightFoot.addBox(-1F, 0F, 0F, 2, 8, 2);
		RightFoot.setRotationPoint(0F, 8F, -1F);
		setRotation(RightFoot, 0F, 0F, 0F);

		LeftLeg = new ModelRenderer(this, 42, 16);
		LeftLeg.addBox(-1F, 0F, -1F, 2, 8, 2);
		LeftLeg.setRotationPoint(1F, 6F, 0F);
		setRotation(LeftLeg, 0F, 0F, 0F);

		LeftFoot = new ModelRenderer(this, 42, 28);
		LeftFoot.addBox(-1F, 0F, 0F, 2, 8, 2);
		LeftFoot.setRotationPoint(0F, 8F, -1F);
		setRotation(LeftFoot, 0F, 0F, 0F);

		LeftLeg.addChild(LeftFoot);
		RightLeg.addChild(RightFoot);

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
	}

	public void render (Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		Head.render(f5);
		Hair.render(f5);
		//Neck.render(f5);
		Torso.render(f5);
		//Breast.render(f5);
		Skirt.render(f5);
		RightArm.render(f5);
		LeftArm.render(f5);
		RightLeg.render(f5);
		LeftLeg.render(f5);
		WingBaseRight.render(f5);
		WingBaseLeft.render(f5);
	}

	private void setRotation (ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	public void setRotationAngles (float f, float f1, float f2, float f3, float f4, float f5, Entity entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
	}

}
