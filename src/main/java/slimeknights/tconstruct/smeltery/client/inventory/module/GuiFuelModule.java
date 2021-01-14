package slimeknights.tconstruct.smeltery.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.screen.ScalableElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule.FuelInfo;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * GUI component handling the fuel module
 */
@RequiredArgsConstructor
public class GuiFuelModule {
  private static final ScalableElementScreen FIRE = new ScalableElementScreen(176, 136, 14, 14, 256, 256);

  // tooltips
  private static final String TOOLTIP_NO_FUEL = Util.makeTranslationKey("gui", "melting.fuel.empty");
  private static final String TOOLTIP_TEMPERATURE = Util.makeTranslationKey("gui", "melting.fuel.temperature");
  private static final String TOOLTIP_INVALID_FUEL = Util.makeTranslationKey("gui", "melting.fuel.invalid");

  private final ContainerScreen<?> screen;
  private final FuelModule fuelModule;
  private final int x, y, width, height;
  private final int fireX, fireY;

  private FuelInfo fuelInfo = null;

  /**
   * Checks if the fuel tank is hovered
   * @param checkX  X position to check
   * @param checkY  Y position to check
   * @return  True if hovered
   */
  private boolean isHovered(int checkX, int checkY) {
    return GuiUtil.isHovered(checkX, checkY, x - 1, y - 1, width + 2, height + 2);
  }

  /**
   * Draws the fuel at the correct location
   * @param matrices  Matrix stack instance
   */
  public void draw(MatrixStack matrices) {
    // draw fire
    int fuel = fuelModule.getFuel();
    if (fuel > 0) {
      FIRE.drawScaledYUp(matrices, fireX + screen.guiLeft, fireY + screen.guiTop, 14 * fuel / fuelModule.getFuelQuality());
    }

    // draw tank second, it changes the image
    // fuel info is stored in a field to share with other methods
    fuelInfo = fuelModule.getFuelInfo();
    if (!fuelInfo.isEmpty()) {
      GuiUtil.renderFluidTank(matrices, screen, fuelInfo.getFuel(), fuelInfo.getTotalAmount(), fuelInfo.getCapacity(), x, y, width, height, 100);
    }
  }

  /**
   * Highlights the hovered fuel
   * @param matrices  Matrix stack instance
   * @param checkX    Top corner relative mouse X
   * @param checkY    Top corner relative mouse Y
   */
  public void renderHighlight(MatrixStack matrices, int checkX, int checkY) {
    if (isHovered(checkX, checkY)) {
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

    if (isHovered(checkX, checkY)) {
      List<ITextComponent> tooltip;
      // fetch info from shared field
      if (fuelInfo != null && !fuelInfo.isEmpty()) {
        FluidStack fluid = fuelInfo.getFuel();
        tooltip = FluidTooltipHandler.getFluidTooltip(fluid, fuelInfo.getTotalAmount());
        int temperature = fuelModule.getTemperature();
        if (temperature > 0) {
          tooltip.add(1, new TranslationTextComponent(TOOLTIP_TEMPERATURE, temperature).mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
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

  /**
   * Gets the fluid stack under the mouse
   * @param checkX  Mouse X position
   * @param checkY  Mouse Y position
   * @return  Fluid stack under mouse
   */
  @Nullable
  public FluidStack getIngredient(int checkX, int checkY) {
    if (isHovered(checkX, checkY) && fuelInfo != null && !fuelInfo.isEmpty()) {
      return fuelInfo.getFuel();
    }
    return null;
  }
}
