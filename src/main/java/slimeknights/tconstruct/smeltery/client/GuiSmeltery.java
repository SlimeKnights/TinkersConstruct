package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.client.gui.GuiMultiModule;
import slimeknights.mantle.inventory.ContainerMultiModule;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.client.module.GuiSmelterySideinventory;
import slimeknights.tconstruct.smeltery.inventory.ContainerSmeltery;
import slimeknights.tconstruct.smeltery.tileentity.TileSmeltery;
import slimeknights.tconstruct.tools.inventory.ContainerSideInventory;

public class GuiSmeltery extends GuiMultiModule {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/smeltery.png");

  protected GuiElement scala = new GuiElement(176, 76, 52, 52, 256, 256);

  protected final GuiSmelterySideinventory sideinventory;
  protected final TileSmeltery smeltery;

  public GuiSmeltery(ContainerSmeltery container, TileSmeltery smeltery) {
    super(container);

    this.smeltery = smeltery;

    sideinventory = new GuiSmelterySideinventory(this, container.getSubContainer(ContainerSideInventory.class),
                                                 smeltery, smeltery.getSizeInventory(), container.calcColumns());
    addModule(sideinventory);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);

    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
  }
}
