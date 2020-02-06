package slimeknights.tconstruct.library.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tconstruct.library.client.RenderUtil;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class ColoredSlimeGelLayer<T extends LivingEntity> extends LayerRenderer<T, SlimeModel<T>> {

  private final EntityModel<T> slimeModel = new SlimeModel<>(0);

  private final int color;

  private float ticking;

  public static boolean magicMushrooms = false;

  public ColoredSlimeGelLayer(IEntityRenderer<T, SlimeModel<T>> entityRenderer, int color) {
    super(entityRenderer);
    this.color = color;
  }

  @Override
  public void render(MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225628_3_, T entity, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
    if (!entity.isInvisible()) {
      if (magicMushrooms) {
        RenderUtil.setColorRGBA(Color.HSBtoRGB(this.ticking / 100f, 0.65f, 0.8f) | (this.color & (0xFF << 24)));
      } else {
        RenderUtil.setColorRGBA(this.color);
      }

      this.getEntityModel().setModelAttributes(this.slimeModel);
      this.slimeModel.setLivingAnimations(entity, p_225628_5_, p_225628_6_, p_225628_7_);
      this.slimeModel.render(entity, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);

      IVertexBuilder ivertexbuilder = renderTypeBuffer.getBuffer(RenderType.entityTranslucent(this.getEntityTexture(entity)));
      this.slimeModel.render(matrixStack, ivertexbuilder, p_225628_3_, LivingRenderer.getPackedOverlay(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
    }
  }
}
