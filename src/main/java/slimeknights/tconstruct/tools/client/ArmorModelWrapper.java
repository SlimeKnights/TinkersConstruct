package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;

import javax.annotation.Nullable;

/** Armor model that wraps another armor model */
public class ArmorModelWrapper<T extends LivingEntity> extends BipedModel<T> {
  @Nullable
  protected BipedModel<?> base;
  protected ArmorModelWrapper() {
    super(1.0f);
  }

  @Override
  public void render(MatrixStack matrixStackIn, IVertexBuilder vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    if (base != null) {
      copyToBase();
      base.render(matrixStackIn, vertexBuilder, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
  }

  /** Copies properties to the base model, most accurate time to call is during render */
  @SuppressWarnings("unchecked")
  protected void copyToBase() {
    if (base != null) {
      this.setModelAttributes((BipedModel<T>)base);
      base.bipedHead.showModel = this.bipedHead.showModel;
      base.bipedHeadwear.showModel = this.bipedHeadwear.showModel;
      base.bipedBody.showModel = this.bipedBody.showModel;
      base.bipedRightArm.showModel = this.bipedRightArm.showModel;
      base.bipedLeftArm.showModel = this.bipedLeftArm.showModel;
      base.bipedRightLeg.showModel = this.bipedRightLeg.showModel;
      base.bipedLeftLeg.showModel = this.bipedLeftLeg.showModel;
    }
  }

  /** Buffer from the render living event, stored as we lose access to it later */
  @Nullable
  protected static IRenderTypeBuffer buffer;

  /** Initalizes the wrapper */
  public static void init() {
    // register listeners to set and clear the buffer
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Pre.class, event -> buffer = event.getBuffers());
    MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, false, RenderLivingEvent.Post.class, event -> buffer = null);
  }
}
