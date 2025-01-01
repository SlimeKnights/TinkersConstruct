package slimeknights.tconstruct.smeltery.client.screen.module;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Module handling the melter tank UI display
 */
public class GuiTankModule implements IScreenWithFluidTank {
  /** Tooltip for when the capacity is 0, it breaks some stuff */
  private static final Component NO_CAPACITY = Component.translatable(Mantle.makeDescriptionId("gui", "fluid.millibucket"), 0).withStyle(ChatFormatting.GRAY);

  private static final int TANK_INDEX = 0;
  private final AbstractContainerScreen<?> screen;
  private final IFluidHandler tank;
  @Getter
  private final int x, y, width, height;
  private final Rect2i fluidLoc;
  private final BiConsumer<Integer,List<Component>> formatter;

  public GuiTankModule(AbstractContainerScreen<?> screen, IFluidHandler tank, int x, int y, int width, int height, ResourceLocation tooltipId) {
    this.screen = screen;
    this.tank = tank;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.fluidLoc = new Rect2i(x, y, width, height);
    this.formatter = (amount, tooltip) -> FluidTooltipHandler.appendNamedList(tooltipId, amount, tooltip);
  }

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
    int capacity =  tank.getTankCapacity(TANK_INDEX);
    if (capacity == 0) {
      return height;
    }
    return height * tank.getFluidInTank(TANK_INDEX).getAmount() / capacity;
  }

  /**
   * Draws the tank
   * @param graphics  GuiGraphics instance
   */
  public void draw(GuiGraphics graphics) {
    GuiUtil.renderFluidTank(graphics.pose(), screen, tank.getFluidInTank(TANK_INDEX), tank.getTankCapacity(TANK_INDEX), x, y, width, height, 100);
  }

  /**
   * Highlights the hovered fluid
   * @param graphics  GuiGraphics instance
   * @param checkX    Mouse X position, screen relative
   * @param checkY    Mouse Y position, screen relative
   */
  public void highlightHoveredFluid(GuiGraphics graphics, int checkX, int checkY) {
    // highlight hovered fluid
    if (isHovered(checkX, checkY)) {
      int fluidHeight = getFluidHeight();
      int middle = y + height - fluidHeight;

      // highlight just fluid
      if (checkY > middle) {
        GuiUtil.renderHighlight(graphics, x, middle, width, fluidHeight);
      } else {
        // or highlight empty
        GuiUtil.renderHighlight(graphics, x, y, width, height - fluidHeight);
      }
    }
  }

  /**
   * Renders the tooltip for hovering over the tank
   * @param graphics  GuiGraphics instance
   * @param mouseX    Global mouse X position
   * @param mouseY    Global mouse Y position
   */
  public void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    int checkX = mouseX - screen.leftPos;
    int checkY = mouseY - screen.topPos;

    if (isHovered(checkX, checkY)) {
      FluidStack fluid = tank.getFluidInTank(TANK_INDEX);
      int amount = fluid.getAmount();
      int capacity = tank.getTankCapacity(TANK_INDEX);

      // if hovering over the fluid, display with name
      final List<Component> tooltip;
      if (capacity > 0 && checkY > (y + height) - getFluidHeight()) {
        tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
      } else {
        // function to call for amounts
        BiConsumer<Integer, List<Component>> formatter = Screen.hasShiftDown()
                                                              ? FluidTooltipHandler.BUCKET_FORMATTER
                                                              : this.formatter;

        // add tooltips
        tooltip = new ArrayList<>();
        tooltip.add(GuiSmelteryTank.TOOLTIP_CAPACITY);
        if (capacity == 0) {
          tooltip.add(NO_CAPACITY);
        } else {
          formatter.accept(capacity, tooltip);
          if (capacity != amount) {
            tooltip.add(GuiSmelteryTank.TOOLTIP_AVAILABLE);
            formatter.accept(capacity - amount, tooltip);
          }
          // add shift message
          FluidTooltipHandler.appendShift(tooltip);
        }
      }

      // TODO: renderComponentTooltip->renderTooltip
      graphics.renderComponentTooltip(screen.font, tooltip, mouseX, mouseY);
    }
  }

  @Override
  public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
    if (isHovered(mouseX, mouseY) && mouseY > (y + height) - getFluidHeight()) {
      return new FluidLocation(tank.getFluidInTank(TANK_INDEX), fluidLoc);
    }
    return null;
  }
}
