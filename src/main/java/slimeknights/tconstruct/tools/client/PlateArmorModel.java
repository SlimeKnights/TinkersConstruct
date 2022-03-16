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
import slimeknights.tconstruct.library.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlateArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Singleton instance */
  private static final PlateArmorModel<LivingEntity> INSTANCE = new PlateArmorModel<>();
  /** Cache of armor render types */
  private static final Map<String,RenderType> ARMOR_RENDER_CACHE = new HashMap<>();
  /** Cache of leg render types */
  private static final Map<String,RenderType> LEG_RENDER_CACHE = new HashMap<>();
  /** Gets the armor texture for a material */
  private static ResourceLocation getArmorTexture(String material, int variant) {
    ResourceLocation location = ResourceLocation.tryCreate(material);
    if (location == null) {
      location = MaterialIds.cobalt;
    }
    return TConstruct.getResource(String.format("textures/models/armor/plate/layer_%d_%s_%s.png", variant, location.getNamespace(), location.getPath()));
  }
  /** Function to get armor render type */
  private static final Function<String,RenderType> ARMOR_GETTER = mat -> RenderType.getEntityCutoutNoCullZOffset(getArmorTexture(mat, 1));
  /** Function to get armor render type */
  private static final Function<String,RenderType> LEG_GETTER = mat -> RenderType.getEntityCutoutNoCullZOffset(getArmorTexture(mat, 2));

  /** Listener to clear caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    ARMOR_RENDER_CACHE.clear();
    LEG_RENDER_CACHE.clear();
  };

  /**
   * Gets the model for a given entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends BipedModel<?>> A getModel(ItemStack stack, EquipmentSlotType slot, A baseModel) {
    INSTANCE.setup(stack, slot, baseModel);
    return (A) INSTANCE;
  }

  private String material = "";
  private boolean isLegs = false;
  private boolean hasGlint = false;

  @Override
  public void render(MatrixStack matrices, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      copyToBase();
      base.render(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if (!material.isEmpty() && buffer != null) {
        IVertexBuilder overlayBuffer = ItemRenderer.getArmorVertexBuilder(buffer, isLegs ? LEG_RENDER_CACHE.computeIfAbsent(material, LEG_GETTER) : ARMOR_RENDER_CACHE.computeIfAbsent(material, ARMOR_GETTER), false, hasGlint);
        base.render(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      }
    }
  }

  private void setup(ItemStack stack, EquipmentSlotType slot, BipedModel<?> base) {
    this.base = base;
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.get()) > 0) {
      this.material = MaterialIds.gold.toString();
    } else {
      this.material = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
    }
    this.isLegs = slot == EquipmentSlotType.LEGS;
    hasGlint = stack.hasEffect();
  }
}
