package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.entity.LivingEntity;

/** Model to render elytra wings as a chestplate */
public class ElytraArmorModel<T extends LivingEntity> extends BipedModel<T> {
  public static final ElytraArmorModel<LivingEntity> INSTANCE = new ElytraArmorModel<>();

  private final ElytraModel<T> elytraModel = new ElytraModel<>();
  private ElytraArmorModel() {
    super(1);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    matrixStackIn.push();
    matrixStackIn.translate(0.0D, 0.0D, 0.125D);
    elytraModel.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    matrixStackIn.pop();
  }

  @Override
  public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    elytraModel.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
  }

  @Override
  public void setVisible(boolean visible) {
    // attributes are copied to elytra through another model's setModelAttributes, this is the best hook to copy them to elytra
    this.copyModelAttributesTo(elytraModel);
  }
}
