package slimeknights.tconstruct.library.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.ArmorTexture;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier.TextureType;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.armor.ModifiableArmorItem;

import javax.annotation.Nullable;

/** Common shared logic for material armor models */
public abstract class AbstractArmorModel extends Model {
  /** Base model instance for rendering */
  @Nullable
  protected HumanoidModel<?> base;
  /** If true, applies the enchantment glint to extra layers */
  protected boolean hasGlint = false;
  /** If true, uses the legs texture */
  protected TextureType textureType = TextureType.ARMOR;

  protected boolean hasWings = false;

  protected AbstractArmorModel() {
    super(RenderType::entityCutoutNoCull);
  }

  /** Sets up the model given the passed arguments */
  protected void setup(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> base) {
    this.base = base;
    this.hasGlint = stack.hasFoil();
    this.textureType = TextureType.fromSlot(slot);
    if (slot == EquipmentSlot.CHEST) {
      this.hasWings = ModifierUtil.checkVolatileFlag(stack, ModifiableArmorItem.ELYTRA);
      if (hasWings) {
        ElytraModel<LivingEntity> wings = getWings();
        wings.setupAnim(living, 0, 0, 0, 0, 0);
        copyProperties(base, wings);
      }
    } else {
      hasWings = false;
    }
  }

  /** Renders a colored model */
  public static void renderColored(Model model, PoseStack matrices, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, int color, float red, float green, float blue, float alpha) {
    if (color != -1) {
      alpha *= (float)(color >> 24 & 255) / 255.0F;
      red *= (float)(color >> 16 & 255) / 255.0F;
      green *= (float)(color >> 8 & 255) / 255.0F;
      blue *= (float)(color & 255) / 255.0F;
    }
    model.renderToBuffer(matrices, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
  }

  /** Renders the wings layer */
  protected void renderWings(PoseStack matrices, int packedLightIn, int packedOverlayIn, ArmorTexture texture, float red, float green, float blue, float alpha, boolean hasGlint) {
    matrices.pushPose();
    matrices.translate(0.0D, 0.0D, 0.125D);
    assert buffer != null;
    texture.renderTexture(getWings(), matrices, buffer, packedLightIn, packedOverlayIn, red, green, blue, alpha, hasGlint);
    matrices.popPose();
  }


  /* Helpers */

  /** Buffer from the render living event, stored as we lose access to it later */
  @Nullable
  public static MultiBufferSource buffer;

  /** Initializes the wrapper */
  public static void init() {
    // register listeners to set and clear the buffer
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Pre.class, event -> buffer = event.getMultiBufferSource());
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Post.class, event -> buffer = null);
  }

  /** Wings model to render */
  @Nullable
  private static ElytraModel<LivingEntity> wingsModel;

  /** Gets or creates the elytra model */
  private ElytraModel<LivingEntity> getWings() {
    if (wingsModel == null) {
      wingsModel = new ElytraModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA));
    }
    return wingsModel;
  }

  /** Handles the unchecked cast to copy entity model properties */
  @SuppressWarnings("unchecked")
  public static <T extends LivingEntity> void copyProperties(EntityModel<T> base, EntityModel<?> other) {
    base.copyPropertiesTo((EntityModel<T>)other);
  }
}
