package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileCasting;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingBasin;
import slimeknights.tconstruct.smeltery.tileentity.TileCastingTable;

public class CastingRenderer<T extends TileCasting> extends TileEntitySpecialRenderer<T> {

  protected final float yMin;
  protected final float yMax;
  protected final float xzMin;
  protected final float xzMax;

  protected float yScale;
  protected float xzScale;
  protected float yOffset;
  protected float xzOffset;


  public CastingRenderer(float yMin, float yMax, float xzMin, float xzMax) {
    // we make the size a tad smaller because of casts so it doesn't overlap
    float s = 0.999f;
    this.yMin = yMin*s;
    this.yMax = yMax*s;
    this.xzMin = xzMin*s;
    this.xzMax = xzMax*s;


    this.yOffset = yMin + (yMax-yMin)/2f;
    this.xzOffset = xzMin + (xzMax - xzMin)/2f;

    this.xzScale = (this.xzMax - this.xzMin);
    this.yScale = xzScale;
  }

  @Override
  public void renderTileEntityAt(T te, double x, double y, double z, float partialTicks, int destroyStage) {
    if(te.tank.getFluidAmount() == 0) {
      return;
    }

    float height = ((float)te.tank.getFluidAmount() - te.renderOffset) / (float)te.tank.getCapacity();

    if(te.renderOffset > 1.2f || te.renderOffset < -1.2f) {
      te.renderOffset -= (te.renderOffset / 12f + 0.1f) * partialTicks;
    }
    else {
      te.renderOffset = 0;
    }

    float yh = yMin + (yMax - yMin) * height;

    //GlStateManager.color(0.1f, 0.1f, 0.1f);
    //RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yh, xzMax);
    FluidStack fluid = te.tank.getFluid();
    float progress = 0.01f;
    if(te.renderOffset == 0) {
      progress = te.getCooldownProgress();
    }

    int color = fluid.getFluid().getColor(fluid);
    int r,g,b,a;
    a = RenderUtil.alpha(color);
    r = RenderUtil.red(color);
    g = RenderUtil.green(color);
    b = RenderUtil.blue(color);

    //a = (int)(((a/255f) * (1f - progress/2f)) * a);
    r = (int)((float)r * (1f - 0.8*progress));
    g = (int)((float)g * (1f - 0.8*progress));
    b = (int)((float)b * (1f - 0.8*progress));

    color = RenderUtil.compose(r, g, b, a);
    RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yh, xzMax, color);

    // render item
    ItemStack stack = te.getCurrentResult();
    if(progress > 0 && stack != null && te.getStackInSlot(1) == null) {
      RenderUtil.pre(x,y,z);
      int brightness = te.getWorld().getCombinedLight(te.getPos(), 0);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)(brightness % 0x10000) / 1f,
                                            (float)(brightness / 0x10000) / 1f);


      //GlStateManager.translate(0.5f, 0.5f, 0.5f);
      GlStateManager.translate(xzOffset, yOffset, xzOffset);
      GlStateManager.scale(xzScale, yScale, xzScale);
      GlStateManager.scale(1.01f, 1.01f, 1.01f); // make it a tad bigger so it renders over the liquid (will be blended)
      GlStateManager.scale(2f, 2f, 2f); // renderItem scales by 0.5

      // align item orientation with casting tile orientation
      GlStateManager.rotate(-90 * te.getFacing().getHorizontalIndex(), 0, 1, 0);
      GlStateManager.rotate(-90, 1, 0, 0);

      GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_SRC_ALPHA);
      GL14.glBlendColor(1f, 1f, 1f, progress/2f);
      //GL14.glBlendColor(1f, 1f, 1f, 1f); // debug

      IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);

      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      RenderUtil.post();
    }
  }

  public static class Table extends CastingRenderer<TileCastingTable> {

    public Table() {
      super(15/16f, 1f, 1/16f, 15/16f);
      this.xzScale = 0.875f;
      this.yScale = 1;
    }
  }

  public static class Basin extends CastingRenderer<TileCastingBasin> {

    public Basin() {
      super(4/16f, 1f, 2/16f, 14/16f);
    }
  }
}
