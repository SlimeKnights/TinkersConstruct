package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.controller.ControllerBlock;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.inventory.module.HeatingStructureSideInventoryScreen;
import slimeknights.tconstruct.smeltery.inventory.HeatingStructureContainer;
import slimeknights.tconstruct.smeltery.tileentity.controller.HeatingStructureTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;

import javax.annotation.Nullable;

public class HeatingStructureScreen extends MultiModuleScreen<HeatingStructureContainer> implements IScreenWithFluidTank {
  public static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 76, 52, 52, 256, 256);

  private final HeatingStructureSideInventoryScreen sideInventory;
  private final HeatingStructureTileEntity te;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  private final GuiFuelModule fuel;

  public HeatingStructureScreen(HeatingStructureContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    HeatingStructureTileEntity te = container.getTile();
    if (te != null) {
      this.te = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, SCALA.w, SCALA.h);
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new HeatingStructureSideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, HeatingStructureContainer.calcColumns(slots));
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
  public void tick() {
    super.tick();
    // if the smeltery becomes invalid or the slot size changes, kill the UI
    if (te == null || !te.getBlockState().get(ControllerBlock.IN_STRUCTURE)
        || te.getMeltingInventory().getSlots() != sideInventory.getSlotCount()) {
      this.closeScreen();
    }
  }
  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);
    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);


    // render fluids
    if (tank != null) tank.renderFluids(matrices);

    // fuel
    if (fuel != null) {
      getMinecraft().getTextureManager().bindTexture(BACKGROUND);
      fuel.draw(matrices);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);

    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 8, 16);

    // highlight hovered fluids
    if (tank != null) tank.renderHighlight(matrices, mouseX, mouseY);
    if (fuel != null) fuel.renderHighlight(matrices, mouseX - this.guiLeft, mouseY - this.guiTop);

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) melting.drawHeatBars(matrices);
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

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
