package slimeknights.tconstruct.library.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

public final class RenderUtil {

  private RenderUtil() {
  }

  public static float FLUID_OFFSET = 0.005f;

  protected static Minecraft mc = Minecraft.getMinecraft();

  /** Renders the given texture tiled into a GUI */
  public static void renderTiledTextureAtlas(int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer worldrenderer = tessellator.getBuffer();
    worldrenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

    putTiledTextureQuads(worldrenderer, x, y, width, height, depth, sprite);

    tessellator.draw();
  }

  public static void renderTiledFluid(int x, int y, int width, int height, float depth, FluidStack fluidStack) {
    TextureAtlasSprite fluidSprite = mc.getTextureMapBlocks().getAtlasSprite(fluidStack.getFluid().getStill(fluidStack).toString());
    RenderUtil.setColorRGBA(fluidStack.getFluid().getColor(fluidStack));
    renderTiledTextureAtlas(x, y, width, height, depth, fluidSprite);
  }

  /** Adds a quad to the rendering pipeline. Call startDrawingQuads beforehand. You need to call draw() yourself. */
  public static void putTiledTextureQuads(VertexBuffer renderer, int x, int y, int width, int height, float depth, TextureAtlasSprite sprite) {
    float u1 = sprite.getMinU();
    float v1 = sprite.getMinV();

    // tile vertically
    do {
      int renderHeight = Math.min(sprite.getIconHeight(), height);
      height -= renderHeight;

      float v2 = sprite.getInterpolatedV((16f * renderHeight) / (float) sprite.getIconHeight());

      // we need to draw the quads per width too
      int x2 = x;
      int width2 = width;
      // tile horizontally
      do {
        int renderWidth = Math.min(sprite.getIconWidth(), width2);
        width2 -= renderWidth;

        float u2 = sprite.getInterpolatedU((16f * renderWidth) / (float) sprite.getIconWidth());

        renderer.pos(x2, y, depth).tex(u1, v1).endVertex();
        renderer.pos(x2, y + renderHeight, depth).tex(u1, v2).endVertex();
        renderer.pos(x2 + renderWidth, y + renderHeight, depth).tex(u2, v2).endVertex();
        renderer.pos(x2 + renderWidth, y, depth).tex(u2, v1).endVertex();

        x2 += renderWidth;
      } while(width2 > 0);

      y += renderHeight;
    } while(height > 0);
  }

  /**
   * Renders a fluid block, call from TESR. x/y/z is the rendering offset.
   *
   * @param fluid Fluid to render
   * @param pos   BlockPos where the Block is rendered. Used for brightness.
   * @param x     Rendering offset. TESR x parameter.
   * @param y     Rendering offset. TESR x parameter.
   * @param z     Rendering offset. TESR x parameter.
   * @param w     Width. 1 = full X-Width
   * @param h     Height. 1 = full Y-Height
   * @param d     Depth. 1 = full Z-Depth
   */
  public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double w, double h, double d) {
    double wd = (1d - w) / 2d;
    double hd = (1d - h) / 2d;
    double dd = (1d - d) / 2d;

    renderFluidCuboid(fluid, pos, x, y, z, wd, hd, dd, 1d - wd, 1d - hd, 1d - dd);
  }

  public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
    int color = fluid.getFluid().getColor(fluid);
    renderFluidCuboid(fluid, pos, x, y, z, x1, y1, z1, x2, y2, z2, color);
  }

  /** Renders block with offset x/y/z from x1/y1/z1 to x2/y2/z2 inside the block local coordinates, so from 0-1 */
  public static void renderFluidCuboid(FluidStack fluid, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer renderer = tessellator.getBuffer();
    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    //RenderUtil.setColorRGBA(color);
    int brightness = mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity());

    pre(x, y, z);

    TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

    // x/y/z2 - x/y/z1 is because we need the width/height/depth
    putTexturedQuad(renderer, still,   x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN,  color, brightness, false);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, true);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST,  color, brightness, true);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, true);
    putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST,  color, brightness, true);
    putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP,    color, brightness, false);

    tessellator.draw();

    post();
  }

  public static void renderStackedFluidCuboid(FluidStack fluid, double px, double py, double pz, BlockPos pos,
                                              BlockPos from, BlockPos to, double ymin, double ymax) {
    if(ymin >= ymax) {
      return;
    }
    Tessellator tessellator = Tessellator.getInstance();
    VertexBuffer renderer = tessellator.getBuffer();
    renderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    int color = fluid.getFluid().getColor(fluid);
    int brightness = mc.theWorld.getCombinedLight(pos, fluid.getFluid().getLuminosity());

    pre(px, py, pz);
    GlStateManager.translate(from.getX(), from.getY(), from.getZ());

    TextureAtlasSprite still = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getStill(fluid).toString());
    TextureAtlasSprite flowing = mc.getTextureMapBlocks().getTextureExtry(fluid.getFluid().getFlowing(fluid).toString());

    if(still == null) {
      still = mc.getTextureMapBlocks().getMissingSprite();
    }
    if(flowing == null) {
      flowing = mc.getTextureMapBlocks().getMissingSprite();
    }

    int xd = to.getX() - from.getX();

    // the liquid can stretch over more blocks than the subtracted height is if ymin's decimal is bigger than ymax's decimal (causing UV over 1)
    // ignoring the decimals prevents this, as yd then equals exactly how many ints are between the two
    // for example, if ymax = 5.1 and ymin = 2.3, 2.8 (which rounds to 2), with the face array becoming 2.3, 3, 4, 5.1
    int yminInt = (int)ymin;
    int yd = (int) (ymax - yminInt);

    // prevents a rare case of rendering the top face multiple times if ymax is perfectly aligned with the block
    // for example, if ymax = 3 and ymin = 1, the values of the face array become 1, 2, 3, 3 as we then have middle ints
    if(ymax % 1d == 0) yd--;
    int zd = to.getZ() - from.getZ();

    double xmin = FLUID_OFFSET;
    double xmax = xd + 1d - FLUID_OFFSET;
    //double ymin = y1;
    //double ymax = y2;
    double zmin = FLUID_OFFSET;
    double zmax = zd + 1d - FLUID_OFFSET;

    double[] xs = new double[2 + xd];
    double[] ys = new double[2 + yd];
    double[] zs = new double[2 + zd];

    xs[0] = xmin;
    for(int i = 1; i <= xd; i++) xs[i] = i;
    xs[xd+1] = xmax;

    // we have to add the whole number for ymin or otherwise things render incorrectly if above the first block
    // example, heights of 2 and 5 would produce array of 2, 1, 2, 5
    ys[0] = ymin;
    for(int i = 1; i <= yd; i++) ys[i] = i + yminInt;
    ys[yd+1] = ymax;

    zs[0] = zmin;
    for(int i = 1; i <= zd; i++) zs[i] = i;
    zs[zd+1] = zmax;

    // render each side
    for(int y = 0; y <= yd; y++) {
      for(int z = 0; z <= zd; z++) {
        for(int x = 0; x <= xd; x++) {

          double x1 = xs[x];
          double x2 = xs[x+1] - x1;
          double y1 = ys[y];
          double y2 = ys[y+1] - y1;
          double z1 = zs[z];
          double z2 = zs[z+1] - z1;

          if(x == 0)  putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.WEST,  color, brightness, true);
          if(x == xd) putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.EAST,  color, brightness, true);
          if(y == 0)  putTexturedQuad(renderer, still,   x1, y1, z1, x2, y2, z2, EnumFacing.DOWN,  color, brightness, false);
          if(y == yd) putTexturedQuad(renderer, still,   x1, y1, z1, x2, y2, z2, EnumFacing.UP,    color, brightness, false);
          if(z == 0)  putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.NORTH, color, brightness, true);
          if(z == zd) putTexturedQuad(renderer, flowing, x1, y1, z1, x2, y2, z2, EnumFacing.SOUTH, color, brightness, true);
        }
      }
    }



    //putTexturedQuad(renderer, still,   x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness);
    //putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness);
    //putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST, color, brightness);
    //putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness);
    //putTexturedQuad(renderer, flowing, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST, color, brightness);
    //putTexturedQuad(renderer, still  , x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP, color, brightness);

    tessellator.draw();

    post();
  }

  public static void putTexturedCuboid(VertexBuffer renderer, ResourceLocation location, double x1, double y1, double z1, double x2, double y2, double z2,
                                       int color, int brightness) {
    boolean flowing = false;
    TextureAtlasSprite sprite = mc.getTextureMapBlocks().getTextureExtry(location.toString());
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.DOWN, color, brightness, flowing);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.NORTH, color, brightness, flowing);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.EAST, color, brightness, flowing);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.SOUTH, color, brightness, flowing);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.WEST, color, brightness, flowing);
    putTexturedQuad(renderer, sprite, x1, y1, z1, x2-x1, y2-y1, z2-z1, EnumFacing.UP, color, brightness, flowing);
  }

  public static void putTexturedQuad(VertexBuffer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                     int color, int brightness, boolean flowing) {
    int l1 = brightness >> 0x10 & 0xFFFF;
    int l2 = brightness & 0xFFFF;

    int a = color >> 24 & 0xFF;
    int r = color >> 16 & 0xFF;
    int g = color >> 8 & 0xFF;
    int b = color & 0xFF;

    putTexturedQuad(renderer, sprite, x, y, z, w, h, d, face, r, g, b, a, l1, l2, flowing);
  }

  // x and x+w has to be within [0,1], same for y/h and z/d
  public static void putTexturedQuad(VertexBuffer renderer, TextureAtlasSprite sprite, double x, double y, double z, double w, double h, double d, EnumFacing face,
                                     int r, int g, int b, int a, int light1, int light2, boolean flowing) {
    // safety
    if(sprite == null) {
      return;
    }
    double minU;
    double maxU;
    double minV;
    double maxV;

    double size = 16f;
    if(flowing) {
      size = 8f;
    }

    double x1 = x;
    double x2 = x + w;
    double y1 = y;
    double y2 = y + h;
    double z1 = z;
    double z2 = z + d;

    double xt1 = x1%1d;
    double xt2 = xt1 + w;
    while(xt2 > 1f) xt2 -= 1f;
    double yt1 = y1%1d;
    double yt2 = yt1 + h;
    while(yt2 > 1f) yt2 -= 1f;
    double zt1 = z1%1d;
    double zt2 = zt1 + d;
    while(zt2 > 1f) zt2 -= 1f;

    // flowing stuff should start from the bottom, not from the start
    if(flowing) {
      double tmp = 1d - yt1;
      yt1 = 1d - yt2;
      yt2 = tmp;
    }

    switch(face) {
      case DOWN:
      case UP:
        minU = sprite.getInterpolatedU(xt1 * size);
        maxU = sprite.getInterpolatedU(xt2 * size);
        minV = sprite.getInterpolatedV(zt1 * size);
        maxV = sprite.getInterpolatedV(zt2 * size);
        break;
      case NORTH:
      case SOUTH:
        minU = sprite.getInterpolatedU(xt2 * size);
        maxU = sprite.getInterpolatedU(xt1 * size);
        minV = sprite.getInterpolatedV(yt1 * size);
        maxV = sprite.getInterpolatedV(yt2 * size);
        break;
      case WEST:
      case EAST:
        minU = sprite.getInterpolatedU(zt2 * size);
        maxU = sprite.getInterpolatedU(zt1 * size);
        minV = sprite.getInterpolatedV(yt1 * size);
        maxV = sprite.getInterpolatedV(yt2 * size);
        break;
      default:
        minU = sprite.getMinU();
        maxU = sprite.getMaxU();
        minV = sprite.getMinV();
        maxV = sprite.getMaxV();
    }

    switch(face) {
      case DOWN:
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        break;
      case UP:
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case NORTH:
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        break;
      case SOUTH:
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case WEST:
        renderer.pos(x1, y1, z1).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y1, z2).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z2).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x1, y2, z1).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        break;
      case EAST:
        renderer.pos(x2, y1, z1).color(r, g, b, a).tex(minU, maxV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z1).color(r, g, b, a).tex(minU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y2, z2).color(r, g, b, a).tex(maxU, minV).lightmap(light1, light2).endVertex();
        renderer.pos(x2, y1, z2).color(r, g, b, a).tex(maxU, maxV).lightmap(light1, light2).endVertex();
        break;
    }
  }

  public static void pre(double x, double y, double z) {
    GlStateManager.pushMatrix();

    RenderHelper.disableStandardItemLighting();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    if(Minecraft.isAmbientOcclusionEnabled()) {
      GL11.glShadeModel(GL11.GL_SMOOTH);
    }
    else {
      GL11.glShadeModel(GL11.GL_FLAT);
    }

    GlStateManager.translate(x, y, z);
  }

  public static void post() {
    GlStateManager.disableBlend();
    GlStateManager.popMatrix();
    RenderHelper.enableStandardItemLighting();
  }

  public static void setColorRGB(int color) {
    setColorRGBA(color | 0xff000000);
  }

  public static void setColorRGBA(int color) {
    float a = (float) alpha(color) / 255.0F;
    float r = (float) red(color) / 255.0F;
    float g = (float) green(color) / 255.0F;
    float b = (float) blue(color) / 255.0F;

    GlStateManager.color(r, g, b, a);
  }

  public static void setBrightness(VertexBuffer renderer, int brightness) {
    renderer.putBrightness4(brightness, brightness, brightness, brightness);
  }

  public static int compose(int r, int g, int b, int a) {
    int rgb = a;
    rgb = (rgb << 8) + r;
    rgb = (rgb << 8) + g;
    rgb = (rgb << 8) + b;
    return rgb;
  }

  public static int alpha(int c) {
    return (c >> 24) & 0xFF;
  }

  public static int red(int c) {
    return (c >> 16) & 0xFF;
  }

  public static int green(int c) {
    return (c >> 8) & 0xFF;
  }

  public static int blue(int c) {
    return (c) & 0xFF;
  }


  /* Fluid amount displays */
  private static Map<Fluid, List<FluidGuiEntry>> fluidGui = Maps.newHashMap();
  private static boolean smelteryLoaded = TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId);

  /**
   * Adds information for the tooltip based on the fluid stacks size.
   *
   * @param fluid Input fluid stack
   * @param text  Text to add information to.
   */
  public static void liquidToString(FluidStack fluid, List<String> text) {
    liquidToString(fluid.getFluid(), fluid.amount, text);
  }

  /**
   * Adds information for the tooltip based on the fluid stacks size.
   *
   * @param fluid  Input fluid, only used for type calculation
   * @param amount Fluid amount
   * @param text   Text to add information to.
   */
  public static void liquidToString(@Nullable Fluid fluid, int amount, List<String> text) {
    if(fluid != null && smelteryLoaded && !Util.isShiftKeyDown()) {
      List<FluidGuiEntry> entries = fluidGui.get(fluid);
      if(entries == null) {
        entries = calcFluidGuiEntries(fluid);
        fluidGui.put(fluid, entries);
      }

      for(FluidGuiEntry entry : entries) {
        amount = calcLiquidText(amount, entry.amount, entry.getText(), text);
      }
    }

    // standard display: bucket amounts
    // we go up to kiloBuckets because we can
    amount = calcLiquidText(amount, 1000000, Util.translate("gui.smeltery.liquid.kilobucket"), text);
    amount = calcLiquidText(amount, 1000, Util.translate("gui.smeltery.liquid.bucket"), text);
    calcLiquidText(amount, 1, Util.translate("gui.smeltery.liquid.millibucket"), text);
  }

  private static List<FluidGuiEntry> calcFluidGuiEntries(Fluid fluid) {
    List<FluidGuiEntry> list = Lists.newArrayList();

    // go through all casting recipes for the fluids and check for known "units" like blocks, ingots,...
    for(ICastingRecipe irecipe : TinkerRegistry.getAllBasinCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;
        // search for a block recipe
        if(recipe.getFluid().getFluid() == fluid && recipe.cast == null) {
          // it's a block that is cast solely from the material, using no cast, therefore it's a block made out of the material
          list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.block"));
        }
      }
    }
    // table casting
    for(ICastingRecipe irecipe : TinkerRegistry.getAllTableCastingRecipes()) {
      if(irecipe instanceof CastingRecipe) {
        CastingRecipe recipe = (CastingRecipe) irecipe;
        if(recipe.getFluid().getFluid() == fluid && recipe.cast != null) {
          // nugget
          if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castNugget}) != null) {
            list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.nugget"));
          }
          // ingot
          if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castIngot}) != null) {
            list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.ingot"));
          }
          // gem
          if(recipe.cast.matches(new ItemStack[]{TinkerSmeltery.castGem}) != null) {
            list.add(new FluidGuiEntry(recipe.getFluid().amount, "gui.smeltery.liquid.gem"));
          }
        }
      }
    }

    // sort by amount descending because the order in which they're accessed is important since it changes the remaining value during processing
    Collections.sort(list, new Comparator<FluidGuiEntry>() {
      @Override
      public int compare(FluidGuiEntry o1, FluidGuiEntry o2) {
        return o2.amount - o1.amount;
      }
    });

    return ImmutableList.copyOf(list);
  }

  private static int calcLiquidText(int amount, int divider, String unit, List<String> text) {
    int full = amount / divider;
    if(full > 0) {
      text.add(String.format("%d %s%s", full, TextFormatting.GRAY, unit));
    }

    return amount % divider;
  }


  private static class FluidGuiEntry {

    public final int amount;
    public final String unlocName;

    private FluidGuiEntry(int amount, String unlocName) {
      this.amount = amount;
      this.unlocName = unlocName;
    }

    public String getText() {
      return Util.translate(unlocName);
    }
  }
}
