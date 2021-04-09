package slimeknights.tconstruct.smeltery.client.inventory.module;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import alexiil.mc.lib.attributes.fluid.volume.FluidVolume;
import net.minecraftforge.fluids.IFluidTank;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;

import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Module handling the melter tank UI display
 */
@RequiredArgsConstructor
public class GuiTankModule {
  private final HandledScreen<?> screen;
  private final IFluidTank tank;
  private final int x, y, width, height;

  /**
   * Checks if the tank is hovered over
   * @param checkX  Screen relative mouse X
   * @param checkY  Screen relative mouse Y
   * @return  True if hovered
   */
  private boolean isHovered(int checkX, int checkY) {
    return GuiUtil.isHovered(checkX, checkY, x - 1, y - 1, width + 2, height + 2);
  }

  /**
   * Gets the height of the fluid in pixels
   * @return  Fluid height
   */
  private int getFluidHeight() {
    return height * tank.getFluidAmount() / tank.getCapacity();
  }

  /**
   * Draws the tank
   * @param matrices  Matrix stack instance
   */
  public void draw(MatrixStack matrices) {
    GuiUtil.renderFluidTank(matrices, screen, tank.getFluid(), tank.getCapacity(), x, y, width, height, 100);
  }

  /**
   * Highlights the hovered fluid
   * @param matrices  Matrix stack instance
   * @param checkX    Mouse X position, screen relative
   * @param checkY    Mouse Y position, screen relative
   */
  public void highlightHoveredFluid(MatrixStack matrices, int checkX, int checkY) {
    // highlight hovered fluid
    if (isHovered(checkX, checkY)) {
      int fluidHeight = getFluidHeight();
      int middle = y + height - fluidHeight;

      // highlight just fluid
      if (checkY > middle) {
        GuiUtil.renderHighlight(matrices, x, middle, width, fluidHeight);
      } else {
        // or highlight empty
        GuiUtil.renderHighlight(matrices, x, y, width, height - fluidHeight);
      }
    }
  }

  /**
   * Renders the tooltip for hovering over the tank
   * @param matrices  Matrix stack instance
   * @param mouseX    Global mouse X position
   * @param mouseY    Global mouse Y position
   */
  public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - screen.x;
    int checkY = mouseY - screen.y;

    if (isHovered(checkX, checkY)) {
      FluidAmount amount = tank.getFluidAmount();
      FluidAmount capacity = tank.getCapacity();

      // if hovering over the fluid, display with name
      final List<Text> tooltip;
      if (checkY > (y + height) - getFluidHeight()) {
        tooltip = FluidTooltipHandler.getFluidTooltip(tank.getFluid());
      } else {
        // function to call for amounts
        BiConsumer<FluidAmount, List<Text>> formatter = Util.isShiftKeyDown()
                                                              ? FluidTooltipHandler::appendBuckets
                                                              : FluidTooltipHandler::appendIngots;

        // add tooltips
        tooltip = new ArrayList<>();
        tooltip.add(new TranslatableText(GuiSmelteryTank.TOOLTIP_CAPACITY));
        formatter.accept(capacity, tooltip);
        if (!capacity.equals(amount)) {
          tooltip.add(new TranslatableText(GuiSmelteryTank.TOOLTIP_AVAILABLE));
          formatter.accept(capacity - amount, tooltip);
        }

        // add shift message
        //tooltip.add("");
        FluidTooltipHandler.appendShift(tooltip);
      }

      // TODO: func_243308_b->renderTooltip
      screen.renderTooltip(matrices, tooltip, mouseX, mouseY);
    }
  }

  /**
   * Gets the fluid stack under the mouse
   * @param checkX  X position to check
   * @param checkY  Y position to check
   * @return  Fluid stack under mouse
   */
  @Nullable
  public FluidVolume getIngreientUnderMouse(int checkX, int checkY) {
    if (isHovered(checkX, checkY) && checkY > (y + height) - getFluidHeight()) {
      return tank.getFluid();
    }
    return null;
  }

}
