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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TravelersGearModel extends Model {
  /** Singleton model instance, all data is passed in via setters */
  private static final Map<ResourceLocation,TravelersGearModel> MODELS = new HashMap<>();
  /** Cached constructor to not need to create each tick */
  private static final Function<ResourceLocation,TravelersGearModel> CONSTRUCTOR = TravelersGearModel::new;
  /** default name */
  private static final ResourceLocation TRAVELERS = TConstruct.getResource("travelers");

  /**
   * Gets the model for a given entity
   */
  public static Model getModel(ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel, ResourceLocation name, boolean forceOverlay) {
    TravelersGearModel model = MODELS.computeIfAbsent(name, CONSTRUCTOR);
    model.setup(baseModel, stack, slot, forceOverlay);
    return model;
  }

  /** Gets the model for traveler */
  public static Model getModel(ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel) {
    return getModel(stack, slot, baseModel, TRAVELERS, false);
  }

  private final ResourceLocation overlayArmor;
  private final ResourceLocation overlayLegs;
  @Nullable
  private HumanoidModel<?> base;
  private boolean forceOverlay = false;
  private int color = -1;
  private boolean isLegs = false;
  /** If true, applies the enchantment glint to extra layers */
  private boolean hasGlint = false;
  public TravelersGearModel(ResourceLocation name) {
    super(RenderType::entityCutoutNoCull);
    this.overlayArmor = new ResourceLocation(name.getNamespace(), "textures/models/armor/" + name.getPath() + "_overlay_1.png");
    this.overlayLegs = new ResourceLocation(name.getNamespace(), "textures/models/armor/" + name.getPath() + "_overlay_2.png");
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      base.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if ((forceOverlay || color != -1) && ArmorModelHelper.buffer != null) {
        float newRed = (float)(color >> 16 & 255) / 255.0F;
        float newGreen = (float)(color >> 8 & 255) / 255.0F;
        float newBlue = (float)(color & 255) / 255.0F;
        VertexConsumer overlayBuffer = ItemRenderer.getArmorFoilBuffer(ArmorModelHelper.buffer, RenderType.entityCutoutNoCullZOffset(isLegs ? overlayLegs : overlayArmor), false, hasGlint);
        base.renderToBuffer(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red * newRed, green * newGreen, blue * newBlue, alpha);
      }
    }
  }

  private void setup(HumanoidModel<?> base, ItemStack stack, EquipmentSlot slot, boolean forceOverlay) {
    this.base = base;
    this.forceOverlay = forceOverlay;
    color = ModifierUtil.getPersistentInt(stack, TinkerModifiers.dyed.getId(), -1);
    isLegs = slot == EquipmentSlot.LEGS;
    hasGlint = stack.hasFoil();
  }
}
