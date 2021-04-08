package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.inventory.module.SmelterySideInventoryScreen;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;

import org.jetbrains.annotations.Nullable;

public class SmelteryScreen extends MultiModuleScreen<SmelteryContainer> implements IScreenWithFluidTank {
  public static final Identifier BACKGROUND = Util.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 76, 52, 52, 256, 256);

  private final SmelterySideInventoryScreen sideInventory;
  private final SmelteryTileEntity smeltery;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  private final GuiFuelModule fuel;

  public SmelteryScreen(SmelteryContainer container, PlayerInventory playerInventory, Text title) {
    super(container, playerInventory, title);

    SmelteryTileEntity te = container.getTile();
    if (te != null) {
      this.smeltery = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, SCALA.w, SCALA.h);
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new SmelterySideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, SmelteryContainer.calcColumns(slots));
      addModule(sideInventory);
      FuelModule fuelModule = te.getFuelModule();
      this.melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, sideInventory::shouldDrawSlot);
      this.fuel = new GuiFuelModule(this, fuelModule, 71, 32, 12, 36, 70, 15, false);
    } else {
      this.smeltery = null;
      this.tank = null;
      this.melting = null;
      this.fuel = null;
      this.sideInventory = null;
    }
  }

  @Override
  public void tick() {
    super.tick();
    // if the smeltery becomes inactive or the slot size changes, kill the UI
    if (smeltery == null || !smeltery.getCachedState().get(ControllerBlock.ACTIVE)
        || smeltery.getMeltingInventory().getSlots() != sideInventory.getSlotCount()) {
      this.onClose();
    }
  }
  @Override
  protected void drawBackground(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);
    super.drawBackground(matrices, partialTicks, mouseX, mouseY);


    // render fluids
    if (tank != null) tank.renderFluids(matrices);

    // fuel
    if (fuel != null) {
      getMinecraft().getTextureManager().bindTexture(BACKGROUND);
      fuel.draw(matrices);
    }
  }

  @Override
  protected void drawForeground(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawForeground(matrices, mouseX, mouseY);

    assert client != null;
    client.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 8, 16);

    // highlight hovered fluids
    if (tank != null) tank.renderHighlight(matrices, mouseX, mouseY);
    if (fuel != null) fuel.renderHighlight(matrices, mouseX - this.x, mouseY - this.y);

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) melting.drawHeatBars(matrices);
  }

  @Override
  protected void drawMouseoverTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawMouseoverTooltip(matrices, mouseX, mouseY);

    // fluid tooltips
    if (tank != null) tank.drawTooltip(matrices, mouseX, mouseY);
    if (fuel != null) {
      boolean hasTank = false;
      if (smeltery.getStructure() != null) {
        hasTank = smeltery.getStructure().hasTanks();
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
