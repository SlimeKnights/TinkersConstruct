package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.tables.client.ToolSlotInformationLoader;
import slimeknights.tconstruct.tables.client.inventory.ButtonItem;
import slimeknights.tconstruct.tables.client.inventory.library.slots.SlotInformation;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;

import java.util.List;

public class ToolStationButtonsScreen extends SideButtonsScreen {

  protected final ToolStationScreen parent;
  protected int selected = 0;
  private int style = 0;

  public ToolStationButtonsScreen(ToolStationScreen parent, Container container, PlayerInventory playerInventory, ITextComponent title) {
    super(parent, container, playerInventory, title, ToolStationScreen.COLUMN_COUNT, false);

    this.parent = parent;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void updatePosition(int parentX, int parentY, int parentSizeX, int parentSizeY) {
    super.updatePosition(parentX, parentY, parentSizeX, parentSizeY);

    int index = 0;
    buttonCount = 0;

    Button.IPressable onPressed = button -> {
      for (Object o : ToolStationButtonsScreen.this.buttons) {
        if (o instanceof ButtonItem) {
          ((ButtonItem<SlotInformation>) o).pressed = false;
        }
      }

      if (button instanceof ButtonItem) {
        ButtonItem<SlotInformation> buildScreenInfoButtonItem = (ButtonItem<SlotInformation>) button;

        buildScreenInfoButtonItem.pressed = true;

        ToolStationButtonsScreen.this.selected = buildScreenInfoButtonItem.buttonId;

        ToolStationButtonsScreen.this.parent.onToolSelection(buildScreenInfoButtonItem.data);
      }
    };

    for (SlotInformation slotInformation : ToolSlotInformationLoader.getSlotInformationList()) {
      ButtonItem<SlotInformation> buttonItem;

      if (slotInformation == ToolSlotInformationLoader.get(ToolSlotInformationLoader.REPAIR_NAME)) {
        buttonItem = new ButtonItem<SlotInformation>(index++, -1, -1, new TranslationTextComponent("gui.tconstruct.repair").getFormattedText(), slotInformation, onPressed) {
          @Override
          protected void drawIcon(Minecraft minecraft) {
            minecraft.getTextureManager().bindTexture(Icons.ICONS);
            Icons.ANVIL.draw(this.x, this.y);
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

    this.parent.updateGUI();
  }

  public void addInfoButton(ButtonItem<SlotInformation> buttonItem) {
    this.shiftButton(buttonItem, 0, -18 * this.style);
    this.addSideButton(buttonItem);
  }

  @SuppressWarnings("unchecked")
  public void wood() {
    for (Object o : this.buttons) {
      this.shiftButton((ButtonItem<SlotInformation>) o, 0, -18);
    }

    this.style = 2;
  }

  @SuppressWarnings("unchecked")
  public void metal() {
    for (Object o : this.buttons) {
      this.shiftButton((ButtonItem<SlotInformation>) o, 0, -18);
    }

    this.style = 1;
  }

  @SuppressWarnings("unchecked")
  public void setSelectedButtonByTool(ItemStack tool) {
    for (Object o : this.buttons) {
      if (o instanceof ButtonItem) {
        ButtonItem<SlotInformation> btn = (ButtonItem<SlotInformation>) o;
        btn.pressed = ItemStack.areItemStacksEqual(btn.data.getItemStack(), tool);
      }
    }
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
