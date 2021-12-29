package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;

import javax.annotation.Nullable;

/** Model to render elytra wings as a chestplate */
public class SlimelytraArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
  //public static final SlimelytraArmorModel<LivingEntity> INSTANCE = new SlimelytraArmorModel<>(ArmorDefinitions.SLIMESUIT);

  /** Base elytra model to render */
  private final ElytraModel<T> elytraModel = new ElytraModel<>(null); // TODO
  /** Texture to use when rendering the elytra */
  private final ResourceLocation elytraTexture;
  /** Original helmet model to render */
  @Nullable
  private HumanoidModel<?> base;

  // TODO: I am kinda scared to have to learn this again
  public SlimelytraArmorModel(ModifiableArmorMaterial material) {
    super(null); // TODO
    //super(1);
    ResourceLocation materialName = material.getNameLocation();
    this.elytraTexture = new ResourceLocation(materialName.getNamespace(), String.format("textures/models/armor/%s_wings.png", materialName.getPath()));
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      base.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
    if (SlimeskullArmorModel.buffer != null) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0D, 0.0D, 0.125D);
      VertexConsumer elytraBuffer = SlimeskullArmorModel.buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(elytraTexture));
      elytraModel.renderToBuffer(matrixStackIn, elytraBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.popPose();
    }
  }

  /** Called before the model is rendered to set the base model and the elytra entity data */
  public void setEntityAndBase(T entity, HumanoidModel<?> base) {
    elytraModel.setupAnim(entity, 0, 0, 0, 0, 0);
    this.base = base;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setAllVisible(boolean visible) {
    // attributes are copied to elytra through another model's setModelAttributes, this is the best hook to copy them to elytra
    this.copyPropertiesTo(elytraModel);
    if (base != null) {
      base.setAllVisible(false);
      base.body.visible = true;
      base.rightArm.visible = true;
      base.leftArm.visible = true;
      this.copyPropertiesTo((HumanoidModel<T>)base);
    }
  }
}
