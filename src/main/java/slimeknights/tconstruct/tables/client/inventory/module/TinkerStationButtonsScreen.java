package slimeknights.tconstruct.tables.client.inventory.module;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.SlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.table.TinkerStationScreen;

import java.util.List;

public class TinkerStationButtonsScreen extends SideButtonsScreen {

  protected final TinkerStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsScreen(TinkerStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, TinkerStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;
    this.buttonCount = 0;

    Button.IPressable onPressed = button -> {
      for (Object o : TinkerStationButtonsScreen.this.buttons) {
        if (o instanceof ButtonItem) {
          ((ButtonItem<SlotInformation>) o).pressed = false;
        }
      }

      if (button instanceof ButtonItem) {
        ButtonItem<SlotInformation> buildScreenInfoButtonItem = (ButtonItem<SlotInformation>) button;

        buildScreenInfoButtonItem.pressed = true;

        TinkerStationButtonsScreen.this.selected = buildScreenInfoButtonItem.buttonId;

        TinkerStationButtonsScreen.this.parent.onToolSelection(buildScreenInfoButtonItem.data);
      }
    };

    for (SlotInformation slotInformation : SlotInformationLoader.getSlotInformationList()) {
      ButtonItem<SlotInformation> buttonItem;

      if (slotInformation == SlotInformationLoader.get(TinkerStationScreen.REPAIR_NAME)) {
        buttonItem = new ButtonItem<SlotInformation>(index++, -1, -1, new TranslationTextComponent("gui.tconstruct.repair"), slotInformation, onPressed) {
          @Override
          protected void drawIcon(MatrixStack matrices, Minecraft minecraft) {
            minecraft.getTextureManager().bindTexture(Icons.ICONS);
            Icons.ANVIL.draw(matrices, this.x, this.y);
          }
        };
      }
      else {
        buttonItem = new ButtonItem<>(index++, -1, -1, slotInformation.getToolForRendering(), slotInformation, onPressed);
      }

      this.addInfoButton(buttonItem);

      if (index - 1 == selected) {
        buttonItem.pressed = true;
      }
    }

    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);
  }

  public void addInfoButton(ButtonItem<SlotInformation> buttonItem) {
    this.shiftButton(buttonItem, 0, -18 * this.style);
    this.addSideButton(buttonItem);
  }

  @SuppressWarnings("unchecked")
  public void shiftStyle(int style) {
    for (Object o : this.buttons) {
      this.shiftButton((ButtonItem<SlotInformation>) o, 0, -18);
    }

    this.style = style;
  }

  protected void shiftButton(ButtonItem<SlotInformation> button, int xd, int yd) {
    button.setGraphics(Icons.BUTTON.shift(xd, yd),
      Icons.BUTTON_HOVERED.shift(xd, yd),
      Icons.BUTTON_PRESSED.shift(xd, yd),
      Icons.ICONS);
  }

  public List<Widget> getButtons() {
    return this.buttons;
  }
}
