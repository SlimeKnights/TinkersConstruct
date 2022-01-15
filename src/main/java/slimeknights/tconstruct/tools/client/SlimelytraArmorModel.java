package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.ElytraModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.data.ISafeManagerReloadListener;
import slimeknights.tconstruct.tools.data.material.MaterialIds;
import slimeknights.tconstruct.tools.item.SlimesuitItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/** Model to render elytra wings as a chestplate */
public class SlimelytraArmorModel<T extends LivingEntity> extends ArmorModelWrapper<T> {
  private static final SlimelytraArmorModel<LivingEntity> INSTANCE = new SlimelytraArmorModel<>();
  /** Cache of wing texture names */
  private static final Map<String,RenderType> WING_RENDER_CACHE = new HashMap<>();
  /** Function to get leg names */
  private static final Function<String,RenderType> WING_GETTER = mat -> RenderType.getEntityCutoutNoCullZOffset(new ResourceLocation(SlimesuitItem.makeArmorTexture(mat, "wings")));

  /** Called on resource reload to clear the model caches */
  public static final ISafeManagerReloadListener RELOAD_LISTENER = manager -> WING_RENDER_CACHE.clear();

  /**
   * Gets the model for a given entity
   * @param living     Entity instance
   * @param baseModel  Base model
   * @param <A>  Model instance
   * @return  Model for the entity
   */
  @SuppressWarnings("unchecked")
  public static <A extends BipedModel<?>> A getModel(LivingEntity living, ItemStack stack, A baseModel) {
    INSTANCE.setupAnim(living, stack, baseModel);
    return (A) INSTANCE;
  }

  /** Base elytra model to render */
  private final ElytraModel<T> elytraModel = new ElytraModel<>();
  /** Texture to use when rendering the elytra */
  private String material = MaterialIds.enderslime.toString();

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    if (SlimeskullArmorModel.buffer != null) {
      this.copyModelAttributesTo(elytraModel);
      matrixStackIn.push();
      matrixStackIn.translate(0.0D, 0.0D, 0.125D);
      IVertexBuilder elytraBuffer = buffer.getBuffer(WING_RENDER_CACHE.computeIfAbsent(material, WING_GETTER));
      elytraModel.render(matrixStackIn, elytraBuffer, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.pop();
    }
  }

  /** Called before the model is rendered to set the base model and the elytra entity data */
  public void setupAnim(T entity, ItemStack stack, BipedModel<?> base) {
    this.elytraModel.setRotationAngles(entity, 0, 0, 0, 0, 0);
    this.base = base;
    this.material = SlimesuitItem.getMaterial(stack);
  }
}
