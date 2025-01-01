package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.entity.controller.MelterBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiTankModule;
import slimeknights.tconstruct.smeltery.menu.MelterContainerMenu;

public class MelterScreen extends AbstractContainerScreen<MelterContainerMenu> implements IScreenWithFluidTank {
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/melter.png");
  private static final ElementScreen SCALA = new ElementScreen(BACKGROUND, 176, 0, 52, 52, 256, 256);
  private static final ElementScreen FUEL_SLOT = new ElementScreen(BACKGROUND, 176, 52, 18, 36, 256, 256);
  private static final ElementScreen FUEL_TANK = new ElementScreen(BACKGROUND, 194, 52, 14, 38, 256, 256);

  private final GuiMeltingModule melting;
  private final GuiFuelModule fuel;
  private final GuiTankModule tank;
  public MelterScreen(MelterContainerMenu container, Inventory inv, Component name) {
    super(container, inv, name);
    MelterBlockEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, slot -> true, BACKGROUND);
      fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot(), BACKGROUND);
      tank = new GuiTankModule(this, te.getTank(), 90, 16, 52, 52, MelterContainerMenu.TOOLTIP_FORMAT);
    } else {
      melting = null;
      fuel = null;
      tank = null;
    }
  }

  @Override
  public void render(GuiGraphics graphics, int x, int y, float partialTicks) {
    this.renderBackground(graphics);
    super.render(graphics, x, y, partialTicks);
    this.renderTooltip(graphics, x, y);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(graphics, this, BACKGROUND);

    // fuel
    if (fuel != null) {
      // draw the correct background for the fuel type
      if (menu.isHasFuelSlot()) {
        FUEL_SLOT.draw(graphics, leftPos + 150, topPos + 31);
      } else {
        FUEL_TANK.draw(graphics, leftPos + 152, topPos + 31);
      }
      fuel.draw(graphics);
    }

    // fluids
    if (tank != null) tank.draw(graphics);
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderLabels(graphics, mouseX, mouseY);
    int checkX = mouseX - this.leftPos;
    int checkY = mouseY - this.topPos;

    // highlight hovered tank
    if (tank != null) tank.highlightHoveredFluid(graphics, checkX, checkY);
    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(graphics, checkX, checkY);

    // scala
    SCALA.draw(graphics, 90, 16);

    // heat bars
    if (melting != null) {
      melting.drawHeatBars(graphics);
    }
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    // tank tooltip
    if (tank != null) tank.renderTooltip(graphics, mouseX, mouseY);

    // heat tooltips
    if (melting != null) melting.drawHeatTooltips(graphics, mouseX, mouseY);

    // fuel tooltip
    if (fuel != null) fuel.addTooltip(graphics, mouseX, mouseY, true);
  }

  @Override
  public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
    int checkX = mouseX - leftPos;
    int checkY = mouseY - topPos;

    // try fuel first, its faster
    if (fuel != null) {
      FluidLocation ingredient = fuel.getFluidUnderMouse(checkX, checkY);
      if (ingredient != null) {
        return ingredient;
      }
    }
    // melter tanks are about as fast
    if (tank != null) {
      return tank.getFluidUnderMouse(checkX, checkY);
    }
    return null;
  }
}
