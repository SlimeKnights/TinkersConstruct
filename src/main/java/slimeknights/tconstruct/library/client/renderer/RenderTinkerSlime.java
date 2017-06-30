package slimeknights.tconstruct.library.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.RenderUtil;

@SideOnly(Side.CLIENT)
public class RenderTinkerSlime extends RenderSlime {

  public static final Factory FACTORY_BlueSlime = new Factory(0xff67f0f5);

  public static final ResourceLocation slimeTextures = Util.getResource("textures/entity/slime.png");

  private final int color;
  private final ResourceLocation texture;

  public RenderTinkerSlime(RenderManager renderManager, int color) {
    this(renderManager, color, slimeTextures);
  }

  public RenderTinkerSlime(RenderManager renderManager, int color, ResourceLocation texture) {
    this(renderManager, color, color, new ModelSlime(16), 0.25f, texture);
  }

  public RenderTinkerSlime(RenderManager renderManagerIn, int color, int colorLayer, ModelBase modelBaseIn, float shadowSizeIn, ResourceLocation texture) {
    super(renderManagerIn);
    this.mainModel = modelBaseIn;
    this.shadowSize = shadowSizeIn;
    this.color = color;
    this.texture = texture;

    // replace slime layer with our own colorable variant
    this.layerRenderers.clear();
    this.addLayer(new LayerSlimeGelColored(this, colorLayer));
  }

  @Override
  public void doRender(EntitySlime entity, double x, double y, double z, float entityYaw, float partialTicks) {
    RenderUtil.setColorRGBA(color);
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
  }

  @Nonnull
  @Override
  protected ResourceLocation getEntityTexture(EntitySlime entity) {
    return this.texture;
  }

  @Override
  protected int getColorMultiplier(EntitySlime entitylivingbaseIn, float lightBrightness, float partialTickTime) {
    return color;
  }

  public static class LayerSlimeGelColored implements LayerRenderer<EntitySlime> {

    private final RenderSlime slimeRenderer;
    private final ModelBase slimeModel;
    private final int color;

    private float ticking;

    public static boolean magicMushrooms = false;

    public LayerSlimeGelColored(RenderSlime slimeRendererIn, int color) {
      this.slimeRenderer = slimeRendererIn;
      this.color = color;
      slimeModel = new ModelSlime(0);
    }

    @Override
    public void doRenderLayer(@Nonnull EntitySlime entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
      ticking += partialTicks;
      if(!entitylivingbaseIn.isInvisible()) {
        if(magicMushrooms) {
          RenderUtil.setColorRGBA(Color.HSBtoRGB(ticking / 100f, 0.65f, 0.8f) | (color & (0xFF << 24)));
        }
        else {
          RenderUtil.setColorRGBA(color);
        }
        GlStateManager.enableNormalize();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
        this.slimeModel.render(entitylivingbaseIn, p_177141_2_, p_177141_3_, p_177141_5_, p_177141_6_, p_177141_7_, scale);
        GlStateManager.disableBlend();
        GlStateManager.disableNormalize();
      }
    }

    @Override
    public boolean shouldCombineTextures() {
      return true;
    }
  }

  private static class Factory implements IRenderFactory<EntitySlime> {

    private final int color;

    public Factory(int color) {
      this.color = color;
    }

    @Override
    public Render<? super EntitySlime> createRenderFor(RenderManager manager) {
      return new RenderTinkerSlime(manager, color);
    }
  }
}
