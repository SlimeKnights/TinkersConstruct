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

import java.util.HashMap;
import java.util.Map;

public class TravelersGearModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Cache of models for each entity type */
  private static final Map<HumanoidModel<?>,TravelersGearModel<?>> MODEL_CACHE = new HashMap<>();
  /** Overlay texture for armor */
  private static final ResourceLocation OVERLAY_ARMOR = TConstruct.getResource("textures/models/armor/travelers_overlay_1.png");
  /** Overlay texture for leggings */
  private static final ResourceLocation OVERLAY_LEGS = TConstruct.getResource("textures/models/armor/travelers_overlay_2.png");
  /** Called on resource reload to clear the model caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> MODEL_CACHE.clear();

  /**
   * Gets the model for a given entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends HumanoidModel<?>> A getModel(ItemStack stack, EquipmentSlot slot, A baseModel) {
    TravelersGearModel<?> model = MODEL_CACHE.computeIfAbsent(baseModel, TravelersGearModel::new);
    model.setupModel(stack, slot);
    return (A) model;
  }

  private int color = -1;
  private boolean isLegs = false;
  public TravelersGearModel(HumanoidModel<T> base) {
    super(base);
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    super.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    if (color != -1 && buffer != null) {
      float newRed = (float)(color >> 16 & 255) / 255.0F;
      float newGreen = (float)(color >> 8 & 255) / 255.0F;
      float newBlue = (float)(color & 255) / 255.0F;
      VertexConsumer overlayBuffer = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(isLegs ? OVERLAY_LEGS : OVERLAY_ARMOR));
      super.renderToBuffer(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red * newRed, green * newGreen, blue * newBlue, alpha);
    }
  }

  private void setupModel(ItemStack stack, EquipmentSlot slot) {
    color = ModifierUtil.getPersistentInt(stack, TinkerModifiers.dyed.getId(), -1);
    isLegs = slot == EquipmentSlot.LEGS;
  }
}
