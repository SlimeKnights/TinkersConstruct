package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.definition.ModifiableArmorMaterial;
import slimeknights.tconstruct.tools.ArmorDefinitions;

import javax.annotation.Nullable;

/** Model to render elytra wings as a chestplate */
public class SlimelytraArmorModel<T extends LivingEntity> extends BipedModel<T> {
  public static final SlimelytraArmorModel<LivingEntity> INSTANCE = new SlimelytraArmorModel<>(ArmorDefinitions.SLIMESUIT);

  /** Base elytra model to render */
  private final ElytraModel<T> elytraModel = new ElytraModel<>();
  /** Texture to use when rendering the elytra */
  private final ResourceLocation elytraTexture;
  /** Original helmet model to render */
  @Nullable
  private BipedModel<?> base;

  public SlimelytraArmorModel(ModifiableArmorMaterial material) {
    super(1);
    ResourceLocation materialName = material.getNameLocation();
    this.elytraTexture = new ResourceLocation(materialName.getNamespace(), String.format("textures/models/armor/%s_wings.png", materialName.getPath()));
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      base.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
    if (SlimeskullArmorModel.buffer != null) {
      matrixStackIn.push();
      matrixStackIn.translate(0.0D, 0.0D, 0.125D);
      IVertexBuilder elytraBuffer = SlimeskullArmorModel.buffer.getBuffer(RenderType.getEntityCutoutNoCullZOffset(elytraTexture));
      elytraModel.render(matrixStackIn, elytraBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.pop();
    }
  }

  /** Called before the model is rendered to set the base model and the elytra entity data */
  public void setEntityAndBase(T entity, BipedModel<?> base) {
    elytraModel.setRotationAngles(entity, 0, 0, 0, 0, 0);
    this.base = base;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setVisible(boolean visible) {
    // attributes are copied to elytra through another model's setModelAttributes, this is the best hook to copy them to elytra
    this.copyModelAttributesTo(elytraModel);
    if (base != null) {
      base.setVisible(false);
      base.bipedBody.showModel = true;
      base.bipedRightArm.showModel = true;
      base.bipedLeftArm.showModel = true;
      this.setModelAttributes((BipedModel<T>)base);
    }
  }
}
