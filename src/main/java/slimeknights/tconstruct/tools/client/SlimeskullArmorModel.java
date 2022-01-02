package slimeknights.tconstruct.tools.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

/** Model to render a slimeskull helmet with both the helmet and skull */
public class SlimeskullArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Cache of models for each entity type */
  private static final Map<HumanoidModel<?>,SlimeskullArmorModel<?>> MODEL_CACHE = new HashMap<>();

  /** Listener to clear caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    HEAD_MODELS = null;
    MODEL_CACHE.clear();
  };

  /**
   * Gets the model for a given entity
   * @param stack      Armor stack object
   * @param baseModel  Base model
   * @param <A>  Model instance
   * @return  Model for the entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends HumanoidModel<?>> A getModel(ItemStack stack, A baseModel) {
    SlimeskullArmorModel<?> model = MODEL_CACHE.computeIfAbsent(baseModel, SlimeskullArmorModel::new);
    model.setTool(stack);
    return (A) model;
  }

  /** Head to render under the helmet */
  @Nullable
  private ResourceLocation headTexture;
  /** Texture for the head */
  @Nullable
  private SkullModelBase headModel;

  private SlimeskullArmorModel(HumanoidModel<T> base) {
    super(base);
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    matrixStackIn.pushPose();
    matrixStackIn.translate(0.0D, this.young ? -0.015D : -0.02D, 0.0D);
    matrixStackIn.scale(1.01f, 1.0f, 1.01f);
    super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    matrixStackIn.popPose();

    if (headModel != null && headTexture != null && buffer != null) {
      VertexConsumer headBuilder = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(headTexture));
      boolean needsPush = this.young || this.crouching;
      if (needsPush) {
        matrixStackIn.pushPose();
        if (young) {
          matrixStackIn.scale(0.75F, 0.75F, 0.75F);
          matrixStackIn.translate(0.0D, 1.0D, 0.0D);
        }
        if (crouching) {
          matrixStackIn.translate(0, head.y / 16.0F, 0);
        }
      }
      headModel.setupAnim(0, this.head.yRot * 180f / (float)(Math.PI), this.head.xRot * 180f / (float)(Math.PI));
      headModel.renderToBuffer(matrixStackIn, headBuilder, packedLightIn, packedOverlayIn, red, green * 0.5f, blue, alpha * 0.8f);
      if (needsPush) {
        matrixStackIn.popPose();
      }
    }
  }

  /** Called before the model is rendered to set the base model and the tool stack data */
  private void setTool(ItemStack stack) {
    MaterialId materialId = MaterialIdNBT.from(stack).getMaterial(0);
    if (!materialId.equals(IMaterial.UNKNOWN_ID)) {
      SkullModelBase model = getHeadModel(materialId);
      ResourceLocation texture = HEAD_TEXTURES.get(materialId);
      if (model != null && texture != null) {
        headModel = model;
        headTexture = texture;
        return;
      }
    }
    headTexture = null;
    headModel = null;
  }


  /* Head models */

  /** Map of all skull factories */
  private static final Map<MaterialId,Function<EntityModelSet,? extends SkullModelBase>> HEAD_MODEL_FACTORIES = new HashMap<>();
  /** Map of texture for the skull textures */
  private static final Map<MaterialId,ResourceLocation> HEAD_TEXTURES = new HashMap<>();

  /** Registers a head model and texture, using the default skull model */
  public static void registerHeadModel(MaterialId materialId, ModelLayerLocation headModel, ResourceLocation texture) {
    registerHeadModel(materialId, modelSet -> new SkullModel(modelSet.bakeLayer(headModel)), texture);
  }

  /** Registers a head model and texture, using a custom skull model */
  public static void registerHeadModel(MaterialId materialId, Function<EntityModelSet,? extends SkullModelBase> headFunction, ResourceLocation texture) {
    if (HEAD_MODEL_FACTORIES.containsKey(materialId)) {
      throw new IllegalArgumentException("Duplicate head model " + materialId);
    }
    HEAD_MODEL_FACTORIES.put(materialId, headFunction);
    HEAD_TEXTURES.put(materialId, texture);
  }

  /** Map of baked head models, if null it is not currently computed */
  private static Map<MaterialId, SkullModelBase> HEAD_MODELS;

  /** Gets the head model for the given material */
  @Nullable
  private static SkullModelBase getHeadModel(MaterialId materialId) {
    if (HEAD_MODELS == null) {
      // vanilla rebakes these a lot, so figure we should at least do it every resource reload
      EntityModelSet modelSet = Minecraft.getInstance().getEntityModels();
      ImmutableMap.Builder<MaterialId,SkullModelBase> models = ImmutableMap.builder();
      for (Entry<MaterialId,Function<EntityModelSet,? extends SkullModelBase>> entry : HEAD_MODEL_FACTORIES.entrySet()) {
        models.put(entry.getKey(), entry.getValue().apply(modelSet));
      }
      HEAD_MODELS = models.build();
    }
    return HEAD_MODELS.get(materialId);
  }
}
