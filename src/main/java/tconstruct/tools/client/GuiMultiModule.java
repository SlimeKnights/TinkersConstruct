package tconstruct.tools.client;

import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

import java.io.IOException;
import java.util.List;

import tconstruct.tools.client.module.GuiModule;
import tconstruct.tools.inventory.ContainerMultiModule;
import tconstruct.tools.inventory.SlotWrapper;

public class GuiMultiModule extends GuiContainer {

  protected List<GuiModule> modules = Lists.newArrayList();

  public int cornerX;
  public int cornerY;
  public int realWidth;
  public int realHeight;

  public GuiMultiModule(ContainerMultiModule container) {
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

    // workaround for NEIs ASM hax. sigh.
    GuiScreen tmp = mc.currentScreen;
    // todo: change this to reflection to set the manager to the same instance as this one
    for(GuiModule module : modules) {
      mc.currentScreen = module;
      module.setWorldAndResolution(mc, width, height);
      updateSubmodule(module);
    }
    mc.currentScreen = tmp;
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


  // needed to get the correct slot on clicking
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

  @Override
  public void drawSlot(Slot slotIn) {
    GuiModule module = getModuleForSlot(slotIn.slotNumber);

    if(module != null) {
      Slot slot = slotIn;
      // unwrap for the call to the module
      if(slotIn instanceof SlotWrapper) {
        slot = ((SlotWrapper) slotIn).parent;
      }
      if(!module.shoudlDrawSlot(slot))
        return;
    }

    super.drawSlot(slotIn);
  }

  @Override
  public boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
    GuiModule module = getModuleForSlot(slotIn.slotNumber);

    // mouse inside the module of the slot?
    if(module != null) {
      if(this.isPointInRegion(module.guiLeft, module.guiTop, module.guiRight(), module.guiBottom(), mouseX, mouseY)) {
        mouseX -= this.cornerX;
        mouseY -= this.cornerY;
        // unwrap for the call to the module
        if(slotIn instanceof SlotWrapper) {
          slotIn = ((SlotWrapper) slotIn).parent;
        }

        return module.isMouseOverSlot(slotIn, mouseX, mouseY);
      }
    }

    return super.isMouseOverSlot(slotIn, mouseX, mouseY);
  }
/*
  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    GuiModule module = getModuleForPoint(mouseX, mouseY);
    if(module != null) {
      mouseX -= this.cornerX;
      mouseY -= this.cornerY;
      module.handleMouseClicked(mouseX, mouseY, mouseButton);
    }
    else {
      super.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    GuiModule module = getModuleForPoint(mouseX, mouseY);
    if(module != null) {
      mouseX -= this.cornerX;
      mouseY -= this.cornerY;
      module.handleMouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
    else {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    GuiModule module = getModuleForPoint(mouseX, mouseY);
    if(module != null) {
      mouseX -= this.cornerX;
      mouseY -= this.cornerY;
      module.handleMouseReleased(mouseX, mouseY, state);
    }
    else {
      super.mouseReleased(mouseX, mouseY, state);
    }
  }

  private GuiModule getModuleForPoint(int x, int y) {
    for(GuiModule module : modules) {
      if(this.isPointInRegion(module.guiLeft, module.guiTop, module.guiRight(), module.guiBottom(), x + this.cornerX, y + this.cornerY)) {
        return module;
      }
    }

    return null;
  }
*/
  private GuiModule getModuleForSlot(int slotNumber) {
    return getModuleForContainer(getContainer().getSlotContainer(slotNumber));
  }

  private GuiModule getModuleForContainer(Container container) {
    for(GuiModule module : modules) {
      if(module.inventorySlots == container) {
        return module;
      }
    }

    return null;
  }

  private ContainerMultiModule getContainer() {
    return (ContainerMultiModule) inventorySlots;
  }

}
