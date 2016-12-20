package slimeknights.tconstruct.tools.common.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.library.client.Icons;

public class GuiButtonItem<T> extends GuiButton {

  // Positions from generic.png
  protected static final GuiElement GUI_Button_pressed = new GuiElement(144, 216, 18, 18, 256, 256);
  protected static final GuiElement GUI_Button_normal = new GuiElement(144 + 18 * 2, 216, 18, 18, 256, 256);
  protected static final GuiElement GUI_Button_hover = new GuiElement(144 + 18 * 4, 216, 18, 18, 256, 256);

  private final ItemStack icon;
  public final T data;
  public boolean pressed;

  private GuiElement guiPressed = GUI_Button_pressed;
  private GuiElement guiNormal = GUI_Button_normal;
  private GuiElement guiHover = GUI_Button_hover;
  private ResourceLocation locBackground = Icons.ICON;

  public GuiButtonItem(int buttonId, int x, int y, String displayName, T data) {
    super(buttonId, x, y, 18, 18, displayName);

    this.icon = null;
    this.data = data;
  }

  public GuiButtonItem(int buttonId, int x, int y, ItemStack icon, T data) {
    super(buttonId, x, y, 18, 18, icon.getDisplayName());

    this.icon = icon;
    this.data = data;
  }

  public GuiButtonItem<T> setGraphics(GuiElement normal, GuiElement hover, GuiElement pressed, ResourceLocation background) {
    guiPressed = pressed;
    guiNormal = normal;
    guiHover = hover;
    locBackground = background;

    return this;
  }

  @Override
  public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(locBackground);

    if(this.visible) {
      this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition &&
                     mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

      if(pressed) {
        guiPressed.draw(xPosition, yPosition);
      }
      else if(hovered) {
        guiHover.draw(xPosition, yPosition);
      }
      else {
        guiNormal.draw(xPosition, yPosition);
      }

      drawIcon(mc);
    }
  }

  protected void drawIcon(Minecraft mc) {
    mc.getRenderItem().renderItemIntoGUI(icon, xPosition + 1, yPosition + 1);
  }
}
