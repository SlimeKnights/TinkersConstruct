package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiFuelModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiTankModule;
import slimeknights.tconstruct.smeltery.inventory.AlloyerContainer;
import slimeknights.tconstruct.smeltery.tileentity.controller.AlloyerTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.module.FuelModule;
import slimeknights.tconstruct.smeltery.tileentity.module.alloying.MixerAlloyTank;

import javax.annotation.Nullable;

public class AlloyerScreen extends ContainerScreen<AlloyerContainer> implements IScreenWithFluidTank {
  private static final int[] INPUT_TANK_START_X = {54, 22, 38, 70, 6};
  private static final ResourceLocation BACKGROUND = TConstruct.getResource("textures/gui/alloyer.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 0, 34, 52, 256, 256);
  private static final ElementScreen FUEL_SLOT = new ElementScreen(176, 52, 18, 36, 256, 256);
  private static final ElementScreen FUEL_TANK = new ElementScreen(194, 52, 14, 38, 256, 256);
  private static final ElementScreen INPUT_TANK = new ElementScreen(208, 52, 16, 54, 256, 256);

  private final GuiFuelModule fuel;
  private final GuiTankModule outputTank;
  private GuiTankModule[] inputTanks = new GuiTankModule[0];
  public AlloyerScreen(AlloyerContainer container, PlayerInventory inv, ITextComponent name) {
    super(container, inv, name);
    AlloyerTileEntity te = container.getTile();
    if (te != null) {
      FuelModule fuelModule = te.getFuelModule();
      fuel = new GuiFuelModule(this, fuelModule, 153, 32, 12, 36, 152, 15, container.isHasFuelSlot());
      outputTank = new GuiTankModule(this, te.getTank(), 114, 16, 34, 52);
      updateTanks();
    } else {
      fuel = null;
      outputTank = null;
    }
  }

  /** Updates the tanks from the tile entity */
  private void updateTanks() {
    AlloyerTileEntity te = container.getTile();
    if (te != null) {
      MixerAlloyTank alloyTank = te.getAlloyTank();
      int numTanks = alloyTank.getTanks();
      GuiTankModule[] tanks = new GuiTankModule[numTanks];
      int max = Math.min(numTanks, 5); // only support 5 tanks, any more is impossible
      for (int i = 0; i < max; i++) {
        tanks[i] = new GuiTankModule(this, alloyTank.getFluidHandler(i), INPUT_TANK_START_X[i], 16, 14, 52);
      }
      this.inputTanks = tanks;
    }
  }

  @Override
  public void tick() {
    super.tick();
    // if the input count changes, update
    AlloyerTileEntity te = container.getTile();
    if (te != null && te.getAlloyTank().getTanks() != inputTanks.length) {
      this.updateTanks();
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
    if (outputTank != null) outputTank.draw(matrices);

    // draw tank backgrounds first, then draw tank contents, less binding
    getMinecraft().getTextureManager().bindTexture(BACKGROUND);
    for (GuiTankModule tankModule : inputTanks) {
      INPUT_TANK.draw(matrices, tankModule.getX() - 1 + this.guiLeft, tankModule.getY() - 1 + this.guiTop);
    }
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.draw(matrices);
    }

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
    if (outputTank != null) outputTank.highlightHoveredFluid(matrices, checkX, checkY);
    for (GuiTankModule tankModule : inputTanks) {
      tankModule.highlightHoveredFluid(matrices, checkX, checkY);
    }

    // highlight hovered fuel
    if (fuel != null) fuel.renderHighlight(matrices, checkX, checkY);

    // scala
    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 114, 16);
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

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
    int checkX = (int) mouseX - guiLeft;
    int checkY = (int) mouseY - guiTop;

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
