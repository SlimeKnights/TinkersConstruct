package slimeknights.tconstruct.tables.client.inventory.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.client.RenderUtils;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;

public class SlotButtonItem extends Button {

  public static int WIDTH = 18, HEIGHT = 18;

  protected static final ElementScreen BUTTON_PRESSED_GUI = new ElementScreen(144, 216, WIDTH, HEIGHT, 256, 256);
  protected static final ElementScreen BUTTON_NORMAL_GUI = new ElementScreen(144 + WIDTH * 2, 216, WIDTH, HEIGHT, 256, 256);
  protected static final ElementScreen BUTTON_HOVER_GUI = new ElementScreen(144 + WIDTH * 4, 216, WIDTH, HEIGHT, 256, 256);

  @Getter
  private final StationSlotLayout layout;
  public boolean pressed;
  public final int buttonId;

  private ElementScreen pressedGui = BUTTON_PRESSED_GUI;
  private ElementScreen normalGui = BUTTON_NORMAL_GUI;
  private ElementScreen hoverGui = BUTTON_HOVER_GUI;
  private ResourceLocation backgroundLocation = Icons.ICONS;

  public SlotButtonItem(int buttonId, int x, int y, StationSlotLayout layout, OnPress onPress) {
    super(x, y, WIDTH, HEIGHT, layout.getDisplayName(), onPress);
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
  public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
    RenderUtils.setup(this.backgroundLocation);

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
