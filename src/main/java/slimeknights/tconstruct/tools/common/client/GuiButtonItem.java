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

  public GuiButtonItem(int buttonId, int x, int y, String displayName, @Nonnull T data) {
    super(buttonId, x, y, 18, 18, displayName);

    this.icon = null;
    this.data = data;
  }

  public GuiButtonItem(int buttonId, int x, int y, ItemStack icon, @Nonnull T data) {
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
  public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(locBackground);

    if(this.visible) {
      this.hovered = mouseX >= this.x && mouseY >= this.y &&
                     mouseX < this.x + this.width && mouseY < this.y + this.height;

      if(pressed) {
        guiPressed.draw(x, y);
      }
      else if(hovered) {
        guiHover.draw(x, y);
      }
      else {
        guiNormal.draw(x, y);
      }

      drawIcon(mc);
    }
  }

  protected void drawIcon(Minecraft mc) {
    mc.getRenderItem().renderItemIntoGUI(icon, x + 1, y + 1);
  }
}
