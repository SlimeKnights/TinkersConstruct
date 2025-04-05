package slimeknights.tconstruct.smeltery.client.screen.module;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule.FuelInfo;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * GUI component handling the fuel module
 */
public class GuiFuelModule implements IScreenWithFluidTank, ClickableTankModule {
  // tooltips
  private static final String TOOLTIP_TEMPERATURE = TConstruct.makeTranslationKey("gui", "melting.fuel.temperature");
  private static final List<Component> TOOLTIP_NO_TANK = Collections.singletonList(Component.translatable(TConstruct.makeTranslationKey("gui", "melting.fuel.no_tank")));
  private static final List<Component> TOOLTIP_NO_FUEL = Collections.singletonList(Component.translatable(TConstruct.makeTranslationKey("gui", "melting.fuel.empty")));
  private static final Component TOOLTIP_INVALID_FUEL = Component.translatable(TConstruct.makeTranslationKey("gui", "melting.fuel.invalid")).withStyle(ChatFormatting.RED);
  private static final Component TOOLTIP_SOLID_FUEL = Component.translatable(TConstruct.makeTranslationKey("gui", "melting.fuel.solid"));

  private final AbstractContainerScreen<?> screen;
  private final FuelModule fuelModule;
  /** location to draw the tank */
  private final int x, y, width, height;
  /** Location of the fluid for JEI */
  private final Rect2i fluidLoc;
  /** location to draw the fire */
  private final int fireX, fireY;
  /** If true, UI has a fuel slot */
  private final boolean hasFuelSlot;
  /** Scalable fire instance */
  private final ScalableElementScreen fire;

  private FuelInfo fuelInfo = FuelInfo.EMPTY;

  public GuiFuelModule(AbstractContainerScreen<?> screen, FuelModule fuelModule, int x, int y, int width, int height, int fireX, int fireY, boolean hasFuelSlot, ResourceLocation background) {
    this.screen = screen;
    this.fuelModule = fuelModule;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    this.fluidLoc = new Rect2i(x - 1, y - 1, width + 2, height + 2);
    this.fireX = fireX;
    this.fireY = fireY;
    this.hasFuelSlot = hasFuelSlot;
    this.fire = makeFire(background);
  }

  @Override
  public AbstractContainerMenu getMenu() {
    return screen.getMenu();
  }

  @Override
  public boolean isHovered(int checkX, int checkY) {
    return GuiUtil.isHovered(checkX, checkY, x - 1, y - 1, width + 2, height + 2);
  }

  /** Gets the current height of the fluid */
  private int getFluidHeight() {
    int capacity = fuelInfo.getCapacity();
    if (capacity == 0) {
      return height;
    }
    return height * fuelInfo.getTotalAmount() / capacity;
  }

  @Override
  public boolean isFluidHovered(int checkY) {
    return checkY > (y + height) - getFluidHeight();
  }


  /**
   * Draws the fuel at the correct location
   * @param graphics  Matrix stack instance
   */
  public void draw(GuiGraphics graphics) {
    // draw fire
    int fuel = fuelModule.getFuel();
    int fuelQuality = fuelModule.getFuelQuality();
    if (fuel > 0 && fuelQuality > 0) {
      fire.drawScaledYUp(graphics, fireX + screen.leftPos, fireY + screen.topPos, 14 * fuel / fuelQuality);
    }

    // draw tank second, it changes the image
    // store fuel info into a field for other methods, this one updates most often
    if (!hasFuelSlot) {
      fuelInfo = fuelModule.getFuelInfo();
      if (!fuelInfo.isEmpty()) {
        GuiUtil.renderFluidTank(graphics.pose(), screen, fuelInfo.getFluid(), fuelInfo.getTotalAmount(), fuelInfo.getCapacity(), x, y, width, height, 100);
      }
    }
  }

  /**
   * Highlights the hovered fuel
   * @param graphics  GuiGraphics instance
   * @param checkX    Top corner relative mouse X
   * @param checkY    Top corner relative mouse Y
   */
  public void renderHighlight(GuiGraphics graphics, int checkX, int checkY) {
    if (isHovered(checkX, checkY)) {
      // if there is a fuel slot, render highlight lower
      if (hasFuelSlot) {
        if (checkY > y + 18) {
          GuiUtil.renderHighlight(graphics, x, y + 18, width, height - 18);
        }
      } else {
        // full fluid highlight
        GuiUtil.renderHighlight(graphics, x, y, width, height);
      }
    }
  }

  /**
   * Adds the tooltip for the fuel
   * @param graphics  GuiGraphics instance
   * @param mouseX    Mouse X position
   * @param mouseY    Mouse Y position
   */
  public void addTooltip(GuiGraphics graphics, int mouseX, int mouseY, boolean hasTank) {
    int checkX = mouseX - screen.leftPos;
    int checkY = mouseY - screen.topPos;

    if (isHovered(checkX, checkY)) {
      List<Component> tooltip;
      // if an item or we have a fuel slot, do item tooltip
      if (hasFuelSlot || fuelInfo.isItem()) {
        // if there is a fuel slot, start below the fuel slot
        if (!hasFuelSlot || checkY > y + 18) {
          if (hasTank) {
            // no invalid fuel, we assume the slot is validated (hasFuelSlot is only true for the heater which validates)
            int temperature = fuelModule.getTemperature();
            if (temperature > 0) {
              tooltip = Arrays.asList(TOOLTIP_SOLID_FUEL, Component.translatable(TOOLTIP_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            } else {
              tooltip = TOOLTIP_NO_FUEL;
            }
          } else {
            tooltip = TOOLTIP_NO_TANK;
          }
        } else {
          tooltip = Collections.emptyList();
        }
      } else if (!fuelInfo.isEmpty()) {
        FluidStack fluid = fuelInfo.getFluid();
        tooltip = FluidTooltipHandler.getFluidTooltip(fluid, fuelInfo.getTotalAmount());
        int temperature = fuelInfo.getTemperature();
        if (temperature > 0) {
          tooltip.add(1, Component.translatable(TOOLTIP_TEMPERATURE, temperature).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        } else {
          tooltip.add(1, TOOLTIP_INVALID_FUEL);
        }
      } else {
        tooltip = hasTank ? TOOLTIP_NO_FUEL : TOOLTIP_NO_TANK;
      }

      graphics.renderComponentTooltip(screen.font, tooltip, mouseX, mouseY);
    }
  }

  @Override
  @Nullable
  public FluidLocation getFluidUnderMouse(int checkX, int checkY) {
    if (!hasFuelSlot && isHovered(checkX, checkY) && !fuelInfo.isEmpty()) {
      return new FluidLocation(fuelInfo.getFluid(), fluidLoc);
    }
    return null;
  }

  /** Creates the fire element from the standard location */
  public static ScalableElementScreen makeFire(ResourceLocation background) {
    return new ScalableElementScreen(background, 176, 136, 14, 14, 256, 256);
  }
}
