package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Model to render a slimeskull helmet with both the helmet and skull */
public class SlimeskullArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Model instance */
  private static final SlimeskullArmorModel<LivingEntity> INSTANCE = new SlimeskullArmorModel<>();

  /**
   * Gets the model for a given entity
   * @param stack      Armor stack object
   * @param baseModel  Base model
   * @param <A>  Model instance
   * @return  Model for the entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends BipedModel<?>> A getModel(ItemStack stack, A baseModel) {
    INSTANCE.setToolAndBase(stack, baseModel);
    return (A) INSTANCE;
  }

  /** Head to render under the helmet */
  @Nullable
  private ResourceLocation headTexture;
  /** Texture for the head */
  @Nullable
  private GenericHeadModel headModel;
  private boolean hasGlint = false;

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      copyToBase();
      matrixStackIn.push();
      matrixStackIn.translate(0.0D, this.isChild ? -0.015D : -0.02D, 0.0D);
      matrixStackIn.scale(1.01f, 1.0f, 1.01f);
      base.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.pop();
    }
    if (headModel != null && headTexture != null && buffer != null) {
      IVertexBuilder headBuilder = ItemRenderer.getArmorVertexBuilder(buffer, RenderType.getEntityCutoutNoCullZOffset(headTexture), false, hasGlint);
      boolean needsPush = this.isChild || (this.isSneak && base != null);
      if (needsPush) {
        matrixStackIn.push();
        if (isChild) {
          matrixStackIn.scale(0.75F, 0.75F, 0.75F);
          matrixStackIn.translate(0.0D, 1.0D, 0.0D);
        }
        if (isSneak && base != null) {
          matrixStackIn.translate(0, base.bipedHead.rotationPointY / 16.0F, 0);
        }
      }
      headModel.func_225603_a_(0, this.bipedHead.rotateAngleY * 180f / (float)(Math.PI), this.bipedHead.rotateAngleX * 180f / (float)(Math.PI));
      headModel.render(matrixStackIn, headBuilder, packedLightIn, packedOverlayIn, red, green * 0.5f, blue, alpha * 0.8f);
      if (needsPush) {
        matrixStackIn.pop();
      }
    }
  }

  /** Called before the model is rendered to set the base model and the tool stack data */
  public void setToolAndBase(ItemStack stack, BipedModel<?> base) {
    this.base = base;
    MaterialId materialId = MaterialIdNBT.from(stack).getMaterial(0);
    if (!materialId.equals(IMaterial.UNKNOWN_ID)) {
      Pair<ResourceLocation, GenericHeadModel> pair = HEAD_MODELS.get(materialId);
      if (pair != null) {
        headTexture = pair.getFirst();
        headModel = pair.getSecond();
        return;
      }
    }
    headTexture = null;
    headModel = null;
    hasGlint = stack.hasEffect();
  }


  /* Head models */

  /** Map of all skull models */
  private static final Map<MaterialId,Pair<ResourceLocation,GenericHeadModel>> HEAD_MODELS = new HashMap<>();

  /** Registers a head model and texture, most of these are registered via world as it already had the needed models setup */
  public static void registerHeadModel(MaterialId materialId, GenericHeadModel headModel, ResourceLocation texture) {
    if (HEAD_MODELS.containsKey(materialId)) {
      throw new IllegalArgumentException("Duplicate head model " + materialId);
    }
    HEAD_MODELS.put(materialId, Pair.of(texture, headModel));
  }
}
