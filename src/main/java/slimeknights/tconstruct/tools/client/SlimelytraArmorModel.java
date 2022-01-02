package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.tools.ArmorDefinitions;

import java.util.HashMap;
import java.util.Map;

/** Model to render elytra wings as a chestplate */
public class SlimelytraArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  /** Cache of models for each entity type */
  private static final Map<HumanoidModel<?>,SlimelytraArmorModel<?>> MODEL_CACHE = new HashMap<>();
  /** Texture to use in rendering the elytra */
  private static final ResourceLocation ELYTRA_TEXTURE;
  static {
    ResourceLocation materialName = ArmorDefinitions.SLIMESUIT.getNameLocation();
    ELYTRA_TEXTURE = new ResourceLocation(materialName.getNamespace(), String.format("textures/models/armor/%s_wings.png", materialName.getPath()));
  }

  /** Called on resource reload to clear the model caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> MODEL_CACHE.clear();

  /**
   * Gets the model for a given entity
   * @param living     Entity instance
   * @param baseModel  Base model
   * @param <A>  Model instance
   * @return  Model for the entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends HumanoidModel<?>> A getModel(LivingEntity living, A baseModel) {
    SlimelytraArmorModel<?> model = MODEL_CACHE.computeIfAbsent(baseModel, SlimelytraArmorModel::new);
    model.setupAnim(living);
    return (A) model;
  }

  /** Base elytra model to render */
  private final ElytraModel<T> elytraModel;

  public SlimelytraArmorModel(HumanoidModel<T> base) {
    super(base);
    this.elytraModel = new ElytraModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.ELYTRA));
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    super.renderToBuffer(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    if (buffer != null) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0D, 0.0D, 0.125D);
      VertexConsumer elytraBuffer = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(ELYTRA_TEXTURE));
      elytraModel.renderToBuffer(matrixStackIn, elytraBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.popPose();
    }
  }

  /** Called before the model is rendered to set the base model and the elytra entity data */
  @SuppressWarnings("unchecked")
  private void setupAnim(LivingEntity living) {
    elytraModel.setupAnim((T)living, 0, 0, 0, 0, 0);
  }

  @Override
  public void setAllVisible(boolean visible) {
    super.setAllVisible(visible);
    // attributes are copied to elytra through another model's setModelAttributes, this is the best hook to copy them to elytra
    this.copyPropertiesTo(elytraModel);
  }
}
