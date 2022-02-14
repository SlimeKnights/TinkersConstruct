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
import slimeknights.mantle.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class PlateArmorModel extends Model {
  /** Singleton model instance, all data is passed in via setters */
  private static final PlateArmorModel INSTANCE = new PlateArmorModel();

  /** Cache of armor render types */
  private static final Map<String,RenderType> ARMOR_RENDER_CACHE = new HashMap<>();
  /** Cache of leg render types */
  private static final Map<String,RenderType> LEG_RENDER_CACHE = new HashMap<>();

  /** Gets the armor texture for a material */
  private static ResourceLocation getArmorTexture(String material, int variant) {
    MaterialVariantId variantId = MaterialVariantId.tryParse(material);
    if (variantId == null) {
      variantId = MaterialIds.cobalt;
    }
    ResourceLocation location = variantId.getLocation('_');
    return TConstruct.getResource(String.format("textures/models/armor/plate/layer_%d_%s_%s.png", variant, location.getNamespace(), location.getPath()));
  }
  /** Function to get armor render type */
  private static final Function<String,RenderType> ARMOR_GETTER = mat -> RenderType.entityCutoutNoCullZOffset(getArmorTexture(mat, 1));
  /** Function to get armor render type */
  private static final Function<String,RenderType> LEG_GETTER = mat -> RenderType.entityCutoutNoCullZOffset(getArmorTexture(mat, 2));

  /** Listener to clear caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    ARMOR_RENDER_CACHE.clear();
    LEG_RENDER_CACHE.clear();
  };

  /**
   * Gets the model for a given entity
   */
  public static Model getModel(ItemStack stack, EquipmentSlot slot, HumanoidModel<?> baseModel) {
    INSTANCE.setup(baseModel, stack, slot);
    return INSTANCE;
  }

  @Nullable
  private HumanoidModel<?> base;
  private String material = "";
  private boolean isLegs = false;
  /** If true, applies the enchantment glint to extra layers */
  private boolean hasGlint = false;
  public PlateArmorModel() {
    super(RenderType::entityCutoutNoCull);
  }

  @Override
  public void renderToBuffer(PoseStack matrices, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (this.base != null) {
      base.renderToBuffer(matrices, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if (!material.isEmpty() && ArmorModelHelper.buffer != null) {
        VertexConsumer overlayBuffer = ItemRenderer.getArmorFoilBuffer(ArmorModelHelper.buffer, isLegs ? LEG_RENDER_CACHE.computeIfAbsent(material, LEG_GETTER) : ARMOR_RENDER_CACHE.computeIfAbsent(material, ARMOR_GETTER), false, hasGlint);
        base.renderToBuffer(matrices, overlayBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      }
    }
  }

  private void setup(HumanoidModel<?> base, ItemStack stack, EquipmentSlot slot) {
    this.base = base;
    if (ModifierUtil.getModifierLevel(stack, TinkerModifiers.golden.get()) > 0) {
      material = MaterialIds.gold.toString();
    } else {
      material = ModifierUtil.getPersistentString(stack, TinkerModifiers.embellishment.getId());
    }
    isLegs = slot == EquipmentSlot.LEGS;
    hasGlint = stack.hasFoil();
  }
}
