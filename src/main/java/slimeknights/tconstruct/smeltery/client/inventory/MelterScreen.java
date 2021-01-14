package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.library.client.util.FluidTooltipHandler;
import slimeknights.tconstruct.library.fluid.FluidTankAnimated;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.inventory.MelterContainer;
import slimeknights.tconstruct.smeltery.tileentity.MelterTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class MelterScreen extends ContainerScreen<MelterContainer> {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/melter.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 52, 52, 256, 256);

  private final GuiMeltingModule melting;
  private final GuiFuelModule fuel;
  public MelterScreen(MelterContainer container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    MelterTileEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      melting = new GuiMeltingModule(this, te.getMeltingInventory(), fuelModule::getTemperature, slot -> true);
      fuel = new GuiFuelModule(this, fuelModule, 153, 16, 12, 52);
    } else {
      melting = null;
      fuel = null;
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
    MelterTileEntity melter = container.getTile();
    if (melter != null) {
      FluidTankAnimated tank = melter.getTank();
      GuiUtil.renderFluidTank(matrices, this, tank.getFluid(), tank.getCapacity(), 90, 16, 52, 52, 100);
    }

    // fuel
    if (fuel != null) fuel.drawTank(matrices);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    GuiUtil.drawContainerNames(matrices, this, this.font, this.playerInventory);

    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    MelterTileEntity melter = container.getTile();
    if (melter == null) {
      return;
    }

    // highlight hovered fluid
    if (GuiUtil.isHovered(checkX, checkY, 89, 15, 54, 54)) {
      FluidTankAnimated tank = melter.getTank();
      int fluidHeight = 52 * tank.getFluidAmount() / tank.getCapacity();
      int middle = 68 - fluidHeight;
      // highlight just fluid
      if (checkY > middle) {
        GuiUtil.renderHighlight(matrices, 90, middle, 52, fluidHeight);
      } else {
        // or highlight empty
        GuiUtil.renderHighlight(matrices, 90, 16, 52, 52 - fluidHeight);
      }
    }

    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(matrices, checkX, checkY);

    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 90, 16);

    if (melting != null) {
      melting.drawHeatBars(matrices);
    }
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

    int checkX = mouseX - this.guiLeft;
    int checkY = mouseY - this.guiTop;
    MelterTileEntity melter = container.getTile();
    if (melter == null) {
      return;
    }
    if (GuiUtil.isHovered(checkX, checkY, 89, 15, 54, 54)) {
      FluidTankAnimated tank = melter.getTank();
      int amount = tank.getFluidAmount();
      int capacity = tank.getCapacity();

      // if hovering over the fluid, display with name
      final List<ITextComponent> tooltip;
      if (checkY > 68 - (52 * amount / capacity)) {
        tooltip = FluidTooltipHandler.getFluidTooltip(tank.getFluid());
      } else {
        // function to call for amounts
        BiConsumer<Integer, List<ITextComponent>> formatter = Util.isShiftKeyDown()
          ? FluidTooltipHandler::appendBuckets
          : FluidTooltipHandler::appendIngots;

        // add tooltips
        tooltip = new ArrayList<>();
        tooltip.add(new TranslationTextComponent(GuiSmelteryTank.TOOLTIP_CAPACITY));
        formatter.accept(capacity, tooltip);
        if (capacity != amount) {
          tooltip.add(new TranslationTextComponent(GuiSmelteryTank.TOOLTIP_AVAILABLE));
          formatter.accept(capacity - amount, tooltip);
        }

        // add shift message
        //tooltip.add("");
        FluidTooltipHandler.appendShift(tooltip);
      }

      // TODO: func_243308_b->renderTooltip
      this.func_243308_b(matrices, tooltip, mouseX, mouseY);
    }

    // heat tooltips
    if (melting != null) {
      melting.drawHeatTooltips(matrices, mouseX, mouseY);
    }

    // fuel tooltip
    if (fuel != null) fuel.addTooltip(matrices, mouseX, mouseY);
  }
}
