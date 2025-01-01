package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MixerAlloyTank;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiTankModule;
import slimeknights.tconstruct.smeltery.menu.AlloyerContainerMenu;

public class AlloyerScreen extends AbstractContainerScreen<AlloyerContainerMenu> implements IScreenWithFluidTank {
  private static final int[] INPUT_TANK_START_X = {54, 22, 38, 70, 6};
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/alloyer.png");
  private static final ElementScreen SCALA = new ElementScreen(BACKGROUND, 176, 0, 34, 52, 256, 256);
  private static final ElementScreen FUEL_SLOT = new ElementScreen(BACKGROUND, 176, 52, 18, 36, 256, 256);
  private static final ElementScreen FUEL_TANK = new ElementScreen(BACKGROUND, 194, 52, 14, 38, 256, 256);
  private static final ElementScreen INPUT_TANK = new ElementScreen(BACKGROUND, 208, 52, 16, 54, 256, 256);

  private final GuiFuelModule fuel;
  private final GuiTankModule outputTank;
  private GuiTankModule[] inputTanks = new GuiTankModule[0];
  public AlloyerScreen(AlloyerContainerMenu container, Inventory inv, Component name) {
    super(container, inv, name);
    AlloyerBlockEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot(), BACKGROUND);
      outputTank = new GuiTankModule(this, te.getTank(), 114, 16, 34, 52, AlloyerContainerMenu.TOOLTIP_FORMAT);
      updateTanks();
    } else {
      fuel = null;
      outputTank = null;
    }
  }

  /** Updates the tanks from the tile entity */
  private void updateTanks() {
    AlloyerBlockEntity te = menu.getTile();
    if (te != null) {
      MixerAlloyTank alloyTank = te.getAlloyTank();
      int numTanks = alloyTank.getTanks();
      GuiTankModule[] tanks = new GuiTankModule[numTanks];
      int max = Math.min(numTanks, 5); // only support 5 tanks, any more is impossible
      for (int i = 0; i < max; i++) {
        tanks[i] = new GuiTankModule(this, alloyTank.getFluidHandler(i), INPUT_TANK_START_X[i], 16, 14, 52, AlloyerContainerMenu.TOOLTIP_FORMAT);
      }
      this.inputTanks = tanks;
    }
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    // if the input count changes, update
    AlloyerBlockEntity te = menu.getTile();
    if (te != null && te.getAlloyTank().getTanks() != inputTanks.length) {
      this.updateTanks();
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

    // fluids
    if (outputTank != null) outputTank.draw(graphics);

    // draw tank backgrounds first, then draw tank contents, less binding
    for (GuiTankModule tankModule : inputTanks) {
      INPUT_TANK.draw(graphics, tankModule.getX() - 1 + this.leftPos, tankModule.getY() - 1 + this.topPos);
    }

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

    // draw tank contents last, reduces bind calls
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.draw(graphics);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderLabels(graphics, mouseX, mouseY);
    int checkX = mouseX - this.leftPos;
    int checkY = mouseY - this.topPos;

    // highlight hovered tank
    if (outputTank != null) outputTank.highlightHoveredFluid(graphics, checkX, checkY);
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.highlightHoveredFluid(graphics, checkX, checkY);
    }

    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(graphics, checkX, checkY);

    // scala
    assert minecraft != null;
    SCALA.draw(graphics, 114, 16);
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    // tank tooltip
    if (outputTank != null) outputTank.renderTooltip(graphics, mouseX, mouseY);

    for (GuiTankModule tankModule : inputTanks) {
      tankModule.renderTooltip(graphics, mouseX, mouseY);
    }

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

    // next output tank
    if (outputTank != null) {
      FluidLocation ingredient = outputTank.getFluidUnderMouse(checkX, checkY);
      if (ingredient != null) {
        return ingredient;
      }
    }

    // finally input tanks
    for (GuiTankModule tankModule : inputTanks) {
      FluidLocation ingredient = tankModule.getFluidUnderMouse(checkX, checkY);
      if (ingredient != null) {
        return ingredient;
      }
    }

    return null;
  }
}
