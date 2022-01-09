package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlateArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Cache of models for each entity type */
  private static final Map<HumanoidModel<?>,PlateArmorModel<?>> MODEL_CACHE = new HashMap<>();
  /** Cache of armor render types */
  private static final Map<String,RenderType> ARMOR_RENDER_CACHE = new HashMap<>();
  /** Cache of leg render types */
  private static final Map<String,RenderType> LEG_RENDER_CACHE = new HashMap<>();
  /** Gets the armor texture for a material */
  private static ResourceLocation getArmorTexture(String material, int variant) {
    ResourceLocation location = ResourceLocation.tryParse(material);
    if (location == null) {
      location = MaterialIds.cobalt;
    }
    return TConstruct.getResource(String.format("textures/models/armor/plate/layer_%d_%s_%s.png", variant, location.getNamespace(), location.getPath()));
  }
  /** Function to get armor render type */
  private static final Function<String,RenderType> ARMOR_GETTER = mat -> RenderType.entityCutoutNoCullZOffset(getArmorTexture(mat, 1));
  /** Function to get armor render type */
  private static final Function<String,RenderType> LEG_GETTER = mat -> RenderType.entityCutoutNoCullZOffset(getArmorTexture(mat, 2));

  /** Listener to clear caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    MODEL_CACHE.clear();
    ARMOR_RENDER_CACHE.clear();
    LEG_RENDER_CACHE.clear();
  };

  /**
   * Gets the model for a given entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends HumanoidModel<?>> A getModel(ItemStack stack, EquipmentSlot slot, A baseModel) {
    PlateArmorModel<?> model = MODEL_CACHE.computeIfAbsent(baseModel, PlateArmorModel::new);
    model.setup(stack, slot);
    return (A) model;
  }

  private String material = "";
  private boolean isLegs = false;
  public PlateArmorModel(HumanoidModel<T> base) {
    super(base);
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    super.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    if (!material.isEmpty() && buffer != null) {
      VertexConsumer overlayBuffer = buffer.getBuffer(isLegs ? LEG_RENDER_CACHE.computeIfAbsent(material, LEG_GETTER) : ARMOR_RENDER_CACHE.computeIfAbsent(material, ARMOR_GETTER));
      super.renderToBuffer(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
  }

  private void setup(ItemStack stack, EquipmentSlot slot) {
    material = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
    isLegs = slot == EquipmentSlot.LEGS;
  }
}
