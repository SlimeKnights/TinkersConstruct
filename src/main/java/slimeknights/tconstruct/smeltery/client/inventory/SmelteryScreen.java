package slimeknights.tconstruct.smeltery.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.block.ControllerBlock;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiMeltingModule;
import slimeknights.tconstruct.smeltery.client.inventory.module.GuiSmelteryTank;
import slimeknights.tconstruct.smeltery.client.inventory.module.SmelterySideInventoryScreen;
import slimeknights.tconstruct.smeltery.inventory.SmelteryContainer;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryTileEntity;

public class SmelteryScreen extends MultiModuleScreen<SmelteryContainer> {
  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");
  private static final ElementScreen SCALA = new ElementScreen(176, 76, 52, 52, 256, 256);

  private final SmelterySideInventoryScreen sideInventory;
  private final SmelteryTileEntity smeltery;
  private final GuiSmelteryTank tank;
  public final GuiMeltingModule melting;
  public SmelteryScreen(SmelteryContainer container, PlayerInventory playerInventory, ITextComponent title) {
    super(container, playerInventory, title);

    SmelteryTileEntity te = container.getTile();
    if (te != null) {
      this.smeltery = te;
      this.tank = new GuiSmelteryTank(this, te.getTank(), 8, 16, SCALA.w, SCALA.h);
      int slots = te.getMeltingInventory().getSlots();
      this.sideInventory = new SmelterySideInventoryScreen(this, container.getSideInventory(), playerInventory, slots, SmelteryContainer.calcColumns(slots));
      this.melting = new GuiMeltingModule(this, te.getMeltingInventory(), te::getTemperature, sideInventory::shouldDrawSlot);
      addModule(sideInventory);
    } else {
      this.smeltery = null;
      this.tank = null;
      this.melting = null;
      this.sideInventory = null;
    }
  }

  @Override
  public void tick() {
    super.tick();
    // if the smeltery becomes inactive or the slot size changes, kill the UI
    if (smeltery == null || !smeltery.getBlockState().get(ControllerBlock.ACTIVE)
        || smeltery.getMeltingInventory().getSlots() != sideInventory.getSlotCount()) {
      this.closeScreen();
    }
  }
  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrices, float partialTicks, int mouseX, int mouseY) {
    GuiUtil.drawBackground(matrices, this, BACKGROUND);
    super.drawGuiContainerBackgroundLayer(matrices, partialTicks, mouseX, mouseY);

    // render fluids
    if (tank != null) {
      tank.renderFluids(matrices);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack matrices, int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(matrices, mouseX, mouseY);

    // todo: highlight hovered fuel

    assert minecraft != null;
    minecraft.getTextureManager().bindTexture(BACKGROUND);
    RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    SCALA.draw(matrices, 8, 16);

    if (tank != null) {
      tank.renderHighlight(matrices, mouseX, mouseY);
    }

    // while this might make sense to draw in the side inventory logic, slots are rendered by the parent screen it seems
    // so we get the most accurate offset rendering it here, as we offset the foreground of submodules but they don't draw their own slots
    // I hate the whole multimodule system right now
    if (melting != null) {
      melting.drawHeatBars(matrices);
    }
  }

  @Override
  protected void renderHoveredTooltip(MatrixStack matrices, int mouseX, int mouseY) {
    super.renderHoveredTooltip(matrices, mouseX, mouseY);

    if (tank != null) {
      tank.drawTooltip(matrices, mouseX, mouseY);
    }
    // todo: fuel tooltip
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0 && tank != null) {
      tank.handleClick((int)mouseX - cornerX, (int)mouseY - cornerY);
    }
    return super.mouseClicked(mouseX, mouseY, mouseButton);
  }
}
