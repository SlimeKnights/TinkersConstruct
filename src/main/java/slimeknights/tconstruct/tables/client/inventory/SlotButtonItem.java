package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;

public class SlotButtonItem extends ButtonWidget {

  protected static final ElementScreen BUTTON_PRESSED_GUI = new ElementScreen(144, 216, 18, 18, 256, 256);
  protected static final ElementScreen BUTTON_NORMAL_GUI = new ElementScreen(144 + 18 * 2, 216, 18, 18, 256, 256);
  protected static final ElementScreen BUTTON_HOVER_GUI = new ElementScreen(144 + 18 * 4, 216, 18, 18, 256, 256);

  private final ItemStack icon;
  public final SlotInformation data;
  public boolean pressed;
  public final int buttonId;

  private ElementScreen pressedGui = BUTTON_PRESSED_GUI;
  private ElementScreen normalGui = BUTTON_NORMAL_GUI;
  private ElementScreen hoverGui = BUTTON_HOVER_GUI;
  private Identifier backgroundLocation = Icons.ICONS;

  public SlotButtonItem(int buttonId, int x, int y, Text text, SlotInformation data, PressAction onPress) {
    super(x, y, 18, 18, text, onPress);

    this.icon = null;
    this.data = data;
    this.buttonId = buttonId;
  }

  public SlotButtonItem(int buttonId, int x, int y, ItemStack icon, SlotInformation data, PressAction onPress) {
    super(x, y, 18, 18, icon.getName(), onPress);

    this.icon = icon;
    this.data = data;
    this.buttonId = buttonId;
  }

  public SlotButtonItem setGraphics(ElementScreen normal, ElementScreen hover, ElementScreen pressed, Identifier background) {
    this.pressedGui = pressed;
    this.normalGui = normal;
    this.hoverGui = hover;
    this.backgroundLocation = background;

    return this;
  }

  @Override
  public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    MinecraftClient.getInstance().getTextureManager().bindTexture(this.backgroundLocation);

    if (this.visible) {
      this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

      if (this.pressed) {
        this.pressedGui.draw(matrices, this.x, this.y);
      } else if (this.hovered) {
        this.hoverGui.draw(matrices, this.x, this.y);
      } else {
        this.normalGui.draw(matrices, this.x, this.y);
      }

      this.drawIcon(matrices, MinecraftClient.getInstance());
    }
  }

  protected void drawIcon(MatrixStack matrices, MinecraftClient mc) {
    mc.getItemRenderer().renderGuiItemIcon(this.icon, this.x + 1, this.y + 1);
  }
}
