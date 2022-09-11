package slimeknights.tconstruct.tables.client.inventory.module;

import net.minecraft.client.gui.components.Button;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.client.inventory.TinkerStationScreen;
import slimeknights.tconstruct.tables.client.inventory.widget.SlotButtonItem;

import java.util.List;

public class TinkerStationButtonsWidget extends SideButtonsWidget<SlotButtonItem> {

  private int style = 0;

  public static final int WOOD_STYLE = 2;
  public static final int METAL_STYLE = 1;

  public TinkerStationButtonsWidget(TinkerStationScreen parent) {
    super(parent, TinkerStationScreen.COLUMN_COUNT, false);

    // Logic to run when a button is pressed
    Button.OnPress onButtonPressed = self -> {
      for (SlotButtonItem button : TinkerStationButtonsWidget.this.buttons) {
        button.pressed = false;
      }
      if (self instanceof SlotButtonItem slotInformationButton) {
        slotInformationButton.pressed = true;
        parent.onToolSelection(slotInformationButton.getLayout());
      }
    };

    // repair button
    SlotButtonItem slotButtonItem = new SlotButtonItem(0, -1, -1, parent.getDefaultLayout(), onButtonPressed);
    this.addInfoButton(slotButtonItem);
    if (parent.getDefaultLayout() == parent.getCurrentLayout()) {
      slotButtonItem.pressed = true;
    }

    // tool buttons
    int index = 1;
    for (StationSlotLayout layout : StationSlotLayoutLoader.getInstance().getSortedSlots()) {
      if (layout.getInputSlots().size() <= parent.getMaxInputs()) {
        slotButtonItem = new SlotButtonItem(index, -1, -1, layout, onButtonPressed);
        this.addInfoButton(slotButtonItem);
        if (layout == parent.getCurrentLayout()) {
          slotButtonItem.pressed = true;
        }
        index++;
      }
    }
  }

  public void addInfoButton(SlotButtonItem slotButtonItem) {
    this.shiftButton(slotButtonItem, 0, -18 * this.style);
    this.buttons.add(slotButtonItem);
  }

  public void shiftStyle(int style) {
      for (SlotButtonItem button : this.buttons) {
        this.shiftButton(button, 0, -18);
    }

    this.style = style;
  }

  protected void shiftButton(SlotButtonItem button, int xd, int yd) {
    button.setGraphics(Icons.BUTTON.shift(xd, yd),
      Icons.BUTTON_HOVERED.shift(xd, yd),
      Icons.BUTTON_PRESSED.shift(xd, yd),
      Icons.ICONS);
  }

  public List<SlotButtonItem> getButtons() {
    return this.buttons;
  }
}
