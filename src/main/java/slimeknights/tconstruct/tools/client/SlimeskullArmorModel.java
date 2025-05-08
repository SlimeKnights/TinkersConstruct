package slimeknights.tconstruct.tools.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.listener.ISafeManagerReloadListener;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModel;
import slimeknights.tconstruct.library.client.armor.MultilayerArmorModel;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.library.utils.SimpleCache;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

/** Model to render a slimeskull helmet with both the helmet and skull */
public class SlimeskullArmorModel extends MultilayerArmorModel {
  /** Singleton model instance, all data is passed in via setters */
  public static final SlimeskullArmorModel INSTANCE = new SlimeskullArmorModel();
  /** Cache of colors for materials */
  private static final SimpleCache<String,Integer> MATERIAL_COLOR_CACHE = new SimpleCache<>(mat ->
    Optional.ofNullable(MaterialVariantId.tryParse(mat))
            .flatMap(MaterialRenderInfoLoader.INSTANCE::getRenderInfo)
            .map(MaterialRenderInfo::vertexColor)
            .orElse(-1));
  /** Listener to clear caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    HEAD_MODELS = null;
    MATERIAL_COLOR_CACHE.clear();
  };

  /** Head to render under the helmet */
  @Nullable
  private ResourceLocation headTexture;
  /** Tint color for the head */
  private int headColor = -1;
  /** Texture for the head */
  @Nullable
  private SkullModelBase headModel;

  private SlimeskullArmorModel() {}

  /** Prepares the model */
  public Model setup(LivingEntity living, ItemStack stack, HumanoidModel<?> base, ArmorModel model) {
    super.setup(living, stack, EquipmentSlot.HEAD, base, model);
    MaterialId materialId = MaterialIdNBT.from(stack).getMaterial(0).getId();
    if (!materialId.equals(IMaterial.UNKNOWN_ID)) {
      SkullModelBase skull = getHeadModel(materialId);
      ResourceLocation texture = HEAD_TEXTURES.get(materialId);
      if (skull != null && texture != null) {
        headModel = skull;
        headTexture = texture;
        // determine the color to tint the helmet, fallback to enderslime if missing
        String embellishmentMaterial = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
        if (embellishmentMaterial.isEmpty()) {
          embellishmentMaterial = MaterialIds.enderslime.toString();
        }
        headColor = MATERIAL_COLOR_CACHE.apply(embellishmentMaterial);
        return this;
      }
    }
    headTexture = null;
    headModel = null;
    headColor = -1;
    return this;
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null && buffer != null) {
      if (model != ArmorModel.EMPTY) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, base.young ? -0.015D : -0.02D, 0.0D);
        matrixStackIn.scale(1.01f, 1.1f, 1.01f);
        super.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
      }
      if (headModel != null && headTexture != null) {
        VertexConsumer heaadBuffer = ItemRenderer.getArmorFoilBuffer(buffer, RenderType.entityCutoutNoCullZOffset(headTexture), false, hasGlint);
        matrixStackIn.pushPose();
        if (base.crouching) {
          matrixStackIn.translate(0, base.head.y / 16.0F, 0);
        }
        if (base.young) {
          matrixStackIn.scale(0.85F, 0.85F, 0.85F);
          matrixStackIn.translate(0.0D, 1.0D, 0.0D);
        } else {
          matrixStackIn.scale(1.115f, 1.115f, 1.115f);
        }
        headModel.setupAnim(0, base.head.yRot * 180f / (float)(Math.PI), base.head.xRot * 180f / (float)(Math.PI));
        renderColored(headModel, matrixStackIn, heaadBuffer, packedLightIn, packedOverlayIn, headColor, red, green, blue, alpha);
        matrixStackIn.popPose();
      }
    }
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
