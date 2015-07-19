package tconstruct.tools.client;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import java.io.IOException;
import java.util.List;

import codechicken.nei.recipe.GuiRecipe;
import tconstruct.tools.client.module.GuiModule;

public class GuiMultiModule extends GuiContainer {

  protected List<GuiModule> modules = Lists.newArrayList();

  public int cornerX;
  public int cornerY;
  public int realWidth;
  public int realHeight;

  public GuiMultiModule(Container container) {
    super(container);

    realWidth = -1;
    realHeight = -1;
  }

  protected void addModule(GuiModule module) {
    modules.add(module);
  }

  @Override
  public void initGui() {
    if(realWidth > -1) {
      // has to be reset before calling initGui so the position is getting retained
      xSize = realWidth;
      ySize = realHeight;
    }
    super.initGui();

    this.cornerX = this.guiLeft;
    this.cornerY = this.guiTop;
    this.realWidth = xSize;
    this.realHeight = ySize;

    //this.guiLeft = this.guiTop = 0;
    //this.xSize = width;
    //this.ySize = height;
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    for(GuiModule module : modules) {
      module.handleDrawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
  }

  @Override
  public void setWorldAndResolution(Minecraft mc, int width, int height) {
    super.setWorldAndResolution(mc, width, height);

    for(GuiModule module : modules) {
      module.setWorldAndResolution(mc, width, height);
      updateSubmodule(module);
    }
  }

  @Override
  public void onResize(Minecraft mc, int width, int height) {
    super.onResize(mc, width, height);

    for(GuiModule module : modules) {
      module.onResize(mc, width, height);
      updateSubmodule(module);
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    int oldX = guiLeft;
    int oldY = guiTop;
    int oldW = xSize;
    int oldH = ySize;

    guiLeft = cornerX;
    guiTop = cornerY;
    xSize = realWidth;
    ySize = realHeight;
    super.drawScreen(mouseX, mouseY, partialTicks);
    guiLeft = oldX;
    guiTop = oldY;
    xSize = oldW;
    ySize = oldH;
  }

  @Override
  protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
    pointX -= this.cornerX;
    pointY -= this.cornerY;
    return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
  }

  protected void updateSubmodule(GuiModule module) {
    module.updatePosition(this.cornerX, this.cornerY, this.realWidth, this.realHeight);

    if(module.guiLeft < this.guiLeft) {
      this.xSize += this.guiLeft - module.guiLeft;
      this.guiLeft = module.guiLeft;
    }
    if(module.guiTop < this.guiTop) {
      this.ySize += this.guiTop - module.guiTop;
      this.guiTop = module.guiTop;
    }
    if(module.guiRight() > this.guiLeft + this.xSize) {
      xSize = module.guiRight() - this.guiLeft;
    }
    if(module.guiBottom() > this.guiTop + this.ySize) {
      ySize = module.guiBottom() - this.guiTop;
    }
  }
}
