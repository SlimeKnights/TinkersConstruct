package slimeknights.tconstruct.smeltery.client;

import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideinventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiMultiModule {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected final GuiSmelterySideinventory sideinventory;
  protected final TileSmeltery smeltery;

  public GuiSmeltery(ContainerSmeltery container, TileSmeltery smeltery) {
    super(container);

    this.smeltery = smeltery;

    sideinventory = new GuiSmelterySideinventory(this, container.getSubContainer(ContainerSideInventory.class), smeltery.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
