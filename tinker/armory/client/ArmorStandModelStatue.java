/*
 * Armor stand model by Fudgy_Fetus
 */
package tinker.armory.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ArmorStandModelStatue extends ModelBase
{
	ModelRenderer head;
	ModelRenderer body;
	ModelRenderer rightarm;
	ModelRenderer leftarm;
	ModelRenderer bottompedestal;
	ModelRenderer toppedestal;
	ModelRenderer standstick;
	
	public ArmorStandModelStatue()
	{	
		textureWidth = 128;
		textureHeight = 64;	
		
		head = new ModelRenderer(this, 0, 0);
		head.addBox(-4F, -8F, -4F, 8, 8, 8);
		head.setRotationPoint(0F, 0F, 0F);
		head.setTextureSize(128, 64);
		head.mirror = true;
		setRotation(head, 0F, 0F, 0F);
		body = new ModelRenderer(this, 16, 16);
		body.addBox(-4F, 0F, -2F, 8, 12, 4);
		body.setRotationPoint(0F, 0F, 0F);
		body.setTextureSize(128, 64);
		body.mirror = true;
		setRotation(body, 0F, 0F, 0F);
		rightarm = new ModelRenderer(this, 40, 16);
		rightarm.addBox(-3F, -2F, -2F, 4, 7, 4);
		rightarm.setRotationPoint(-5F, 2F, 0F);
		rightarm.setTextureSize(128, 64);
		rightarm.mirror = true;
		setRotation(rightarm, 0F, 0F, 0F);
		leftarm = new ModelRenderer(this, 40, 16);
		leftarm.addBox(-1F, -2F, -2F, 4, 7, 4);
		leftarm.setRotationPoint(5F, 2F, 0F);
		leftarm.setTextureSize(128, 64);
		leftarm.mirror = true;
		setRotation(leftarm, 0F, 0F, 0F);
		bottompedestal = new ModelRenderer(this, 56, 15);
		bottompedestal.addBox(0F, 0F, 0F, 16, 2, 16);
		bottompedestal.setRotationPoint(-8F, 26F, -8F);
		bottompedestal.setTextureSize(128, 64);
		bottompedestal.mirror = true;
		setRotation(bottompedestal, 0F, 0F, 0F);
		toppedestal = new ModelRenderer(this, 60, 0);
		toppedestal.addBox(0F, 0F, 0F, 14, 1, 14);
		toppedestal.setRotationPoint(-7F, 25F, -7F);
		toppedestal.setTextureSize(128, 64);
		toppedestal.mirror = true;
		setRotation(toppedestal, 0F, 0F, 0F);
		standstick = new ModelRenderer(this, 0, 16);
		standstick.addBox(0F, 0F, 0F, 2, 13, 2);
		standstick.setRotationPoint(-1F, 12F, -1F);
		standstick.setTextureSize(128, 64);
		standstick.mirror = true;
		setRotation(standstick, 0F, 0F, 0F);
	}
	
	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float rotation)
	{
		super.render(entity, f, f1, f2, f3, f4, rotation);
		setRotationAngles(f, f1, f2, f3, f4, rotation, entity);
		head.render(rotation);
		body.render(rotation);
		rightarm.render(rotation);
		leftarm.render(rotation);
		bottompedestal.render(rotation);
		toppedestal.render(rotation);
		standstick.render(rotation);
	}
	
	void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity par7Entity)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, par7Entity);
	}

}
