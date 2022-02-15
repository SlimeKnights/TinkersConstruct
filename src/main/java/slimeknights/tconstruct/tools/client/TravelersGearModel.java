package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

public class TravelersGearModel extends Model {
  /** Singleton model instance, all data is passed in via setters */
  private static final TravelersGearModel INSTANCE = new TravelersGearModel();
  /** Overlay texture for armor */
  private static final ResourceLocation OVERLAY_ARMOR = TConstruct.getResource("textures/models/armor/travelers_overlay_1.png");
  /** Overlay texture for leggings */
  private static final ResourceLocation OVERLAY_LEGS = TConstruct.getResource("textures/models/armor/travelers_overlay_2.png");

  /**
   * Gets the model for a given entity
   */
  public static Model getModel(ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel) {
    INSTANCE.setup(baseModel, stack, slot);
    return INSTANCE;
  }

  @Nullable
  private HumanoidModel<?> base;
  private int color = -1;
  private boolean isLegs = false;
  /** If true, applies the enchantment glint to extra layers */
  private boolean hasGlint = false;
  public TravelersGearModel() {
    super(RenderType::entityCutoutNoCull);
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      base.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if (color != -1 && ArmorModelHelper.buffer != null) {
        float newRed = (float)(color >> 16 & 255) / 255.0F;
        float newGreen = (float)(color >> 8 & 255) / 255.0F;
        float newBlue = (float)(color & 255) / 255.0F;
        VertexConsumer overlayBuffer = ItemRenderer.getArmorFoilBuffer(ArmorModelHelper.buffer, RenderType.entityCutoutNoCullZOffset(isLegs ? OVERLAY_LEGS : OVERLAY_ARMOR), false, hasGlint);
        base.renderToBuffer(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red * newRed, green * newGreen, blue * newBlue, alpha);
      }
    }
  }

  private void setup(HumanoidModel<?> base, ItemStack stack, EquipmentSlot slot) {
    this.base = base;
    color = ModifierUtil.getPersistentInt(stack, TinkerModifiers.dyed.getId(), -1);
    isLegs = slot == EquipmentSlot.LEGS;
    hasGlint = stack.hasFoil();
  }
}
