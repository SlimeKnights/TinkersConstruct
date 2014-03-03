package common.darkknight.jewelrycraft.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelJewlersCraftingBench extends ModelBase
{
  //fields
    ModelRenderer Leg1;
    ModelRenderer Leg2;
    ModelRenderer Leg3;
    ModelRenderer Leg4;
    ModelRenderer Top;
    ModelRenderer Support1;
    ModelRenderer Support2;
    ModelRenderer Support3;
    ModelRenderer Support4;
    ModelRenderer Support5;
    ModelRenderer Support6;
    ModelRenderer Support7;
    ModelRenderer Support8;
  
  public ModelJewlersCraftingBench()
  {
    textureWidth = 64;
    textureHeight = 32;
    
      Leg1 = new ModelRenderer(this, 0, 0);
      Leg1.addBox(0F, 0F, 0F, 2, 10, 2);
      Leg1.setRotationPoint(-6F, 14F, 4F);
      Leg1.setTextureSize(64, 32);
      Leg1.mirror = true;
      setRotation(Leg1, 0F, 0F, 0F);
      Leg2 = new ModelRenderer(this, 0, 0);
      Leg2.addBox(0F, 0F, 0F, 2, 10, 2);
      Leg2.setRotationPoint(-6F, 14F, -6F);
      Leg2.setTextureSize(64, 32);
      Leg2.mirror = true;
      setRotation(Leg2, 0F, 0F, 0F);
      Leg3 = new ModelRenderer(this, 0, 0);
      Leg3.addBox(0F, 0F, 0F, 2, 10, 2);
      Leg3.setRotationPoint(4F, 14F, -6F);
      Leg3.setTextureSize(64, 32);
      Leg3.mirror = true;
      setRotation(Leg3, 0F, 0F, 0F);
      Leg4 = new ModelRenderer(this, 0, 0);
      Leg4.addBox(0F, 0F, 0F, 2, 10, 2);
      Leg4.setRotationPoint(4F, 14F, 4F);
      Leg4.setTextureSize(64, 32);
      Leg4.mirror = true;
      setRotation(Leg4, 0F, 0F, 0F);
      Top = new ModelRenderer(this, 0, 13);
      Top.addBox(0F, 0F, 0F, 16, 1, 16);
      Top.setRotationPoint(-8F, 13F, -8F);
      Top.setTextureSize(64, 32);
      Top.mirror = true;
      setRotation(Top, 0F, 0F, 0F);
      Support1 = new ModelRenderer(this, 0, 0);
      Support1.addBox(0F, 0F, 0F, 3, 1, 1);
      Support1.setRotationPoint(3F, 12F, 5F);
      Support1.setTextureSize(64, 32);
      Support1.mirror = true;
      setRotation(Support1, 0F, 0F, 0F);
      Support2 = new ModelRenderer(this, 0, 0);
      Support2.addBox(0F, 0F, 0F, 1, 1, 3);
      Support2.setRotationPoint(2F, 12F, 2F);
      Support2.setTextureSize(64, 32);
      Support2.mirror = true;
      setRotation(Support2, 0F, 0F, 0F);
      Support3 = new ModelRenderer(this, 0, 0);
      Support3.addBox(0F, 0F, 0F, 1, 1, 3);
      Support3.setRotationPoint(6F, 12F, 2F);
      Support3.setTextureSize(64, 32);
      Support3.mirror = true;
      setRotation(Support3, 0F, 0F, 0F);
      Support4 = new ModelRenderer(this, 0, 0);
      Support4.addBox(0F, 0F, 0F, 3, 1, 1);
      Support4.setRotationPoint(3F, 12F, 1F);
      Support4.setTextureSize(64, 32);
      Support4.mirror = true;
      setRotation(Support4, 0F, 0F, 0F);
      Support5 = new ModelRenderer(this, 0, 0);
      Support5.addBox(0F, 0F, 0F, 1, 1, 3);
      Support5.setRotationPoint(-3F, 12F, 2F);
      Support5.setTextureSize(64, 32);
      Support5.mirror = true;
      setRotation(Support5, 0F, 0F, 0F);
      Support6 = new ModelRenderer(this, 0, 0);
      Support6.addBox(0F, 0F, 0F, 3, 1, 1);
      Support6.setRotationPoint(-6F, 12F, 5F);
      Support6.setTextureSize(64, 32);
      Support6.mirror = true;
      setRotation(Support6, 0F, 0F, 0F);
      Support7 = new ModelRenderer(this, 0, 0);
      Support7.addBox(0F, 0F, 0F, 1, 1, 3);
      Support7.setRotationPoint(-7F, 12F, 2F);
      Support7.setTextureSize(64, 32);
      Support7.mirror = true;
      setRotation(Support7, 0F, 0F, 0F);
      Support8 = new ModelRenderer(this, 0, 0);
      Support8.addBox(0F, 0F, 0F, 3, 1, 1);
      Support8.setRotationPoint(-6F, 12F, 1F);
      Support8.setTextureSize(64, 32);
      Support8.mirror = true;
      setRotation(Support8, 0F, 0F, 0F);
  }
  
  public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
  {
    super.render(entity, f, f1, f2, f3, f4, f5);
    setRotationAngles(f, f1, f2, f3, f4, f5);
    Leg1.render(f5);
    Leg2.render(f5);
    Leg3.render(f5);
    Leg4.render(f5);
    Top.render(f5);
    Support1.render(f5);
    Support2.render(f5);
    Support3.render(f5);
    Support4.render(f5);
    Support5.render(f5);
    Support6.render(f5);
    Support7.render(f5);
    Support8.render(f5);
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
