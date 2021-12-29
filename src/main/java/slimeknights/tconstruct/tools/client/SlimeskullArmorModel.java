package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/** Model to render a slimeskull helmet with both the helmet and skull */
public class SlimeskullArmorModel<T extends LivingEntity> extends HumanoidModel<T> {
  /** Model instance */
  public static final SlimeskullArmorModel<LivingEntity> INSTANCE = new SlimeskullArmorModel<>();
  /** Map of all skull models */
  private static final Map<MaterialId,Pair<ResourceLocation,SkullModel>> HEAD_MODELS = new HashMap<>();

  /** Registers a head model and texture, most of these are registered via world as it already had the needed models setup */
  public static void registerHeadModel(MaterialId materialId, SkullModel headModel, ResourceLocation texture) {
    if (HEAD_MODELS.containsKey(materialId)) {
      throw new IllegalArgumentException("Duplicate head model " + materialId);
    }
    HEAD_MODELS.put(materialId, Pair.of(texture, headModel));
  }

  /** Buffer from the render living event, stored as we lose access to it later */
  @Nullable
  static MultiBufferSource buffer;
  /** Original helmet model to render */
  @Nullable
  private HumanoidModel<?> base;
  /** Head to render under the helmet */
  @Nullable
  private ResourceLocation headTexture;
  /** Texture for the head */
  @Nullable
  private SkullModel headModel;

  // TODO: need to fix
  private SlimeskullArmorModel() {
    //super(1.0f);
    super(null); // TODO
    // register listeners to set and clear the buffer
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Pre.class, event -> buffer = event.getMultiBufferSource());
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Post.class, event -> buffer = null);
  }

  @Override
  public void renderToBuffer(PoseStack matrixStackIn, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      matrixStackIn.pushPose();
      matrixStackIn.translate(0.0D, this.young ? -0.015D : -0.02D, 0.0D);
      matrixStackIn.scale(1.01f, 1.0f, 1.01f);
      base.renderToBuffer(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.popPose();
    }
    if (headModel != null && headTexture != null && buffer != null) {
      VertexConsumer headBuilder = buffer.getBuffer(RenderType.entityCutoutNoCullZOffset(headTexture));
      boolean needsPush = this.young || (this.crouching && base != null);
      if (needsPush) {
        matrixStackIn.pushPose();
        if (young) {
          matrixStackIn.scale(0.75F, 0.75F, 0.75F);
          matrixStackIn.translate(0.0D, 1.0D, 0.0D);
        }
        if (crouching && base != null) {
          matrixStackIn.translate(0, base.head.y / 16.0F, 0);
        }
      }
      headModel.setupAnim(0, this.head.yRot * 180f / (float)(Math.PI), this.head.xRot * 180f / (float)(Math.PI));
      headModel.renderToBuffer(matrixStackIn, headBuilder, packedLightIn, packedOverlayIn, red, green * 0.5f, blue, alpha * 0.8f);
      if (needsPush) {
        matrixStackIn.popPose();
      }
    }
  }

  /** Called before the model is rendered to set the base model and the tool stack data */
  public void setToolAndBase(ItemStack stack, HumanoidModel<?> base) {
    this.base = base;
    MaterialId materialId = MaterialIdNBT.from(stack).getMaterial(0);
    if (!materialId.equals(IMaterial.UNKNOWN_ID)) {
      Pair<ResourceLocation, SkullModel> pair = HEAD_MODELS.get(materialId);
      if (pair != null) {
        headTexture = pair.getFirst();
        headModel = pair.getSecond();
        return;
      }
    }
    headTexture = null;
    headModel = null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setAllVisible(boolean visible) {
    if (base != null) {
      base.setAllVisible(false);
      base.head.visible = true;
      base.hat.visible = true;
      // attributes are copied to skull through another model's setModelAttributes, this is the best hook to copy them to the model
      this.copyPropertiesTo((HumanoidModel<T>)base);
    }
  }
}
