package slimeknights.tconstruct.smeltery.client.inventory.module;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.smeltery.network.SmelteryFluidClickedPacket;
import slimeknights.tconstruct.smeltery.tileentity.tank.SmelteryTank;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Helper class to draw the smeltery tank in UIs
 */
@RequiredArgsConstructor
public class GuiSmelteryTank {
  // fluid tooltips
  public static final String TOOLTIP_CAPACITY = Util.makeTranslationKey("gui", "melting.capacity");
  public static final String TOOLTIP_AVAILABLE = Util.makeTranslationKey("gui", "melting.available");
  public static final String TOOLTIP_USED = Util.makeTranslationKey("gui", "melting.used");

  private final HandledScreen<?> parent;
  private final SmelteryTank tank;
  private final int x, y, width, height;

  private int[] liquidHeights;

  /**
   * Calculates the heights of the liquids
   * @param   refresh  If true, refresh the heights
   * @return  Array of liquid heights at each index
   */
  private int[] calcLiquidHeights(boolean refresh) {
    assert tank != null;
    if (liquidHeights == null || refresh) {
      liquidHeights = calcLiquidHeights(tank.getFluids(), Math.max(tank.getContained(), tank.getCapacity()), height, 3);
    }
    return liquidHeights;
  }

  /**
   * Checks if a position is within the tank
   * @param checkX  X position to check
   * @param checkY  Y position to check
   * @return  True if within the tank
   */
  private boolean withinTank(int checkX, int checkY) {
    return x <= checkX && checkX < (x + width) && y <= checkY && checkY < (y + height);
  }

  /**
   * Renders the smeltery tank
   * @param matrices  Matrix stack instance
   */
  public void renderFluids(MatrixStack matrices) {
    // draw liquids
    if (tank.getContained() > 0) {
      int[] heights = calcLiquidHeights(true);

      int bottom = y + width;
      for (int i = 0; i < heights.length; i++) {
        int fluidH = heights[i];
        FluidVolume liquid = tank.getFluids().get(i);
        GuiUtil.renderTiledFluid(matrices, parent, liquid, x, bottom - fluidH, width, fluidH, 100);
        bottom -= fluidH;
      }
    }
  }

  /**
   * Gets the fluid under the mouse at the given Y position relative to the tank bottom
   * @param heights  Fluids heights
   * @param y  Y position to check
   * @return  Fluid index under mouse, or -1 if no fluid
   */
  private int getFluidHovered(int[] heights, int y) {
    for (int i = 0; i < heights.length; i++) {
      if (y < heights[i]) {
        return i;
      }
      y -= heights[i];
    }

    return -1;
  }

  /**
   * Gets the fluid under the mouse at the given Y mouse position
   * @param heights  Fluids heights
   * @param checkY   Mouse Y position
   * @return  Fluid index under mouse, or -1 if no fluid
   */
  private int getFluidFromMouse(int[] heights, int checkY) {
    return getFluidHovered(heights, (y + height) - checkY - 1);
  }

  /**
   * Renders a highlight on the hovered fluid
   * @param matrices  Matrix stack instance
   * @param mouseX    Mouse X
   * @param mouseY    Mouse Y
   */
  public void renderHighlight(MatrixStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - parent.x;
    int checkY = mouseY - parent.y;
    if (withinTank(checkX, checkY)) {
      int[] heights = calcLiquidHeights(false);
      int hovered = getFluidFromMouse(heights, checkY);

      // sum all heights below the hovered fluid
      int heightSum = 0;
      int loopMax = hovered == -1 ? heights.length : hovered + 1;
      for (int i = 0; i < loopMax; i++) {
        heightSum += heights[i];
      }
      // render the area
      if (hovered == -1) {
        GuiUtil.renderHighlight(matrices, x, y, width, height - heightSum);
      } else {
        GuiUtil.renderHighlight(matrices, x, (y + height) - heightSum, width, heights[hovered]);
      }
    }
  }

  /**
   * Gets the tooltip for the tank based on the given mouse position
   * @param matrices  Matrix stack instance
   * @param mouseX    Mouse X
   * @param mouseY    Mouse Y
   */
  public void drawTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    // Liquids
    int checkX = mouseX - parent.x;
    int checkY = mouseY - parent.y;
    if (withinTank(checkX, checkY)) {
      int hovered = getFluidFromMouse(calcLiquidHeights(false), checkY);
      List<Text> tooltip;
      if (hovered == -1) {
        BiConsumer<Integer, List<Text>> formatter =
          Util.isShiftKeyDown() ? FluidTooltipHandler::appendBuckets : FluidTooltipHandler::appendIngots;

        tooltip = new ArrayList<>();
        tooltip.add(new TranslatableText(TOOLTIP_CAPACITY));

        formatter.accept(tank.getCapacity(), tooltip);
        int remaining = tank.getRemainingSpace();
        if (remaining > 0) {
          tooltip.add(new TranslatableText(TOOLTIP_AVAILABLE));
          formatter.accept(remaining, tooltip);
        }
        int used = tank.getContained();
        if (used > 0) {
          tooltip.add(new TranslatableText(TOOLTIP_USED));
          formatter.accept(used, tooltip);
        }
        FluidTooltipHandler.appendShift(tooltip);
      }
      else {
        tooltip = FluidTooltipHandler.getFluidTooltip(tank.getFluidInTank(hovered));
      }
      parent.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
  }

  /**
   * Checks if the tank was clicked at the given location
   */
  public void handleClick(int mouseX, int mouseY) {
    if (withinTank(mouseX, mouseY)) {
      int index = getFluidFromMouse(calcLiquidHeights(false), mouseY);
      if (index != -1) {
        TinkerNetwork.getInstance().sendToServer(new SmelteryFluidClickedPacket(index));
      }
    }
  }

  /**
   * Gets the ingredient under the mouse
   * @param checkX  Mouse X position
   * @param checkY  Mouse Y position
   * @return  Ingredient
   */
  @Nullable
  public FluidVolume getIngredient(int checkX, int checkY) {
    if (withinTank(checkX, checkY)) {
      int index = getFluidFromMouse(calcLiquidHeights(false), checkY);
      if (index != -1) {
        return tank.getFluidInTank(index);
      }
    }
    return null;
  }


  /* Utils */

  /**
   * Calculate the rendering heights for all the liquids
   *
   * @param liquids  The liquids
   * @param capacity Max capacity of smeltery, to calculate how much height one liquid takes up
   * @param height   Maximum height, basically represents how much height full capacity is
   * @param min      Minimum amount of height for a fluid. A fluid can never have less than this value height returned
   * @return Array with heights corresponding to input-list liquids
   */
  public static int[] calcLiquidHeights(List<FluidVolume> liquids, FluidAmount capacity, int height, int min) {
    int[] fluidHeights = new int[liquids.size()];

    FluidAmount totalFluidAmount = FluidAmount.ZERO;
    if (liquids.size() > 0) {
      for(int i = 0; i < liquids.size(); i++) {
        FluidVolume liquid = liquids.get(i);

        float h = (float) liquid.getAmount_F().div(capacity).asInexactDouble();
        totalFluidAmount.add(liquid.amount());
        fluidHeights[i] = Math.max(min, (int) Math.ceil(h * (float) height));
      }

      // if not completely full, leave a few pixels for the empty tank display
      if(totalFluidAmount.isLessThan(capacity)) {
        height -= min;
      }

      // check if we have enough height to render everything, if not remove pixels from the tallest liquid
      int sum;
      do {
        sum = 0;
        int biggest = -1;
        int m = 0;
        for(int i = 0; i < fluidHeights.length; i++) {
          sum += fluidHeights[i];
          if(fluidHeights[i] > biggest) {
            biggest = fluidHeights[i];
            m = i;
          }
        }

        // we can't get a result without going negative
        if(fluidHeights[m] == 0) {
          break;
        }

        // remove a pixel from the biggest one
        if(sum > height) {
          fluidHeights[m]--;
        }
      } while(sum > height);
    }

    return fluidHeights;
  }
}
