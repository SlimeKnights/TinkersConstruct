package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.SlimesuitItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Model to render elytra wings as a chestplate */
public class SlimelytraArmorModel extends Model {
  /** Singleton model instance, all data is passed in via setters */
  private static final SlimelytraArmorModel INSTANCE = new SlimelytraArmorModel();
  /** Cache of wing texture names */
  private static final Map<String,RenderType> WING_RENDER_CACHE = new HashMap<>();
  /** Function to get leg names */
  private static final Function<String,RenderType> WING_GETTER = mat -> RenderType.entityCutoutNoCullZOffset(new ResourceLocation(SlimesuitItem.makeArmorTexture(mat, "wings")));

  /** Called on resource reload to clear the model caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> {
    INSTANCE.elytraModel = null;
    WING_RENDER_CACHE.clear();
  };

  /**
   * Gets the model for a given entity
   * @param living     Entity instance
   * @param baseModel  Base model
   * @return  Model for the entity
   */
  public static Model getModel(LivingEntity living, ItemStack stack, HumanoidModel<?> baseModel) {
    INSTANCE.setup(baseModel, living, stack);
    return INSTANCE;
  }

  /** Base elytra model to render */
  @Nullable
  private ElytraModel<LivingEntity> elytraModel;
  /** Base armor model to render */
  @Nullable
  private HumanoidModel<?> base;
  /** Material name for rendering */
  private String material = MaterialIds.enderslime.toString();
  /** If true, applies the enchantment glint to extra layers */
  private boolean hasGlint = false;

  public SlimelytraArmorModel() {
    super(RenderType::entityCutoutNoCull);
  }

  private ElytraModel<LivingEntity> getElytraModel() {
    if (elytraModel == null) {
      elytraModel = new ElytraModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA));
    }
    return elytraModel;
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      base.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      if (ArmorModelHelper.buffer != null) {
        matrixStackIn.pushPose();
        matrixStackIn.translate(0.0D, 0.0D, 0.125D);
        VertexConsumer elytraBuffer = ItemRenderer.getArmorFoilBuffer(ArmorModelHelper.buffer, WING_RENDER_CACHE.computeIfAbsent(material, WING_GETTER), false, hasGlint);
        getElytraModel().renderToBuffer(matrixStackIn, elytraBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        matrixStackIn.popPose();
      }
    }
  }

  /** Called before the model is rendered to set the base model and the elytra entity data */
  private void setup(HumanoidModel<?> base, LivingEntity living, ItemStack stack) {
    this.base = base;
    ElytraModel<LivingEntity> elytraModel = getElytraModel();
    elytraModel.setupAnim(living, 0, 0, 0, 0, 0);
    ArmorModelHelper.copyProperties(base, elytraModel);
    material = SlimesuitItem.getMaterial(stack);
    hasGlint = stack.hasFoil();
  }
}
