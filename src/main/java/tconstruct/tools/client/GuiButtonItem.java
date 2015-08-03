package tconstruct.tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import tconstruct.common.client.gui.GuiElement;
import tconstruct.library.Util;
import tconstruct.library.client.ToolBuildGuiInfo;

public class GuiButtonItem extends GuiButton {
  private static final ResourceLocation BACKGROUND = Util.getResource("textures/gui/icons.png");

  protected static final GuiElement GUI_Button_pressed = new GuiElement(144, 216, 18, 18, 256, 256);
  protected static final GuiElement GUI_Button_normal = new GuiElement(144 + 18 * 2, 216, 18, 18, 256, 256);
  protected static final GuiElement GUI_Button_hover = new GuiElement(144 + 18 * 4, 216, 18, 18, 256, 256);

  private final ItemStack icon;
  public final ToolBuildGuiInfo info;
  public boolean pressed;

  private GuiElement guiPressed = GUI_Button_pressed;
  private GuiElement guiNormal = GUI_Button_normal;
  private GuiElement guiHover = GUI_Button_hover;

  public GuiButtonItem(int buttonId, int x, int y, String displayName, ToolBuildGuiInfo info) {
    super(buttonId, x, y, 18, 18, displayName);

    this.icon = null;
    this.info = info;
  }

  public GuiButtonItem(int buttonId, int x, int y, ItemStack icon, ToolBuildGuiInfo info) {
    super(buttonId, x, y, 18, 18, icon.getDisplayName());

    this.icon = icon;
    this.info = info;
  }

  public GuiButtonItem setGraphics(GuiElement normal, GuiElement hover, GuiElement pressed) {
    guiPressed = pressed;
    guiNormal = normal;
    guiHover = hover;

    return this;
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(BACKGROUND);

    if(this.visible) {
      this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                     mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

      if(pressed) {
        guiPressed.draw(xPosition, yPosition);
      }
      else if(hovered) {
        guiNormal.draw(xPosition, yPosition);
      }
      else {
        guiHover.draw(xPosition, yPosition);
      }

      drawIcon(mc);
    }
  }

  protected void drawIcon(Minecraft mc) {
    mc.getRenderItem().renderItemIntoGUI(icon, xPosition+1, yPosition+1);
  }
}
