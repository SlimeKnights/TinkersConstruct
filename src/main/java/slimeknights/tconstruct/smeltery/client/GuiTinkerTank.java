package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.io.IOException;
import java.util.List;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.GuiUtil;
import slimeknights.tconstruct.smeltery.tileentity.TileTinkerTank;

public class GuiTinkerTank extends GuiContainer implements IGuiLiquidTank {

  public static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/tinker_tank.png");
  protected GuiElement scala = new GuiElement(122, 0, 106, 106, 256, 256);

  public TileTinkerTank tinkerTank;

  public GuiTinkerTank(Container inventorySlotsIn, TileTinkerTank tinkerTank) {
    super(inventorySlotsIn);

    this.xSize = 122;
    this.ySize = 130;

    this.tinkerTank = tinkerTank;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();
    super.drawScreen(mouseX, mouseY, partialTicks);

    // draw the tooltips, if any
    List<String> tooltip = GuiUtil.getTankTooltip(tinkerTank.getTank(), mouseX - guiLeft, mouseY - guiTop, 8, 16, 114, 122);
    if(tooltip != null) {
      this.drawHoveringText(tooltip, mouseX, mouseY);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    // draw the scale
    this.mc.getTextureManager().bindTexture(BACKGROUND);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    scala.draw(8, 16);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    drawBackground(BACKGROUND);
    drawContainerName();

    // draw liquids
    GuiUtil.drawGuiTank(tinkerTank.getTank(), 8 + guiLeft, 16 + guiTop, scala.w, scala.h, this.zLevel);
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    if(mouseButton == 0) {
      GuiUtil.handleTankClick(tinkerTank.getTank(), mouseX - guiLeft, mouseY - guiTop, 8, 16, 114, 122);
    }

    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public FluidStack getFluidStackAtPosition(int mouseX, int mouseY) {
    return GuiUtil.getFluidStackAtPosition(tinkerTank.getTank(), mouseX - guiLeft, mouseY - guiTop, 8, 16, 114, 122);
  }

  protected void drawBackground(ResourceLocation background) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    this.mc.getTextureManager().bindTexture(background);
    this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
  }

  protected void drawContainerName() {
    BaseContainer<?> multiContainer = (BaseContainer<?>) this.inventorySlots;
    String localizedName = multiContainer.getInventoryDisplayName();
    if(localizedName != null) {
      this.fontRenderer.drawString(localizedName, 8 + guiLeft, 6 + guiTop, 0x404040);
    }
  }

}
