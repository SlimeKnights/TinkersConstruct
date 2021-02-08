package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiTankModule;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;

import javax.annotation.Nullable;

public class MelterScreen extends ContainerScreen<MelterContainer> implements IScreenWithFluidTank {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/melter.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 52, 52, 256, 256);
  private static final ElementScreen FUEL_SLOT = new ElementScreen(176, 52, 18, 36, 256, 256);
  private static final ElementScreen FUEL_TANK = new ElementScreen(194, 52, 14, 38, 256, 256);

  private final GuiMeltingModule melting;
  private final GuiFuelModule fuel;
  private final GuiTankModule tank;
  public MelterScreen(MelterContainer container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    MelterTileEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, slot -> true);
      fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot());
      tank = new GuiTankModule(this, te.getTank(), 90, 16, 52, 52);
    } else {
      melting = null;
      fuel = null;
      tank = null;
    }
  }

  @Override
  public void render(MatrixStack matrices, int x, int y, float partialTicks) {
    this.renderBackground(matrices);
    super.render(matrices, x, y, partialTicks);
    this.renderHoveredTooltip(matrices, x, y);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);

    // fluids
    if (tank != null) tank.draw(matrices);

    // fuel
    if (fuel != null) {
      getMinecraft().getTextureManager().bindTexture(BACKGROUND);
      // draw the correct background for the fuel type
      if (container.isHasFuelSlot()) {
        FUEL_SLOT.draw(matrices, guiLeft + 150, guiTop + 31);
      } else {
        FUEL_TANK.draw(matrices, guiLeft + 152, guiTop + 31);
      }
      fuel.draw(matrices);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    GuiUtil.drawContainerNames(matrices, this, this.font, this.playerInventory);
    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;

    // highlight hovered tank
    if (tank != null) tank.highlightHoveredFluid(matrices, checkX, checkY);
    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(matrices, checkX, checkY);

    // scala
    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 90, 16);

    // heat bars
    if (melting != null) {
      melting.drawHeatBars(matrices);
    }
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

    // tank tooltip
    if (tank != null) tank.renderTooltip(matrices, mouseX, mouseY);

    // heat tooltips
    if (melting != null) melting.drawHeatTooltips(matrices, mouseX, mouseY);

    // fuel tooltip
    if (fuel != null) fuel.addTooltip(matrices, mouseX, mouseY, true);
  }

  @Nullable
  @Override
  public Object getIngredientUnderMouse(double mouseX, double mouseY) {
    Object ingredient = null;
    int checkX = (int) mouseX - guiLeft;
    int checkY = (int) mouseY - guiTop;

    // try fuel first, its faster
    if (fuel != null)
      ingredient = fuel.getIngredient(checkX, checkY);

    if (tank != null && ingredient == null)
      ingredient = tank.getIngreientUnderMouse(checkX, checkY);

    return ingredient;
  }
}
