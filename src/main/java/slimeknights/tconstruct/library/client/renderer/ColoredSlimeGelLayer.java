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
  public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
    this.ticking += partialTicks;

    float red = 1.0F;
    float green = 1.0F;
    float blue = 1.0F;
    float alpha = 1.0F;

    if (!entitylivingbaseIn.isInvisible()) {
      if (magicMushrooms) {
        int color = Color.HSBtoRGB(this.ticking / 100f, 0.65f, 0.8f) | (this.color & (0xFF << 24));
        red = RenderUtil.red(color) / 255.0F;
        green = RenderUtil.green(color) / 255.0F;
        blue = RenderUtil.blue(color) / 255.0F;
        alpha = RenderUtil.alpha(color) / 255.0F;

        RenderUtil.setColorRGBA(color);
      } else {
        red = RenderUtil.red(this.color) / 255.0F;
        green = RenderUtil.green(this.color) / 255.0F;
        blue = RenderUtil.blue(this.color) / 255.0F;
        alpha = RenderUtil.alpha(this.color) / 255.0F;

        RenderUtil.setColorRGBA(this.color);
      }

      this.getEntityModel().setModelAttributes(this.slimeModel);
      this.slimeModel.setLivingAnimations(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
      this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

      IVertexBuilder ivertexbuilder = bufferIn.getBuffer(RenderType.entityTranslucent(this.getEntityTexture(entitylivingbaseIn)));
      this.slimeModel.render(matrixStackIn, ivertexbuilder, packedLightIn, LivingRenderer.getPackedOverlay(entitylivingbaseIn, 0.0F), red, green, blue, alpha);
    }
  }
}
