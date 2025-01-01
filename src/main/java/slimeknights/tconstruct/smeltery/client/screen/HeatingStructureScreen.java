package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.screen.module.HeatingStructureSideInventoryScreen;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;

import java.util.Objects;

public class HeatingStructureScreen extends MultiModuleScreen<HeatingStructureContainerMenu> implements IScreenWithFluidTank {
  public static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(BACKGROUND, 176, 76, 52, 52, 256, 256);

  private final HeatingStructureSideInventoryScreen sideInventory;
  private final HeatingStructureBlockEntity te;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  private final GuiFuelModule fuel;

  @SuppressWarnings("deprecation")  // no you're deprecated Forge
  public HeatingStructureScreen(HeatingStructureContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);

    HeatingStructureBlockEntity te = container.getTile();
    if (te != null) {
      this.te = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, SCALA.w, SCALA.h, Objects.requireNonNull(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(te.getType())));
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new HeatingStructureSideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, HeatingStructureContainerMenu.calcColumns(slots));
      addModule(sideInventory);
      FuelModule fuelModule = te.getFuelModule();
      this.melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, sideInventory::shouldDrawSlot, BACKGROUND);
      this.fuel = new GuiFuelModule(this, fuelModule, 71, 32, 12, 36, 70, 15, false, BACKGROUND);
    } else {
      this.te = null;
      this.tank = null;
      this.melting = null;
      this.fuel = null;
      this.sideInventory = null;
    }
  }


  @Override
  protected void containerTick() {
    super.containerTick();
    // if the smeltery becomes invalid or the slot size changes, kill the UI
    if (te == null || !te.getBlockState().getValue(ControllerBlock.IN_STRUCTURE)
        || te.getMeltingInventory().getSlots() != sideInventory.getSlotCount()) {
      this.onClose();
    }
  }
  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
    // draw stuff with background
    GuiUtil.drawBackground(graphics, this, BACKGROUND);
    // fuel
    if (fuel != null) {
      fuel.draw(graphics);
    }

    // draw other components
    super.renderBg(graphics, partialTicks, mouseX, mouseY);

    // render fluids
    if (tank != null) tank.renderFluids(graphics.pose());
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderLabels(graphics, mouseX, mouseY);

    assert minecraft != null;
    SCALA.draw(graphics, 8, 16);

    // highlight hovered fluids
    if (tank != null) tank.renderHighlight(graphics, mouseX, mouseY);
    if (fuel != null) fuel.renderHighlight(graphics, mouseX - this.leftPos, mouseY - this.topPos);

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) melting.drawHeatBars(graphics);
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    // fluid tooltips
    if (tank != null) tank.drawTooltip(graphics, mouseX, mouseY);
    if (fuel != null) {
      boolean hasTank = false;
      if (te.getStructure() != null) {
        hasTank = te.getStructure().hasTanks();
      }
      fuel.addTooltip(graphics, mouseX, mouseY, hasTank);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0 && tank != null) {
      tank.handleClick((int)mouseX - cornerX, (int)mouseY - cornerY);
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
    int checkX = mouseX - cornerX;
    int checkY = mouseY - cornerY;

    // try fuel first, its faster
    if (fuel != null)  {
      FluidLocation ingredient = fuel.getFluidUnderMouse(checkX, checkY);
      if (ingredient != null) {
        return ingredient;
      }
    }
    // then try tank
    if (tank != null) {
      return tank.getFluidUnderMouse(checkX, checkY);
    }
    return null;
  }
}
