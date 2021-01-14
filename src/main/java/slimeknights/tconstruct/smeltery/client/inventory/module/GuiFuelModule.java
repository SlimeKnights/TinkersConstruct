package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule.FuelInfo;

import java.util.Collections;
import java.util.List;

/**
 * GUI component handling the fuel module
 */
@RequiredArgsConstructor
public class GuiFuelModule {
  // tooltips
  private static final String TOOLTIP_NO_FUEL = Util.makeTranslationKey("gui", "melting.fuel.empty");
  private static final String TOOLTIP_TEMPERATURE = Util.makeTranslationKey("gui", "melting.fuel.temperature");
  private static final String TOOLTIP_INVALID_FUEL = Util.makeTranslationKey("gui", "melting.fuel.invalid");

  private final ContainerScreen<?> screen;
  private final FuelModule fuelModule;
  private final int x, y, width, height;

  /**
   * Draws the fuel at the correct location
   * @param matrices  Matrix stack instance
   */
  public void drawTank(MatrixStack matrices) {
    FuelInfo fuelInfo = fuelModule.getFuelInfo();
    if (!fuelInfo.isEmpty()) {
      GuiUtil.renderFluidTank(matrices, screen, fuelInfo.getFuel(), fuelInfo.getCapacity(), x, y, width, height, 100);
    }
  }

  /**
   * Highlights the hovered fuel
   * @param matrices  Matrix stack instance
   * @param checkX    Top corner relative mouse X
   * @param checkY    Top corner relative mouse Y
   */
  public void renderHighlight(MatrixStack matrices, int checkX, int checkY) {
    if (GuiUtil.isHovered(checkX, checkY, x - 1, y - 1, width + 2, height + 2)) {
      GuiUtil.renderHighlight(matrices, x, y, width, height);
    }
  }

  /**
   * Adds the tooltip for the fuel
   * @param matrices  Matrix stack instance
   * @param mouseX    Mouse X position
   * @param mouseY    Mouse Y position
   */
  public void addTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    int checkX = mouseX - screen.guiLeft;
    int checkY = mouseY - screen.guiTop;

    if (GuiUtil.isHovered(checkX, checkY, x - 1, y - 1, width + 2, height + 2)) {
      List<ITextComponent> tooltip = null;
      // make sure we have a tank below
      FuelInfo fuelInfo = fuelModule.getFuelInfo();
      if (!fuelInfo.isEmpty()) {
        FluidStack fluid = fuelInfo.getFuel();
        tooltip = FluidTooltipHandler.getFluidTooltip(fluid);
        int temperature = fuelModule.getTemperature();
        if (temperature > 0) {
          tooltip.add(1, new TranslationTextComponent(TOOLTIP_TEMPERATURE, fuelModule.getTemperature()).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        } else {
          tooltip.add(1, new TranslationTextComponent(TOOLTIP_INVALID_FUEL).mergeStyle(TextFormatting.RED));
        }
      } else {
        tooltip = Collections.singletonList(new TranslationTextComponent(TOOLTIP_NO_FUEL));
      }

      // TODO: func_243308_b->renderTooltip
      screen.func_243308_b(matrices, tooltip, mouseX, mouseY);
    }
  }
}
