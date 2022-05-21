package slimeknights.tconstruct.smeltery.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.block.entity.module.alloying.MixerAlloyTank;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiTankModule;
import slimeknights.tconstruct.smeltery.menu.AlloyerContainerMenu;

import javax.annotation.Nullable;

public class AlloyerScreen extends AbstractContainerScreen<AlloyerContainerMenu> implements IScreenWithFluidTank {
  private static final int[] INPUT_TANK_START_X = {54, 22, 38, 70, 6};
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/alloyer.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 34, 52, 256, 256);
  private static final ElementScreen FUEL_SLOT = new ElementScreen(176, 52, 18, 36, 256, 256);
  private static final ElementScreen FUEL_TANK = new ElementScreen(194, 52, 14, 38, 256, 256);
  private static final ElementScreen INPUT_TANK = new ElementScreen(208, 52, 16, 54, 256, 256);

  private final GuiFuelModule fuel;
  private final GuiTankModule outputTank;
  private GuiTankModule[] inputTanks = new GuiTankModule[0];
  public AlloyerScreen(AlloyerContainerMenu container, Inventory inv, Component name) {
    super(container, inv, name);
    AlloyerBlockEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot());
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
  public void render(PoseStack matrices, int x, int y, float partialTicks) {
    this.renderBackground(matrices);
    super.render(matrices, x, y, partialTicks);
    this.renderTooltip(matrices, x, y);
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);

    // fluids
    if (outputTank != null) outputTank.draw(matrices);

    // draw tank backgrounds first, then draw tank contents, less binding
    RenderUtils.setup(BACKGROUND);
    for (GuiTankModule tankModule : inputTanks) {
      INPUT_TANK.draw(matrices, tankModule.getX() - 1 + this.leftPos, tankModule.getY() - 1 + this.topPos);
    }

    // fuel
    if (fuel != null) {
      // draw the correct background for the fuel type
      if (menu.isHasFuelSlot()) {
        FUEL_SLOT.draw(matrices, leftPos + 150, topPos + 31);
      } else {
        FUEL_TANK.draw(matrices, leftPos + 152, topPos + 31);
      }
      fuel.draw(matrices);
    }

    // draw tank contents last, reduces bind calls
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.draw(matrices);
    }
  }

  @Override
  protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    GuiUtil.drawContainerNames(matrices, this, this.font, this.playerInventoryTitle);
    int checkX = mouseX - this.leftPos;
    int checkY = mouseY - this.topPos;

    // highlight hovered tank
    if (outputTank != null) outputTank.highlightHoveredFluid(matrices, checkX, checkY);
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.highlightHoveredFluid(matrices, checkX, checkY);
    }

    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(matrices, checkX, checkY);

    // scala
    assert minecraft != null;
    RenderUtils.setup(BACKGROUND);
    SCALA.draw(matrices, 114, 16);
  }

  @Override
  protected void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
    super.renderTooltip(matrices, mouseX, mouseY);

    // tank tooltip
    if (outputTank != null) outputTank.renderTooltip(matrices, mouseX, mouseY);

    for (GuiTankModule tankModule : inputTanks) {
      tankModule.renderTooltip(matrices, mouseX, mouseY);
    }

    // fuel tooltip
    if (fuel != null) fuel.addTooltip(matrices, mouseX, mouseY, true);
  }

  @Nullable
  @Override
  public Object getIngredientUnderMouse(double mouseX, double mouseY) {
    Object ingredient = null;
    int checkX = (int) mouseX - leftPos;
    int checkY = (int) mouseY - topPos;

    // try fuel first, its faster
    if (fuel != null) {
      ingredient = fuel.getIngredient(checkX, checkY);
    }

    // next output tank
    if (outputTank != null && ingredient == null) {
      ingredient = outputTank.getIngreientUnderMouse(checkX, checkY);
    }

    // finally input tanks
    if (ingredient == null) {
      for (GuiTankModule tankModule : inputTanks) {
        ingredient = tankModule.getIngreientUnderMouse(checkX, checkY);
        if (ingredient != null) {
          return ingredient;
        }
      }
    }

    return ingredient;
  }
}
