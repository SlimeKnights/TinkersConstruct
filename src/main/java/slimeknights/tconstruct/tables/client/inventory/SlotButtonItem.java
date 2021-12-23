package slimeknights.tconstruct.tables.client.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;

public class SlotButtonItem extends Button {
  protected static final ElementScreen BUTTON_PRESSED_GUI = new ElementScreen(144, 216, 18, 18, 256, 256);
  protected static final ElementScreen BUTTON_NORMAL_GUI = new ElementScreen(144 + 18 * 2, 216, 18, 18, 256, 256);
  protected static final ElementScreen BUTTON_HOVER_GUI = new ElementScreen(144 + 18 * 4, 216, 18, 18, 256, 256);

  @Getter
  private final StationSlotLayout layout;
  public boolean pressed;
  public final int buttonId;

  private ElementScreen pressedGui = BUTTON_PRESSED_GUI;
  private ElementScreen normalGui = BUTTON_NORMAL_GUI;
  private ElementScreen hoverGui = BUTTON_HOVER_GUI;
  private ResourceLocation backgroundLocation = Icons.ICONS;

  public SlotButtonItem(int buttonId, int x, int y, StationSlotLayout layout, IPressable onPress) {
    super(x, y, 18, 18, layout.getDisplayName(), onPress);
    this.layout = layout;
    this.buttonId = buttonId;
  }

  public SlotButtonItem setGraphics(ElementScreen normal, ElementScreen hover, ElementScreen pressed, ResourceLocation background) {
    this.pressedGui = pressed;
    this.normalGui = normal;
    this.hoverGui = hover;
    this.backgroundLocation = background;

    return this;
  }

  @Override
  public void renderWidget(MatrixStack matrices, int mouseX, int mouseY, float partialTicks) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    Minecraft.getInstance().getTextureManager().bindTexture(this.backgroundLocation);

    if (this.visible) {
      this.isHovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

      if (this.pressed) {
        this.pressedGui.draw(matrices, this.x, this.y);
      } else if (this.isHovered) {
        this.hoverGui.draw(matrices, this.x, this.y);
      } else {
        this.normalGui.draw(matrices, this.x, this.y);
      }

      //this.drawIcon(matrices, Minecraft.getInstance());
      TinkerStationScreen.renderIcon(matrices, layout.getIcon(), this.x + 1, this.y + 1);
    }
  }

//  protected void drawIcon(MatrixStack matrices, Minecraft mc) {
//    mc.getItemRenderer().renderItemIntoGUI(this.icon, this.x + 1, this.y + 1);
//  }
}
