package slimeknights.tconstruct.smeltery.client;

import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.Util;

public class GuiSmeltery extends GuiMultiModule {

  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  public GuiSmeltery(ContainerMultiModule container) {
    super(container);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
