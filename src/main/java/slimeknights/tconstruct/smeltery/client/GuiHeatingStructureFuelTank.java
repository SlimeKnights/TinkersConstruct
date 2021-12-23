package slimeknights.tconstruct.smeltery.client;

import com.google.common.collect.Lists;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileHeatingStructureFuelTank;

public class GuiHeatingStructureFuelTank extends GuiMultiModule {

  protected TileHeatingStructureFuelTank.FuelInfo fuelInfo;

  public GuiHeatingStructureFuelTank(ContainerMultiModule<?> container) {
    super(container);
  }

  /**
   * Draws the fuel at the specified location
   *
   * @param displayX Display X location, excluding cornerX
   * @param displayY Display Y location, excluding cornerY
   * @param width    Width
   * @param height   Height of the whole area, note that displayed size may differ due to how full the fuel is
   */
  protected void drawFuel(int displayX, int displayY, int width, int height) {
    if(fuelInfo.fluid != null && fuelInfo.fluid.amount > 0) {
      int x = displayX + cornerX;
      int y = displayY + cornerY + height;
      int w = width;
      int h = height * fuelInfo.fluid.amount / fuelInfo.maxCap;

      GuiUtil.renderTiledFluid(x, y - h, w, h, this.zLevel, fuelInfo.fluid);
    }
  }

  /**
   * Draws the fuel tooltip
   */
  protected void drawFuelTooltip(int mouseX, int mouseY) {
    List<String> text = Lists.newArrayList();
    FluidStack fuel = fuelInfo.fluid;
    text.add(TextFormatting.WHITE + Util.translate("gui.smeltery.fuel"));
    if(fuel != null) {
      if(TinkerRegistry.isSmelteryFuel(fuel)) {
        text.add(fuel.getLocalizedName());
        GuiUtil.liquidToString(fuel, text);
        text.add(Util.translateFormatted("gui.smeltery.fuel.heat", Util.temperatureString(fuelInfo.heat)));
      }
      else {
        text.add(Util.translateFormatted("gui.smeltery.fuel.invalid", fuel.getLocalizedName()));
      }
    }
    else {
      text.add(Util.translate("gui.smeltery.fuel.empty"));
    }
    this.drawHoveringText(text, mouseX, mouseY);
  }
}
