package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

public class TravelersGearModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  private static final TravelersGearModel<LivingEntity> INSTANCE = new TravelersGearModel<>();
  /** Overlay texture for armor */
  private static final ResourceLocation OVERLAY_ARMOR = TConstruct.getResource("textures/models/armor/travelers_overlay_1.png");
  /** Overlay texture for leggings */
  private static final ResourceLocation OVERLAY_LEGS = TConstruct.getResource("textures/models/armor/travelers_overlay_2.png");

  /**
   * Gets the model for a given entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends BipedModel<?>> A getModel(ItemStack stack, EquipmentSlotType slot, A baseModel) {
    INSTANCE.setupModel(stack, slot, baseModel);
    return (A) INSTANCE;
  }

  private int color = -1;
  private boolean isLegs = false;
  private boolean hasGlint = false;

  @Override
  public void render(MatrixStack matrices, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      copyToBase();
      base.render(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if (color != -1 && buffer != null) {
        float newRed = (float)(color >> 16 & 255) / 255.0F;
        float newGreen = (float)(color >> 8 & 255) / 255.0F;
        float newBlue = (float)(color & 255) / 255.0F;
        IVertexBuilder overlayBuffer = ItemRenderer.getArmorVertexBuilder(buffer, RenderType.getEntityCutoutNoCullZOffset(isLegs ? OVERLAY_LEGS : OVERLAY_ARMOR), false, hasGlint);
        base.render(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red * newRed, green * newGreen, blue * newBlue, alpha);
      }
    }
  }

  private void setupModel(ItemStack stack, EquipmentSlotType slot, BipedModel<?> base) {
    this.base = base;
    this.color = ModifierUtil.getPersistentInt(stack, TinkerModifiers.dyed.getId(), -1);
    this.isLegs = slot == EquipmentSlotType.LEGS;
    this.hasGlint = stack.hasEffect();
  }
}
