package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
public class SlimeskullArmorModel<T extends LivingEntity> extends BipedModel<T> {
  /** Model instance */
  public static final SlimeskullArmorModel<LivingEntity> INSTANCE = new SlimeskullArmorModel<>();
  /** Map of all skull models */
  private static final Map<MaterialId,Pair<ResourceLocation,GenericHeadModel>> HEAD_MODELS = new HashMap<>();

  /** Registers a head model and texture, most of these are registered via world as it already had the needed models setup */
  public static void registerHeadModel(MaterialId materialId, GenericHeadModel headModel, ResourceLocation texture) {
    if (HEAD_MODELS.containsKey(materialId)) {
      throw new IllegalArgumentException("Duplicate head model " + materialId);
    }
    HEAD_MODELS.put(materialId, Pair.of(texture, headModel));
  }

  /** Buffer from the render living event, stored as we lose access to it later */
  @Nullable
  static IRenderTypeBuffer buffer;
  /** Original helmet model to render */
  @Nullable
  private BipedModel<?> base;
  /** Head to render under the helmet */
  @Nullable
  private ResourceLocation headTexture;
  /** Texture for the head */
  @Nullable
  private GenericHeadModel headModel;

  private SlimeskullArmorModel() {
    super(1.0f);
    // register listeners to set and clear the buffer
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Pre.class, event -> buffer = event.getBuffers());
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Post.class, event -> buffer = null);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      matrixStackIn.push();
      matrixStackIn.translate(0.0D, this.isChild ? -0.015D : -0.02D, 0.0D);
      matrixStackIn.scale(1.01f, 1.0f, 1.01f);
      base.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      matrixStackIn.pop();
    }
    if (headModel != null && headTexture != null && buffer != null) {
      IVertexBuilder headBuilder = buffer.getBuffer(RenderType.getEntityCutoutNoCullZOffset(headTexture));
      boolean needsPush = this.isChild || (this.isSneak && base != null);
      if (needsPush) {
        matrixStackIn.push();
        if (isChild) {
          matrixStackIn.scale(0.75F, 0.75F, 0.75F);
          matrixStackIn.translate(0.0D, 1.0D, 0.0D);
        }
        if (isSneak && base != null) {
          matrixStackIn.translate(0, base.bipedHead.rotationPointY / 16.0F, 0);
        }
      }
      headModel.func_225603_a_(0, this.bipedHead.rotateAngleY * 180f / (float)(Math.PI), this.bipedHead.rotateAngleX * 180f / (float)(Math.PI));
      headModel.render(matrixStackIn, headBuilder, packedLightIn, packedOverlayIn, red, green * 0.5f, blue, alpha * 0.8f);
      if (needsPush) {
        matrixStackIn.pop();
      }
    }
  }

  /** Called before the model is rendered to set the base model and the tool stack data */
  public void setToolAndBase(ItemStack stack, BipedModel<?> base) {
    this.base = base;
    MaterialId materialId = MaterialIdNBT.from(stack).getMaterial(0);
    if (!materialId.equals(IMaterial.UNKNOWN_ID)) {
      Pair<ResourceLocation, GenericHeadModel> pair = HEAD_MODELS.get(materialId);
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
  public void setVisible(boolean visible) {
    if (base != null) {
      base.setVisible(false);
      base.bipedHead.showModel = true;
      base.bipedHeadwear.showModel = true;
      // attributes are copied to skull through another model's setModelAttributes, this is the best hook to copy them to the model
      this.setModelAttributes((BipedModel<T>)base);
    }
  }
}
