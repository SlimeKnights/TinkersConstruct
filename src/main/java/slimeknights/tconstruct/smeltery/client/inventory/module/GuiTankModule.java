package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.library.client.FluidTooltipHandler;
import slimeknights.tconstruct.library.client.GuiUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Module handling the melter tank UI display
 */
@RequiredArgsConstructor
public class GuiTankModule {
  private static final int TANK_INDEX = 0;
  private final AbstractContainerScreen<?> screen;
  private final IFluidHandler tank;
  @Getter
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
    return height * tank.getFluidInTank(TANK_INDEX).getAmount() / tank.getTankCapacity(TANK_INDEX);
  }

  /**
   * Draws the tank
   * @param matrices  Matrix stack instance
   */
  public void draw(PoseStack matrices) {
    GuiUtil.renderFluidTank(matrices, screen, tank.getFluidInTank(TANK_INDEX), tank.getTankCapacity(TANK_INDEX), x, y, width, height, 100);
  }

  /**
   * Highlights the hovered fluid
   * @param matrices  Matrix stack instance
   * @param checkX    Mouse X position, screen relative
   * @param checkY    Mouse Y position, screen relative
   */
  public void highlightHoveredFluid(PoseStack matrices, int checkX, int checkY) {
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
  public void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - screen.leftPos;
    int checkY = mouseY - screen.topPos;

    if (isHovered(checkX, checkY)) {
      FluidStack fluid = tank.getFluidInTank(TANK_INDEX);
      int amount = fluid.getAmount();
      int capacity = tank.getTankCapacity(TANK_INDEX);

      // if hovering over the fluid, display with name
      final List<Component> tooltip;
      if (checkY > (y + height) - getFluidHeight()) {
        tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
      } else {
        // function to call for amounts
        BiConsumer<Integer, List<Component>> formatter = Screen.hasShiftDown()
                                                              ? FluidTooltipHandler::appendBuckets
                                                              : FluidTooltipHandler::appendIngots;

        // add tooltips
        tooltip = new ArrayList<>();
        tooltip.add(new TranslatableComponent(GuiSmelteryTank.TOOLTIP_CAPACITY));
        formatter.accept(capacity, tooltip);
        if (capacity != amount) {
          tooltip.add(new TranslatableComponent(GuiSmelteryTank.TOOLTIP_AVAILABLE));
          formatter.accept(capacity - amount, tooltip);
        }

        // add shift message
        //tooltip.add("");
        FluidTooltipHandler.appendShift(tooltip);
      }

      // TODO: renderComponentTooltip->renderTooltip
      screen.renderComponentTooltip(matrices, tooltip, mouseX, mouseY);
    }
  }

  /**
   * Gets the fluid stack under the mouse
   * @param checkX  X position to check
   * @param checkY  Y position to check
   * @return  Fluid stack under mouse
   */
  @Nullable
  public FluidStack getIngreientUnderMouse(int checkX, int checkY) {
    if (isHovered(checkX, checkY) && checkY > (y + height) - getFluidHeight()) {
      return tank.getFluidInTank(TANK_INDEX);
    }
    return null;
  }

}
