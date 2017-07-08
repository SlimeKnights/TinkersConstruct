package slimeknights.tconstruct.smeltery.client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.RenderUtil;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
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
    float s = 0.9995f;
    this.yMin = yMin * s;
    this.yMax = yMax * s;
    this.xzMin = xzMin * s;
    this.xzMax = xzMax * s;

    this.yOffset = yMin + (yMax - yMin) / 2f;
    this.xzOffset = xzMin + (xzMax - xzMin) / 2f;

    this.xzScale = (this.xzMax - this.xzMin);
    this.yScale = xzScale;
  }

  @Override
  public void render(@Nonnull T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
    FluidTankAnimated tank = te.tank;
    if(tank.getFluidAmount() == 0 || tank.getCapacity() == 0) {
      return;
    }

    float height = (tank.getFluidAmount() - tank.renderOffset) / tank.getCapacity();

    if(tank.renderOffset > 1.2f || tank.renderOffset < -1.2f) {
      tank.renderOffset -= (tank.renderOffset / 12f + 0.1f) * partialTicks;
    }
    else {
      tank.renderOffset = 0;
    }

    float yh = yMin + (yMax - yMin) * height;

    //GlStateManager.color(0.1f, 0.1f, 0.1f);
    //RenderUtil.renderFluidCuboid(te.tank.getFluid(), te.getPos(), x,y,z, xzMin, yMin, xzMin, xzMax, yh, xzMax);
    FluidStack fluid = tank.getFluid();
    float progress = 0f;
    //if(te.renderOffset == 0) {
    progress = te.getProgress();
    //}

    assert fluid != null;
    int color = fluid.getFluid().getColor(fluid);
    int r, g, b, a;
    a = RenderUtil.alpha(color);
    r = RenderUtil.red(color);
    g = RenderUtil.green(color);
    b = RenderUtil.blue(color);

    if(progress > 2 / 3) {
      float af = progress / 3f;

      a = (int) (((a / 255f) * (1f - af)) * a);
      //r = (int)((float)r * (1f - progress));
      //g = (int)((float)g * (1f - progress));
      //b = (int)((float)b * (1f - progress));
      color = RenderUtil.compose(r, g, b, a);
    }
    RenderUtil.renderFluidCuboid(tank.getFluid(), te.getPos(), x, y, z, xzMin, yMin, xzMin, xzMax, yh, xzMax, color);

    // render item
    ItemStack stack = te.getCurrentResult();
    if(progress > 0 && !stack.isEmpty() && te.getStackInSlot(1).isEmpty()) {
      RenderUtil.pre(x, y, z);
      int brightness = te.getWorld().getCombinedLight(te.getPos(), 0);
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, brightness % 0x10000 / 1f,
                                            brightness / 0x10000 / 1f);

      GlStateManager.translate(xzOffset, yOffset, xzOffset);
      GlStateManager.scale(xzScale, yScale, xzScale);

      // align item orientation with casting tile orientation
      GlStateManager.rotate(-90 * te.getFacing().getHorizontalIndex(), 0, 1, 0);
      if(!(stack.getItem() instanceof ItemBlock) || Block.getBlockFromItem(stack.getItem()) instanceof BlockPane) {
        GlStateManager.rotate(-90, 1, 0, 0);
      }

      //GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_CONSTANT_ALPHA);
      //GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_DST_ALPHA);
      //GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_CONSTANT_ALPHA, GL11.GL_SRC_COLOR);
      GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
      GL14.glBlendColor(1f, 1f, 1f, progress);
      //GL14.glBlendColor(r, g, b, progress);
      //GL14.glBlendColor(1f, 1f, 1f, 1f); // debug

      GL11.glDepthMask(false);
      IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, te.getWorld(), null);
      Minecraft.getMinecraft().getRenderItem().renderItem(stack, model);
      GL11.glDepthMask(true);
      GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      RenderUtil.post();
    }
  }

  public static class Table extends CastingRenderer<TileCastingTable> {

    public Table() {
      super(15 / 16f, 1f + 0.001f, 1 / 16f, 15 / 16f);
      this.xzScale = 0.875f;
      this.yScale = 1f;
      this.yOffset += 0.001f;
    }
  }

  public static class Basin extends CastingRenderer<TileCastingBasin> {

    public Basin() {
      super(4 / 16f, 1f, 2 / 16f, 14 / 16f);

      this.xzScale = 0.751f;
    }
  }
}
