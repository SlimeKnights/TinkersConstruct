package slimeknights.tconstruct.library.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.smeltery.SmelteryTank;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.client.SmelteryRenderer;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClicked;

public class GuiUtil {

  private GuiUtil() {}

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

  /* GUI Tanks */
  public static List<String> drawTankTooltip(SmelteryTank tank, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {

    // Liquids
    if(xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
      FluidStack hovered = getFluidHovered(tank, ymax - mouseY - 1, ymax - ymin);
      List<String> text = Lists.newArrayList();

      if(hovered == null) {
        int usedCap = tank.getFluidAmount();
        int maxCap = tank.getCapacity();
        text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.capacity"));
        GuiUtil.amountToString(maxCap, text);
        text.add(Util.translateFormatted("gui.smeltery.capacity_available"));
        GuiUtil.amountToString(maxCap - usedCap, text);
      }
      else {
        text.add(TextFormatting.WHITE + hovered.getLocalizedName());
        GuiUtil.liquidToString(hovered, text);
      }

      return text;
    }

    return null;
  }

  private static FluidStack getFluidHovered(SmelteryTank tank, int y, int height) {
    int[] heights = calcLiquidHeights(tank.getFluids(), tank.getCapacity(), height);

    for(int i = 0; i < heights.length; i++) {
      if(y < heights[i]) {
        return tank.getFluids().get(i);
      }
      y -= heights[i];
    }

    return null;
  }

  private static int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height) {
    return SmelteryRenderer.calcLiquidHeights(liquids, capacity, height, 3);
  }

  public static void drawGuiTank(SmelteryTank liquids, int x, int y, int w, int height, float zLevel) {
    // draw liquids
    if(liquids.getFluidAmount() > 0) {
      int capacity = Math.max(liquids.getFluidAmount(), liquids.getCapacity());
      int[] heights = calcLiquidHeights(liquids.getFluids(), capacity, height);

      int bottom = y + w;
      for(int i = 0; i < heights.length; i++) {
        int h = heights[i];
        FluidStack liquid = liquids.getFluids().get(i);
        renderTiledFluid(x, bottom - h, w, h, zLevel, liquid);

        bottom -= h;
      }
    }
  }

  public static void handleTankClick(SmelteryTank tank, int mouseX, int mouseY, int xmin, int ymin, int xmax, int ymax) {
    if(xmin <= mouseX && mouseX < xmax && ymin <= mouseY && mouseY < ymax) {
      int[] heights = calcLiquidHeights(tank.getFluids(), tank.getCapacity(), ymax - ymin);
      int y = ymax - mouseY - 1;

      for(int i = 0; i < heights.length; i++) {
        if(y < heights[i]) {
          TinkerNetwork.sendToServer(new SmelteryFluidClicked(i));
          return;
        }
        y -= heights[i];
      }
    }
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
    int amount = fluid.amount;

    if(smelteryLoaded && !Util.isShiftKeyDown()) {
      List<FluidGuiEntry> entries = fluidGui.get(fluid.getFluid());
      if(entries == null) {
        entries = calcFluidGuiEntries(fluid.getFluid());
        fluidGui.put(fluid.getFluid(), entries);
      }

      for(FluidGuiEntry entry : entries) {
        amount = calcLiquidText(amount, entry.amount, entry.getText(), text);
      }
    }

    // standard display stuff: bucket amounts
    amountToString(amount, text);
  }

  /**
   * Adds information to the tooltip based on the fluid amount
   * @param amount Fluid amount
   * @param text   Text to add information to.
   */
  public static void amountToString(int amount, List<String> text) {
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
