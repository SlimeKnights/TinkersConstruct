package slimeknights.tconstruct.library.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.model.SlimeModel;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;

@OnlyIn(Dist.CLIENT)
public class BlueSlimeRenderer extends MobRenderer<SlimeEntity, SlimeModel<SlimeEntity>> {

  public static final Factory BLUE_SLIME_FACTORY = new Factory(0xff67f0f5);

  public static final ResourceLocation SLIME_TEXTURE = Util.getResource("textures/entity/slime.png");

  private final int color;

  public BlueSlimeRenderer(EntityRendererManager renderManager, int color) {
    this(renderManager, color, color);
  }

  public BlueSlimeRenderer(EntityRendererManager renderManagerIn, int color, int colorLayer) {
    super(renderManagerIn, new SlimeModel<>(16), 0.25F);

    this.color = color;

    this.addLayer(new ColoredSlimeGelLayer<>(this, colorLayer));
  }

  @Override
  public void render(SlimeEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_225623_6_) {
    RenderUtil.setColorRGBA(this.color);
    this.shadowSize = 0.25F * (float) entity.getSlimeSize();
    super.render(entity, p_225623_2_, p_225623_3_, matrixStack, renderTypeBuffer, p_225623_6_);
  }

  @Override
  protected void func_225620_a_(SlimeEntity entity, MatrixStack matrixStack, float p_225620_3_) {
    float f = 0.999F;
    matrixStack.scale(0.999F, 0.999F, 0.999F);
    matrixStack.translate(0.0D, (double) 0.001F, 0.0D);
    float f1 = (float) entity.getSlimeSize();
    float f2 = MathHelper.lerp(p_225620_3_, entity.prevSquishFactor, entity.squishFactor) / (f1 * 0.5F + 1.0F);
    float f3 = 1.0F / (f2 + 1.0F);
    matrixStack.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
  }

  @Override
  protected float func_225625_b_(SlimeEntity p_225625_1_, float p_225625_2_) {
    return this.color;
  }

  @Override
  public ResourceLocation getEntityTexture(SlimeEntity entity) {
    return SLIME_TEXTURE;
  }

  private static class Factory implements IRenderFactory<SlimeEntity> {

    private final int color;

    public Factory(int color) {
      this.color = color;
    }

    @Override
    public EntityRenderer<? super SlimeEntity> createRenderFor(EntityRendererManager manager) {
      return new BlueSlimeRenderer(manager, this.color);
    }
  }
}
