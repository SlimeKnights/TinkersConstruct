package slimeknights.tconstruct.smeltery.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.block.entity.controller.HeatingStructureBlockEntity;
import slimeknights.tconstruct.smeltery.block.entity.module.FuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.screen.module.HeatingStructureSideInventoryScreen;
import slimeknights.tconstruct.smeltery.menu.HeatingStructureContainerMenu;

import javax.annotation.Nullable;
import java.util.Objects;

public class HeatingStructureScreen extends MultiModuleScreen<HeatingStructureContainerMenu> implements IScreenWithFluidTank {
  public static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 76, 52, 52, 256, 256);

  private final HeatingStructureSideInventoryScreen sideInventory;
  private final HeatingStructureBlockEntity te;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  private final GuiFuelModule fuel;

  public HeatingStructureScreen(HeatingStructureContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);

    HeatingStructureBlockEntity te = container.getTile();
    if (te != null) {
      this.te = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, SCALA.w, SCALA.h, Objects.requireNonNull(te.getType().getRegistryName()));
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new HeatingStructureSideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, HeatingStructureContainerMenu.calcColumns(slots));
      addModule(sideInventory);
      FuelModule fuelModule = te.getFuelModule();
      this.melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, sideInventory::shouldDrawSlot);
      this.fuel = new GuiFuelModule(this, fuelModule, 71, 32, 12, 36, 70, 15, false);
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
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    // draw stuff with background
    GuiUtil.drawBackground(matrices, this, BACKGROUND);
    // fuel
    if (fuel != null) {
      fuel.draw(matrices);
    }

    // draw other components
    super.renderBg(matrices, partialTicks, mouseX, mouseY);

    // render fluids
    if (tank != null) tank.renderFluids(matrices);
  }

  @Override
  protected void renderLabels(PoseStack matrices, int mouseX, int mouseY) {
    super.renderLabels(matrices, mouseX, mouseY);

    assert minecraft != null;
    RenderUtils.setup(BACKGROUND);
    SCALA.draw(matrices, 8, 16);

    // highlight hovered fluids
    if (tank != null) tank.renderHighlight(matrices, mouseX, mouseY);
    if (fuel != null) fuel.renderHighlight(matrices, mouseX - this.leftPos, mouseY - this.topPos);

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) melting.drawHeatBars(matrices);
  }

  @Override
  protected void renderTooltip(PoseStack matrices, int mouseX, int mouseY) {
    super.renderTooltip(matrices, mouseX, mouseY);

    // fluid tooltips
    if (tank != null) tank.drawTooltip(matrices, mouseX, mouseY);
    if (fuel != null) {
      boolean hasTank = false;
      if (te.getStructure() != null) {
        hasTank = te.getStructure().hasTanks();
      }
      fuel.addTooltip(matrices, mouseX, mouseY, hasTank);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0 && tank != null) {
      tank.handleClick((int)mouseX - cornerX, (int)mouseY - cornerY);
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Nullable
  @Override
  public Object getIngredientUnderMouse(double mouseX, double mouseY) {
    Object ingredient = null;

    int checkX = (int) mouseX - cornerX;
    int checkY = (int) mouseY - cornerY;

    // try fuel first, its faster
    if (fuel != null) ingredient = fuel.getIngredient(checkX, checkY);
    // then try tank
    if (tank != null && ingredient == null) ingredient = tank.getIngredient(checkX, checkY);

    return ingredient;
  }
}
